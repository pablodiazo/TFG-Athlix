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
    distanceOrDuration: str = Field(..., description="ATENCIÓN: Duración/Distancia de UNA SOLA repetición. ¡Se multiplicará por sets y reps!")
    pace: str = Field(..., description="Ritmo o zona de intensidad (USAR SOLO LAS ZONAS PERMITIDAS PARA ESE DEPORTE)")
    sets: int = Field(..., description="Número de series. Multiplicador. Si es carrera/nado continuo, pon SIEMPRE 1. Solo usa >1 para intervalos cortos.")
    reps: int = Field(..., description="Número de repeticiones. Multiplicador. Si es carrera/nado continuo, pon SIEMPRE 1.")
    rest: str = Field(..., description="Descanso. Si no hay, pon '0'")

class UpdatedSession(BaseModel):
    date: str = Field(..., description="Fecha de la sesión")
    sport: str = Field(..., description="Deporte de la sesión")
    newCe: float = Field(..., description="CE final de la sesión")
    updatedBlocks: List[UpdatedBlock] = Field(..., description="Bloques de la sesión")

class RescheduledSession(BaseModel):
    newDate: str = Field(..., description="Nueva fecha asignada a la sesión fallida (DEBE ser estrictamente una de las permitidas en las reglas)")
    sport: str = Field(..., description="CÓPIALO EXACTAMENTE del campo 'sport' de la 'failedSession'")
    ce: float = Field(..., description="CÓPIALO EXACTAMENTE del campo 'ce' de la 'failedSession'")
    blocks: List[UpdatedBlock] = Field(..., description="CÓPIALO EXACTAMENTE de los 'blocks' de la 'failedSession'")

class PlanReadjustment(BaseModel):
    readjustmentReasoning: str = Field(..., description="Escribe MÁXIMO 15 palabras resumiendo la acción. (Ej: 'Se aumenta la duración del RUN').")
    updatedSessions: List[UpdatedSession] = Field(description="Sesiones originales de la semana (con CE ajustado o mantenido)")
    rescheduledSession: Optional[RescheduledSession] = Field(default=None, description="SOLO RELLENAR si se ha recolocado una sesión entera")


@app.post("/api/replan")
async def replan_week(request: ReplanRequest):
    input_data = request.model_dump()
    
    zones_rule = """
    REGLA DE ZONAS DE INTENSIDAD ('pace'):
    - Si el deporte es SWIM, usa ÚNICAMENTE: Suave, AER1, AER2, AER3, Fuerte.
    - Si el deporte es BIKE, usa ÚNICAMENTE: Z1, Z2, Z3, Z4, Z5, Z6, Z7.
    - Si el deporte es RUN, usa ÚNICAMENTE: R0, R1, R1+, R2, R3, R3+, R4, R5, R6.

    GUÍA PARA ESTIMAR CE:
    - Baja intensidad (AER1, Z2, R1): ~40-50 CE por hora.
    - Media intensidad (AER2, Z3, R3): ~60-70 CE por hora.
    - Alta intensidad (AER3, Z4, R4): ~100 CE por hora.
    
    ¡ADVERTENCIA MATEMÁTICA CRÍTICA PARA LOS BLOQUES!:
    - El tiempo/distancia TOTAL de un bloque se calcula multiplicando: distanceOrDuration * sets * reps.
    - PROHIBIDO hacer esto: distanceOrDuration="20 min", sets=8, reps=1 (¡El atleta correría 160 minutos!).
    - CORRECTO: Si quieres que corra 20 minutos en total, pon distanceOrDuration="20 min", sets=1, reps=1.
    - Usa 'sets' mayores a 1 ÚNICAMENTE para intervalos cortos (ej: distanceOrDuration="2 min", sets=5).
    """
    
    try:
        failed_sport = input_data["failedSession"]["sport"]
        adjustable_sessions = input_data["adjustableSessions"]

        matching_sessions = [s for s in adjustable_sessions if s["sport"] == failed_sport]

        if matching_sessions:
            extra_ce = input_data["context"]["missingCeToCompensate"] / len(matching_sessions)
            instrucciones_dinamicas = ""

            for session in adjustable_sessions:
                if session["sport"] == failed_sport:
                    session["targetCe"] = round(session["ce"] + extra_ce, 2)
                    instrucciones_dinamicas += f"- MODIFICAR: Sube la sesión de {session['sport']} del {session['date']} a {session['targetCe']} CE. IMPORTANTE: Para lograrlo, DEBES AÑADIR NUEVOS BLOQUES (nuevas series, intervalos o trabajo extra) a la lista 'updatedBlocks' de esta sesión.\n"
                else:
                    session["targetCe"] = session["ce"]
                    instrucciones_dinamicas += f"- MANTENER: Deja la sesión de {session['sport']} del {session['date']} exactamente igual, con {session['ce']} CE. No modifiques ni añadas bloques.\n"

            system_prompt = f"""Eres un entrenador experto.
            REGLAS ESTRICTAS:
            1. DEBES devolver exactamente las {len(adjustable_sessions)} sesiones de entrada. NO omitas ninguna.
            2. INSTRUCCIONES POR SESIÓN (¡Síguelas al pie de la letra!):
            {instrucciones_dinamicas}
            3. NO rellenes el campo 'rescheduledSession', devuélvelo como null.
            4. Si se te pide MODIFICAR una sesión, inventa y AÑADE nuevos objetos a la lista 'updatedBlocks' con un ritmo ('pace') y duración adecuados para compensar la carga perdida.
            
            ¡ADVERTENCIA CRÍTICA!: 
            - Está TOTALMENTE PROHIBIDO devolver 'readjustmentReasoning' vacío.
            - Está TOTALMENTE PROHIBIDO devolver 'updatedSessions' como una lista vacía [].
            {zones_rule}"""

        else:
            available_dates = [s["date"] for s in adjustable_sessions]
            available_dates_str = ", ".join(available_dates)

            f_sport = input_data["failedSession"]["sport"]
            f_ce = input_data["failedSession"]["ce"]
            f_blocks = json.dumps(input_data["failedSession"]["blocks"], ensure_ascii=False)

            for session in adjustable_sessions:
                session["targetCe"] = session["ce"]
                
            system_prompt = f"""Eres un entrenador experto. El atleta ha fallado una sesión y NO hay sesiones de ese mismo deporte en los días restantes.
            REGLAS ESTRICTAS:
            1. Tu ÚNICA tarea es RECOLOCAR la 'failedSession' rellenando el objeto 'rescheduledSession'.
            2. FECHA OBLIGATORIA: La 'newDate' DEBE SER EXACTAMENTE UNA DE ESTAS FECHAS: [{available_dates_str}].
            3. COPIA EXACTA: En 'rescheduledSession', el 'sport' DEBE ser "{f_sport}", el 'ce' DEBE ser {f_ce}, y los 'blocks' DEBEN ser exactamente: {f_blocks}.
            4. Devuelve las 'updatedSessions' exactamente igual que entraron, no modifiques ni su CE ni sus bloques.
            
            ¡ADVERTENCIA CRÍTICA!: 
            - Está TOTALMENTE PROHIBIDO devolver 'readjustmentReasoning' vacío.
            - Está TOTALMENTE PROHIBIDO devolver 'updatedSessions' como una lista vacía [].
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