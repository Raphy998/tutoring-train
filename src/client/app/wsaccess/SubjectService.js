import ResponseObject from './ResponseObject';

export default class SubjectService {
  constructor() {

  }
  static get baseUrl() { return "http://localhost:8080/TutoringTrainWebservice/services" };
  static get urlGetSubjects() { return "/subject" };

  static async getAllSubjects(sessionKey) {
    let ro = new ResponseObject();
    return new Promise((resolve, reject) => {
      fetch(this.baseUrl + this.urlGetSubjects, {
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
