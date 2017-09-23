import React from 'react';
import ReactDOM from 'react-dom';
import MuiThemeProvider from 'material-ui/styles/MuiThemeProvider';
import LoginMain from './Login/LoginMain';


ReactDOM.render(
  <MuiThemeProvider>
  <LoginMain />
  </MuiThemeProvider>,
  document.getElementById('app')
);
