package at.tutoringtrain.adminclient.ui.controller;

import at.tutoringtrain.adminclient.data.user.User;
import at.tutoringtrain.adminclient.data.user.UserRole;
import at.tutoringtrain.adminclient.internationalization.LocalizedValueProvider;
import at.tutoringtrain.adminclient.io.network.Communicator;
import at.tutoringtrain.adminclient.io.network.RequestResult;
import at.tutoringtrain.adminclient.io.network.listener.user.RequestGetAvatarListener;
import at.tutoringtrain.adminclient.io.network.listener.user.RequestResetPasswordListener;
import at.tutoringtrain.adminclient.io.network.listener.user.RequestUnblockUserListener;
import at.tutoringtrain.adminclient.main.ApplicationManager;
import at.tutoringtrain.adminclient.main.DataStorage;
import at.tutoringtrain.adminclient.main.DefaultValueProvider;
import at.tutoringtrain.adminclient.main.MessageCodes;
import at.tutoringtrain.adminclient.main.MessageContainer;
import at.tutoringtrain.adminclient.ui.WindowService;
import at.tutoringtrain.adminclient.ui.listener.MessageListener;
import at.tutoringtrain.adminclient.ui.listener.UserAvatarChangedListner;
import at.tutoringtrain.adminclient.ui.listener.UserBlockListner;
import at.tutoringtrain.adminclient.ui.listener.UserDataChangedListner;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXSpinner;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javax.imageio.ImageIO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * FXML Controller class
 *
 * @author Marco Wilscher marco.wilscher@edu.htl-villach.at
 */
public class UserListItemController implements Initializable, UserDataChangedListner, UserAvatarChangedListner, UserBlockListner, RequestGetAvatarListener, RequestUnblockUserListener, RequestResetPasswordListener {
    @FXML
    private AnchorPane pane;
    @FXML
    private ImageView ivAvatar;
    @FXML
    private JFXSpinner spinnerAvatar;
    @FXML
    private Label lblName;
    @FXML
    private Label lblUsername;
    @FXML
    private Label lblGender;
    @FXML
    private Label lblEducation;
    @FXML
    private Label lblEmail;
    @FXML
    private VBox boxBlocked;
    @FXML
    private Label lblBlocked;
    @FXML
    private Label lblReason;
    @FXML
    private Label lblDue;
    @FXML
    private JFXButton btnEdit;
    @FXML
    private JFXButton btnBlock;
    @FXML
    private JFXButton btnUnblock;
    @FXML
    private JFXButton btnDelete;
    @FXML
    private JFXButton btnResetPassword;
    @FXML
    private VBox boxUserDetails;
    @FXML
    private HBox boxAvatar;
    @FXML
    private VBox boxButtons;
 
    private Logger logger;
    private LocalizedValueProvider localizedValueProvider;
    private DefaultValueProvider defaultValueProvider;
    private WindowService windowService;
    private Communicator communicator;
    private DataStorage dataStorage;
    
