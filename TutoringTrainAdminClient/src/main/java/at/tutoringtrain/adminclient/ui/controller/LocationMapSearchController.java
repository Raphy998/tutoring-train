package at.tutoringtrain.adminclient.ui.controller;

import at.tutoringtrain.adminclient.data.entry.Entry;
import at.tutoringtrain.adminclient.data.entry.EntryType;
import at.tutoringtrain.adminclient.data.entry.Location;
import at.tutoringtrain.adminclient.data.entry.Offer;
import at.tutoringtrain.adminclient.data.entry.Request;
import at.tutoringtrain.adminclient.data.mapper.DataMapper;
import at.tutoringtrain.adminclient.data.mapper.DataMappingViews;
import at.tutoringtrain.adminclient.internationalization.LocalizedValueProvider;
import at.tutoringtrain.adminclient.io.network.Communicator;
import at.tutoringtrain.adminclient.io.network.RequestResult;
import at.tutoringtrain.adminclient.io.network.listener.entry.offer.RequestOfferSearchLocationListener;
import at.tutoringtrain.adminclient.io.network.listener.entry.request.RequestRequestSearchLocationListener;
import at.tutoringtrain.adminclient.main.ApplicationManager;
import at.tutoringtrain.adminclient.main.MessageCodes;
import at.tutoringtrain.adminclient.main.MessageContainer;
import at.tutoringtrain.adminclient.map.EntryMarker;
import at.tutoringtrain.adminclient.ui.TutoringTrainWindow;
import at.tutoringtrain.adminclient.ui.WindowService;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXSlider;
import com.jfoenix.controls.JFXSnackbar;
import com.jfoenix.controls.JFXSpinner;
import com.lynden.gmapsfx.GoogleMapView;
import com.lynden.gmapsfx.MapComponentInitializedListener;
import com.lynden.gmapsfx.javascript.event.UIEventType;
import com.lynden.gmapsfx.javascript.object.Animation;
import com.lynden.gmapsfx.javascript.object.GoogleMap;
import com.lynden.gmapsfx.javascript.object.InfoWindow;
import com.lynden.gmapsfx.javascript.object.InfoWindowOptions;
import com.lynden.gmapsfx.javascript.object.LatLong;
import com.lynden.gmapsfx.javascript.object.MapOptions;
import com.lynden.gmapsfx.javascript.object.MapTypeIdEnum;
import com.lynden.gmapsfx.javascript.object.MarkerOptions;
import com.lynden.gmapsfx.shapes.Circle;
import com.lynden.gmapsfx.shapes.CircleOptions;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * FXML Controller class
 *
 * @author Marco Wilscher marco.wilscher@edu.htl-villach.at
 */
public class LocationMapSearchController implements Initializable, TutoringTrainWindow, MapComponentInitializedListener, RequestOfferSearchLocationListener, RequestRequestSearchLocationListener {
    @FXML
    private AnchorPane pane;
    @FXML
    private GoogleMapView mapView;
    @FXML
    private JFXButton btnClose;
    @FXML
    private JFXSlider sliderRadius;
    @FXML
    private JFXButton btnReset;
    @FXML
    private JFXButton btnSearch;
    @FXML
    private JFXSpinner spinner;
    @FXML
    private JFXComboBox<EntryType> comboType;
    
    private LocalizedValueProvider localizedValueProvider; 
    private Communicator communicator;
    private DataMapper dataMapper;
    private JFXSnackbar snackbar;    
    private Logger logger; 
    private WindowService windowService;
    private GoogleMap map;
    
