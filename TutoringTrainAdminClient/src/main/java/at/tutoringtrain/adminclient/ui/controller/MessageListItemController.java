package at.tutoringtrain.adminclient.ui.controller;

import at.tutoringtrain.adminclient.internationalization.LocalizedValueProvider;
import at.tutoringtrain.adminclient.main.ApplicationManager;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * FXML Controller class
 *
 * @author Marco Wilscher marco.wilscher@edu.htl-villach.at
 */
public class MessageListItemController implements Initializable {
    @FXML
    private Label lblMessage;
    
    private Logger logger;
    private LocalizedValueProvider localizedValueProvider;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        logger = LogManager.getLogger(this);
        localizedValueProvider = ApplicationManager.getLocalizedValueProvider();
        initializeControls();
        logger.debug("UserListItemController initialized");        
    }    
    
    private void initializeControls() {
        lblMessage.setText("NULL");
    }
    
    public void setText(String message, boolean isMessageKey) {
        if (isMessageKey) {
            lblMessage.setText(localizedValueProvider.getString(message));
        } else {
            lblMessage.setText(message);
        }
    }
}