    private MessageListener messageListener;
    private User user;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        logger = LogManager.getLogger(this);
        localizedValueProvider = ApplicationManager.getLocalizedValueProvider();
        defaultValueProvider = ApplicationManager.getDefaultValueProvider();
        dataStorage = ApplicationManager.getDataStorage();
        windowService = ApplicationManager.getWindowService();
        communicator = ApplicationManager.getCommunicator();
        initializeControls();
        logger.debug("UserListItemController initialized");        
    }    
    
    private void initializeControls() {
        spinnerAvatar.setVisible(false);
        boxBlocked.setVisible(false);
    }
    
    private void disableControls(boolean disable) {
        Platform.runLater(() -> {
            boxAvatar.setDisable(disable);
            boxUserDetails.setDisable(disable);
            boxButtons.setDisable(disable);    
        });
    }
    
    private void disableButtonControls(boolean disable) {
        Platform.runLater(() -> {
            boxButtons.setDisable(disable);  
        });
    }
    
    @FXML
    void onBtnEdit(ActionEvent event) {
        try {
            windowService.openUpdateUserWindow(user, this, this, this);
        } catch (Exception e) {
            displayMessage(new MessageContainer(MessageCodes.SEE_APPLICATION_LOG, localizedValueProvider.getString("messageSeeLogForFurtherInformation")));
            logger.error("onBtnEdit: exception occurred", e);
        }
    }

    @FXML
    void onBtnBlock(ActionEvent event) {
        try {
            windowService.openBlockUserWindow(this, user);
        } catch (Exception ex) {
            logger.error(ex);
            displayMessage(new MessageContainer(MessageCodes.EXCEPTION, localizedValueProvider.getString("messageUnexpectedFailure")));
        }
    }
    
    @FXML
    void onBtnUnblock(ActionEvent event) {
        try {
            btnUnblock.setDisable(true);
            if(!communicator.requestUnblockUser(this, user.getUsername())) {
                btnUnblock.setDisable(false);
                displayMessage(new MessageContainer(MessageCodes.EXCEPTION, localizedValueProvider.getString("messageReauthentication")));
            }
        } catch (Exception ex) {
            logger.error(ex);
            displayMessage(new MessageContainer(MessageCodes.EXCEPTION, localizedValueProvider.getString("messageUnexpectedFailure")));
        }
    }

    @FXML
    void onBtnDelete(ActionEvent event) {
        try {
            displayMessage(new MessageContainer(MessageCodes.NOT_IMPLEMENTED_YET, "NOT IMPLEMENTED YET"));
            //if (windowService.openConfirmDialog("dialogHeaderDeleteUser", "dialogContenDeleteUser", new StringPlaceholder("name", user.getName()), new StringPlaceholder("username", user.getUsername())).get() == ButtonType.OK) {
                //TODO     
            //}
        } catch (Exception ex) {
            logger.error(ex);
            displayMessage(new MessageContainer(MessageCodes.EXCEPTION, localizedValueProvider.getString("messageUnexpectedFailure")));
        }
    }

    @FXML
    void onBtnResetPassword(ActionEvent event) {
        try {
            btnResetPassword.setDisable(true);
            if(!communicator.requestResetPassword(this, user.getUsername())) {
                btnResetPassword.setDisable(false);
                displayMessage(new MessageContainer(MessageCodes.EXCEPTION, localizedValueProvider.getString("messageReauthentication")));
            }
        } catch (Exception ex) {
            btnResetPassword.setDisable(false);
            logger.error(ex);
            displayMessage(new MessageContainer(MessageCodes.EXCEPTION, localizedValueProvider.getString("messageUnexpectedFailure")));
        }
    }

    public void setMessageListener(MessageListener messageListener) {
        this.messageListener = messageListener;
    }
    
    private void displayMessage(MessageContainer container) {
        if (messageListener != null) {
           container.addPrefix("@" + user.getUsername() + ": ");
           messageListener.displayMessage(container);
        } else {
            logger.warn("no message listener specified");
        }
    }
    
    public void setUser(User user) {
        this.user = user;
        displayUser();
    }
    
    private void displayUser() {
        if (user != null) {
            lblUsername.setText("@" + user.getUsername() + " [" + UserRole.valueOf(user.getRole()) + "]");
            lblName.setText(user.getName() + (user.isCurrentUser() ? (" (" + localizedValueProvider.getString("myAccount") + ")") : ""));
            lblEducation.setText(user.getEducation());
            lblGender.setText(dataStorage.getGender(user.getGender()).getName());
            lblEmail.setText(user.getEmail());        
            displayAvatar();
            displayBlocked();
            disableButtonControls(false);
            if (UserRole.valueOf(user.getRole()) == UserRole.ROOT) {
                disableButtonControls(true);
            } else {
                if (user.isCurrentUser()) {
                    btnDelete.setDisable(true);
                }
                if (user.isCurrentUser() || UserRole.valueOf(user.getRole()) == UserRole.ADMIN) {
                    btnBlock.setDisable(true);
                    btnUnblock.setDisable(true);
                }
            }
        } else {
            lblUsername.setText("@NULL");
            lblName.setText("NULL");
            lblEducation.setText("NULL");
            lblGender.setText("NULL");
            lblEmail.setText("NULL");
            setDefualtAvatar();
            disableButtonControls(true);
        }
    }
    
    private void loadAvatarFromWebService() {
        try {
            spinnerAvatar.setVisible(true);
            communicator.requestUserAvatar(this, user.getUsername());
        } catch (Exception ex) {
            spinnerAvatar.setVisible(false);
            setDefualtAvatar();
            logger.error("loading avatar for " + user.getUsername() + " failed", ex);
        }
    }
    
    private void displayAvatar() {
        if (user.getAvatar() != null) {
            setAvatar(user.getAvatar());
        } else {
            loadAvatarFromWebService();  
        }
    }
    
    private void displayBlocked() {
        Platform.runLater(() -> {
        if (user.getBlock() != null) {
            boxBlocked.setVisible(true);
            lblReason.setText(user.getBlock().getReason());
            if(user.getBlock().getDuedate() == null) {
                lblDue.setText(localizedValueProvider.getString("UNLIMITED"));
            } else {
                lblDue.setText(new SimpleDateFormat("dd.MM.yyyy HH:mm").format(user.getBlock().getDuedate()));
            }
        } else {
            boxBlocked.setVisible(false);
        }});
    }
    
    private void setAvatar(BufferedImage avatar) {
        Platform.runLater(() -> {
            ivAvatar.setImage(SwingFXUtils.toFXImage(avatar, null));
        });
    }

    private void setDefualtAvatar() {
        setAvatar(defaultValueProvider.getDefaultAvatar());
    }
    
    @Override
    public void userAvatarChanged(User user) {
        if (user.getAvatar() != null) {
            setAvatar(user.getAvatar());
        } else {
            setDefualtAvatar();
        }
    }

    @Override
    public void userBlocked(User user) {
        displayBlocked();
    }

    @Override
    public void userUnblocked() {
        displayBlocked();
    }
    
    @Override
    public void userDataChanged(User user) {
        setUser(user);
    }

    @Override
    public void requestAvatarFinished(RequestResult result, InputStream iStream) {
        Platform.runLater(() -> spinnerAvatar.setVisible(false));
        try {
            if (result.isSuccessful()) {
                user.setAvatar(ImageIO.read(iStream));
                iStream.close();
                setAvatar(user.getAvatar());
            } else {
                setDefualtAvatar();
            }
        } catch (IOException ioex) {
            logger.error("requestAvatarFinished: loading avatar failed", ioex);
            setDefualtAvatar();
        }
    }

    @Override
    public void requestResetPasswordFinished(RequestResult result) {
        btnResetPassword.setDisable(false);
        if (result.isSuccessful()) {   
            displayMessage(new MessageContainer(MessageCodes.OK, localizedValueProvider.getString("messageUserPasswordReset")));
        } else {
            if (result.getMessageContainer().getCode() == 3 || result.getMessageContainer().getCode() == 4) {
                displayMessage(new MessageContainer(MessageCodes.EXCEPTION, localizedValueProvider.getString("messageReauthentication")));
            } else {
                displayMessage(result.getMessageContainer());
            }
        }
    }
    
    @Override
    public void requestUnblockUserFinished(RequestResult result) {
        btnUnblock.setDisable(false);
        if (result.isSuccessful()) {
            user.setBlock(null);
            userUnblocked();      
            displayMessage(new MessageContainer(MessageCodes.OK, localizedValueProvider.getString("messageUserUnblocked")));
        } else {
            if (result.getMessageContainer().getCode() == 3 || result.getMessageContainer().getCode() == 4) {
                displayMessage(new MessageContainer(MessageCodes.EXCEPTION, localizedValueProvider.getString("messageReauthentication")));
            } else {
                displayMessage(result.getMessageContainer());
            }
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
