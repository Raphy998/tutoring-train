export default class UserDetails {
  constructor(name, email, password, education, gender) {
    this._name = name;
    this._email = email;
    this._password = md5(password);
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
    let retJson = {};
    if(this._name != null) {
      retJson.name = this._name;
    }
    if(this._email != null) {
      retJson.email = this._email;
    }
    if(this._password != null) {
      retJson.password = this._password;
    }
    if(this._education != null) {
      retJson.education = this._education;
    }
    if(this._gender != null) {
      retJson.gender = this._gender;
    }
    return retJson;
  }
}
