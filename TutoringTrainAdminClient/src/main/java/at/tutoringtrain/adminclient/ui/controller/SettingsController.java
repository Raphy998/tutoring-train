package at.tutoringtrain.adminclient.ui.controller;

import at.tutoringtrain.adminclient.internationalization.Language;
import at.tutoringtrain.adminclient.internationalization.LocalizedValueProvider;
import at.tutoringtrain.adminclient.main.ApplicationConfiguration;
import at.tutoringtrain.adminclient.main.ApplicationManager;
import at.tutoringtrain.adminclient.main.DefaultValueProvider;
import at.tutoringtrain.adminclient.main.MessageCodes;
import at.tutoringtrain.adminclient.main.MessageContainer;
import at.tutoringtrain.adminclient.main.WebserviceHostInfo;
import at.tutoringtrain.adminclient.ui.TutoringTrainWindow;
import at.tutoringtrain.adminclient.ui.WindowService;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXSnackbar;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * FXML Controller class
 *
 * @author Marco Wilscher marco.wilscher@edu.htl-villach.at
 */
public class SettingsController implements Initializable, TutoringTrainWindow {
    @FXML
    private AnchorPane pane;
    @FXML
    private JFXComboBox<Language> comboLanguage;
    @FXML
    private JFXButton btnSave;
    @FXML
    private JFXButton btnClose;
    @FXML
    private JFXButton btnCredits;
    @FXML
    private ListView<String> lvWebserviceHosts;
    
    private JFXSnackbar snackbar;
    
    private LocalizedValueProvider localizedValueProvider;
    private DefaultValueProvider defaultValueProvider;
    private Logger logger; 
    private WindowService windowService;
    private ApplicationManager applicationManager;
    
    
    
    private boolean writeImmediately;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        logger = LogManager.getLogger(this);
        applicationManager = ApplicationManager.getInstance();
        windowService = ApplicationManager.getWindowService();
        localizedValueProvider = ApplicationManager.getLocalizedValueProvider();
        defaultValueProvider = ApplicationManager.getDefaultValueProvider();
        initializeControls();
        logger.debug("SettingsController initialized"); 
    } 
    
    private void initializeControls() {
        snackbar = new JFXSnackbar(pane);
        comboLanguage.setItems(FXCollections.observableArrayList(Language.values()));
        loadApplicationConfiguration();
    }

    
    private void loadApplicationConfiguration() {
        lvWebserviceHosts.getItems().clear();
        lvWebserviceHosts.getItems().add(applicationManager.getApplicationConfiguration().getWebserviceHostInfo().toString());   
        for (WebserviceHostInfo fbHost : applicationManager.getApplicationConfiguration().getWebserviceFallbackHostsInfo()) {
            lvWebserviceHosts.getItems().add(fbHost.toString() + " (FALLBACK)");
        }
        comboLanguage.getSelectionModel().select(applicationManager.getApplicationConfiguration().getLanguage());
    }
    
    @Override
    public void displayMessage(MessageContainer container) {
        windowService.displayMessage(snackbar, container);
    }
    
    @Override
    public void closeWindow() {
        windowService.closeWindow(pane);
    } 
    
    private Language getLanguage() {
        return comboLanguage.getSelectionModel().getSelectedItem();
    }

    public void setWriteImmediately(boolean writeImmediately) {
        this.writeImmediately = writeImmediately;
        comboLanguage.setDisable(writeImmediately);
    }

    public boolean isWriteImmediately() {
        return writeImmediately;
    }
    
    @FXML
    void onBtnSave(ActionEvent event) {
        try {
            ApplicationConfiguration temporaryApplicationConfiguration = new ApplicationConfiguration();
            temporaryApplicationConfiguration.setLanguage(getLanguage());      
            if (writeImmediately) {
                applicationManager.getApplicationConfiguration().apply(temporaryApplicationConfiguration);
            } else {
                applicationManager.setTemporaryApplicationConfiguration(temporaryApplicationConfiguration);
                applicationManager.writeTemporaryConfigFile();
            }
            displayMessage(new MessageContainer(MessageCodes.OK, localizedValueProvider.getString(writeImmediately ? "messageSettingsSavedWI" : "messageSettingsSaved")));
        } catch (IOException ioex) {
            logger.error(ioex);
            displayMessage(new MessageContainer(MessageCodes.EXCEPTION, localizedValueProvider.getString("messageUnexpectedFailure")));
        }
    }
    
    @FXML
    void onBtnCredits(ActionEvent event) {
        try {
            windowService.openCreditsWindow();
        } catch (Exception ex) {
            logger.error(ex);
            displayMessage(new MessageContainer(MessageCodes.EXCEPTION, localizedValueProvider.getString("messageUnexpectedFailure")));
        }
    }
    
    @FXML
    void onBtnClose(ActionEvent event) {
        closeWindow();
    }
}
