package at.tutoringtrain.adminclient.ui.controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.Initializable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * FXML Controller class
 *
 * @author Marco Wilscher marco.wilscher@edu.htl-villach.at
 */
public class LoadingListItemController implements Initializable {
    private Logger logger;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        logger = LogManager.getLogger(this);
        logger.debug("LoadingListItemController initialized");        
    } 
}