    private Circle circle;
    private ArrayList<Entry> entries;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        logger = LogManager.getLogger(this);
        windowService = ApplicationManager.getWindowService();
        communicator = ApplicationManager.getCommunicator();
        dataMapper = ApplicationManager.getDataMapper();
        localizedValueProvider = ApplicationManager.getLocalizedValueProvider();
        entries = new ArrayList<>();
        initializeControls();
        logger.debug("LocationMapSearchController initialized");  
    } 
    
    private void initializeControls() {
        snackbar = new JFXSnackbar(pane);
        spinner.setVisible(false);
        mapView.addMapInializedListener(this);
        comboType.getItems().addAll(EntryType.values());
        comboType.getSelectionModel().select(EntryType.OFFER);
    }

    private void disableControls(boolean disable) {
        Platform.runLater(() -> {
            btnReset.setDisable(disable);
            btnSearch.setDisable(disable);  
            btnClose.setDisable(disable);
            comboType.setDisable(disable);
            sliderRadius.setDisable(disable);
            mapView.setOpacity(disable ? 0.5 : 1);
            spinner.setVisible(disable);
        });
    }
    
    @Override
    public void mapInitialized() {
        MapOptions mapOptions = new MapOptions();
        mapOptions.center(new LatLong(46.61028, 13.85583));
        mapOptions.mapType(MapTypeIdEnum.TERRAIN);
        mapOptions.overviewMapControl(true);
        mapOptions.panControl(true);
        mapOptions.rotateControl(true);
        mapOptions.scaleControl(true);
        mapOptions.streetViewControl(true);
        mapOptions.zoomControl(true);
        mapOptions.zoom(8); 
        map = mapView.createMap(mapOptions);      
        map.addMouseEventHandler(UIEventType.click, (arg) -> {
            if (circle != null) {
                map.removeMapShape(circle);
            }
            circle = generateCircle(arg.getLatLong(), sliderRadius.getValue() * 1000);
            map.addMapShape(circle);
            map.addUIEventHandler(circle, UIEventType.rightclick, (arg0) -> {
                map.clearMarkers();
                map.removeMapShape(circle);
                circle = null;
            });
        });
        sliderRadius.valueProperty().addListener((arg) -> {
            if (sliderRadius.getValue() < 1) {
                sliderRadius.setValue(1);
            }
            if (circle != null) {
                circle.setRadius(sliderRadius.getValue() * 1000);
            } 
        });
    }
    
    private EntryMarker generateMarker(LatLong latlong, Entry entry) {
        MarkerOptions mo = new MarkerOptions();
        mo.position(latlong);
        return new EntryMarker(mo, entry);
    }
    
    private Circle generateCircle(LatLong center, double radius) {
        CircleOptions co = new CircleOptions();
        co.fillOpacity(0.2);
        co.strokeColor("#084C61");
        co.center(center);
        co.radius(radius);
        return new Circle(co);
    }
    
    private void generateInfoWindow(EntryMarker entryMarker) {
        InfoWindowOptions infoWindowOptions = new InfoWindowOptions();
        infoWindowOptions.content("<h1>" + entryMarker.getEntry().getHeadline() + "</h1><h2>" + entryMarker.getEntry().getSubject().getName() + "</h2><p>" + entryMarker.getEntry().getUser().getUsername() + " (" + entryMarker.getEntry().getUser().getName() + ")</p>");
        InfoWindow infoWindow = new InfoWindow(infoWindowOptions);
        infoWindow.open(map, entryMarker);
    }
    
    @FXML
    void onBtnReset(ActionEvent event) {
        if (circle != null) {
            map.removeMapShape(circle);
            circle = null;
        }
        map.clearMarkers();
        sliderRadius.setValue(1);
    }

    @FXML
    void onBtnSearch(ActionEvent event) {
        try {            
            if (circle != null) {
                disableControls(true);
                Location loc = new Location();
                loc.setLat(circle.getCenter().getLatitude());
                loc.setLon(circle.getCenter().getLongitude());
                
                if (comboType.getSelectionModel().getSelectedItem() == EntryType.OFFER) {
                    if (!communicator.requestOfferSearchLocation(this, loc, sliderRadius.getValue() * 1000)) {
                        disableControls(false);
                        displayMessage(new MessageContainer(MessageCodes.EXCEPTION, localizedValueProvider.getString("messageReauthentication")));  
                    }
                } else {
                    if (!communicator.requestRequestSearchLocation(this, loc, sliderRadius.getValue() * 1000)) {
                        disableControls(false);
                        displayMessage(new MessageContainer(MessageCodes.EXCEPTION, localizedValueProvider.getString("messageReauthentication")));  
                    }
                }
            } else {
                displayMessage(new MessageContainer(MessageCodes.INFO, localizedValueProvider.getString("messageSelectLocation")));
            }
        } catch (Exception e) {
            logger.error("onBtnSearch: exception occurred", e);
        }
    }
    
    @FXML
    void onBtnClose(ActionEvent event) {
        closeWindow();
    }
    
    @Override
    public void displayMessage(MessageContainer container) {
        windowService.displayMessage(snackbar, container);
    }
    
    @Override
    public void closeWindow() {
        windowService.closeWindow(pane);
    }

    @Override
    public void requestFailed(RequestResult result) {
        disableControls(false);
        ApplicationManager.getHostFallbackService().requestCheck();
        displayMessage(new MessageContainer(MessageCodes.REQUEST_FAILED, localizedValueProvider.getString("messageConnectionFailed")));
        logger.error(result.getMessageContainer().toString());
    }

    @Override
    public void requestOfferSearchLocationFinished(RequestResult result) {
        disableControls(false);
        if (result.isSuccessful()) {
            Platform.runLater(() -> {
                try {
                    map.clearMarkers();
                    entries.clear();
                    entries.addAll(dataMapper.toOfferArrayList(result.getData(), DataMappingViews.Entry.In.Get.class));  
                    entries.forEach((entry) -> {                        
                        Platform.runLater(() -> {
                            EntryMarker marker = generateMarker(new LatLong(entry.getLocation().getLat(), entry.getLocation().getLon()), entry);
                            marker.setAnimation(Animation.BOUNCE);
                            map.addMarker(marker);
                            map.addUIEventHandler(marker, UIEventType.click, (arg) -> {
                                generateInfoWindow(marker);
                            });      
                            map.addUIEventHandler(marker, UIEventType.dblclick, (arg) -> {
                                try {
                                    windowService.openUpdateOfferWindow((Offer) marker.getEntry(), (offer) -> {
                                        marker.setEntry(offer);
                                    });
                                } catch (Exception ex) {
                                    logger.error(ex);
                                    displayMessage(new MessageContainer(MessageCodes.EXCEPTION, localizedValueProvider.getString("messageUnexpectedFailure")));
                                }             
                            });
                        });
                    });                   
                } catch (IOException ioex) {
                    disableControls(false);
                    logger.error("requestOfferSearchLocationFinished: loading offers failed", ioex);
                }
            });         
        } else {      
            if (result.getMessageContainer().getCode() == 3 || result.getMessageContainer().getCode() == 4) {
                displayMessage(new MessageContainer(MessageCodes.EXCEPTION, localizedValueProvider.getString("messageReauthentication")));
            } else {
                displayMessage(result.getMessageContainer());
            }
        }   
    }

    @Override
    public void requestRequestSearchLocationFinished(RequestResult result) {
        disableControls(false);
        if (result.isSuccessful()) {
            Platform.runLater(() -> {
                try {
                    map.clearMarkers();
                    entries.clear();
                    entries.addAll(dataMapper.toRequestArrayList(result.getData(), DataMappingViews.Entry.In.Get.class));  
                    entries.forEach((entry) -> {                        
                        Platform.runLater(() -> {
                            EntryMarker marker = generateMarker(new LatLong(entry.getLocation().getLat(), entry.getLocation().getLon()), entry);
                            marker.setAnimation(Animation.BOUNCE);
                            map.addMarker(marker);
                            map.addUIEventHandler(marker, UIEventType.click, (arg) -> {
                                generateInfoWindow(marker);
                            });      
                            map.addUIEventHandler(marker, UIEventType.dblclick, (arg) -> {
                                try {
                                    windowService.openUpdateRequestWindow((Request) marker.getEntry(), (request) -> {
                                        marker.setEntry(request);
                                    });
                                } catch (Exception ex) {
                                    logger.error(ex);
                                    displayMessage(new MessageContainer(MessageCodes.EXCEPTION, localizedValueProvider.getString("messageUnexpectedFailure")));
                                }             
                            });
                        });
                    });                   
                } catch (IOException ioex) {
                    disableControls(false);
                    logger.error("requestRequestSearchLocationFinished: loading offers failed", ioex);
                }
            });         
        } else {      
            if (result.getMessageContainer().getCode() == 3 || result.getMessageContainer().getCode() == 4) {
                displayMessage(new MessageContainer(MessageCodes.EXCEPTION, localizedValueProvider.getString("messageReauthentication")));
            } else {
                displayMessage(result.getMessageContainer());
            }
        }   
    }
}