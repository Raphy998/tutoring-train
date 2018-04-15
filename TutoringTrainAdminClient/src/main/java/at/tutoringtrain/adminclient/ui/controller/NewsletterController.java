/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.tutoringtrain.adminclient.ui.controller;

import at.tutoringtrain.adminclient.data.user.UserRole;
import at.tutoringtrain.adminclient.data.user.newsletter.Newsletter;
import at.tutoringtrain.adminclient.data.user.newsletter.NewsletterTemplateDefaultImages;
import at.tutoringtrain.adminclient.internationalization.LocalizedValueProvider;
import at.tutoringtrain.adminclient.io.network.Communicator;
import at.tutoringtrain.adminclient.io.network.RequestResult;
import at.tutoringtrain.adminclient.io.network.listener.user.newsletter.RequestNewsletterListener;
import at.tutoringtrain.adminclient.io.network.listener.user.newsletter.RequestNewsletterTemplateDefaultImagesListener;
import at.tutoringtrain.adminclient.io.network.listener.user.newsletter.RequestNewsletterTemplateListener;
import at.tutoringtrain.adminclient.main.ApplicationManager;
import at.tutoringtrain.adminclient.main.DefaultValueProvider;
import at.tutoringtrain.adminclient.main.MessageCodes;
import at.tutoringtrain.adminclient.main.MessageContainer;
import at.tutoringtrain.adminclient.ui.TutoringTrainWindow;
import at.tutoringtrain.adminclient.ui.WindowService;
import at.tutoringtrain.adminclient.ui.validators.TextFieldValidator;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXSnackbar;
import com.jfoenix.controls.JFXSpinner;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.TreeSet;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.web.WebView;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * FXML Controller class
 *
 * @author Marco Wilscher marco.wilscher@edu.htl-villach.at
 */
public class NewsletterController implements Initializable, TutoringTrainWindow, RequestNewsletterListener, RequestNewsletterTemplateListener, RequestNewsletterTemplateDefaultImagesListener {
    @FXML
    private AnchorPane pane;
    @FXML
    private Label lblTitle;
    @FXML
    private JFXTextField txtSubject;
    @FXML
    private JFXTextField txtTitle;
    @FXML
    private JFXTextArea txtTextTop;
    @FXML
    private JFXTextArea txtTextBottom;
    @FXML
    private JFXTextField txtImgTop;
    @FXML
    private JFXTextField txtImgBottom;
    @FXML
    private JFXCheckBox checkUser;
    @FXML
    private JFXCheckBox checkModerator;
    @FXML
    private JFXCheckBox checkAdministrator;
    @FXML
    private WebView webView;
    @FXML
    private JFXButton btnPreview;
    @FXML
    private JFXButton btnSend;
    @FXML
    private JFXButton btnClose;
    @FXML
    private JFXSpinner spinner;

    private JFXSnackbar snackbar;
    
    private DefaultValueProvider defaultValueProvider;
    private Logger logger; 
    private WindowService windowService;
    private Communicator communicator;
    private LocalizedValueProvider localizedValueProvider;
    
    private TextFieldValidator validatorSubjectField;
    private TextFieldValidator validatorTitleField;
    private TextFieldValidator validatorTxtTopField;
    private TextFieldValidator validatorTxtBottomField;
    private TextFieldValidator validatorImgTopField;
    private TextFieldValidator validatorImgBottomField;
    
    private String htmlTemplate;
    private NewsletterTemplateDefaultImages defaultImages;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        logger = LogManager.getLogger(this);
        windowService = ApplicationManager.getWindowService();
        defaultValueProvider = ApplicationManager.getDefaultValueProvider();
        localizedValueProvider = ApplicationManager.getLocalizedValueProvider();
        communicator = ApplicationManager.getCommunicator();
        initializeControls();
        initializeControlValidators();
        
        try {
            loadHtmlTemplate();
            loadDefaultImages();
        } catch (Exception ex) {
            closeWindow();
        }
        
