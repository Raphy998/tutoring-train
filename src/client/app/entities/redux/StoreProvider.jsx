import { createStore } from 'redux';
import tutoringTrainReducer from 'app/entities/redux/ActionReducers.jsx'
import {
  setOffers,
  addOffers
} from 'app/entities/redux/ActionCreators.jsx';
var store = null;

export default function getStore() {
  if(!store) {
    store = createStore(tutoringTrainReducer);
  }
  return store;
}
