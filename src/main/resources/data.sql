-- Usuarios -------------------------------------------------------------------

INSERT INTO Users (userName, password, firstName, lastName, email, role, coachId)
VALUES (
    'coach_coach',
    '$2a$10$V5QjEd9hDENFwC2bMUmgGehZoffn/JkyLJzpkRC2ChESG4C9a5Oye', -- pa2425
    'Coach',
    'Coach',
    'testcoach@example.com',
    'COACH',
    NULL
);

INSERT INTO Users (userName, password, firstName, lastName, email, role, coachId)
VALUES (
    'user_user',
    '$2a$10$V5QjEd9hDENFwC2bMUmgGehZoffn/JkyLJzpkRC2ChESG4C9a5Oye', -- pa2425
    'User',
    'User',
    'testuser@example.com',
    'USER',
    1
);

INSERT INTO Users (userName, password, firstName, lastName, email, role, coachId)
VALUES (
    'user_user2',
    '$2a$10$V5QjEd9hDENFwC2bMUmgGehZoffn/JkyLJzpkRC2ChESG4C9a5Oye', -- pa2425
    'User',
    'User2',
    'testuser2@example.com',
    'USER',
    NULL
);


-- ============================================================================
-- ============================ HACE 7 DÍAS ===================================
-- ============================================================================

-- Sesión 1: Bici Larga
INSERT INTO TrainingSession (userId, coachId, sessionDate, startTime, sport, objective, totalDistanceOrDuration) 
VALUES (2, 1, DATEADD('DAY', -7, CURRENT_DATE), '08:30:00', 'BIKE', 'Fondo aeróbico', '3 h 30 min');

INSERT INTO TrainingBlock (trainingSessionId, blockOrder, name, sets, reps, distanceOrDuration, pace, rest, done)
VALUES (1, 1, 'Rodaje Z2', 1, 1, '3 h 30 min', 'Z2', '0', 1.0);

-- Sesión 2: Transición carrera
INSERT INTO TrainingSession (userId, coachId, sessionDate, startTime, sport, objective, totalDistanceOrDuration) 
VALUES (2, 1, DATEADD('DAY', -7, CURRENT_DATE), '12:00:00', 'RUN', 'Transición rápida', '5 km');

INSERT INTO TrainingBlock (trainingSessionId, blockOrder, name, sets, reps, distanceOrDuration, pace, rest, done)
VALUES (2, 1, 'Carrera alegre', 1, 1, '5 km', 'Z3', '0', 1.0);

-- Nutrición y Descanso
INSERT INTO NutritionPlan (userId, coachId, planDate, targetCalories, proteinGrams, carbsGrams, fatGrams, hydrationLiters, guidelines, done)
VALUES (2, 1, DATEADD('DAY', -7, CURRENT_DATE), 4200, 150, 600, 100, 4.5, 'Carga alta post-bici.', 1.0);

INSERT INTO RestPlan (userId, coachId, planDate, targetSleepHours, guidelines, done)
VALUES (2, 1, DATEADD('DAY', -7, CURRENT_DATE), 9.0, 'Dormir bien tras tirada larga.', 1.0);


-- ============================================================================
-- ============================ HACE 6 DÍAS ===================================
-- ============================================================================
-- Día de descanso total (Sin entrenamientos)

INSERT INTO NutritionPlan (userId, coachId, planDate, targetCalories, proteinGrams, carbsGrams, fatGrams, hydrationLiters, guidelines, done)
VALUES (2, 1, DATEADD('DAY', -6, CURRENT_DATE), 2800, 160, 300, 80, 3.0, 'Día bajo en hidratos.', 1.0);

INSERT INTO RestPlan (userId, coachId, planDate, targetSleepHours, guidelines, done)
VALUES (2, 1, DATEADD('DAY', -6, CURRENT_DATE), 8.5, 'Día de recuperación total.', 1.0);


-- ============================================================================
-- ============================ HACE 5 DÍAS ===================================
-- ============================================================================

-- Sesión 3: Natación
INSERT INTO TrainingSession (userId, coachId, sessionDate, startTime, sport, objective, totalDistanceOrDuration) 
VALUES (2, 1, DATEADD('DAY', -5, CURRENT_DATE), '07:15:00', 'SWIM', 'Fuerza en el agua', '2800 m');

