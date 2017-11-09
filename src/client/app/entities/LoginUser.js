var md5 = require('md5');

export default class LoginUser {
  constructor(username, password) {
    this._username = username;
    this._password = md5(password);
  }

  get username() { return this._username };
  set username(value) { this._username = value }
  get password() { return this._password };
  set password(value) { return this._password }

  toJSON() {
    return {
      username: this._username,
      password: this._password
    }
  }
}
