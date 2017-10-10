package at.bsd.tutoringtrain.data;

import at.bsd.tutoringtrain.data.entities.User;
import at.bsd.tutoringtrain.debugging.MessageLogger;

/**
 *
 * @author Marco Wilscher <marco.wilscher@edu.htl-villach.at>
 */
public class Database {
    private static Database INSTANCE;
    public static Database getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new Database();
        }
        return INSTANCE;
    }
    
    private final MessageLogger logger;
    private User user;
   
    
    public Database() {
        logger = MessageLogger.getINSTANCE();
        user = null;
        
        logger.objectInitialized(this, getClass());
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
