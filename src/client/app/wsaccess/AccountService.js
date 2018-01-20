import ResponseObject from './ResponseObject';
import UrlConstants from './UrlConstants.js';

export default class AccountService {
  constructor() {
  }

  static get urlRegisterAccount() { return '/user/register' };
  static get urlLoginAccount() { return '/authentication' };
  static get urlGetOwnUserDetails() { return '/user' };
  static get urlGetGenders() { return '/user/gender' };
  static get urlGetOwnAvatar() { return '/user/avatar'};
  static get urlUpdateOwnUser() { return "/user/update/own" };
  static get urlUploadProfileImage() { return "/user/avatar/B64" };

  static async register(userRegister) {
    let ro = new ResponseObject();
    return new Promise((resolve, reject) => {
      fetch(UrlConstants.BASE_URL + this.urlRegisterAccount, {
        method: "POST",
        body: JSON.stringify(userRegister),
        headers: {
          "Content-type": "application/json; charset=UTF-8"
        }
      })
      .then((response) => {
        ro.code = response.status;
        return response.json();
      })
      .then((data) => {
        ro.data = data;
        resolve(ro);
      })
      .catch((ex) => {
        reject(ex);
      });
    });
  }

  static async login(loginUser) {
    let ro = new ResponseObject();
    return new Promise((resolve, reject) => {
      fetch(UrlConstants.BASE_URL + this.urlLoginAccount, {
        method: "POST",
        body: JSON.stringify(loginUser),
        headers: {
          "Content-type": "application/json; charset=UTF-8"
        }
      }).then((response) => {
        ro.code = response.status;
        return response.text();
      })
      .then((data) => {
        ro.message = data;
        resolve(ro);
      })
      .catch((ex) => {
        reject(ex);
      })
    });
  }

  static async getOwnUserDetails(sessionKey) {
    let ro = new ResponseObject();
    return new Promise((resolve, reject) => {
      fetch(UrlConstants.BASE_URL + this.urlGetOwnUserDetails, {
        method: "GET",
        headers: {
          'Authorization': 'Bearer ' + sessionKey,
          "Content-type": "application/json; charset=UTF-8"
        }
      }).then((response) => {
        ro.code = response.status;
        return response.text();
      })
      .then((data) => {
        ro.message = data;
        resolve(ro);
      })
      .catch((ex) => {
        reject(ex);
      })
    });
  }

  static async getValidGenders(sessionKey) {
    let ro = new ResponseObject();
    return new Promise((resolve, reject) => {
      fetch(UrlConstants.BASE_URL + this.urlGetGenders, {
        method: "GET",
        headers: {
          'Authorization': 'Bearer ' + sessionKey,
          "Content-type": "application/json; charset=UTF-8"
        }
      }).then((response) => {
        ro.code = response.status;
        return response.text();
      })
      .then((data) => {
        ro.message = data;
        resolve(ro);
      })
      .catch((ex) => {
        reject(ex);
      })
    });
  }

  static async getOwnAvatar(sessionKey) {
    let ro = new ResponseObject();
    return new Promise((resolve, reject) => {
      fetch(UrlConstants.BASE_URL + this.urlGetOwnAvatar, {
        method: "GET",
        headers: {
          'Authorization': 'Bearer ' + sessionKey,
          "Content-type": "application/json; charset=UTF-8"
        }
      }).then((response) => {
        ro.code = response.status;
        return response.blob();
      })
      .then((blob) => {
        var objectURL = URL.createObjectURL(blob);
        ro.message = objectURL;
        resolve(ro);
      })
      .catch((ex) => {
        reject(ex);
      })
    });
  }

  static async updateOwnUser(sessionKey, updateOwnUser) {
    let ro = new ResponseObject();
    console.log(JSON.stringify(updateOwnUser));
    return new Promise((resolve, reject) => {
      fetch(UrlConstants.BASE_URL + this.urlUpdateOwnUser, {
        method: "PUT",
        headers: {
          'Authorization': 'Bearer ' + sessionKey,
          "Content-type": "application/json; charset=UTF-8"
        },
        body: JSON.stringify(updateOwnUser)
      }).then((response) => {
        ro.code = response.status;
        return response.blob();
      })
      .then((blob) => {
        var objectURL = URL.createObjectURL(blob);
        ro.message = objectURL;
        resolve(ro);
      })
      .catch((ex) => {
        reject(ex);
      })
    });
  }

  static async uploadProfileImage(sessionKey, image) {
    var reader  = new FileReader();
    let ro = new ResponseObject();
    let fd = new FormData();
    let imgObj = new Image();
    imgObj.src = image;
    fd.append("name", localStorage.getItem('username'));
    fd.append("file", image);
    console.log(fd);
    return new Promise((resolve, reject) => {
      fetch(UrlConstants.BASE_URL + this.urlUploadProfileImage, {
        method: "POST",
        headers: {
          'Authorization': 'Bearer ' + sessionKey,
          'Content-type': 'image/jpg'
        },
        body: image
      }).then((response) => {
        ro.code = response.status;
        return response.text();
      })
      .then((text) => {
        ro.message = text;
        resolve(ro);
      })
      .catch((ex) => {
        reject(ex);
      })
    });
  }
}
