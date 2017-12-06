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
    
    public static UserRole valueOf(char roleid) {
        UserRole role = USER;
        if (roleid == 'A') {
            role = ADMIN;
        } else if (roleid == 'M') {
            role = MODERATOR;
        }
        return role;
    }

    @Override
    public String toString() {
        return name();
    }
}