INSERT INTO TrainingBlock (trainingSessionId, blockOrder, name, sets, reps, distanceOrDuration, pace, rest, done)
VALUES (3, 1, 'Calentamiento + Palas', 1, 1, '1000 m', 'AER1', '0', 1.0);
INSERT INTO TrainingBlock (trainingSessionId, blockOrder, name, sets, reps, distanceOrDuration, pace, rest, done)
VALUES (3, 2, 'Series con palas y pull', 8, 1, '200m', 'AER2', '20"', 1.0);

-- Sesión 4: Fuerza
INSERT INTO TrainingSession (userId, coachId, sessionDate, startTime, sport, objective, totalDistanceOrDuration) 
VALUES (2, 1, DATEADD('DAY', -5, CURRENT_DATE), '19:00:00', 'STRENGTH', 'Fuerza máxima', '45 min');

INSERT INTO TrainingBlock (trainingSessionId, blockOrder, name, sets, reps, distanceOrDuration, pace, rest, done)
VALUES (4, 1, 'Sentadilla pesada', 4, 5, '-', 'NONE', '2 min', 1.0);

-- Nutrición y Descanso
INSERT INTO NutritionPlan (userId, coachId, planDate, targetCalories, proteinGrams, carbsGrams, fatGrams, hydrationLiters, guidelines, done)
VALUES (2, 1, DATEADD('DAY', -5, CURRENT_DATE), 3200, 190, 400, 85, 3.5, 'Batido de protes post-fuerza.', 1.0);

INSERT INTO RestPlan (userId, coachId, planDate, targetSleepHours, guidelines, done)
VALUES (2, 1, DATEADD('DAY', -5, CURRENT_DATE), 8.0, 'Descanso normal.', 0.8);


-- ============================================================================
-- ============================ HACE 4 DÍAS ===================================
-- ============================================================================

-- Sesión 5: Carrera
INSERT INTO TrainingSession (userId, coachId, sessionDate, startTime, sport, objective, totalDistanceOrDuration) 
VALUES (2, 1, DATEADD('DAY', -4, CURRENT_DATE), '14:00:00', 'RUN', 'Series de umbral', '12 km');

INSERT INTO TrainingBlock (trainingSessionId, blockOrder, name, sets, reps, distanceOrDuration, pace, rest, done)
VALUES (5, 1, 'Calentamiento', 1, 1, '20 min', 'Z1', '0', 1.0);
INSERT INTO TrainingBlock (trainingSessionId, blockOrder, name, sets, reps, distanceOrDuration, pace, rest, done)
VALUES (5, 2, 'Miles', 6, 1, '1 km', 'Z4', '90"', 0.5); -- A medias

-- Nutrición y Descanso
INSERT INTO NutritionPlan (userId, coachId, planDate, targetCalories, proteinGrams, carbsGrams, fatGrams, hydrationLiters, guidelines, done)
VALUES (2, 1, DATEADD('DAY', -4, CURRENT_DATE), 3300, 160, 450, 80, 4.0, 'Bien de agua hoy.', 1.0);

INSERT INTO RestPlan (userId, coachId, planDate, targetSleepHours, guidelines, done)
VALUES (2, 1, DATEADD('DAY', -4, CURRENT_DATE), 8.0, 'Descanso normal.', 1.0);


-- ============================================================================
-- ============================ HACE 3 DÍAS ===================================
-- ============================================================================

-- Sesión 6: Bici Tempo
INSERT INTO TrainingSession (userId, coachId, sessionDate, startTime, sport, objective, totalDistanceOrDuration) 
VALUES (2, 1, DATEADD('DAY', -3, CURRENT_DATE), '18:00:00', 'BIKE', 'Rodaje con cambios', '2 h');

INSERT INTO TrainingBlock (trainingSessionId, blockOrder, name, sets, reps, distanceOrDuration, pace, rest, done)
VALUES (6, 1, 'Tempo sostenido', 3, 1, '20 min', 'Z3', '5 min Z1', 0.8);

-- Nutrición y Descanso
INSERT INTO NutritionPlan (userId, coachId, planDate, targetCalories, proteinGrams, carbsGrams, fatGrams, hydrationLiters, guidelines, done)
VALUES (2, 1, DATEADD('DAY', -3, CURRENT_DATE), 3500, 150, 500, 80, 3.5, '-', 1.0);

