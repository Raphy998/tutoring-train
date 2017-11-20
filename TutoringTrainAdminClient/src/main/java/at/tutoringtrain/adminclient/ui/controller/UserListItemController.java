package at.tutoringtrain.adminclient.ui.controller;

import at.tutoringtrain.adminclient.data.User;
import at.tutoringtrain.adminclient.internationalization.LocalizedValueProvider;
import at.tutoringtrain.adminclient.io.network.Communicator;
import at.tutoringtrain.adminclient.io.network.RequestResult;
import at.tutoringtrain.adminclient.io.network.listener.user.RequestGetAvatarListener;
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
import javafx.scene.layout.VBox;
import javax.imageio.ImageIO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * FXML Controller class
 *
 * @author Marco Wilscher marco.wilscher@edu.htl-villach.at
 */
public class UserListItemController implements Initializable, UserDataChangedListner, UserAvatarChangedListner, UserBlockListner, RequestGetAvatarListener {
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
    private ImageView ivAvatar;
    @FXML
    private JFXSpinner spinnerAvatar;
    @FXML
    private JFXButton btnEdit;
    
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
    
    @FXML
    void onBtnEdit(ActionEvent event) {
        try {
            windowService.openUpdateUserWindow(user, this, this, this);
        } catch (Exception e) {
            displayMessage(new MessageContainer(MessageCodes.SEE_APPLICATION_LOG, localizedValueProvider.getString("messageSeeLogForFurtherInformation")));
            logger.error("onBtnEdit: exception occurred", e);
        }
    }

    public void setMessageListener(MessageListener messageListener) {
        this.messageListener = messageListener;
    }
    
    private void displayMessage(MessageContainer container) {
        if (messageListener != null) {
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
            lblUsername.setText("@" + user.getUsername());
            lblName.setText(user.getName() + (user.isCurrentUser() ? (" (" + localizedValueProvider.getString("myAccount") + ")") : ""));
            lblEducation.setText(user.getEducation());
            lblGender.setText(dataStorage.getGender(user.getGender()).getName());
            lblEmail.setText(user.getEmail());
            
            displayAvatar();
            displayBlocked();
        } else {
            lblUsername.setText("@NULL");
            lblName.setText("NULL");
            lblEducation.setText("NULL");
            lblGender.setText("NULL");
            lblEmail.setText("NULL");
            setDefualtAvatar();
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
        if (user.getBlock() != null) {
            boxBlocked.setVisible(true);
            lblReason.setText(user.getBlock().getReason());
            lblDue.setText(new SimpleDateFormat("dd.MM.yyyy hh:mm").format(user.getBlock().getDuedate()));
        } else {
            boxBlocked.setVisible(false);
        }
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
        if (this.user.isCurrentUser()) {
            setUser(ApplicationManager.getInstance().getCurrentUser());
        } else {
            setUser(user);
        }
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
    public void requestFailed(RequestResult result) {
        displayMessage(new MessageContainer(MessageCodes.REQUEST_FAILED, localizedValueProvider.getString("messageUnexpectedFailure")));
        logger.error("Request failed with status code:" + result.getStatusCode());
        logger.error(result.getMessageContainer().toString());
    }
}
