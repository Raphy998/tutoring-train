import { SET_OFFERS, ADD_OFFERS } from './ActionCreators.jsx';

let initialState = {
  type: SET_OFFERS,
  offers: []
}

export default function tutoringTrainReducer(state = initialState, action) {
  switch (action.type) {
    case SET_OFFERS:
      return action;
      break;
    case ADD_OFFERS:
      return action;
      break;
    default:
      return action;
    break;
    }
  }
