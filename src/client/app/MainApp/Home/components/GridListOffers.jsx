import React from 'react';
import ReactDOM from 'react-dom';
import PropTypes from 'prop-types';
import MenuItem from 'material-ui/MenuItem';
import InfiniteScroll from 'react-infinite-scroller';
import OfferService from 'app/wsaccess/OfferService';
import IconMenu from 'material-ui/IconMenu';
import IconButton from 'material-ui/IconButton';
import FontIcon from 'material-ui/FontIcon';
import NavigationExpandMoreIcon from 'material-ui/svg-icons/navigation/expand-more';
import DropDownMenu from 'material-ui/DropDownMenu';
import RaisedButton from 'material-ui/RaisedButton';
import NewOfferDialog from './NewOfferDialog';
import OfferDetailDialog from './OfferDetailDialog';
import {Toolbar, ToolbarGroup, ToolbarSeparator, ToolbarTitle} from 'material-ui/Toolbar';
import SearchBar from 'material-ui-search-bar';
import Snackbar from 'material-ui/Snackbar';
import { addOffers } from 'app/entities/redux/ActionCreators';
import {
  Table,
  TableBody,
  TableHeader,
  TableHeaderColumn,
  TableRow,
  TableRowColumn,
} from 'material-ui/Table';
import moment from 'moment';
require("app/styles/offergridstyle.less");
import getStore from 'app/entities/redux/StoreProvider';
let store = getStore();
import { SET_OFFERS, ADD_OFFERS } from 'app/entities/redux/ActionCreators.jsx';
import InfoIcon from 'material-ui/svg-icons/action/info';

