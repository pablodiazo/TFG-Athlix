import {
  fetchConfig,
  appFetch,
  setServiceToken,
  getServiceToken,
  removeServiceToken,
  setReauthenticationCallback,
} from "./appFetch";

const processLoginSignUp = (authenticatedUser, reauthenticationCallback, onSuccess) => {
  setServiceToken(authenticatedUser.serviceToken);
  setReauthenticationCallback(reauthenticationCallback);
  onSuccess(authenticatedUser);
}

export const login = (
  userName,
  password,
  onSuccess,
  onErrors,
  reauthenticationCallback
) =>
  appFetch(
    "/users/login",
    fetchConfig("POST", { userName, password }),
    (authenticatedUser) => {
      processLoginSignUp(authenticatedUser, reauthenticationCallback, onSuccess);
    },
    onErrors
  );

export const tryLoginFromServiceToken = (
  onSuccess,
  reauthenticationCallback
) => {
  const serviceToken = getServiceToken();

  if (!serviceToken) {
    onSuccess();
    return;
  }

  setReauthenticationCallback(reauthenticationCallback);

  appFetch(
    "/users/loginFromServiceToken",
    fetchConfig("POST"),
    (authenticatedUser) => onSuccess(authenticatedUser),
    () => removeServiceToken()
  );
};

export const signUp = (user, onSuccess, onErrors, reauthenticationCallback) => {
  appFetch(
    "/users/signUp",
    fetchConfig("POST", user),
    (authenticatedUser) => {
      processLoginSignUp(authenticatedUser, reauthenticationCallback, onSuccess);
    },
    onErrors
  );
};

export const logout = () => removeServiceToken();

export const updateProfile = (user, onSuccess, onErrors) =>
  appFetch(`/users/${user.id}`, fetchConfig("PUT", user), onSuccess, onErrors);

export const changePassword = (
  id,
  oldPassword,
  newPassword,
  onSuccess,
  onErrors
) =>
  appFetch(
    `/users/${id}/changePassword`,
    fetchConfig("POST", { oldPassword, newPassword }),
    onSuccess,
    onErrors
  );

export const getAthletesByCoach = (onSuccess, onErrors) =>
  appFetch("/users/athletes", fetchConfig("GET"), onSuccess, onErrors);

export const sendCoachRequest = (athleteEmail, onSuccess, onErrors) => {
    appFetch('/users/coach-requests', fetchConfig('POST', { athleteEmail }), onSuccess, onErrors);
};

export const getPendingCoachRequests = (onSuccess, onErrors) => {
    appFetch('/users/coach-requests/pending', fetchConfig('GET'), onSuccess, onErrors);
};

export const acceptCoachRequest = (requestId, onSuccess, onErrors) => {
    appFetch(`/users/coach-requests/${requestId}/accept`, fetchConfig('POST'), onSuccess, onErrors);
};

export const rejectCoachRequest = (requestId, onSuccess, onErrors) => {
    appFetch(`/users/coach-requests/${requestId}/reject`, fetchConfig('POST'), onSuccess, onErrors);
};

export const getSentCoachRequests = (onSuccess, onErrors) => {
    appFetch('/users/coach-requests/sent', fetchConfig('GET'), onSuccess, onErrors);
};