INSERT INTO RestPlan (userId, coachId, planDate, targetSleepHours, guidelines, done)
VALUES (2, 1, DATEADD('DAY', -3, CURRENT_DATE), 8.0, '-', 1.0);


-- ============================================================================
-- ============================ HACE 2 DÍAS ===================================
-- ============================================================================

-- Sesión 7: Natación
INSERT INTO TrainingSession (userId, coachId, sessionDate, startTime, sport, objective, totalDistanceOrDuration) 
VALUES (2, 1, DATEADD('DAY', -2, CURRENT_DATE), '08:00:00', 'SWIM', 'Recuperación activa', '2000 m');

INSERT INTO TrainingBlock (trainingSessionId, blockOrder, name, sets, reps, distanceOrDuration, pace, rest, done)
VALUES (7, 1, 'Rodaje suave', 1, 1, '2000 m', 'AER1', '0', 1.0);

-- Sesión 8: Carrera
INSERT INTO TrainingSession (userId, coachId, sessionDate, startTime, sport, objective, totalDistanceOrDuration) 
VALUES (2, 1, DATEADD('DAY', -2, CURRENT_DATE), '20:00:00', 'RUN', 'Trote', '45 min');

INSERT INTO TrainingBlock (trainingSessionId, blockOrder, name, sets, reps, distanceOrDuration, pace, rest, done)
VALUES (8, 1, 'Trote cochinero', 1, 1, '45 min', 'Z1', '0', 1.0);

-- Nutrición y Descanso
INSERT INTO NutritionPlan (userId, coachId, planDate, targetCalories, proteinGrams, carbsGrams, fatGrams, hydrationLiters, guidelines, done)
VALUES (2, 1, DATEADD('DAY', -2, CURRENT_DATE), 3100, 160, 400, 80, 3.0, '-', 1.0);

INSERT INTO RestPlan (userId, coachId, planDate, targetSleepHours, guidelines, done)
VALUES (2, 1, DATEADD('DAY', -2, CURRENT_DATE), 8.5, '-', 1.0);


-- ============================================================================
-- ============================ AYER (HACE 1 DÍA) =============================
-- ============================================================================

-- Sesión 9: Transición
INSERT INTO TrainingSession (userId, coachId, sessionDate, startTime, sport, objective, totalDistanceOrDuration)
VALUES (2, 1, DATEADD('DAY', -1, CURRENT_DATE), '12:00:00', 'BRICK', 'Simulación de carrera', '1 h');

INSERT INTO TrainingBlock (trainingSessionId, blockOrder, name, sets, reps, distanceOrDuration, pace, rest, done)
VALUES (9, 1, 'Primera transición', 1, 1, '5 min', 'NONE', '0', 1.0);
INSERT INTO TrainingBlock (trainingSessionId, blockOrder, name, sets, reps, distanceOrDuration, pace, rest, done)
VALUES (9, 2, 'Bici con 5 minutos fuertes al principio y al final', 1, 1, '40min', 'NONE', '0', 1.0);
INSERT INTO TrainingBlock (trainingSessionId, blockOrder, name, sets, reps, distanceOrDuration, pace, rest, done)
VALUES (9, 3, 'Transición a correr', 1, 1, '5 min', 'NONE', '0', 1.0);

-- Sesión 10: Fuerza
INSERT INTO TrainingSession (userId, coachId, sessionDate, startTime, sport, objective, totalDistanceOrDuration)
VALUES (2, 1, DATEADD('DAY', -1, CURRENT_DATE), '20:00:00', 'STRENGTH', 'Ejercicios de prevención', '30 min');

INSERT INTO TrainingBlock (trainingSessionId, blockOrder, name, sets, reps, distanceOrDuration, pace, rest, done)
VALUES (10, 1, 'Movilidad de cadera', 1, 1, '10 min', 'NONE', '0', 1.0);
INSERT INTO TrainingBlock (trainingSessionId, blockOrder, name, sets, reps, distanceOrDuration, pace, rest, done)
VALUES (10, 2, 'Rutina de propiocepción', 8, 1, '10 min', 'NONE', '0', 1.0);

