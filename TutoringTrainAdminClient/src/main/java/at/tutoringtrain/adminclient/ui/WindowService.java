package at.tutoringtrain.adminclient.ui;

import at.tutoringtrain.adminclient.data.Subject;
import at.tutoringtrain.adminclient.data.User;
import at.tutoringtrain.adminclient.exception.RequiredParameterException;
import at.tutoringtrain.adminclient.io.network.WebserviceOperation;
import at.tutoringtrain.adminclient.main.ApplicationManager;
import at.tutoringtrain.adminclient.main.MessageContainer;
import at.tutoringtrain.adminclient.ui.controller.AllSubjectsController;
import at.tutoringtrain.adminclient.ui.controller.AllUsersController;
import at.tutoringtrain.adminclient.ui.controller.AuthenticationController;
import at.tutoringtrain.adminclient.ui.controller.BlockUserController;
import at.tutoringtrain.adminclient.ui.controller.ReauthenticationController;
import at.tutoringtrain.adminclient.ui.controller.RegisterSubjectController;
import at.tutoringtrain.adminclient.ui.controller.RegisterUserController;
import at.tutoringtrain.adminclient.ui.controller.SubjectListItemController;
import at.tutoringtrain.adminclient.ui.controller.UpdateUserController;
import at.tutoringtrain.adminclient.ui.listener.ReauthenticationListener;
import at.tutoringtrain.adminclient.ui.listener.UserAvatarChangedListner;
import at.tutoringtrain.adminclient.ui.listener.UserBlockListner;
import at.tutoringtrain.adminclient.ui.listener.UserDataChangedListner;
import com.jfoenix.controls.JFXSnackbar;
import java.io.IOException;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author Marco Wilscher marco.wilscher@edu.htl-villach.at
 */
public class WindowService {
    private static WindowService INSTANCE;
    
