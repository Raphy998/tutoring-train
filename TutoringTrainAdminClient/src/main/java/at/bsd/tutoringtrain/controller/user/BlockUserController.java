package at.bsd.tutoringtrain.controller.user;

import at.bsd.tutoringtrain.data.Database;
import at.bsd.tutoringtrain.data.entities.BlockRequest;
import at.bsd.tutoringtrain.data.entities.User;
import at.bsd.tutoringtrain.debugging.MessageLogger;
import at.bsd.tutoringtrain.io.network.Communicator;
import at.bsd.tutoringtrain.io.network.RequestResult;
import at.bsd.tutoringtrain.io.network.listener.user.RequestBlockUserListener;
import at.bsd.tutoringtrain.messages.MessageCode;
import at.bsd.tutoringtrain.messages.MessageContainer;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDatePicker;
import com.jfoenix.controls.JFXSnackbar;
import com.jfoenix.controls.JFXSpinner;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXTimePicker;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author Marco Wilscher marco.wilscher@edu.htl-villach.at
 */
public class BlockUserController implements Initializable, RequestBlockUserListener {
    @FXML
    private AnchorPane pane;
    @FXML
    private JFXTextField txtUsername;
    @FXML
    private JFXTextField txtName;
    @FXML
    private JFXTextArea txtReason;
    @FXML
    private JFXTimePicker pickerTime;
    @FXML
    private JFXDatePicker pickerDate;
    @FXML
    private JFXButton btnBlock;
    @FXML
    private JFXButton btnClose;
    @FXML
    private ImageView ivAvatar;
    @FXML
    private JFXSpinner spinner;

    private JFXSnackbar snackbar;    
    private Communicator communicator;
    private Database db;
    private MessageLogger logger; 
    private BufferedImage avatar;
    private User user;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        logger = MessageLogger.getINSTANCE();
        communicator = Communicator.getInstance();
        db = Database.getInstance();           
        snackbar = new JFXSnackbar(pane);     
        pickerTime.setIs24HourView(true);        
    } 
    
    @FXML
    void onBtnBlock(ActionEvent event) {
        try {
            communicator.requestBlockUser(this, new BlockRequest(getUsername(), getReason(), getDuedate()));
            
            
        } catch (Exception ex) {
            logger.exception(ex, getClass());
        }
    }

    @FXML
    void onBtnClose(ActionEvent event) {
        closeWindow();
    }

    private void closeWindow() {
        ((Stage)pane.getScene().getWindow()).close();
    }

    public void setUser(User user) {
        this.user = user;
        txtUsername.setText(user.getUsername());
        txtName.setText(user.getName());
        pickerDate.setValue(LocalDate.now().plusDays(1));
        pickerTime.setValue(LocalTime.now());
    }
    
    private String getUsername() {
        return user.getUsername();
   }

    private String getReason() {
        return txtReason.getText();
    }
    
    private ZonedDateTime getDuedate() {
        return ZonedDateTime.of(pickerDate.getValue(), pickerTime.getValue(), ZoneId.systemDefault());
    }
    
    private void displayMessage(MessageContainer container) {
        Platform.runLater(() -> snackbar.enqueue(new JFXSnackbar.SnackbarEvent(container.toString())));
    }
    
    private void lockUi(boolean lock) {
        Platform.runLater(() -> {
            txtUsername.setDisable(lock);
            txtName.setDisable(lock);
            txtReason.setDisable(lock);
            pickerDate.setDisable(lock);
            pickerTime.setDisable(lock);
            btnClose.setDisable(lock);
            btnBlock.setDisable(lock);
            spinner.setVisible(lock);
        });
    }
    
    @Override
    public void requestBlockUserFinished(RequestResult result) {
        lockUi(false);
        if (result.isSuccessful()) {
            displayMessage(new MessageContainer(MessageCode.OK, "Your account has been sucessfully blocked!"));
        } else {     
            displayMessage(result.getMessageContainer());
            logger.debug(result.getMessageContainer().toString(), getClass());
        }  
    }

    @Override
    public void requestFailed(RequestResult result) {
        lockUi(false);
        displayMessage(result.getMessageContainer());
    }
}