-- Nutrición y Descanso 
INSERT INTO NutritionPlan (userId, coachId, planDate, targetCalories, proteinGrams, carbsGrams, fatGrams, hydrationLiters, guidelines, done)
VALUES (2, 1, DATEADD('DAY', -1, CURRENT_DATE), 3800, 180, 500, 90, 3.5, 'Importante la carga de hidratos.', 1.0);

INSERT INTO RestPlan (userId, coachId, planDate, targetSleepHours, guidelines, done)
VALUES (2, 1, DATEADD('DAY', -1, CURRENT_DATE), 10, 'Siesta de 1 hora después de comer.', 1.0);


-- ============================================================================
-- ============================ HOY ===========================================
-- ============================================================================

-- Sesión 11: Natación 
INSERT INTO TrainingSession (userId, coachId, sessionDate, startTime, sport, objective, totalDistanceOrDuration) 
VALUES ( 2, 1, CURRENT_DATE, '07:00:00', 'SWIM', 'Aeróbico ligero y técnica', '3400 m');

INSERT INTO TrainingBlock (trainingSessionId, blockOrder, name, sets, reps, distanceOrDuration, pace, rest, done)
VALUES (11, 1, 'Calentamiento', 1, 1, '600 m', 'AER1', '0', 1.0);
INSERT INTO TrainingBlock (trainingSessionId, blockOrder, name, sets, reps, distanceOrDuration, pace, rest, done)
VALUES (11, 2, 'Técnica crol', 12, 1, '50 m', 'SUAVE', '15"', 0.5);
INSERT INTO TrainingBlock (trainingSessionId, blockOrder, name, sets, reps, distanceOrDuration, pace, rest, done)
VALUES (11, 3, 'Nado largo', 4, 1, '400 m', 'AER2', '1min', 0.0);

-- Sesión 12: Bici 
INSERT INTO TrainingSession (userId, coachId, sessionDate, startTime, sport, objective, totalDistanceOrDuration)
VALUES (2, 1, CURRENT_DATE, '09:00:00', 'BIKE', 'Ruta tranquila con 2 subidas largas', '3 h');

INSERT INTO TrainingBlock (trainingSessionId, blockOrder, name, sets, reps, distanceOrDuration, pace, rest, done)
VALUES (12, 1, 'Puerto de Xiabre', 1, 1, '10 km', 'Z1', '0', 0.0);
INSERT INTO TrainingBlock (trainingSessionId, blockOrder, name, sets, reps, distanceOrDuration, pace, rest, done)
VALUES (12, 2, 'Puerto de Iroite', 1, 1, '12 km', 'Z1', '0', 0.0);

-- Sesión 13: Carrera 
INSERT INTO TrainingSession (userId, coachId, sessionDate, startTime, sport, objective, totalDistanceOrDuration)
VALUES (2, 1, CURRENT_DATE, '18:30:00', 'RUN', 'Series VO2Max', '10 km');

INSERT INTO TrainingBlock (trainingSessionId, blockOrder, name, sets, reps, distanceOrDuration, pace, rest, done)
VALUES (13, 1, 'Calentar', 1, 1, '20 min', 'R1', '0', 0.0);
INSERT INTO TrainingBlock (trainingSessionId, blockOrder, name, sets, reps, distanceOrDuration, pace, rest, done)
VALUES (13, 2, 'Series 400m', 8, 1, '400 m', 'R3_PLUS', '1 min', 0.0);

-- Nutrición y Descanso 
INSERT INTO NutritionPlan (userId, coachId, planDate, targetCalories, proteinGrams, carbsGrams, fatGrams, hydrationLiters, guidelines, done)
VALUES (2, 1, CURRENT_DATE, 3800, 180, 500, 90, 4.0, 'Ingerir 2 litros de agua por la mañana.', 0.3);

INSERT INTO RestPlan (userId, coachId, planDate, targetSleepHours, guidelines, done)
VALUES (2, 1, CURRENT_DATE, 8.5, 'Siesta de 20 min recomendada entre sesiones.', 0.0);


-- ============================================================================
-- ============================ MAÑANA (DÍA +1) ===============================
-- ============================================================================
-- Día de descanso

INSERT INTO NutritionPlan (userId, coachId, planDate, targetCalories, proteinGrams, carbsGrams, fatGrams, hydrationLiters, guidelines, done)
VALUES (2, 1, DATEADD('DAY', 1, CURRENT_DATE), 2500, 150, 250, 80, 3.0, 'Día de descanso, recortar carbohidratos.', 0.0);

