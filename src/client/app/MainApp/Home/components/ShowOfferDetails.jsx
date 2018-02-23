import React, {PropTypes} from 'react'
import ReactDOM from 'react-dom';
import RaisedButton from 'material-ui/RaisedButton';
import Divider from 'material-ui/Divider';
import Drawer from 'material-ui/Drawer';
import TextField from 'material-ui/TextField';
import {Tabs, Tab} from 'material-ui/Tabs';
import Snackbar from 'material-ui/Snackbar';
import RegisterUser from 'app/entities/RegisterUser';
import LoginUser from 'app/entities/LoginUser';
import AccountService from 'app/wsaccess/AccountService';
import ImageLoader from 'react-imageloader';
import Paper from 'material-ui/Paper';
import Dialog from 'material-ui/Dialog';
import DatePicker from 'material-ui/DatePicker';
import DropDownMenu from 'material-ui/DropDownMenu';
import MenuItem from 'material-ui/MenuItem';
import { Grid, Row, Col } from 'react-flexbox-grid';
import OfferService from 'app/wsaccess/OfferService';
import SubjectService from 'app/wsaccess/SubjectService';
import Offer from 'app/entities/Offer';
var Loader = require('halogen/PulseLoader');
require("app/styles/offergridstyle.less");


const dialogContentStyle = {
  width: '30%',
  maxWidth: 'none',
};

//Dialog to enter the details for the new offer and submit the offer creation process.
export default class NewOfferDialog extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      open: true,
      newOfferDate: new Date(),
      availableSubjects: null,
      onNewOfferDialogClose: this.props.onNewOfferDialogClose,
      showSnackbar: this.props.showSnackbar,
      subjectsLoaded: false
    };
    this.loadSubjectList();
  }

  componentWillMount() {
  }

  //Loads the available subjects, for which a offer can be created.
  loadSubjectList = () => {
    SubjectService.getAllSubjects(localStorage.getItem('session-key'))
      .then((ro) => {
        if(ro.code == 200) {
          console.log(ro.message);
          let parsedJSON = ro.message;
          this.setState({availableSubjects: parsedJSON, subjectsLoaded: true});
        }
      });
  }

  handleNewOfferDateChange = (event, val) => {
    this.setState({newOfferDate: val});
  }

  handleSubjectChange = (event, index, value) => {
    this.setState({selectedSubject: value});
  }

  btnCreateOfferClick = () => {
    OfferService.createOffer(localStorage.getItem('session-key'), new Offer(-1, this.state.newOfferDate, true, this.state.newOfferDescription, this.state.selectedSubject.id, localStorage.getItem('username')))
    .then((ro) => {
        if(ro.code == 200) {
          this.state.showSnackbar('Offer created successfully')
          this.state.onNewOfferDialogClose();
        }
        else {
          this.state.showSnackbar("Error creating offer");
        }
      });
    }

  btnCancelCreateOffer = () => {
    this.setState({open: !open});
    this.state.onNewOfferDialogClose();
  }

  handleCreateOfferInputChange = (event) => {
    const target = event.target;
    const value = target.type === 'checkbox' ? target.checked : target.value;
    const name = target.name;
    this.setState({
      [name]: value
    });
  }
  render() {
    if((this.state.subjectsLoaded)) {
      return(
        <Dialog
          title="Create offer"
          modal={false}
          open={this.state.open}
          onRequestClose={this.btnCancelCreateOffer}
          contentStyle={dialogContentStyle}>
            <Grid style={{width:'100%'}}>
              <br/>
              <Row>
                <Col xs={12} className="colNewOfferGrid">Description <TextField id="tfDescription" fullWidth={false} name="newOfferDescription" hintText="Short description about your offer." multiLine={true} rows={1} rowsMax={4} onChange={this.handleCreateOfferInputChange}/></Col>
              <br/>
                <Col xs={12} className="colNewOfferGrid">Subject
                  <DropDownMenu style={{width: 400, paddingLeft: 0, marginLeft: 0}} autoWidth={false} name="newOfferSubject" value={this.state.selectedSubject} onChange={this.handleSubjectChange}>
                    {
                      this.state.availableSubjects != null ? (
                      this.state.availableSubjects.map((val, index) => {
                        return(<MenuItem key={val.id} value={val} primaryText={val.name} />)
                      }))
                      :
                      ('')
                    }
                  </DropDownMenu>
                </Col>
              </Row>
            <br/>
              <Col xs={6} className="colNewOfferGrid">
                  <RaisedButton primary label="Create" className="btnCreateOffer" onClick={this.btnCreateOfferClick}/>
                  <RaisedButton primary label="Cancel" className="btnCancelCreateOffer" onClick={this.btnCancelCreateOffer}/>
                </Col>
            </Grid>
      </Dialog>
      );
    }
    else {
      return(
        <div id="divPageLoader">
          <Loader color='#86BAB8' size="3em"/>
        </div>
      )
    }
  }
}
