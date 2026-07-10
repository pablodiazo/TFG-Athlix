import { fetchConfig, appFetch } from './appFetch';

export const getDailyPlan = (date, onSuccess, onErrors) => {
    appFetch(`/plans/daily?date=${date}`, fetchConfig('GET'), onSuccess, onErrors);
};

export const getWeeklyPlan = (startDate, onSuccess, onErrors) => {
    appFetch(`/plans/weekly?startDate=${startDate}`, fetchConfig('GET'), onSuccess, onErrors);
};

export const createTrainingSession = (trainingSessionData, onSuccess, onErrors) => {
    appFetch(`/plans/create-training-session`, fetchConfig('POST', trainingSessionData), onSuccess, onErrors);
};

export const createNutritionPlan = (nutritionPlanData, onSuccess, onErrors) => {
    appFetch(`/plans/create-nutrition-plan`, fetchConfig('POST', nutritionPlanData), onSuccess, onErrors);
};

export const createRestPlan = (restPlanData, onSuccess, onErrors) => {
    appFetch(`/plans/create-rest-plan`, fetchConfig('POST', restPlanData), onSuccess, onErrors);
};

export const updateTrainingBlockDone = (trainingBlockData, onSuccess, onErrors) => {
    appFetch(`/plans/update-training-block-done`, fetchConfig('POST', trainingBlockData), onSuccess, onErrors);
};

export const updateNutritionPlanDone = (nutritionPlanData, onSuccess, onErrors) => {
    appFetch(`/plans/update-nutrition-plan-done`, fetchConfig('POST', nutritionPlanData), onSuccess, onErrors);
};

export const updateRestPlanDone = (restPlanData, onSuccess, onErrors) => {
    appFetch(`/plans/update-rest-plan-done`, fetchConfig('POST', restPlanData), onSuccess, onErrors);
};

export const rescheduleTrainingSession = (rescheduleData, onSuccess, onErrors) => {
    appFetch(`/plans/reschedule-training-session`, fetchConfig('POST', rescheduleData), onSuccess, onErrors);
};

export const getAthleteDailyPlan = (athleteId, date, onSuccess, onErrors) => {
    appFetch(`/plans/athletes/${athleteId}/daily?date=${date}`, fetchConfig('GET'), onSuccess, onErrors);
};

export const getAthleteWeeklyPlan = (athleteId, startDate, onSuccess, onErrors) => {
    appFetch(`/plans/athletes/${athleteId}/weekly?startDate=${startDate}`, fetchConfig('GET'), onSuccess, onErrors);
};

export const getNotifications = (onSuccess, onErrors) => {
    appFetch(`/plans/notifications`, fetchConfig('GET'), onSuccess, onErrors);
};

export const markNotificationAsRead = (id, onSuccess, onErrors) => {
    appFetch(`/plans/notifications/${id}/read`, fetchConfig('POST'), onSuccess, onErrors);
};

export const acceptReadjustment = (notificationId, params, onSuccess, onErrors) => {
    appFetch(`/plans/notifications/${notificationId}/accept`, fetchConfig('POST', params), onSuccess, onErrors);
};

export const denyReadjustment = (notificationId, params, onSuccess, onErrors) => {
    appFetch(`/plans/notifications/${notificationId}/deny`, fetchConfig('POST', params), onSuccess, onErrors);
};

export const deleteTrainingSession = (sessionId, onSuccess, onErrors) => {
    appFetch(`/plans/training-sessions/${sessionId}`, fetchConfig('DELETE'), onSuccess, onErrors);
};

export const updateTrainingSession = (sessionId, params, onSuccess, onErrors) => {
    appFetch(`/plans/training-sessions/${sessionId}`, fetchConfig('PUT', params), onSuccess, onErrors);
};