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
import at.tutoringtrain.adminclient.ui.validators.IpFieldValidator;
import at.tutoringtrain.adminclient.ui.validators.TextFieldValidator;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXSnackbar;
import com.jfoenix.controls.JFXTextField;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
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
public class SettingsController implements Initializable, TutoringTrainWindow {
    @FXML
    private AnchorPane pane;
    @FXML
    private JFXComboBox<Language> comboLanguage;
    @FXML
    private JFXTextField txtServerIp;
    @FXML
    private JFXTextField txtServerPort;
    @FXML
    private JFXButton btnSave;
    @FXML
    private JFXButton btnClose;
    
    private JFXSnackbar snackbar;
    
    private LocalizedValueProvider localizedValueProvider;
    private DefaultValueProvider defaultValueProvider;
    private Logger logger; 
    private WindowService windowService;
    private ApplicationManager applicationManager;
    
    private IpFieldValidator validatorServerIPField;
    private TextFieldValidator validatorServerPortField;
    
    private boolean writeImmediately;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        logger = LogManager.getLogger(this);
        applicationManager = ApplicationManager.getInstance();
        windowService = ApplicationManager.getWindowService();
        localizedValueProvider = ApplicationManager.getLocalizedValueProvider();
        defaultValueProvider = ApplicationManager.getDefaultValueProvider();
        initializeControls();
        initializeControlValidators();
        logger.debug("SettingsController initialized"); 
    } 
    
    private void initializeControls() {
        snackbar = new JFXSnackbar(pane);
        comboLanguage.setItems(FXCollections.observableArrayList(Language.values()));
        loadApplicationConfiguration();
    }

    private void initializeControlValidators() {
        validatorServerIPField = new IpFieldValidator(defaultValueProvider.getDefaultValidationPattern("serverip"));  
        validatorServerPortField = new TextFieldValidator(defaultValueProvider.getDefaultValidationPattern("serverport"));
        txtServerIp.getValidators().add(validatorServerIPField);
        txtServerPort.getValidators().add(validatorServerPortField);
    }
    
    private void loadApplicationConfiguration() {
        txtServerIp.setText(applicationManager.getApplicationConfiguration().getWebserviceHostInfo().getHost());
        txtServerPort.setText(Integer.toString(applicationManager.getApplicationConfiguration().getWebserviceHostInfo().getPort()));
        comboLanguage.getSelectionModel().select(applicationManager.getApplicationConfiguration().getLanguage());
    }

    private boolean validateInputControls() {
        boolean isValid;
        txtServerIp.validate();
        txtServerPort.validate();
        isValid = !(validatorServerIPField.getHasErrors() || validatorServerPortField.getHasErrors());
        return isValid;
    }
    
    @Override
    public void displayMessage(MessageContainer container) {
        logger.debug(container.toString());
        windowService.displayMessage(snackbar, container);
    }
    
    @Override
    public void closeWindow() {
        windowService.closeWindow(pane);
    } 
    
    private Language getLanguage() {
        return comboLanguage.getSelectionModel().getSelectedItem();
    }
    
    private String getServerIp() {
        return txtServerIp.getText();
    }
    
    private int getServerPort() {
        return Integer.parseInt(txtServerPort.getText());
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
        if (validateInputControls()) {
            try {
                ApplicationConfiguration temporaryApplicationConfiguration = new ApplicationConfiguration();
                temporaryApplicationConfiguration.setLanguage(getLanguage());
                temporaryApplicationConfiguration.setWebserviceHostInfo(new WebserviceHostInfo(getServerIp(), getServerPort()));          
                if (writeImmediately) {
                    applicationManager.getApplicationConfiguration().apply(temporaryApplicationConfiguration);
                } else {
                    applicationManager.setTemporaryApplicationConfiguration(temporaryApplicationConfiguration);
                    applicationManager.writeTemporaryConfigFile();
                }
                displayMessage(new MessageContainer(MessageCodes.OK, localizedValueProvider.getString(writeImmediately ? "messageSettingsSavedWI" : "messageSettingsSaved")));
            } catch (IOException ioex) {
                logger.error("onBtnUpdate: excpetion occurred", ioex);
            }
        }
    }
    
    @FXML
    void onBtnClose(ActionEvent event) {
        closeWindow();
    }
}
