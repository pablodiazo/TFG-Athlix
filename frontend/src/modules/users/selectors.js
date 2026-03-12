const getModuleState = state => state.users;

export const getUser = state =>
    getModuleState(state).user;

export const isLoggedIn = state =>
    getUser(state) !== null

export const getUserId = state =>
    isLoggedIn(state) ? getUser(state).id : null;

export const getUserName = state =>
    isLoggedIn(state) ? getUser(state).userName : null;

export const getFirstName = state =>
    isLoggedIn(state) ? getUser(state).firstName : null;

export const getLastName = state =>
    isLoggedIn(state) ? getUser(state).lastName : null;

export const getEmail = state =>
    isLoggedIn(state) ? getUser(state).email : null;

export const getRole = state =>
    isLoggedIn(state) ? getUser(state).role : null;

export const getAvatar = state =>
    isLoggedIn(state) ? getUser(state).avatar : null;

export const isCoach = state =>
    isLoggedIn(state) && getUser(state).role === "COACH";

export const isUser = state =>
    isLoggedIn(state) && getUser(state).role === "USER";

export const getAge = state => state.users.user?.age;

export const getHeight = state => state.users.user?.height;

export const getWeight = state => state.users.user?.weight;