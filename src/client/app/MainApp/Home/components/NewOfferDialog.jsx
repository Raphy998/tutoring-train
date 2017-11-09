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
require("app/styles/offergridstyle.less");

//Dialog to enter the details for the new offer and submit the offer creation process.
export default class NewOfferDialog extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      open: true,
      newOfferDate: new Date(),
      availableSubjects: null,
      onNewOfferDialogClose: this.props.onNewOfferDialogClose,
      showSnackbar: this.props.showSnackbar
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
          this.setState({availableSubjects: parsedJSON});
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
      //console.log(new Offer(-1, new Date(), true, this.state.newOfferDescription, this.state.newOfferSubject, localStorage.getItem('username')));
        if(ro.code == 200) {
          this.state.showSnackbar('Offer created successfully')
          this.state.onNewOfferDialogClose();

        }
        else {
          console.log('create offer failure');
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
    return(
      <Dialog
        title="Create offer"
        //actions={actions}
        modal={false}
        open={this.state.open}
        onRequestClose={this.btnCancelCreateOffer}>
          <Grid className="gridNewOfferDialog">
            <br/>
            <Row>
              <Col xs={6} md={6} className="colNewOfferGrid">Description <TextField id="tfDescription" fullWidth={true} name="newOfferDescription" hintText="Short description about your offer." multiLine={true} rows={1} rowsMax={4} onChange={this.handleCreateOfferInputChange}/></Col>
            <br/>
              <Col xs={6} md={6} className="colNewOfferGrid">Subject
                <DropDownMenu style={{width: 400, paddingLeft: 0, marginLeft: 0}} autoWidth={false} name="newOfferSubject" value={this.state.selectedSubject} onChange={this.handleSubjectChange}>
                  {
                    this.state.availableSubjects != null ? (
                    this.state.availableSubjects.map((val, index) => {
                      return(<MenuItem key={val.id} value={val} primaryText={val.enname} />)
                    }))
                    :
                    ('')
                  }
                </DropDownMenu>
              </Col>
            </Row>
          <br/>
            <Col xs={12} className="colNewOfferGrid">
                <RaisedButton primary label="Create" className="btnCreateOffer" onClick={this.btnCreateOfferClick}/>
                <RaisedButton primary label="Cancel" className="btnCancelCreateOffer" onClick={this.btnCancelCreateOffer}/>
              </Col>
          </Grid>
    </Dialog>
    );
  }
}
