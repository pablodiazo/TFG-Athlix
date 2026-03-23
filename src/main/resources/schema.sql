DROP TABLE IF EXISTS TrainingBlock;
DROP TABLE IF EXISTS TrainingSession;
DROP TABLE IF EXISTS NutritionPlan;
DROP TABLE IF EXISTS RestPlan;
DROP TABLE IF EXISTS Users;

CREATE TABLE Users (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    userName VARCHAR(60) NOT NULL,
    password VARCHAR(60) NOT NULL, 
    firstName VARCHAR(60) NOT NULL,
    lastName VARCHAR(60) NOT NULL, 
    email VARCHAR(60) NOT NULL,
    role VARCHAR(60) NOT NULL
);

-- 1. Sesión de Entrenamiento (Puede haber varias al día)
CREATE TABLE TrainingSession (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    userId BIGINT NOT NULL,
    coachId BIGINT NOT NULL,
    sessionDate DATE NOT NULL,
    startTime TIME NOT NULL,           -- Hora a la que se debe realizar (ej. '07:00:00')
    sport VARCHAR(20) NOT NULL,        -- 'SWIM', 'BIKE', 'RUN', 'STRENGTH', 'BRICK'
    objective VARCHAR(100),            -- ej. 'Series VO2Max' o 'Rodaje regenerativo'
    totalDistanceOrDuration VARCHAR(50), -- ej. '5km', '1h30m'
    CONSTRAINT fk_session_user FOREIGN KEY (userId) REFERENCES Users(id),
    CONSTRAINT fk_session_coach FOREIGN KEY (coachId) REFERENCES Users(id)
);

-- 2. Bloques/Ejercicios dentro de UNA Sesión
CREATE TABLE TrainingBlock (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    trainingSessionId BIGINT NOT NULL,
    blockOrder INT NOT NULL,           
    name VARCHAR(100) NOT NULL,        
    sets INT NOT NULL,                 
    reps INT DEFAULT 1,                
    distanceOrDuration VARCHAR(50),    
    pace VARCHAR(50),                  
    rest VARCHAR(50),                  
    CONSTRAINT fk_block_session FOREIGN KEY (trainingSessionId) REFERENCES TrainingSession(id)
);

-- 3. Plan de Nutrición (Este sí suele ser uno por día)
CREATE TABLE NutritionPlan (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    userId BIGINT NOT NULL,
    coachId BIGINT NOT NULL,
    planDate DATE NOT NULL,
    targetCalories INT,
    proteinGrams INT,
    carbsGrams INT,
    fatGrams INT,
    hydrationLiters DECIMAL(3,1),
    guidelines VARCHAR(500),
    CONSTRAINT fk_nutrition_user FOREIGN KEY (userId) REFERENCES Users(id),
    CONSTRAINT fk_nutrition_coach FOREIGN KEY (coachId) REFERENCES Users(id),
    CONSTRAINT uq_nutrition_user_date UNIQUE (userId, planDate)
);

-- 4. Plan de Descanso (Uno por día)
CREATE TABLE RestPlan (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    userId BIGINT NOT NULL,
    coachId BIGINT NOT NULL,
    planDate DATE NOT NULL,
    targetSleepHours DECIMAL(4,1),     
    guidelines VARCHAR(500),           
    CONSTRAINT fk_rest_user FOREIGN KEY (userId) REFERENCES Users(id),
    CONSTRAINT fk_rest_coach FOREIGN KEY (coachId) REFERENCES Users(id),
    CONSTRAINT uq_rest_user_date UNIQUE (userId, planDate)
);