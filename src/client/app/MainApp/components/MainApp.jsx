import React from 'react';
import ReactDOM from 'react-dom';
import RaisedButton from 'material-ui/RaisedButton';
import Divider from 'material-ui/Divider';
import TextField from 'material-ui/TextField';
import Drawer from 'material-ui/Drawer';
import AppBar from 'material-ui/AppBar';
import MenuItem from 'material-ui/MenuItem';
import {Tabs, Tab} from 'material-ui/Tabs';
import RegisterUser from 'app/entities/RegisterUser.js'
import AccountService from 'app/wsaccess/AccountService.js';
import LoginUser from 'app/entities/LoginUser.js';
import {Redirect} from 'react-router-dom';
import Menu from 'material-ui/Menu';

export default class MainApp extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      menuDrawerOpen: false,
      redirectTo: '',
      redirect: false
    }
  }

  btnMenu_Click = () => {
    this.setState({menuDrawerOpen: !this.state.menuDrawerOpen});
  }

  menuClick = (open, value) => {
    this.setState({ menuDrawerOpen: false });
    //Routing
    if(value == 'Home') {
      this.setState({redirectTo: '/mainapp/home'})
    }
    else if(value == 'Profile') {
      this.setState({redirectTo: '/mainapp/profile'})
    }
    this.setState({redirect: true});
  }

  render() {
    return(
      <div>
        {
          (this.state.redirect) ? (
            (<Redirect to={this.state.redirectTo}/>)
          )
          :
          ''
        }
        <AppBar
          title="Welcome aboard the TutoringTrain!"
          iconClassNameRight="muidocs-icon-navigation-expand-more"
          onLeftIconButtonTouchTap={this.btnMenu_Click}
        />
        <Drawer open={this.state.menuDrawerOpen}
          open={this.state.menuDrawerOpen}
          onRequestChange={(open) => this.setState({menuDrawerOpen: open})}>
          <Menu onChange={this.menuClick}>
            <MenuItem primaryText="Home" value="Home"/>
            <MenuItem primaryText="Profile" value="Profile"/>
          </Menu>
       </Drawer>
      </div>
    );
  }
}
