-- Usuarios -------------------------------------------------------------------

INSERT INTO Users (userName, password, firstName, lastName, email, role)
VALUES (
    'user_user',
    '$2a$10$V5QjEd9hDENFwC2bMUmgGehZoffn/JkyLJzpkRC2ChESG4C9a5Oye', -- pa2425
    'User',
    'User',
    'testuser@example.com',
    'USER'
);

INSERT INTO Users (userName, password, firstName, lastName, email, role)
VALUES (
    'coach_coach',
    '$2a$10$V5QjEd9hDENFwC2bMUmgGehZoffn/JkyLJzpkRC2ChESG4C9a5Oye', -- pa2425
    'Coach',
    'Coach',
    'testcoach@example.com',
    'COACH'
);

-- DÍA ACTUAL --

-- Sesión 1: Natación a las 07:00 AM
INSERT INTO TrainingSession (id, userId, coachId, sessionDate, startTime, sport, objective, totalDistanceOrDuration) 
VALUES (1, 1, 2, CURRENT_DATE, '07:00:00', 'SWIM', 'Aeróbico ligero y técnica', '3400m');

INSERT INTO TrainingBlock (trainingSessionId, blockOrder, name, sets, reps, distanceOrDuration, pace, rest)
VALUES (1, 1, 'Calentamiento', 1, 1, '600m', 'AE1', '0');
INSERT INTO TrainingBlock (trainingSessionId, blockOrder, name, sets, reps, distanceOrDuration, pace, rest)
VALUES (1, 2, 'Técnica crol', 12, 1, '50m 1 tec/nado 1 ritmo', '0', '15"');
INSERT INTO TrainingBlock (trainingSessionId, blockOrder, name, sets, reps, distanceOrDuration, pace, rest)
VALUES (1, 3, 'Nado largo', 4, 1, '400m', 'AE1-AE2', '1min');
INSERT INTO TrainingBlock (trainingSessionId, blockOrder, name, sets, reps, distanceOrDuration, pace, rest)
VALUES (1, 4, 'Velocidad', 2, 12, '25m', '2 90% 1 suave', '15" y 3min entre bloques');

-- Sesión 2: Bici a las 09:00 AM
INSERT INTO TrainingSession (id, userId, coachId, sessionDate, startTime, sport, objective, totalDistanceOrDuration)
VALUES (2, 1, 2, CURRENT_DATE, '09:00:00', 'BIKE', 'Ruta tranquila con 2 subidas largas', '3h');

INSERT INTO TrainingBlock (trainingSessionId, blockOrder, name, sets, reps, distanceOrDuration, pace, rest)
VALUES (2, 1, 'Puerto de Xiabre', 1, 1, '10km', 'Suave', '0');
INSERT INTO TrainingBlock (trainingSessionId, blockOrder, name, sets, reps, distanceOrDuration, pace, rest)
VALUES (2, 2, 'Puerto de Iroite', 1, 1, '12km', 'Suave', '0');

-- Sesión 3: Carrera a pie a las 18:30 PM
INSERT INTO TrainingSession (id, userId, coachId, sessionDate, startTime, sport, objective, totalDistanceOrDuration)
VALUES (3, 1, 2, CURRENT_DATE, '18:30:00', 'RUN', 'Series VO2Max', '10km');

INSERT INTO TrainingBlock (trainingSessionId, blockOrder, name, sets, reps, distanceOrDuration, pace, rest)
VALUES (3, 1, 'Calentar', 1, 1, '20min', 'R1', '0');
INSERT INTO TrainingBlock (trainingSessionId, blockOrder, name, sets, reps, distanceOrDuration, pace, rest)
VALUES (3, 2, 'Series 400m', 8, 1, '400m', 'R3+', '1 min');
INSERT INTO TrainingBlock (trainingSessionId, blockOrder, name, sets, reps, distanceOrDuration, pace, rest)
VALUES (3, 3, 'Bloque 200m', 4, 1, '200m', 'R5', '2 min');
INSERT INTO TrainingBlock (trainingSessionId, blockOrder, name, sets, reps, distanceOrDuration, pace, rest)
VALUES (3, 4, 'Soltar', 1, 1, '5min', 'R0', '0');

-- Nutrición y Descanso para el día
INSERT INTO NutritionPlan (userId, coachId, planDate, targetCalories, proteinGrams, carbsGrams, fatGrams, hydrationLiters, guidelines)
VALUES (1, 2, CURRENT_DATE, 3800, 180, 500, 90, 4.0, 'Ingerir 2 litros de agua por la mañana.');

INSERT INTO RestPlan (userId, coachId, planDate, targetSleepHours, guidelines)
VALUES (1, 2, CURRENT_DATE, 8.5, 'Siesta de 20 min recomendada entre sesiones.');

-- AYER --

-- Sesión 1
INSERT INTO TrainingSession (id, userId, coachId, sessionDate, startTime, sport, objective, totalDistanceOrDuration)
VALUES (4, 1, 2, DATEADD('DAY', -1, CURRENT_TIMESTAMP), '12:00:00', 'BRICK', 'Simulación de carrera', '1h');

INSERT INTO TrainingBlock (trainingSessionId, blockOrder, name, sets, reps, distanceOrDuration, pace, rest)
VALUES (4, 1, 'Primera transición', 1, 1, '5min', 'Ritmo de prueba', '0');
INSERT INTO TrainingBlock (trainingSessionId, blockOrder, name, sets, reps, distanceOrDuration, pace, rest)
VALUES (4, 2, 'Bici con 5 minutos fuertes al principio y al final', 1, 1, '40min', 'Ritmo medio', '0');
INSERT INTO TrainingBlock (trainingSessionId, blockOrder, name, sets, reps, distanceOrDuration, pace, rest)
VALUES (4, 3, 'Transición a correr', 1, 1, '5min', 'Ritmo de prueba', '0');

-- Sesión 2
INSERT INTO TrainingSession (id, userId, coachId, sessionDate, startTime, sport, objective, totalDistanceOrDuration)
VALUES (5, 1, 2, DATEADD('DAY', -1, CURRENT_TIMESTAMP), '20:00:00', 'STRENGTH', 'Ejercicios de prevención', '30min');

INSERT INTO TrainingBlock (trainingSessionId, blockOrder, name, sets, reps, distanceOrDuration, pace, rest)
VALUES (5, 1, 'Movilidad de cadera', 1, 1, '10min', '0', '0');
INSERT INTO TrainingBlock (trainingSessionId, blockOrder, name, sets, reps, distanceOrDuration, pace, rest)
VALUES (5, 2, 'Rutina de propiocepción', 8, 1, '10min', '0', '0');
INSERT INTO TrainingBlock (trainingSessionId, blockOrder, name, sets, reps, distanceOrDuration, pace, rest)
VALUES (5, 3, 'Estiramientos de cuerpo completo', 1, 1, '10min', '0', '0');

-- Nutrición y Descanso para el día
INSERT INTO NutritionPlan (userId, coachId, planDate, targetCalories, proteinGrams, carbsGrams, fatGrams, hydrationLiters, guidelines)
VALUES (1, 2, DATEADD('DAY', -1, CURRENT_TIMESTAMP), 3800, 180, 1200, 90, 3.5, 'Importante la carga de hidratos pre-competición.');

INSERT INTO RestPlan (userId, coachId, planDate, targetSleepHours, guidelines)
VALUES (1, 2, DATEADD('DAY', -1, CURRENT_TIMESTAMP), 10, 'Siesta de 1 hora después de comer.');