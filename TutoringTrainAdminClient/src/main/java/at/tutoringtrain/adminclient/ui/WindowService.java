package at.tutoringtrain.adminclient.ui;

import at.tutoringtrain.adminclient.data.subject.Subject;
import at.tutoringtrain.adminclient.data.user.User;
import at.tutoringtrain.adminclient.internationalization.LocalizedValueProvider;
import at.tutoringtrain.adminclient.internationalization.StringPlaceholder;
import at.tutoringtrain.adminclient.main.ApplicationManager;
import at.tutoringtrain.adminclient.main.DefaultValueProvider;
import at.tutoringtrain.adminclient.main.MessageContainer;
import at.tutoringtrain.adminclient.ui.controller.AllOffersController;
import at.tutoringtrain.adminclient.ui.controller.AllSubjectsController;
import at.tutoringtrain.adminclient.ui.controller.AllUsersController;
import at.tutoringtrain.adminclient.ui.controller.AuthenticationController;
import at.tutoringtrain.adminclient.ui.controller.BlockUserController;
import at.tutoringtrain.adminclient.ui.controller.CreditsController;
import at.tutoringtrain.adminclient.ui.controller.RegisterSubjectController;
import at.tutoringtrain.adminclient.ui.controller.RegisterUserController;
import at.tutoringtrain.adminclient.ui.controller.SettingsController;
import at.tutoringtrain.adminclient.ui.controller.SubjectListItemController;
import at.tutoringtrain.adminclient.ui.controller.UpdateSubjectController;
import at.tutoringtrain.adminclient.ui.controller.UpdateUserController;
import at.tutoringtrain.adminclient.ui.listener.UserAvatarChangedListner;
import at.tutoringtrain.adminclient.ui.listener.UserBlockListner;
import at.tutoringtrain.adminclient.ui.listener.UserDataChangedListner;
import com.jfoenix.controls.JFXSnackbar;
import java.io.IOException;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
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
    private final LocalizedValueProvider localizedValueProvider;
    private final DefaultValueProvider defaultValueProvider;
    
    private WindowService() {
        this.logger = LogManager.getLogger(this);
        this.applicationManager = ApplicationManager.getInstance();
        this.resourceBundle = applicationManager.getLanguageResourceBundle();
        this.localizedValueProvider = ApplicationManager.getLocalizedValueProvider();
        this.defaultValueProvider = ApplicationManager.getDefaultValueProvider();
        logger.debug("WindowService initialized");
    }

    
    public void openMainWindow(AuthenticationController controller) throws IOException {
        Parent root;
        Stage stage;
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Main.fxml"), resourceBundle);
        root = loader.load();
        stage = new Stage();
        stage.setTitle(localizedValueProvider.getString("titleApplication"));
        stage.getIcons().add(defaultValueProvider.getDefaultApplicationIcon());
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
        stage.setTitle(localizedValueProvider.getString("titleApplication"));
        stage.getIcons().add(defaultValueProvider.getDefaultApplicationIcon());
        stage.setResizable(false);
        stage.setScene(new Scene(root));      
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();
    }
    
    public void openRegisterUserWindow() throws IOException {
        Parent root;
        Stage stage;
        FXMLLoader loader;
        RegisterUserController controller;
        loader = new FXMLLoader(getClass().getResource("/fxml/RegisterUser.fxml"), resourceBundle);
        root = loader.load();
        controller = (RegisterUserController)loader.getController();
        stage = new Stage();
        stage.setTitle(localizedValueProvider.getString("titleApplication"));
        stage.getIcons().add(defaultValueProvider.getDefaultApplicationIcon());
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
        stage.setTitle(localizedValueProvider.getString("titleApplication"));
        stage.getIcons().add(defaultValueProvider.getDefaultApplicationIcon());
        stage.setResizable(false);
        stage.setScene(new Scene(root));      
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();
    }
    
    public void openUpdateSubjectWindow(Subject subject, SubjectListItemController itemController) throws Exception {
        Parent root;
        Stage stage;
        FXMLLoader loader;
        UpdateSubjectController controller;
        loader = new FXMLLoader(getClass().getResource("/fxml/UpdateSubject.fxml"), resourceBundle);
        root = loader.load();
        controller = (UpdateSubjectController)loader.getController();
        stage = new Stage();
        stage.setTitle(localizedValueProvider.getString("titleApplication"));
        stage.getIcons().add(defaultValueProvider.getDefaultApplicationIcon());
        stage.setResizable(false);
        stage.setScene(new Scene(root));   
        controller.setSubject(subject);
        controller.setSubjectStateListener(itemController);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();
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
        stage.setTitle(localizedValueProvider.getString("titleApplication"));
        stage.getIcons().add(defaultValueProvider.getDefaultApplicationIcon());
        stage.setResizable(false);
        stage.setScene(new Scene(root));          
        controller.setUser(user);
        controller.setUserDataChangedListner(userDataChangedListner);
        controller.setUserAvatarChangedListner(userAvatarChangedListner);
        controller.setUserBlockListner(userBlockListner);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();
    }
    
    public void openBlockUserWindow(UserBlockListner listener, User user) throws Exception {
        Parent root;
        Stage stage;
        FXMLLoader loader;
        BlockUserController controller;
        loader = new FXMLLoader(getClass().getResource("/fxml/BlockUser.fxml"), resourceBundle);
        root = loader.load();
        controller = (BlockUserController) loader.getController();
        stage = new Stage();
        stage.setTitle(localizedValueProvider.getString("titleApplication"));
        stage.getIcons().add(defaultValueProvider.getDefaultApplicationIcon());
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
        stage.setTitle(localizedValueProvider.getString("titleApplication"));
        stage.getIcons().add(defaultValueProvider.getDefaultApplicationIcon());
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
        stage.setTitle(localizedValueProvider.getString("titleApplication"));
        stage.getIcons().add(defaultValueProvider.getDefaultApplicationIcon());
        stage.setResizable(false);
        stage.setScene(new Scene(root));
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();
    }
    
    public void openShowAllOffersWindow() throws Exception {
        Parent root;
        Stage stage;
        FXMLLoader loader;
        AllOffersController controller;
        loader = new FXMLLoader(getClass().getResource("/fxml/AllOffers.fxml"), resourceBundle);
        root = loader.load();
        controller = (AllOffersController)loader.getController();
        stage = new Stage();
        stage.setTitle(localizedValueProvider.getString("titleApplication"));
        stage.getIcons().add(defaultValueProvider.getDefaultApplicationIcon());
        stage.setResizable(false);
        stage.setScene(new Scene(root));
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();
    }
    
    public void openSettingsWindow() throws Exception {
        openSettingsWindow(false);
    }
    
    public void openSettingsWindow(boolean immediately) throws Exception {
        Parent root;
        Stage stage;
        FXMLLoader loader;
        SettingsController controller;
        loader = new FXMLLoader(getClass().getResource("/fxml/Settings.fxml"), resourceBundle);
        root = loader.load();
        controller = (SettingsController)loader.getController();
        stage = new Stage();
        stage.setTitle(localizedValueProvider.getString("titleApplication"));
        stage.getIcons().add(defaultValueProvider.getDefaultApplicationIcon());
        stage.setResizable(false);
        stage.setScene(new Scene(root));
        controller.setWriteImmediately(immediately);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();
    }
    
    public void openCreditsWindow() throws Exception {
        Parent root;
        Stage stage;
        FXMLLoader loader;
        CreditsController controller;
        loader = new FXMLLoader(getClass().getResource("/fxml/Credits.fxml"), resourceBundle);
        root = loader.load();
        controller = (CreditsController)loader.getController();
        stage = new Stage();
        stage.setTitle(localizedValueProvider.getString("titleApplication"));
        stage.getIcons().add(defaultValueProvider.getDefaultApplicationIcon());
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
        logger.info(message.toString());
    }
    
    public Optional<ButtonType> openConfirmDialog(String headerKey, String contentKey, StringPlaceholder... placeholders) {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        ((Stage)alert.getDialogPane().getScene().getWindow()).getIcons().add(defaultValueProvider.getDefaultApplicationIcon());
        alert.setTitle(localizedValueProvider.getString("titleApplication"));
        alert.setHeaderText(localizedValueProvider.getString(headerKey, placeholders));
        alert.setContentText(localizedValueProvider.getString(contentKey, placeholders));
        return alert.showAndWait(); 
    }
}
