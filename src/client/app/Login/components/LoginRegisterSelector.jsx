import React from 'react';
import ReactDOM from 'react-dom';
import RaisedButton from 'material-ui/RaisedButton';
import Snackbar from 'material-ui/Snackbar';
import Divider from 'material-ui/Divider';
import Drawer from 'material-ui/Drawer';
import TextField from 'material-ui/TextField';
import {Tabs, Tab} from 'material-ui/Tabs';
import RegisterUser from 'app/entities/RegisterUser'
import AccountService from 'app/wsaccess/AccountService';
import LoginTabComponent from './LoginTabComponent';
import RegisterTabComponent from './RegisterTabComponent'
require("app/Login/styles/style.less");

export default class LoginRegisterSelector extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      tabVal : 'login',
      snackbarMessage: '',
      showSnackbarMessage: false,
    }
  }

  showSnackbar = (value) => {
    this.setState({snackbarMessage: value, showSnackbarMessage: true});
  }

  onSnackbarRequestClose = () => {
    this.setState({showSnackbarMessage: false});
  }

  handleTabChange = (value) => {
    this.setState({
      tabVal: value
    });
  };

  handleRegisterInputChange (event) {
    const target = event.target;
    const value = target.type === 'checkbox' ? target.checked : target.value;
    const name = target.name;
    console.log(name);
    this.setState({
      [name]: value
    });
  }

  render() {
    return(
      <div id="divMainLogin">
      <div id="divLoginRegister">
        <img src="../app/Login/resources/logo.png" className="loginLogo"/>
        <br/>
          <div id="divLoginRegisterInner">
              <Tabs value={this.state.tabVal} onChange={this.handleTabChange}>
                <Tab label="LOG IN" value="login">
                  <LoginTabComponent showSnackbar={this.showSnackbar}/>
                </Tab>
                <Tab label="REGISTER" value="register">
                  <RegisterTabComponent showSnackbar={this.showSnackbar}/>
                </Tab>
            </Tabs>
      </div>
      </div>
    <Snackbar
      open={this.state.showSnackbarMessage}
      message={this.state.snackbarMessage}
      onRequestClose={this.onSnackbarRequestClose}
      autoHideDuration={4000}/>
    </div>
    );
  }
}
