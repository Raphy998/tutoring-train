export default class Subject {
  constructor(id, name) {
    this._id = id;
    this._name = name
  }

  get id() { return this._id };
  set id(value) { this._id = value }
  get name() { return this._name };
  set name(value) { this._name = value }

  toJSON() {
    return {
      id: this._id,
      name: this._name
    }
  }
}
