import ResponseObject from './ResponseObject';

export default class OfferService {
  constructor() {
    this.baseUrl = "http://localhost:8080/TutoringTrainWebservice/services";
  }
  static get baseUrl() { return 'http://localhost:8080/TutoringTrainWebservice/services' };
  static get urlCreateOffer() { return '/offer' };
  static get urlGetNewestOffers() { return "/offer/new" };

  static async createOffer(sessionKey, newOffer) {
    let ro = new ResponseObject();
    return new Promise((resolve, reject) => {
      fetch(this.baseUrl + this.urlCreateOffer, {
        method: "POST",
        body: newOffer,
        headers: {
          'Authorization': 'Bearer ' + sessionKey,
          "Content-type": "application/json; charset=UTF-8"
        }
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
      console.log((this.baseUrl + this.urlGetNewestOffers) + '/?start=' + startIndex + '&pageSize=' + itemsPerPage);
      fetch(this.baseUrl + this.urlGetNewestOffers + '/?start=' + startIndex + '&pageSize=' + itemsPerPage, {
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
        resolve(ro);
      }).catch((ex) => {
        reject(ex);
      });
    });
  }
}
