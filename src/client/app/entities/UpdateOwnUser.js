var md5 = require('md5');

export default class UpdateOwnUser {
  constructor(name, email, password, education, gender) {
    this._name = name;
    this._email = email;
    if(password != null) {
      this._password = md5(password);
    }
    this._education = education;
    this._gender = gender;
  }

  get name() { return this._name };
  set name(value) { this._name = value }
  get email() { return this._email };
  set email(value) { this._email = value }
  get password() { return this._password };
  set password(value) { this._password = value }
  get education() { return this._education };
  set education(value) { this._education = value }
  get gender() { return this._gender };
  set gender(value) { this._gender = value }

  toJSON() {
    return {
      name: this._name,
      email: this._email,
      password: this._password,
      education: this._education,
      gender: this._gender
    }
  }
}
