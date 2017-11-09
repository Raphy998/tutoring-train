import React from 'react';
import ReactDOM from 'react-dom';
import PropTypes from 'prop-types';
import MenuItem from 'material-ui/MenuItem';
import InfiniteScroll from 'react-infinite-scroll-component';
import OfferService from 'app/wsaccess/OfferService';
import IconMenu from 'material-ui/IconMenu';
import IconButton from 'material-ui/IconButton';
import FontIcon from 'material-ui/FontIcon';
import NavigationExpandMoreIcon from 'material-ui/svg-icons/navigation/expand-more';
import DropDownMenu from 'material-ui/DropDownMenu';
import RaisedButton from 'material-ui/RaisedButton';
import {Toolbar, ToolbarGroup, ToolbarSeparator, ToolbarTitle} from 'material-ui/Toolbar';
import SearchBar from 'material-ui-search-bar';
import Snackbar from 'material-ui/Snackbar';
import TextField from 'material-ui/TextField';
import {
  Table,
  TableBody,
  TableHeader,
  TableHeaderColumn,
  TableRow,
  TableRowColumn,
} from 'material-ui/Table';
import moment from 'moment';
import { Grid, Row, Col } from 'react-flexbox-grid';
import {Card, CardActions, CardHeader, CardMedia, CardTitle, CardText} from 'material-ui/Card';
import FlatButton from 'material-ui/FlatButton';
import Toggle from 'material-ui/Toggle';
import AccountService from 'app/wsaccess/AccountService';
import UpdateOwnUser from 'app/entities/UpdateOwnUser';
require("app/MainApp/Account/styles/style.less");
require("app/styles/editAccountStyle.less");
var Loader = require('halogen/PulseLoader');

export default class EditAccountDetails extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      expanded: false,
      getOwnUserDetailsFinished: false,
      getValidGendersFinished: false,
      getOwnAvatarFinished: false,
      showSnackbar: this.props.showSnackbar
    };
  }

  componentWillMount = () =>  {
    //Loading user details of account.
    AccountService.getOwnUserDetails(localStorage.getItem('session-key'))
    .then((ro) => {
      if(ro.code == 200) {
        let userDetails = JSON.parse(ro.message);
        this.setState({
          editName: userDetails.name,
          editGender: userDetails.gender,
          editEmail: userDetails.mail,
          editEducation: userDetails.education,
          editUsername: userDetails.username
        });
        this.setState({getOwnUserDetailsFinished: true}); //Loading data finished.
      }
    })

    //Loading account avatar
    AccountService.getOwnAvatar(localStorage.getItem('session-key'), localStorage.getItem('username'))
    .then((ro) => {
      if(ro.code == 200) { //Avatar returned
        this.setState({avatar: ro.message})
      }
      else if(ro.code == 204) { //User has no avatar
        this.setState({avatar: new Image('')})
      }
      this.setState({getOwnAvatarFinished: true});
    })

      //Loading valid genders.
      AccountService.getValidGenders(localStorage.getItem('session-key'))
      .then((ro) => {
        if(ro.code == 200) {
          let validGenders = JSON.parse(ro.message);
          console.log('genders' + ro.message);
          this.setState({genders: validGenders});
          this.setState({accountGender: this.state.genders[0]})
          this.setState({getValidGendersFinished: true}); //Loading data finished.
        }
    })
  }

  handleLoginInputChange = (event) => {
    const target = event.target;
    const value = target.type === 'checkbox' ? target.checked : target.value;
    const name = target.name;
    this.setState({
      [name]: value
    });
  }

  handleExpandChange = (expanded) => {
    this.setState({expanded: expanded});
  };

  handleToggle = (event, toggle) => {
    this.setState({expanded: toggle});
  };

  handleExpand = () => {
    this.setState({expanded: true});
  };

  handleReduce = () => {
    this.setState({expanded: false});
  };

  /*
   * Update button event handler. Sending the changes to the webservice.
  */
  btnUpdate_Click = () => {
    AccountService.updateOwnUser(localStorage.getItem('session-key'),
    new UpdateOwnUser(this.state.accountName, this.state.accountEmail, null, this.state.accountEducation, this.state.accountGender))
    .then((ro) => {
      if(ro.code == 200) {
        //this.state.showSnackbar("Details updated successfully.");
      }
      else {
        //this.state.showSnackbar("Error updating your details. Please try again later.");
      }
    });
  }

  handleGenderChange = (event, index, value) => {
    this.setState({accountGender: value});
  }


  render() {
    if((this.state.getOwnUserDetailsFinished) && (this.state.getValidGendersFinished) && (this.state.getOwnAvatarFinished)) {
      return(
        <div id="divMainEditAccountDetails">
          <Card expanded={this.state.expanded} onExpandChange={this.handleExpandChange} id="cardEditAccountDetails">
          <CardMedia
            overlay={<CardTitle title={localStorage.getItem('username')} />}>
            <img src={this.state.avatar} alt="Profile Image" style={{height: 700, marginLeft: "auto", marginRight: "auto"}}/>
          </CardMedia>
          {/*<CardTitle title="Card title" subtitle="Card subtitle"/>*/}
          <CardText>
            <div id="divEditAccountTextfields">
            <Row>
              <Col xs={12}>
                <TextField name="accountName" fullWidth={true} floatingLabelText="Name" defaultValue={this.state.editName} onChange={this.handleLoginInputChange}/>
              </Col>
            <br/>
              <Col xs={12}>
                <TextField name="accountEmail" fullWidth={true} floatingLabelText="E-mail" defaultValue={this.state.editEmail} onChange={this.handleLoginInputChange}/>
              </Col>
            <br/>
              <Col xs={12}>
                <TextField name="accountEducation" fullWidth={true} floatingLabelText="Education" defaultValue={this.state.editEducation} onChange={this.handleLoginInputChange}/>
              </Col>
            <br/>
              <Col xs={12}>
                {
                <DropDownMenu value={this.state.accountGender.name} style={{width: "100%", marginLeft: 0, marginRight: 0}} onChange={this.handleGenderChange}>
                  {
                    this.state.genders.map((value, index) => {
                      return <MenuItem key={value.name} value={value.name} primaryText={value.name}/>
                    })
                  }
                </DropDownMenu>
              }
              </Col>
              <br/>
              <Col xs={12}>
                <RaisedButton label="Update" fullWidth={true} onClick={this.btnUpdate_Click}/>
              </Col>
            <br/>
            </Row>
          </div>
          </CardText>
        </Card>
        </div>
      );
    }
    return(
      <div id="divPageLoader">
        <Loader color='#86BAB8' size="3em"/>
      </div>
    )
  }
}
