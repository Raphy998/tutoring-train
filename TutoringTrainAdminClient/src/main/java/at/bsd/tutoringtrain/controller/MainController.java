package at.bsd.tutoringtrain.controller;

import at.bsd.tutoringtrain.data.Database;
import at.bsd.tutoringtrain.data.entities.User;
import at.bsd.tutoringtrain.data.mapper.EntityMapper;
import at.bsd.tutoringtrain.debugging.MessageLogger;
import at.bsd.tutoringtrain.network.Communicator;
import at.bsd.tutoringtrain.network.Result;
import at.bsd.tutoringtrain.network.listener.user.RequestAllUsersListener;
import at.bsd.tutoringtrain.network.listener.user.RequestBlockUserListener;
import at.bsd.tutoringtrain.network.listener.user.RequestOwnUserListener;
import at.bsd.tutoringtrain.network.listener.user.RequestRegisterUserListener;
import at.bsd.tutoringtrain.network.listener.user.RequestUnblockUserListener;
import at.bsd.tutoringtrain.network.listener.user.RequestUpdateOwnUserListener;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.fxml.Initializable;

/**
 * FXML Controller class
 *
 * @author Marco Wilscher <marco.wilscher@edu.htl-villach.at>
 */
public class MainController implements Initializable, RequestAllUsersListener, RequestOwnUserListener, RequestUpdateOwnUserListener, RequestBlockUserListener, RequestUnblockUserListener, RequestRegisterUserListener {

    private Communicator communicator;
    private Database db;
    private MessageLogger logger;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        logger = MessageLogger.getINSTANCE();
        communicator = Communicator.getInstance();
        db = Database.getInstance();
        try {
            if (!communicator.requestOwnUser(this)) {
                //TODO aquire a new token
            }
            
            //communicator.requestBlockUser(this, new BlockRequest("elias", "admin client", new Date(2017, 11, 11)));
            
            //communicator.requestUnblockUser(this, "elias");
            
            //User u = new User("cdefgh1");
            //u.setName("ABC DEFG");
            //u.setEmail("cde1@def.g");
            //u.setGender(new Gender(BigDecimal.ONE));
            //u.setEducation("HTL");
            //u.setPassword("abcdefg");
            
            //logger.info(u.toString(), getClass());
            
            //communicator.requestRegisterUser(this, u);
            
        } catch (Exception ex) {
            logger.error(ex.getMessage(), getClass());
        }
    }

    @Override
    public void requestOwnUserFinished(Result result) {
        try {
            if (!result.isError()) {
                db.setUser(EntityMapper.toUser(result.getResultValue()));
            }
            else {
                //TODO 
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage(), getClass());
        }
    }

    @Override
    public void requestAllUsersFinished(Result result) {
        try {
            if (!result.isError()) {
                ArrayList<User> users = EntityMapper.toUsers(result.getResultValue());
                System.out.println(users.size());
            } else {
                
            }            
        } catch (Exception ex) {
            logger.error(ex.getMessage(), getClass());
        }
    }

    @Override
    public void requestFailed(Result result) {
        logger.error(result.toString(), getClass());
    }

    @Override
    public void requestUpdateOwnUserFinished(Result result) {
        logger.info(result.toString(), getClass());
    }

    @Override
    public void requestBlockUserFinished(Result result) {
        logger.info(result.toString(), getClass());
    }

    @Override
    public void requestUnblockUserFinished(Result result) {
        logger.info(result.toString(), getClass());
    }

    @Override
    public void requestRegisterUserFinished(Result result) {
        logger.info(result.toString(), getClass());
    }   
}