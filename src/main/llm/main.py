import json
from typing import Optional, List
from fastapi import FastAPI, HTTPException
from pydantic import BaseModel, Field
from openai import OpenAI

client = OpenAI(
    base_url="https://irlab.org/inference-olorin", 
    api_key="sk-irlab-llm-ff0044f00601bf11c8f6bd77a111d9b6"
)

app = FastAPI(title="ATHLIX - AI Replanning API")

class ReplanRequest(BaseModel):
    context: dict
    failedSession: dict
    adjustableSessions: list

class UpdatedBlock(BaseModel):
    name: str = Field(..., description="Nombre del bloque")
    distanceOrDuration: str = Field(..., description="Ej: '20 min', '10 km'. OBLIGATORIO AUMENTAR EL VALOR NUMÉRICO si el TSS sube.")
    pace: str = Field(..., description="Ritmo o zona de intensidad (USAR SOLO LAS ZONAS PERMITIDAS PARA ESE DEPORTE)")
    sets: int = Field(..., description="Número de series")
    reps: int = Field(..., description="Número de repeticiones")
    rest: str = Field(..., description="Descanso. Si no hay, pon '0'")

class UpdatedSession(BaseModel):
    date: str = Field(..., description="Fecha de la sesión")
    sport: str = Field(..., description="Deporte de la sesión")
    newTss: float = Field(..., description="TSS final de la sesión")
    updatedBlocks: List[UpdatedBlock] = Field(..., description="Bloques de la sesión")

class RescheduledSession(BaseModel):
    newDate: str = Field(..., description="Nueva fecha asignada a la sesión fallida (DEBE ser estrictamente una de las permitidas en las reglas)")
    sport: str = Field(..., description="CÓPIALO EXACTAMENTE del campo 'sport' de la 'failedSession'")
    tss: float = Field(..., description="CÓPIALO EXACTAMENTE del campo 'tss' de la 'failedSession'")
    blocks: List[UpdatedBlock] = Field(..., description="CÓPIALO EXACTAMENTE de los 'blocks' de la 'failedSession'")

class PlanReadjustment(BaseModel):
    readjustmentReasoning: str = Field(..., description="Escribe MÁXIMO 15 palabras resumiendo la acción. (Ej: 'Se aumenta la duración del RUN').")
    updatedSessions: List[UpdatedSession] = Field(default=[], description="Sesiones originales de la semana (con TSS ajustado o mantenido)")
    rescheduledSession: Optional[RescheduledSession] = Field(default=None, description="SOLO RELLENAR si se ha recolocado una sesión entera")


@app.post("/api/replan")
async def replan_week(request: ReplanRequest):
    input_data = request.model_dump()
    
    zones_rule = """
    REGLA DE ZONAS DE INTENSIDAD ('pace'):
    - Si el deporte es SWIM, usa ÚNICAMENTE: Suave, AER1, AER2, AER3, Fuerte.
    - Si el deporte es BIKE, usa ÚNICAMENTE: Z1, Z2, Z3, Z4, Z5, Z6, Z7.
    - Si el deporte es RUN, usa ÚNICAMENTE: R0, R1, R1+, R2, R3, R3+, R4, R5, R6.
    """
    
    try:
        failed_sport = input_data["failedSession"]["sport"]
        adjustable_sessions = input_data["adjustableSessions"]

        matching_sessions = [s for s in adjustable_sessions if s["sport"] == failed_sport]

        if matching_sessions:
            extra_tss = input_data["context"]["missingTssToCompensate"] / len(matching_sessions)
            instrucciones_dinamicas = ""

            for session in adjustable_sessions:
                if session["sport"] == failed_sport:
                    session["targetTss"] = round(session["tss"] + extra_tss, 2)
                    instrucciones_dinamicas += f"- MODIFICAR: Sube la sesión de {session['sport']} del {session['date']} a {session['targetTss']} TSS. Aumenta ligeramente el campo 'distanceOrDuration' o 'pace' de sus bloques.\n"
                else:
                    session["targetTss"] = session["tss"]
                    instrucciones_dinamicas += f"- MANTENER: Deja la sesión de {session['sport']} del {session['date']} exactamente igual, con {session['tss']} TSS.\n"

            system_prompt = f"""Eres un entrenador experto.
            REGLAS ESTRICTAS:
            1. DEBES devolver exactamente las {len(adjustable_sessions)} sesiones de entrada. NO omitas ninguna.
            2. INSTRUCCIONES POR SESIÓN (¡Síguelas al pie de la letra!):
            {instrucciones_dinamicas}
            3. NO rellenes el campo 'rescheduledSession', devuélvelo como null.
            {zones_rule}"""

        else:
            available_dates = [s["date"] for s in adjustable_sessions]
            available_dates_str = ", ".join(available_dates)

            f_sport = input_data["failedSession"]["sport"]
            f_tss = input_data["failedSession"]["tss"]
            f_blocks = json.dumps(input_data["failedSession"]["blocks"], ensure_ascii=False)

            for session in adjustable_sessions:
                session["targetTss"] = session["tss"]
                
            system_prompt = f"""Eres un entrenador experto. El atleta ha fallado una sesión y NO hay sesiones de ese mismo deporte en los días restantes.
            REGLAS ESTRICTAS:
            1. Tu ÚNICA tarea es RECOLOCAR la 'failedSession' rellenando el objeto 'rescheduledSession'.
            2. FECHA OBLIGATORIA: La 'newDate' DEBE SER EXACTAMENTE UNA DE ESTAS FECHAS: [{available_dates_str}].
            3. COPIA EXACTA: En 'rescheduledSession', el 'sport' DEBE ser "{f_sport}", el 'tss' DEBE ser {f_tss}, y los 'blocks' DEBEN ser exactamente: {f_blocks}.
            4. Devuelve las 'updatedSessions' exactamente igual que entraron, no modifiques ni su TSS ni sus bloques.
            {zones_rule}"""

        completion = client.beta.chat.completions.parse(
            temperature=0.0, 
            model="llama3.2:3b",
            max_tokens=2000,
            messages=[
                {"role": "system", "content": system_prompt},
                {"role": "user", "content": json.dumps(input_data)}
            ],
            response_format=PlanReadjustment,
        )

        readjustment_response = completion.choices[0].message
        
        if readjustment_response.parsed:
            return readjustment_response.parsed.model_dump()
        else:
            raise HTTPException(status_code=500, detail="El modelo no devolvió un JSON válido")
            
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))