        logger.debug("NewsletterController initialized"); 
    }    
    
    private void initializeControls() {
        snackbar = new JFXSnackbar(pane);
        spinner.setVisible(false); 
    }
    
     private void initializeControlValidators() {
        validatorSubjectField = new TextFieldValidator(defaultValueProvider.getDefaultValidationPattern("subject"));
        validatorTitleField = new TextFieldValidator(defaultValueProvider.getDefaultValidationPattern("title"));
        validatorTxtTopField = new TextFieldValidator(defaultValueProvider.getDefaultValidationPattern("txt"));
        validatorTxtBottomField = new TextFieldValidator(defaultValueProvider.getDefaultValidationPattern("txt"));
        validatorImgTopField = new TextFieldValidator(defaultValueProvider.getDefaultValidationPattern("img"));
        validatorImgBottomField = new TextFieldValidator(defaultValueProvider.getDefaultValidationPattern("img"));
        
        txtSubject.getValidators().add(validatorSubjectField);
        txtTitle.getValidators().add(validatorTitleField);
        txtTextTop.getValidators().add(validatorTxtTopField);
        txtTextBottom.getValidators().add(validatorTxtBottomField);
        txtImgTop.getValidators().add(validatorImgTopField);
        txtImgBottom.getValidators().add(validatorImgBottomField);
    }
     
    private void loadHtmlTemplate() throws Exception {
        communicator.requestNewsletterTemplate(this);
    }
    
    private void loadDefaultImages() throws Exception {
        communicator.requestNewsletterTemplateDefaultImages(this);
    }
    
    private boolean validateInputControls() {
        boolean isValid;
        txtSubject.validate();
        txtTitle.validate();
        txtTextTop.validate();
        txtTextBottom.validate();   
        //txtImgTop.validate();   
        //txtImgBottom.validate();   
        
        isValid = !(validatorSubjectField.getHasErrors() || validatorTitleField.getHasErrors() || validatorTxtTopField.getHasErrors() || validatorTxtBottomField.getHasErrors() || validatorImgTopField.getHasErrors() || validatorImgBottomField.getHasErrors());
        return isValid;
    }
    
    private void disableControls(boolean disable) {
        Platform.runLater(() -> {
            txtTitle.setDisable(disable);
            txtSubject.setDisable(disable);
            txtTextTop.setDisable(disable);
            txtTextBottom.setDisable(disable);
            txtImgTop.setDisable(disable);    
            txtImgBottom.setDisable(disable);
            checkUser.setDisable(disable);
            checkModerator.setDisable(disable);
            checkAdministrator.setDisable(disable);
            webView.setDisable(disable);
            btnClose.setDisable(disable);       
            btnPreview.setDisable(disable);       
            btnSend.setDisable(disable);       
            spinner.setVisible(disable);
        });
    }
    
    public TreeSet<Character> getTargetGroup() {
        TreeSet<Character> targets = new TreeSet<>();
        if (checkUser.isSelected()) {
            targets.add(UserRole.USER.getValue());
        }
        if (checkModerator.isSelected()) {
            targets.add(UserRole.MODERATOR.getValue());
        }
        if (checkAdministrator.isSelected()) {
            targets.add(UserRole.ADMIN.getValue());
        }
        return targets;
    }
    
    public String getSubject() {
        return txtSubject.getText();
    }
     
    public String getTitle() {
        return txtTitle.getText();
    }
    
    public String getTextTop() {
        return txtTextTop.getText();
    }
    
    public String getTextBottom() {
        return txtTextBottom.getText();
    }
      
    public String getImgTop() {
        return txtImgTop.getText();
    }

    public String getImgBottom() {
        return txtImgBottom.getText();
    }    
    
    @FXML
    void onBtnSend(ActionEvent event) {
        try {
            if (validateInputControls()) {
                disableControls(true);
                Newsletter newsletter = new Newsletter(getSubject(), getTitle(), (StringUtils.isBlank(getTextTop()) ? null : getTextTop()), (StringUtils.isBlank(getTextBottom()) ? null : getTextBottom()), (StringUtils.isBlank(getImgTop()) ? null : getImgTop()), (StringUtils.isBlank(getImgBottom()) ? null : getImgBottom()), getTargetGroup());   
                communicator.requestNewsletter(this, newsletter);         
            }    
        } catch (Exception ex) {
            disableControls(false);
            displayMessage(new MessageContainer(MessageCodes.EXCEPTION, localizedValueProvider.getString("messageUnexpectedFailure")));
            logger.error("onBtnRegister", ex);
        }
    }
    
    @FXML
    void onBtnPreview(ActionEvent event) {
        try {
            if (validateInputControls()) {
                Newsletter newsletter = new Newsletter(getSubject(), getTitle(), (StringUtils.isBlank(getTextTop()) ? null : getTextTop()), (StringUtils.isBlank(getTextBottom()) ? null : getTextBottom()), (StringUtils.isBlank(getImgTop()) ? null : getImgTop()), (StringUtils.isBlank(getImgBottom()) ? null : getImgBottom()), (getTargetGroup().isEmpty() ? null : getTargetGroup()));                
                if (StringUtils.isBlank(htmlTemplate)) {
                    displayMessage(new MessageContainer(MessageCodes.INFO, localizedValueProvider.getString("messageHtmlTemplateMissing")));
                } else {
                    String preview = htmlTemplate.replace("{title}", newsletter.getTitle()).replace("{text_top}", newsletter.getText_top()).replace("{text_bottom}", newsletter.getText_bottom());               
                    if (!StringUtils.isBlank(newsletter.getUrl_img_top())) {
                        preview = preview.replace("{url_img_top}", newsletter.getUrl_img_top());
                    } else {
                        if (defaultImages != null && !StringUtils.isBlank(defaultImages.getDefault_url_img_top())) {
                            preview = preview.replace("{url_img_top}", defaultImages.getDefault_url_img_top());
                        }
                    }                    
                    if (!StringUtils.isBlank(newsletter.getUrl_img_bottom())) {
                        preview = preview.replace("{url_img_bottom}", newsletter.getUrl_img_bottom());
                    } else {
                        if (defaultImages != null && !StringUtils.isBlank(defaultImages.getDefault_url_img_bottom())) {
                            preview = preview.replace("{url_img_bottom}", defaultImages.getDefault_url_img_bottom());
                        } 
                    }  
                    webView.getEngine().loadContent(preview);
                }
            }    
        } catch (Exception ex) {
            displayMessage(new MessageContainer(MessageCodes.EXCEPTION, localizedValueProvider.getString("messageUnexpectedFailure")));
            logger.error("onBtnRegister", ex);
        }
    }
    
    @FXML
    void onBtnClose(ActionEvent event) {
        closeWindow();
    }
    
     @Override
    public void closeWindow() {
        windowService.closeWindow(pane);
    }

    @Override
    public void displayMessage(MessageContainer message) {
        windowService.displayMessage(snackbar, message);
    }
   
    @Override
    public void requestNewsletterFinished(RequestResult result) {
        disableControls(false);
        try {
            if (result.isSuccessful()) {
                displayMessage(new MessageContainer(MessageCodes.OK, localizedValueProvider.getString("messageNewsletterSuccessfullySend")));
            } else {
                displayMessage(result.getMessageContainer());
            }
        } catch (Exception ex) {
            displayMessage(new MessageContainer(MessageCodes.EXCEPTION, localizedValueProvider.getString("messageUnexpectedFailure")));
            logger.error("requestNewsletterFinished", ex);
        }
    }
    
    @Override
    public void requestNewsletterTemplateFinished(RequestResult result) {
        try {
            if (result.isSuccessful()) {
                htmlTemplate = result.getData();
            } else {
                displayMessage(result.getMessageContainer());
            }
        } catch (Exception ex) {
            displayMessage(new MessageContainer(MessageCodes.EXCEPTION, localizedValueProvider.getString("messageUnexpectedFailure")));
            logger.error("requestNewsletterTemplateFinished", ex);
        }
    }
    
    @Override
    public void requestNewsletterTemplateDefaultImagesFinished(RequestResult result) {
        try {
            if (result.isSuccessful()) {
                defaultImages = ApplicationManager.getDataMapper().toNewsletterTemplateDefaultImages(result.getData());
            } else {
                displayMessage(result.getMessageContainer());
            }
        } catch (Exception ex) {
            displayMessage(new MessageContainer(MessageCodes.EXCEPTION, localizedValueProvider.getString("messageUnexpectedFailure")));
            logger.error("requestNewsletterTemplateFinished", ex);
        }
    }
    
    @Override
    public void requestFailed(RequestResult result) {
        disableControls(false);
        ApplicationManager.getHostFallbackService().requestCheck();
        displayMessage(new MessageContainer(MessageCodes.REQUEST_FAILED, localizedValueProvider.getString("messageConnectionFailed")));
        logger.error(result.getMessageContainer().toString());
    }
}
