import React from 'react';
import RaisedButton from 'material-ui/RaisedButton';
import Divider from 'material-ui/Divider';
import Drawer from 'material-ui/Drawer';
import TextField from 'material-ui/TextField';
import {Tabs, Tab} from 'material-ui/Tabs';

const styles = {
  loginRegisterTabs : {

  }
};

export default class LoginRegisterSelector extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      tabVal : 'login'
    }
  }

  handleChange = (value) => {
    this.setState({
      tabVal: value
    });
  };

  render() {
    return(
      <div id="divMainLogin">
      <div id="divLoginRegister">
        <img src="../app/Login/resources/logo.png" className="loginLogo"/>
        <br/>
          <div id="divLoginRegisterInner">
              <Tabs value={this.state.tabVal} onChange={this.handleChange}>
                <Tab label="LOG IN" value="login">
                  <div className="divTabsLoginRegister">
                    <TextField className="login-field-mail" floatingLabelText="E-mail"/><br/>
                    <Divider/>
                    <TextField className="login-field-password" floatingLabelText="Password" type="Password"/><br/>
                    <Divider/>
                    <RaisedButton label="LOG IN" backgroundColor="#f6ece9" className="btnLogin"/>
                  </div>
                </Tab>
                <Tab label="REGISTER" value="ŕegister">
                  <div className="divTabsLoginRegister">
                    <TextField className="login-field-mail" floatingLabelText="E-mail"/><br/>
                    <Divider/>
                    <TextField className="login-field-password" floatingLabelText="Password" type="Password"/><br/>
                    <Divider/>
                    <TextField className="login-field-password" floatingLabelText="Retype Password" type="Password"/><br/>
                    <Divider/>
                    <RaisedButton label="LOG IN" backgroundColor="#f6ece9" className="btnLogin"/>
                  </div>
                </Tab>
              </Tabs>
      </div>
      </div>
    </div>
    );
  }
}
