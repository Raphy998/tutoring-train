import Subject from 'app/entities/Subject';
import moment from 'moment';

export default class Offer {
  constructor(id, headline, postedon, isactive, description, idsubject, username) {
    this._id = id
    this._headline = headline;
    this._postedon = postedon;
    this._isActive = isactive;
    this._description = description;
    this._subject = {
      id: idsubject
    };
    this._user = {
      username: username
    }
  }

  get id() { return this._id };
  set id(value) { this._id = value };
  get headline() { return this._headline };
  set headline(value) { this.headline = value};
  get postedon() { return this._postedon };
  set postedon(value) { this._postedon = value };
  get isactive() { return this._isActive };
  set isactive(value) { return this._isActive = value };
  get description() { return this._description };
  set description(value) { return this._description = value };
  get subject() { return this._subject };
  set subject(value) { return this._subject = value };
  get user() { return this._user };
  set user(value) { this._user = value };


  toJSON() {
    return {
      id : this._id,
      headline: this._headline,
      postedon: moment(this._postedon).format("YYYY-MM-DD'T'hh:mm:ssZ"),
      isactive: this._isActive,
      description: this._description,
      subject: this._subject,
      user: this._user
    }
  }
}
