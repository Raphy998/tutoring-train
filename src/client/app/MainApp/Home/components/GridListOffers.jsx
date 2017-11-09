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
import NewOfferDialog from './NewOfferDialog';
import {Toolbar, ToolbarGroup, ToolbarSeparator, ToolbarTitle} from 'material-ui/Toolbar';
import SearchBar from 'material-ui-search-bar';
import Snackbar from 'material-ui/Snackbar';
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
  }

  componentWillMount = () => {
    this.fetchData();
  }

  //Handler for when the user wants to search the offers.
  onSearchBarSearch = () => {
    console.log('searchbar clicked');
  }

  //Event handler for when the create offer button is being clicked. -> Opening the NewOfferDialog
  btnCreateOfferClick = () => {
    this.setState({showNewOfferDialog: true});
  }

  //responsible for loading offers from the webservice.
  fetchData = () => {
    let itemsPerPage = 5;
    OfferService.getNewestOffers(localStorage.getItem('session-key'), this.state.startIndex, itemsPerPage)
    .then((ro) => {
      if(ro.code == 200) {
        let parsedJSON = JSON.parse(ro.message);
        if((parsedJSON && parsedJSON.length)) {
          for (var i=0; i < parsedJSON.length; i++) {
               this.state.offers.push(parsedJSON[i]);
           }
           this.setState({startIndex: this.state.startIndex + itemsPerPage});
        }
        else {
          this.setState({hasMoreItems: false});
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
      this.setState({messageSnackbarState: true, messageSnackbarMessage: message});
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
                   pageStart={0}
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
                 <Table>
                  <TableHeader>
                    <TableRow>
                      <TableHeaderColumn>date</TableHeaderColumn>
                      <TableHeaderColumn>description</TableHeaderColumn>
                      <TableHeaderColumn>username</TableHeaderColumn>
                      <TableHeaderColumn>subject</TableHeaderColumn>
                    </TableRow>
                  </TableHeader>
                  <TableBody>
                    {
                      this.state.offers.map((val, index) => {
                        return(<TableRow key={val + index}>
                          <TableRowColumn>{moment(val.postedon).format('DD/MM/YYYY')}</TableRowColumn>
                          <TableRowColumn>{val.description}</TableRowColumn>
                          <TableRowColumn>{val['user']['username']}</TableRowColumn>
                          <TableRowColumn>{val['subject']['enname']}</TableRowColumn>
                        </TableRow>)
                      })
                    }
                  </TableBody>
                </Table>
                   </div>
               </InfiniteScroll>
          </div>
        <div>
          { this.state.showNewOfferDialog ? <NewOfferDialog showSnackbar={this.showSnackbar} onNewOfferDialogClose={this.handleNewOfferDialogClose}/> : ''}
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
