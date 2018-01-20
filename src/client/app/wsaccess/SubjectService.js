import ResponseObject from './ResponseObject';
import UrlConstants from './UrlConstants.js';

export default class SubjectService {
  constructor() {

  }
  static get urlGetSubjects() { return "/subject" };

  static async getAllSubjects(sessionKey) {
    let ro = new ResponseObject();
    return new Promise((resolve, reject) => {
      fetch(UrlConstants.BASE_URL + this.urlGetSubjects, {
        method: "GET",
        headers: {
          "Content-type": "application/json; charset=UTF-8",
          'Authorization': 'Bearer ' + sessionKey,
        }
      })
      .then((response) => {
        ro.code = response.status;
        return response.json();
      })
      .then((data) => {
        ro.message = data;
        resolve(ro);
      })
      .catch((ex) => {
        reject(ex);
      });
    });
  }
}
