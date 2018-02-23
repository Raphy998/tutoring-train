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
import Offer from 'app/entities/Offer';
var Loader = require('halogen/PulseLoader');
import moment from 'moment';
require("app/styles/offerDetailDialogStyle.less");
import { getProfileImage } from 'app/entities/redux/ActionReducers'
import {
  withScriptjs,
  withGoogleMap,
  GoogleMap,
  Marker,
} from "react-google-maps";

const MapWithAMarker = withScriptjs(withGoogleMap(props =>
  <GoogleMap
    defaultZoom={8}
    defaultCenter={{ lat: props.lat, lng: props.lon }}
  >
    <Marker
      position={{ lat: props.lat, lng: props.lon }}
    />
  </GoogleMap>
));

//Dialog to show the user the details of an offer.
export default class NewOfferDialog extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      open: true,
      onOfferDetailDialogClose: this.props.onOfferDetailDialogClose,
      showSnackbar: this.props.showSnackbar,
      detailsLoaded: true,
      activeOffer: this.props.activeOffer,
      getUserAvatarFinished: false,
      avatar: require('app/resources/default_avatar.png')
    };
  }

  componentWillMount() {
      this.loadUserAvatar();
  }

  handleCreateOfferInputChange = (event) => {
    const target = event.target;
    const value = target.type === 'checkbox' ? target.checked : target.value;
    const name = target.name;
    this.setState({
      [name]: value
    });
  }

  loadUserAvatar() {
    //let img = getProfileImage().image;
    AccountService.getAvatarOfUser(localStorage.getItem('session-key'), localStorage.getItem('username'))
    .then((ro) => {
      if(ro.code == 200) { //Avatar returned
        this.setState({avatar: ro.message})
      }
      this.setState({getUserAvatarFinished: true});
    });
  }

  btnCloseOfferDetailDialog = () => {
    this.setState({open: !open});
    this.state.onOfferDetailDialogClose();
  }

  render() {
    if((this.state.detailsLoaded)) {
      return(
        <Dialog
          title="Offer details"
          modal={false}
          open={this.state.open}
          onRequestClose={this.btnCloseOfferDetailDialog}
          contentStyle={{width:'100%', height:'auto'}}
          autoScrollBodyContent={true}>
          <br/>
          <Grid style={{width:'95%'}}>
          <Row>
            <Col>
              <div id="divAvatar">
                <img src={this.state.avatar} className="offerUserAvatar"/>
              </div>
            </Col>
          </Row>
          <br/>
          <Row>
            <Col xs={2}>
              <b>headline</b>
            </Col>
            <Col xs={2}>
              {this.state.activeOffer.headline}
            </Col>
          </Row>
          <br/>
          <Row>
            <Col xs={2}>
              <b>created on</b>
            </Col>
            <Col xs={2}>
              {moment(this.state.activeOffer.postedon).format('DD/MM/YYYY')}
            </Col>
          </Row>
          <br/>
          <Row>
            <Col xs={2}>
            <b>description</b>
            </Col>
            <br/>
            <Col xs={2}>
              {this.state.activeOffer.description}
            </Col>
          </Row>
          <br/>
          <Row>
            <Col xs={2}>
            <b>username</b>
            </Col>
            <br/>
            <Col xs={2}>
              {this.state.activeOffer.user['username']}
            </Col>
          </Row>
          <br/>
          <Row>
            <Col xs={2}>
            <b>subject</b>
            </Col>
            <br/>
            <Col xs={2}>
              {this.state.activeOffer.subject['enname']}
            </Col>
          </Row>
          <br/>
          {
            this.state.activeOffer.location != null ? (
                <Row>
                  <Col>
                  <MapWithAMarker
                    googleMapURL="https://maps.googleapis.com/maps/api/js?key=AIzaSyAsjanynByJIkUXq7MtW7aJHwrm5ZtKuS8"
                    loadingElement={<div style={{ height: '50m', width:'10em'}} />}
                    containerElement={<div style={{ height: '30em', width:'30em'}} />}
                    mapElement={<div style={{ height: '100%', width:'100%'}} />}
                    lat={this.state.activeOffer.location['lat']}
                    lon={this.state.activeOffer.location['lon']}
                  />
                  </Col>
                </Row>
              )
              :
              ''
          }
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