INSERT INTO RestPlan (userId, coachId, planDate, targetSleepHours, guidelines, done)
VALUES (2, 1, DATEADD('DAY', 1, CURRENT_DATE), 9.0, 'Dormir al menos 9 horas para recuperar.', 0.0);


-- ============================================================================
-- ============================ DÍA +2 ========================================
-- ============================================================================

-- Sesión 14: Natación
INSERT INTO TrainingSession (userId, coachId, sessionDate, startTime, sport, objective, totalDistanceOrDuration) 
VALUES (2, 1, DATEADD('DAY', 2, CURRENT_DATE), '07:30:00', 'SWIM', 'Series cortas de velocidad', '2500 m');

INSERT INTO TrainingBlock (trainingSessionId, blockOrder, name, sets, reps, distanceOrDuration, pace, rest, done)
VALUES (14, 1, 'Calentamiento', 1, 1, '800 m', 'AER1', '0', 0.0);
INSERT INTO TrainingBlock (trainingSessionId, blockOrder, name, sets, reps, distanceOrDuration, pace, rest, done)
VALUES (14, 2, 'Sprints 50m', 10, 1, '50m', 'FUERTE', '30"', 0.0);

-- Sesión 15: Fuerza
INSERT INTO TrainingSession (userId, coachId, sessionDate, startTime, sport, objective, totalDistanceOrDuration) 
VALUES (2, 1, DATEADD('DAY', 2, CURRENT_DATE), '18:00:00', 'STRENGTH', 'Fuerza resistencia tren inferior', '1 h');

INSERT INTO TrainingBlock (trainingSessionId, blockOrder, name, sets, reps, distanceOrDuration, pace, rest, done)
VALUES (15, 1, 'Prensa y extensiones', 4, 15, '-', 'NONE', '1 min', 0.0);

INSERT INTO NutritionPlan (userId, coachId, planDate, targetCalories, proteinGrams, carbsGrams, fatGrams, hydrationLiters, guidelines, done)
VALUES (2, 1, DATEADD('DAY', 2, CURRENT_DATE), 3400, 170, 450, 85, 3.5, '-', 0.0);

INSERT INTO RestPlan (userId, coachId, planDate, targetSleepHours, guidelines, done)
VALUES (2, 1, DATEADD('DAY', 2, CURRENT_DATE), 8.0, '-', 0.0);


-- ============================================================================
-- ============================ DÍA +3 ========================================
-- ============================================================================

-- Sesión 16: Carrera
INSERT INTO TrainingSession (userId, coachId, sessionDate, startTime, sport, objective, totalDistanceOrDuration) 
VALUES (2, 1, DATEADD('DAY', 3, CURRENT_DATE), '19:30:00', 'RUN', 'Fartlek', '1h 10 min');

INSERT INTO TrainingBlock (trainingSessionId, blockOrder, name, sets, reps, distanceOrDuration, pace, rest, done)
VALUES (16, 1, 'Fartlek 2min fuerte / 1 suave', 10, 1, '3 min', 'R4', '0', 0.0);

INSERT INTO NutritionPlan (userId, coachId, planDate, targetCalories, proteinGrams, carbsGrams, fatGrams, hydrationLiters, guidelines, done)
VALUES (2, 1, DATEADD('DAY', 3, CURRENT_DATE), 3200, 160, 400, 80, 3.5, '-', 0.0);

INSERT INTO RestPlan (userId, coachId, planDate, targetSleepHours, guidelines, done)
VALUES (2, 1, DATEADD('DAY', 3, CURRENT_DATE), 8.5, '-', 0.0);


-- ============================================================================
-- ============================ DÍA +4 ========================================
-- ============================================================================

-- Sesión 17: Bici
INSERT INTO TrainingSession (userId, coachId, sessionDate, startTime, sport, objective, totalDistanceOrDuration) 
VALUES (2, 1, DATEADD('DAY', 4, CURRENT_DATE), '17:00:00', 'BIKE', 'Rodaje Recuperación', '1.5 h');

INSERT INTO TrainingBlock (trainingSessionId, blockOrder, name, sets, reps, distanceOrDuration, pace, rest, done)
VALUES (17, 1, 'Soltar piernas', 1, 1, '1.5 h', 'Z1', '0', 0.0);

