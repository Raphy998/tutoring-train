import React from 'react';
import ReactDOM from 'react-dom';
import RaisedButton from 'material-ui/RaisedButton';
import Divider from 'material-ui/Divider';
import Drawer from 'material-ui/Drawer';
import TextField from 'material-ui/TextField';
import {Tabs, Tab} from 'material-ui/Tabs';
import RegisterUser from 'app/entities/RegisterUser.js'
import AccountService from 'app/wsaccess/AccountService.js';
import LoginUser from 'app/entities/LoginUser.js';
import {Redirect} from 'react-router-dom';


export default class LoginTabComponent extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      loginMail: '',
      loginPassword: '',
      showSnackbar: this.props.showSnackbar,
      loginSuccess: false
    };
    this.btnLogin_Click = this.btnLogin_Click.bind(this.btnLogin_Click);
    //console.log(store.getState())
  }

  //Registrating the user with the entered details using the web service.
  btnLogin_Click = async (ev) => {
    AccountService.login(new LoginUser(this.state.loginUsername, this.state.loginPassword))
    .then((ro) => {
      switch(ro.code) {
        case 200:
          localStorage.setItem('session-key', ro.message);
          localStorage.setItem('username', this.state.loginUsername);
          this.setState({loginSuccess: true});
        break;
        case 401:
          this.state.showSnackbar('Password and/or username is invalid.');
        break;
        case 403:
          this.state.showSnackbar('You cannot be identified in the current context.');
        break;
        case 450:
          this.state.showSnackbar('You are currently blocked due to the following reason: ');
        break;
      }
    },
    ((ex) => {
      console.log(ex);
      this.state.showSnackbar('Our service is not available at the moment. Please try again later.')
    })
  );
  }

  handleLoginInputChange = (event) => {
    const target = event.target;
    const value = target.type === 'checkbox' ? target.checked : target.value;
    const name = target.name;
    this.setState({
      [name]: value
    });
  }

  render() {
    return(
        <div id="divLoginRegisterInner">
          { this.state.loginSuccess ?
            <Redirect to="/mainapp/home"/> : null
          }
          <div className="divTabsLoginRegister">
            <TextField required className="login-field-mail" name="loginUsername" floatingLabelText="Username" onChange={this.handleLoginInputChange}/><br/>
            <br/>
            <TextField required className="login-field-password" name="loginPassword" floatingLabelText="Password" type="Password" onChange={this.handleLoginInputChange}/><br/>
            <br/>
            <RaisedButton fullWidth={true} label="LOG IN" className="btnLogin" onClick={this.btnLogin_Click}/>
          </div>
        </div>
    );
  }
}