    public static WindowService getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new WindowService();
        }
        return INSTANCE;
    } 
    
    private final ApplicationManager applicationManager;
    private final Logger logger;
    private final ResourceBundle resourceBundle;
    
    private WindowService() {
        this.logger = LogManager.getLogger(this);
        this.applicationManager = ApplicationManager.getInstance();
        this.resourceBundle = applicationManager.getLanguageResourceBundle();
        
    }

    
    public void openMainWindow(AuthenticationController controller) throws IOException {
        Parent root;
        Stage stage;
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Main.fxml"), resourceBundle);
        root = loader.load();
        stage = new Stage();
        stage.setTitle("TutoringTrain - Administrator Client");
        stage.setResizable(false);
        stage.setScene(new Scene(root));
        stage.show();
        controller.closeWindow();      
    }
    
    public void openAuthenticationWindow() throws IOException {
        Parent root;
        Stage stage;
        FXMLLoader loader;
        AuthenticationController controller;
        loader = new FXMLLoader(getClass().getResource("/fxml/Authentication.fxml"), resourceBundle);
        root = loader.load();
        controller = (AuthenticationController)loader.getController();
        stage = new Stage();
        stage.setTitle("TutoringTrain - Admin Client");
        stage.setResizable(false);
        stage.setScene(new Scene(root));      
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();
    }
    
    public void openReauthenticationWindow(WebserviceOperation webserviceOperation, ReauthenticationListener listener) throws Exception {
        Parent root;
        Stage stage;
        FXMLLoader loader;
        ReauthenticationController controller;
        
        if (listener == null) {
             throw new RequiredParameterException(listener, "must not be null");
        }
        
        loader = new FXMLLoader(getClass().getResource("/fxml/Reauthentication.fxml"), resourceBundle);
        root = loader.load();
        controller = (ReauthenticationController)loader.getController();
        controller.setWebserviceOperation(webserviceOperation);
        controller.setReauthenticationListener(listener);
        stage = new Stage();
        stage.setTitle("TutoringTrain - Admin Client");
        stage.setResizable(false);
        stage.setScene(new Scene(root));      
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();
    }
    
    public void openRegisterUserWindow(ReauthenticationListener listener) throws IOException {
        Parent root;
        Stage stage;
        FXMLLoader loader;
        RegisterUserController controller;
        loader = new FXMLLoader(getClass().getResource("/fxml/RegisterUser.fxml"), resourceBundle);
        root = loader.load();
        controller = (RegisterUserController)loader.getController();
        stage = new Stage();
        stage.setTitle("TutoringTrain - Admin Client");
        stage.setResizable(false);
        stage.setScene(new Scene(root));      
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();
    }

    public void openRegisterSubjectWindow() throws IOException {
        Parent root;
        Stage stage;
        FXMLLoader loader;
        RegisterSubjectController controller;
        loader = new FXMLLoader(getClass().getResource("/fxml/RegisterSubject.fxml"), resourceBundle);
        root = loader.load();
        controller = (RegisterSubjectController)loader.getController();
        stage = new Stage();
        stage.setTitle("TutoringTrain - Admin Client");
        stage.setResizable(false);
        stage.setScene(new Scene(root));      
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();
    }
    
    public void openUpdateSubjectWindow(Subject subject, SubjectListItemController itemController) {
        
    }
    
    public void openUpdateUserWindow(User user) throws Exception {
        openUpdateUserWindow(user, null, null, null);
    }
    
    public void openUpdateUserWindow(User user, UserDataChangedListner userDataChangedListner, UserAvatarChangedListner userAvatarChangedListner, UserBlockListner userBlockListner) throws Exception {
        Parent root;
        Stage stage;
        FXMLLoader loader;
        UpdateUserController controller;
        loader = new FXMLLoader(getClass().getResource("/fxml/UpdateUser.fxml"), resourceBundle);
        root = loader.load();
        controller = (UpdateUserController)loader.getController();
        stage = new Stage();
        stage.setTitle("TutoringTrain - Admin Client");
        stage.setResizable(false);
        stage.setScene(new Scene(root));          
        controller.setUser(user);
        controller.setUserDataChangedListner(userDataChangedListner);
        controller.setUserAvatarChangedListner(userAvatarChangedListner);
        controller.setUserBlockListner(userBlockListner);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();
    }
    
    public void openBlockUserWindow(UserBlockListner listener, User user) throws IOException {
        Parent root;
        Stage stage;
        FXMLLoader loader;
        BlockUserController controller;
        loader = new FXMLLoader(getClass().getResource("/fxml/BlockUser.fxml"), resourceBundle);
        root = loader.load();
        controller = (BlockUserController) loader.getController();
        stage = new Stage();
        stage.setTitle("TutoringTrain - Admin Client");
        stage.setResizable(false);
        stage.setScene(new Scene(root));
        controller.setUser(user);
        controller.setUserBlockListner(listener);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();
    }
    
    
    public void openShowAllUsersWindow() throws Exception {
        Parent root;
        Stage stage;
        FXMLLoader loader;
        AllUsersController controller;
        loader = new FXMLLoader(getClass().getResource("/fxml/AllUsers.fxml"), resourceBundle);
        root = loader.load();
        controller = (AllUsersController)loader.getController();
        stage = new Stage();
        stage.setTitle("TutoringTrain - Admin Client");
        stage.setResizable(false);
        stage.setScene(new Scene(root));
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();
    }
    
    public void openShowAllSubjectsWindow() throws Exception {
        Parent root;
        Stage stage;
        FXMLLoader loader;
        AllSubjectsController controller;
        loader = new FXMLLoader(getClass().getResource("/fxml/AllSubjects.fxml"), resourceBundle);
        root = loader.load();
        controller = (AllSubjectsController)loader.getController();
        stage = new Stage();
        stage.setTitle("TutoringTrain - Admin Client");
        stage.setResizable(false);
        stage.setScene(new Scene(root));
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();
    }
    
    public void closeWindow(Node node) {
        if (node != null) {
            ((Stage)node.getScene().getWindow()).close();
        }
    }
    
    public void displayMessage(JFXSnackbar snackbar, MessageContainer message) {
        Platform.runLater(() -> {
            snackbar.enqueue(new JFXSnackbar.SnackbarEvent(message.toString()));
        });
    }
}
