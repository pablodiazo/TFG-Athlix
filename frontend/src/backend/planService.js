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