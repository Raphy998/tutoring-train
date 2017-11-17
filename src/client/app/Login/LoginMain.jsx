import React from 'react';
import ReactDOM from 'react-dom';
import LoginRegisterSelector from './components/LoginRegisterSelector'

export default class LoginMain extends React.Component {
  constructor(props) {
    super(props);
  }

  render() {
    return(
      <div>
        <LoginRegisterSelector/>
      </div>
    );
  }
}
