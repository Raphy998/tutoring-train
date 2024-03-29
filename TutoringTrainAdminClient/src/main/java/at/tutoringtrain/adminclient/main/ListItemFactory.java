package at.tutoringtrain.adminclient.main;

import at.tutoringtrain.adminclient.data.entry.Offer;
import at.tutoringtrain.adminclient.data.subject.Subject;
import at.tutoringtrain.adminclient.data.user.User;
import at.tutoringtrain.adminclient.ui.controller.AllSubjectsController;
import at.tutoringtrain.adminclient.ui.controller.MessageListItemController;
import at.tutoringtrain.adminclient.ui.controller.OfferListItemController;
import at.tutoringtrain.adminclient.ui.controller.SubjectListItemController;
import at.tutoringtrain.adminclient.ui.controller.UserListItemController;
import at.tutoringtrain.adminclient.ui.listener.MessageListener;
import java.io.IOException;
import java.util.ResourceBundle;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author Marco Wilscher marco.wilscher@edu.htl-villach.at
 */
public class ListItemFactory {
    private static ListItemFactory INSTANCE;

    public static ListItemFactory getINSTANCE() {
        if (INSTANCE == null) {
            INSTANCE = new ListItemFactory();
        }
        return INSTANCE;
    }
    
    private final ApplicationManager applicationManager;
    private final Logger logger;
    private final ResourceBundle resourceBundle;
    
    private ListItemFactory() {
        this.logger = LogManager.getLogger(this);
        this.applicationManager = ApplicationManager.getInstance();
        this.resourceBundle = applicationManager.getLanguageResourceBundle();
        this.logger.debug("ListItemFactory initialized");
    }
    
    public AnchorPane generateUserListItem(User user, MessageListener listener) {
        AnchorPane itemPane;
        FXMLLoader loader;
        UserListItemController controller;
        try {
            loader = new FXMLLoader(getClass().getResource("/fxml/UserListItem.fxml"), resourceBundle);
            itemPane = loader.<AnchorPane>load();
            controller = (UserListItemController)loader.getController();
            controller.setUser(user);
            controller.setMessageListener(listener);
        } catch (IOException ioex) {
            itemPane = null;
            logger.error("Generating user-list-item failed", ioex);
        }
        return itemPane;
    }
    
    public AnchorPane generateMessageListItem(String message, boolean isMessageKey) {
        AnchorPane itemPane;
        FXMLLoader loader;
        MessageListItemController controller;
        try {
            loader = new FXMLLoader(getClass().getResource("/fxml/MessageListItem.fxml"), resourceBundle);
            itemPane = loader.<AnchorPane>load();
            controller = (MessageListItemController)loader.getController();
            controller.setText(message, isMessageKey);
        } catch (IOException ioex) {
            itemPane = null;
            logger.error("Generating message-list-item failed", ioex);
        }
        return itemPane;
    }
    
    public AnchorPane generateSubjectListItem(Subject subject, MessageListener listener, AllSubjectsController parentController) {
        AnchorPane itemPane;
        FXMLLoader loader;
        SubjectListItemController controller;
        try {
            loader = new FXMLLoader(getClass().getResource("/fxml/SubjectListItem.fxml"), resourceBundle);
            itemPane = loader.<AnchorPane>load();
            controller = (SubjectListItemController)loader.getController();
            controller.setSubject(subject);
            controller.setMessageListener(listener);
            controller.setParentController(parentController);
        } catch (IOException ioex) {
            itemPane = null;
            logger.error("Generating subject-list-item failed", ioex);
        }
        return itemPane;
    }
    
    public AnchorPane generateOfferListItem(Offer offer, MessageListener listener) {
        AnchorPane itemPane;
        FXMLLoader loader;
        OfferListItemController controller;
        try {
            loader = new FXMLLoader(getClass().getResource("/fxml/OfferListItem.fxml"), resourceBundle);
            itemPane = loader.<AnchorPane>load();
            controller = (OfferListItemController)loader.getController();
            controller.setOffer(offer);
            controller.setMessageListener(listener);
        } catch (IOException ioex) {
            itemPane = null;
            logger.error("Generating offer-list-item failed", ioex);
        }
        return itemPane;
    }
}
