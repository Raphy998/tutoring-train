import React from 'react';
import RaisedButton from 'material-ui/RaisedButton';
import Divider from 'material-ui/Divider';

export default class LoginRegisterSelector extends React.Component {
  constructor(props) {
    super(props);
  }
  render() {
    return(
      <div id="divMainLogin">
      <div id="divLoginBtns">
        <img src="../app/Login/resources/logo.png" className="loginLogo"/>
        <RaisedButton label="Login" primary={true} className="btnLogin"/>
        <br/>
        <Divider/>
        <br/>
        <RaisedButton label="Register" secondary={true} className="btnRegister"/>
      </div>
      </div>
    );
  }
}
