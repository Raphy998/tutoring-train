package at.tutoringtrain.adminclient.ui.controller;

import at.tutoringtrain.adminclient.data.entry.Entry;
import at.tutoringtrain.adminclient.data.entry.Offer;
import at.tutoringtrain.adminclient.data.entry.Request;
import at.tutoringtrain.adminclient.internationalization.LocalizedValueProvider;
import at.tutoringtrain.adminclient.main.ApplicationManager;
import at.tutoringtrain.adminclient.main.MessageCodes;
import at.tutoringtrain.adminclient.main.MessageContainer;
import at.tutoringtrain.adminclient.map.EntryMarker;
import at.tutoringtrain.adminclient.ui.TutoringTrainWindow;
import at.tutoringtrain.adminclient.ui.WindowService;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXSnackbar;
import com.lynden.gmapsfx.GoogleMapView;
import com.lynden.gmapsfx.MapComponentInitializedListener;
import com.lynden.gmapsfx.javascript.event.UIEventType;
import com.lynden.gmapsfx.javascript.object.GoogleMap;
import com.lynden.gmapsfx.javascript.object.InfoWindow;
import com.lynden.gmapsfx.javascript.object.InfoWindowOptions;
import com.lynden.gmapsfx.javascript.object.LatLong;
import com.lynden.gmapsfx.javascript.object.MapOptions;
import com.lynden.gmapsfx.javascript.object.MapTypeIdEnum;
import com.lynden.gmapsfx.javascript.object.MarkerOptions;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * FXML Controller class
 *
 * @author Marco Wilscher marco.wilscher@edu.htl-villach.at
 */
public class LocationMapController implements Initializable, TutoringTrainWindow, MapComponentInitializedListener {
    @FXML
    private AnchorPane pane;
    @FXML
    private GoogleMapView mapView;
    @FXML
    private JFXButton btnClose;

    private JFXSnackbar snackbar;    
    private Logger logger; 
    private WindowService windowService;
    private LocalizedValueProvider localizedValueProvider;
    private Entry entry;
    private GoogleMap map;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        logger = LogManager.getLogger(this);
        windowService = ApplicationManager.getWindowService();
        localizedValueProvider = ApplicationManager.getLocalizedValueProvider();
        initializeControls();
        logger.debug("LocationMapController initialized");  
    } 
    
    private void initializeControls() {
        snackbar = new JFXSnackbar(pane);
    }

    @Override
    public void mapInitialized() {
        LatLong location = new LatLong(entry.getLocation().getLat(), entry.getLocation().getLon());
        MapOptions mapOptions = new MapOptions();
        mapOptions.center(location);
        mapOptions.mapType(MapTypeIdEnum.TERRAIN);
        mapOptions.overviewMapControl(true);
        mapOptions.panControl(true);
        mapOptions.rotateControl(true);
        mapOptions.scaleControl(true);
        mapOptions.streetViewControl(false);
        mapOptions.zoomControl(true);
        mapOptions.zoom(13); 
        map = mapView.createMap(mapOptions);
        EntryMarker marker = generateMarker(location, entry);
        map.addMarker(marker);
        generateInfoWindow(marker);
        map.addUIEventHandler(marker, UIEventType.click, (arg) -> {
            generateInfoWindow(marker);
        });      
        map.addUIEventHandler(marker, UIEventType.dblclick, (arg) -> {
            try {
                if (Offer.class.isInstance(entry)) {
                    windowService.openUpdateOfferWindow((Offer) marker.getEntry(), (offer) -> {
                        marker.setEntry(offer);
                    });
                } else {
                    windowService.openUpdateRequestWindow((Request) marker.getEntry(), (offer) -> {
                        marker.setEntry(offer);
                    });
                }        
            } catch (Exception ex) {
                logger.error(ex);
                displayMessage(new MessageContainer(MessageCodes.EXCEPTION, localizedValueProvider.getString("messageUnexpectedFailure")));
            }             
        });
    }
    
    private EntryMarker generateMarker(LatLong latlong, Entry entry) {
        MarkerOptions mo = new MarkerOptions();
        mo.position(latlong);
        return new EntryMarker(mo, entry);
    }
    
    private void generateInfoWindow(EntryMarker entryMarker) {
        InfoWindowOptions infoWindowOptions = new InfoWindowOptions();
        infoWindowOptions.content("<h1>" + entryMarker.getEntry().getHeadline() + "</h1><h2>" + entryMarker.getEntry().getSubject().getName() + "</h2><p>" + entryMarker.getEntry().getUser().getUsername() + (!StringUtils.isBlank(entryMarker.getEntry().getUser().getName()) ? " (" + entryMarker.getEntry().getUser().getName() + ")" : "") + "</p>");
        InfoWindow infoWindow = new InfoWindow(infoWindowOptions);
        infoWindow.open(map, entryMarker);
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
    
    public void setEntry(Entry entry) {
        this.entry = entry;
        mapView.addMapInializedListener(this);
    }
}