INSERT INTO NutritionPlan (userId, coachId, planDate, targetCalories, proteinGrams, carbsGrams, fatGrams, hydrationLiters, guidelines, done)
VALUES (2, 1, DATEADD('DAY', 4, CURRENT_DATE), 3000, 150, 350, 80, 3.0, '-', 0.0);

INSERT INTO RestPlan (userId, coachId, planDate, targetSleepHours, guidelines, done)
VALUES (2, 1, DATEADD('DAY', 4, CURRENT_DATE), 8.0, '-', 0.0);


-- ============================================================================
-- ============================ DÍA +5 ========================================
-- ============================================================================

-- Sesión 18: Natación
INSERT INTO TrainingSession (userId, coachId, sessionDate, startTime, sport, objective, totalDistanceOrDuration) 
VALUES (2, 1, DATEADD('DAY', 5, CURRENT_DATE), '07:00:00', 'SWIM', 'Tirada continua', '3000 m');

INSERT INTO TrainingBlock (trainingSessionId, blockOrder, name, sets, reps, distanceOrDuration, pace, rest, done)
VALUES (18, 1, 'Nado continuo', 1, 1, '3000 m', 'AER2', '0', 0.0);

INSERT INTO NutritionPlan (userId, coachId, planDate, targetCalories, proteinGrams, carbsGrams, fatGrams, hydrationLiters, guidelines, done)
VALUES (2, 1, DATEADD('DAY', 5, CURRENT_DATE), 3100, 160, 400, 80, 3.5, '-', 0.0);

INSERT INTO RestPlan (userId, coachId, planDate, targetSleepHours, guidelines, done)
VALUES (2, 1, DATEADD('DAY', 5, CURRENT_DATE), 8.0, '-', 0.0);


-- ============================================================================
-- ============================ DÍA +6 ========================================
-- ============================================================================

-- Sesión 19: Carrera (Tirada Larga)
INSERT INTO TrainingSession (userId, coachId, sessionDate, startTime, sport, objective, totalDistanceOrDuration) 
VALUES (2, 1, DATEADD('DAY', 6, CURRENT_DATE), '09:00:00', 'RUN', 'Tirada Larga Fin de Semana', '21 km');

INSERT INTO TrainingBlock (trainingSessionId, blockOrder, name, sets, reps, distanceOrDuration, pace, rest, done)
VALUES (19, 1, 'Progresivo', 1, 1, '21 km', 'Z3', '0', 0.0);

INSERT INTO NutritionPlan (userId, coachId, planDate, targetCalories, proteinGrams, carbsGrams, fatGrams, hydrationLiters, guidelines, done)
VALUES (2, 1, DATEADD('DAY', 6, CURRENT_DATE), 3800, 160, 550, 90, 4.0, 'Geles cada 45 min en carrera.', 0.0);

INSERT INTO RestPlan (userId, coachId, planDate, targetSleepHours, guidelines, done)
VALUES (2, 1, DATEADD('DAY', 6, CURRENT_DATE), 8.5, '-', 0.0);


-- ============================================================================
-- ============================ DÍA +7 ========================================
-- ============================================================================

-- Sesión 20: Bici (Tirada Larga)
INSERT INTO TrainingSession (userId, coachId, sessionDate, startTime, sport, objective, totalDistanceOrDuration) 
VALUES (2, 1, DATEADD('DAY', 7, CURRENT_DATE), '08:30:00', 'BIKE', 'Salida con grupeta', '4 h');

INSERT INTO TrainingBlock (trainingSessionId, blockOrder, name, sets, reps, distanceOrDuration, pace, rest, done)
VALUES (20, 1, 'Ruta libre', 1, 1, '4 h', 'Z2', '0', 0.0);

INSERT INTO NutritionPlan (userId, coachId, planDate, targetCalories, proteinGrams, carbsGrams, fatGrams, hydrationLiters, guidelines, done)
VALUES (2, 1, DATEADD('DAY', 7, CURRENT_DATE), 4500, 170, 700, 110, 5.0, 'Llevar 2 bidones isotónico y barritas.', 0.0);

INSERT INTO RestPlan (userId, coachId, planDate, targetSleepHours, guidelines, done)
VALUES (2, 1, DATEADD('DAY', 7, CURRENT_DATE), 9.0, 'Recuperar piernas.', 0.0);