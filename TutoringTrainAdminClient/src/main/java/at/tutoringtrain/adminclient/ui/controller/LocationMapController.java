package at.tutoringtrain.adminclient.ui.controller;

import at.tutoringtrain.adminclient.data.entry.Entry;
import at.tutoringtrain.adminclient.main.ApplicationManager;
import at.tutoringtrain.adminclient.main.MessageContainer;
import at.tutoringtrain.adminclient.ui.TutoringTrainWindow;
import at.tutoringtrain.adminclient.ui.WindowService;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXSnackbar;
import com.lynden.gmapsfx.GoogleMapView;
import com.lynden.gmapsfx.MapComponentInitializedListener;
import com.lynden.gmapsfx.javascript.object.GoogleMap;
import com.lynden.gmapsfx.javascript.object.InfoWindow;
import com.lynden.gmapsfx.javascript.object.InfoWindowOptions;
import com.lynden.gmapsfx.javascript.object.LatLong;
import com.lynden.gmapsfx.javascript.object.MapOptions;
import com.lynden.gmapsfx.javascript.object.MapTypeIdEnum;
import com.lynden.gmapsfx.javascript.object.Marker;
import com.lynden.gmapsfx.javascript.object.MarkerOptions;
import java.net.URL;
import java.util.ResourceBundle;
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
    private Entry entry;
    private GoogleMap map;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        logger = LogManager.getLogger(this);
        windowService = ApplicationManager.getWindowService();
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
        mapOptions.mapType(MapTypeIdEnum.ROADMAP);
        mapOptions.overviewMapControl(true);
        mapOptions.panControl(true);
        mapOptions.rotateControl(true);
        mapOptions.scaleControl(true);
        mapOptions.streetViewControl(false);
        mapOptions.zoomControl(true);
        mapOptions.zoom(13); 
        map = mapView.createMap(mapOptions);
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(location);
        Marker marker = new Marker(markerOptions);
        map.addMarker(marker);
        InfoWindowOptions infoWindowOptions = new InfoWindowOptions();
        infoWindowOptions.content("<h1>" + entry.getHeadline() + "</h1><p>" + entry.getUser().getUsername() + " (" + entry.getUser().getName() + ")</p>");
        InfoWindow infoWindow = new InfoWindow(infoWindowOptions);
        infoWindow.open(map, marker);
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
