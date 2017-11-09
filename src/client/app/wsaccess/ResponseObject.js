
export default class ResponseObject {
  constructor(code, message) {
    this._code = code;
    this._message = message;
  }

  get code() { return this._code };
  set code(value) { this._code = value };
  get message() { return this._message };
  set message(value) { this._message = value };

  toJSON() {
    return {
      code: this._code,
      message: this._message
    }
  }
}
