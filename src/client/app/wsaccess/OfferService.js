import ResponseObject from './ResponseObject';
import UrlConstants from './UrlConstants.js';
import { createStore } from 'redux'
import tutoringTrainReducer from 'app/entities/redux/ActionReducers.jsx'
import getStore from 'app/entities/redux/StoreProvider.jsx';
let store = getStore();
import ActionCreators from 'app/entities/redux/ActionCreators.jsx';

export default class OfferService {
  constructor() {
  }

  static get urlCreateOffer() { return '/offer' };
  static get urlGetNewestOffers() { return "/offer/new" };

  static async createOffer(sessionKey, newOffer) {
    let ro = new ResponseObject();
    return new Promise((resolve, reject) => {
      fetch(UrlConstants.BASE_URL + this.urlCreateOffer, {
        method: "POST",
        headers: {
          'Authorization': 'Bearer ' + sessionKey,
          "Content-type": "application/json; charset=UTF-8"
        },
        body: JSON.stringify(newOffer),
      }).then((response) => {
        ro.code = response.status;
        return response.json();
      }).then((data) => {
        ro.data = data;
        resolve(ro);
      }).catch((ex) => {
        reject(ex);
      });
    });
  }

  static async getNewestOffers(sessionKey, startIndex, itemsPerPage) {
    let ro = new ResponseObject();
    return new Promise((resolve, reject) => {
      console.log((UrlConstants.BASE_URL + this.urlGetNewestOffers) + '/?start=' + startIndex + '&pageSize=' + itemsPerPage);
      fetch(UrlConstants.BASE_URL + this.urlGetNewestOffers + '/?start=' + startIndex + '&pageSize=' + itemsPerPage, {
        method: "GET",
        headers: {
          'Authorization': 'Bearer ' + sessionKey,
          "Content-type": "application/json; charset=UTF-8"
        }
      }).then((response) => {
        ro.code = response.status;
        return response.text();
      }).then((data) => {
        ro.message = data;
        if(ro.code == 200) {
          resolve(ro);
        }
        else {
          reject(ro);
        }
      }).catch((ex) => {
        reject(ex);
      });
    });
  }
}
