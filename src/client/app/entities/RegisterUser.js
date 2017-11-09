var md5 = require('md5');

export default class RegisterUser {
  constructor(email, username, password) {
    this._email = email;
    this._username = username;
    this._password = md5(password);
  }

  get email() { return this._email };
  set email(value) { this._email = value }
  get username() { return this._username };
  set username(value) { this._username = value }
  get password() { return this._password };
  set password(value) { return this._password }

  toJSON() {
    return {
      email: this._email,
      username: this._username,
      password: this._password
    }
  }
}
