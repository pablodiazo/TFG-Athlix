import { fetchConfig, appFetch } from './appFetch';

export const getDailyPlan = (date, onSuccess, onErrors) => {
    appFetch(`/plans/daily?date=${date}`, fetchConfig('GET'), onSuccess, onErrors);
};