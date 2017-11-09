import React from 'react';
import ReactDOM from 'react-dom';
import MuiThemeProvider from 'material-ui/styles/MuiThemeProvider';
import LoginMain from 'app/Login/LoginMain';
import MainApp from 'app/MainApp/components/MainApp';
import GridListOffers from 'app/MainApp/Home/components/GridListOffers';
import EditAccountDetails from 'app/MainApp/Account/components/EditAccountDetails'
import { HashRouter, Route, Link, Redirect, Switch } from 'react-router-dom';
import customTheme from 'app/styles/customTheme';
import getMuiTheme from 'material-ui/styles/getMuiTheme';

ReactDOM.render(
  <MuiThemeProvider muiTheme={getMuiTheme(customTheme)}>
  <HashRouter>
    <div>
      <Route exact path="/" component={LoginMain}/>
      <Route path="/mainapp" component={MainApp}/>
      <Route exact path="/mainapp/home" component={GridListOffers}/>
      <Route exact path="/mainapp/profile" component={EditAccountDetails}/>
    </div>
  </HashRouter>
  </MuiThemeProvider>,
  document.getElementById('app')
);
let injectTapEventPlugin = require("react-tap-event-plugin");
injectTapEventPlugin({
  shouldRejectClick: function (lastTouchEventTimestamp, clickEventTimestamp) {
    return true;
  }
});
