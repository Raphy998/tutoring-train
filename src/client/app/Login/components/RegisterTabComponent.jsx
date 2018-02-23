import React from 'react';
import ReactDOM from 'react-dom';
import RaisedButton from 'material-ui/RaisedButton';
import Divider from 'material-ui/Divider';
import Drawer from 'material-ui/Drawer';
import TextField from 'material-ui/TextField';
import {Tabs, Tab} from 'material-ui/Tabs';
import RegisterUser from 'app/entities/RegisterUser'
import AccountService from 'app/wsaccess/AccountService';


export default class RegisterTabComponent extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      showSnackbar: this.props.showSnackbar
    }
    this.btnRegister_Click = this.btnRegister_Click.bind(this);
  }

  //Registrating the user with the entered details using the web service.
  async btnRegister_Click (ev) {
    //Check if user has entered all fields.
    if(this.state.tfMailRegister && this.state.tfUsernameRegister && this.state.tfPasswordRegister) {
      await AccountService.register(new RegisterUser(this.state.tfMailRegister, this.state.tfUsernameRegister, this.state.tfPasswordRegister))
      .then((ro) => {
        switch(ro.code) {
          case 200:
            this.state.showSnackbar('Your registration was successful. Please check your e-mail in order to finish the registration process.');
          break;
          case 409:
            this.state.showSnackbar('An account with the given username already exists.');
          break;
        }
      }),
        ((error) => {
          this.state.showSnackbar('Our service is not available at the moment. Please try again later.');
        })
    }
    //User has not entered all fields.
    else {
      this.state.showSnackbar('Please enter all fields in order to register.');
    }
  }

  handleRegisterInputChange = (event) => {
    const target = event.target;
    const value = target.type === 'checkbox' ? target.checked : target.value;
    const name = target.name;
    this.setState({
      [name]: value
    });
  }

  onFormEnter = (ev) => {
    if(ev.key == 'Enter') {
      this.btnRegister_Click();
    }
  }

  render() {
    return(
          <div id="divLoginRegisterInner">
            <div className="divTabsLoginRegister">
              <TextField className="login-field-mail" name="tfMailRegister" floatingLabelText="E-mail" onChange={this.handleRegisterInputChange} onKeyPress={this.onFormEnter}/><br/>
              <br/>
              <TextField className="login-field-username" name="tfUsernameRegister" floatingLabelText="Username" onChange={this.handleRegisterInputChange} onKeyPress={this.onFormEnter}/><br/>
              <br/>
              <TextField className="login-field-password" name="tfPasswordRegister" floatingLabelText="Password" type="Password" onChange={this.handleRegisterInputChange} onKeyPress={this.onFormEnter}/><br/>
              <br/>
              <RaisedButton fullWidth={true} label="REGISTER" className="btnLogin" onClick={this.btnRegister_Click}/>
            </div>
          </div>
    );
  }
}