export default class GridListOffers extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      startIndex: 0,
      offers: [], renderOffers: [],
      hasMoreItems: true,
      value: 3,
      showNewOfferDialog: false
    };
    store.subscribe(() => {
      //New offers have been loaded -> add to offer collection
      if(store.getState().type == ADD_OFFERS) {
        if(store.getState().offers.length) {
          let pOffers = this.state.offers;
          Array.prototype.push.apply(pOffers, store.getState().offers);
          this.setState({offers: pOffers});
        }
      }
    })
  }

  getNumberOfOffers = () => {
    OfferService.getNumberOfExistingOffers(localStorage.getItem('session-key'))
    .then((ro) => {
      if(ro.code == 200) {
          this.setState({numberOfOffers: int(ro.message)});
      }
    });
  }


  componentWillMount = () => {
    //this.fetchData();
    this.getNumberOfOffers();
  }

  //Handler for when the user wants to search the offers.
  onSearchBarSearch = () => {
    console.log('searchbar clicked');
  }

  //Event handler for when the create offer button is being clicked. -> Opening the NewOfferDialog
  btnCreateOfferClick = () => {
    this.setState({showNewOfferDialog: true});
  }

  btnShowOfferDetailClick = (idx) => {
    this.setState({activeOffer: this.state.offers[idx]});
    this.setState({showOfferDetailDialog: true});
  }

  //responsible for loading offers from the webservice.
  fetchData = () => {
    let mOffers = [];
    let itemsPerPage = 5;
    OfferService.getNewestOffers(localStorage.getItem('session-key'), this.state.startIndex, itemsPerPage)
    .then((ro) => {
      if(ro.code == 200) {
        let parsedJSON = JSON.parse(ro.message);
        console.log('fetched');
        console.log(parsedJSON);
        //console.log("parsed JSON" +  parsedJSON);
        if((parsedJSON && parsedJSON.length)) {
          store.dispatch(addOffers(parsedJSON));
          this.setState({startIndex: this.state.startIndex + parsedJSON.length});
          if(parsedJSON.length < itemsPerPage) {
            this.setState({hasMoreItems: false});
          }
        }
        else {
          //this.setState({hasMoreItems: false});
        }
      }
    }
    );
    }

    handleNewOfferDialogClose = () => {
      this.setState({showNewOfferDialog: false});
      this.setState({hasMoreItems: true}); //Reloading the entrys.
    }


    //Callback function for the Tab subcomponents in order to display the snackbar.
    showSnackbar = (message) => {
      this.setState({snackbarMessage: message, showSnackbarMessage: true});
    }

  //Called when a certain offer is being clicked.
  onOfferClicked = (ev) => {
    //ev.preventDefault();
  }

  handleOnOfferDetailDialogClose = () => {
    this.setState({showOfferDetailDialog: false});
  }

  render() {
    return(
      <div>
          <div id="divContent">
            <Toolbar>
              <ToolbarGroup>
                <SearchBar
                 onChange={(val) => this.setState({searchBarValue: val})}
                 onRequestSearch={this.onSearchBarSearch}
                 style={{
                   margin: '0 auto',
                   maxWidth: 800
                 }}
                 hintText="Search offers"
               />
             </ToolbarGroup>
               <ToolbarGroup>
                 <RaisedButton label="Create Offer" primary={true} onClick={this.btnCreateOfferClick}/>
               </ToolbarGroup>
             </Toolbar>
            <InfiniteScroll
                   pageStart={5}
                   loadMore={this.fetchData.bind(this)}
                   hasMore={this.state.hasMoreItems}
                   loader={<p style={{textAlign: 'center'}}>
                     <b>Loading more offers for you ...</b>
                   </p>}
                   next={this.fetchData}
                   endMessage={
                     <p style={{textAlign: 'center'}}>
                       <b>You have seen all offers. Check back later for more ...</b>
                     </p>
                   }>
                   <div>
                 <Table
                 onCellClick={this.onOfferClicked}>
                  <TableHeader displaySelectAll={false} adjustForCheckbox={false}>
                    <TableRow>
                      <TableHeaderColumn>headline</TableHeaderColumn>
                      <TableHeaderColumn>date</TableHeaderColumn>
                      <TableHeaderColumn>description</TableHeaderColumn>
                      <TableHeaderColumn>username</TableHeaderColumn>
                      <TableHeaderColumn>subject</TableHeaderColumn>
                      <TableHeaderColumn></TableHeaderColumn>
                    </TableRow>
                  </TableHeader>
                  <TableBody displayRowCheckbox={false}>
                    {
                        this.state.offers.map((val,index) => {
                          console.log(val);
                          return(<TableRow key={val.id}>
                            <TableRowColumn>{val.headline}</TableRowColumn>
                            <TableRowColumn>{moment(val.postedon).format('DD/MM/YYYY')}</TableRowColumn>
                            <TableRowColumn>{val.description}</TableRowColumn>
                            <TableRowColumn>{val.user['username']}</TableRowColumn>
                            <TableRowColumn>{val.subject['dename']}</TableRowColumn>
                            <TableRowColumn>
                            <IconButton key={index} onClick={() => this.btnShowOfferDetailClick(index)}>
                              <InfoIcon/>
                            </IconButton>
                            </TableRowColumn>
                          </TableRow>
                        )
                        })
                      }

                  </TableBody>
                </Table>
                   </div>
               </InfiniteScroll>
          </div>
        <div>
          { this.state.showNewOfferDialog ? <NewOfferDialog showSnackbar={this.showSnackbar} onNewOfferDialogClose={this.handleNewOfferDialogClose}/> : ''}
          { this.state.showOfferDetailDialog ? <OfferDetailDialog showSnackbar={this.showSnackbar} onOfferDetailDialogClose={this.handleOnOfferDetailDialogClose} activeOffer={this.state.activeOffer}/> : ''}
        </div>
        <Snackbar
          open={this.state.messageSnackbarState}
          message={this.state.messageSnackbarMessage}
          autoHideDuration={4000}
          onRequestClose={this.handleRegisterSnackbarClose}
        />
      </div>
    );
  }
}
