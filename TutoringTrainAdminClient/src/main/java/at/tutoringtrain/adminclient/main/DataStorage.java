package at.tutoringtrain.adminclient.main;

import at.tutoringtrain.adminclient.data.Gender;
import at.tutoringtrain.adminclient.data.Subject;
import at.tutoringtrain.adminclient.data.User;
import java.math.BigDecimal;
import java.util.TreeMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author Marco Wilscher marco.wilscher@edu.htl-villach.at
 */
public final class DataStorage {
    private static DataStorage INSTANCE;

    public static DataStorage getINSTANCE() {
        if (INSTANCE == null) {
            INSTANCE = new DataStorage();
        }
        return INSTANCE;
    }
    
    private final Logger logger;   
    private final TreeMap<Character, Gender> genders;
    private final TreeMap<String, User> users;
    private final TreeMap<BigDecimal, Subject> subjects;
    
    private DataStorage() {
        this.logger = LogManager.getLogger(this);
        this.genders = new TreeMap<>();
        this.users = new TreeMap<>();
        this.subjects = new TreeMap<>();
        this.logger.debug("DataStorage initialized");
    }   

    public TreeMap<Character, Gender> getGenders() {
        return new TreeMap<>(genders);
    }
    
    public void clearGenders() {
        genders.clear();
    }
    
    public Gender addGender(Gender gender) {
        return genders.put(gender.getCode(), gender);
    }
    
    public Gender getGender(Character code) {
        return genders.get(code);
    }

    public TreeMap<String, User> getUsers() {
        return users;
    }
    
    public void clearUsers() {
        users.clear();
    }
    
    public User addUser(User user) {
        return users.put(user.getUsername(), user);
    }
    
    public User getUser(String username) {
        return users.get(username);
    }
    
    public TreeMap<BigDecimal, Subject> getSubjects() {
        return subjects;
    }
    
    public void clearSubjects() {
        subjects.clear();
    }
    
    public Subject addSubject(Subject subject) {
        return subjects.put(subject.getId(), subject);
    }
    
    public Subject getSubject(BigDecimal id) {
        return subjects.get(id);
    }

    public Subject removeSubject(Subject subject) {
        return subjects.remove(subject.getId());
    }
}
