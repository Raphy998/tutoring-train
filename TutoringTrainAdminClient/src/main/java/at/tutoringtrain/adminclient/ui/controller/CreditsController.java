/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.tutoringtrain.adminclient.ui.controller;

import at.tutoringtrain.adminclient.main.ApplicationManager;
import at.tutoringtrain.adminclient.main.DefaultValueProvider;
import at.tutoringtrain.adminclient.ui.WindowService;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXSnackbar;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.web.WebView;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * FXML Controller class
 *
 * @author Marco Wilscher marco.wilscher@edu.htl-villach.at
 */
public class CreditsController implements Initializable {
    @FXML
    private AnchorPane pane;
    @FXML
    private Label lblTitle;
    @FXML
    private JFXButton btnClose;
    @FXML
    private WebView webView;
    
    private JFXSnackbar snackbar;
    
    private DefaultValueProvider defaultValueProvider;
    private Logger logger; 
    private WindowService windowService;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        logger = LogManager.getLogger(this);
        windowService = ApplicationManager.getWindowService();
        defaultValueProvider = ApplicationManager.getDefaultValueProvider();
        initializeControls();
        logger.debug("CreditsController initialized"); 
    }    
    
    private void initializeControls() {
        snackbar = new JFXSnackbar(pane);
        webView.getEngine().load(getClass().getResource("/docs/credits.html").toExternalForm());
    }
    
    @FXML
    void onBtnClose(ActionEvent event) {
        windowService.closeWindow(pane);
    }
}
