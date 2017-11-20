package at.tutoringtrain.adminclient.io.network;

/**
 *
 * @author Marco Wilscher marco.wilscher@edu.htl-villach.at
 */
public enum UserRole {
    ADMIN('A'),
    MODERATOR('M'),
    USER('U');
    
    private Character value;

    private UserRole(Character value) {
        this.value = value;
    }

    public Character getValue() {
        return value;
    }
}
