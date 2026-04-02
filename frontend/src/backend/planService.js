import { fetchConfig, appFetch } from './appFetch';

export const getDailyPlan = (date, onSuccess, onErrors) => {
    appFetch(`/plans/daily?date=${date}`, fetchConfig('GET'), onSuccess, onErrors);
};

export const createTrainingSession = (trainingSessionData, onSuccess, onErrors) => {
    appFetch(`/plans/create-training-session`, fetchConfig('POST', trainingSessionData), onSuccess, onErrors);
};