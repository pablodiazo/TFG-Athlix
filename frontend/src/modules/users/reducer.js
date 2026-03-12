import { combineReducers } from 'redux';
import { error } from '../app/actions';
import * as actionTypes from './actionTypes';

const initialState = {
    user: null,
    error: null,
};

const user = (state = initialState.user, action) => {

    switch (action.type) {

        case actionTypes.SIGN_UP_COMPLETED:
            return action.authenticatedUser.user;

        case actionTypes.LOGIN_COMPLETED:
            return action.authenticatedUser.user;

        case actionTypes.LOGOUT:
            return initialState.user;

        default:
            return state;

    }

}

const reducer = combineReducers({
    user,
    error,
});

export default reducer;
