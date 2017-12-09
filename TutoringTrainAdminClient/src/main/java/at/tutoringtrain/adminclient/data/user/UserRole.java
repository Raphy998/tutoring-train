package at.tutoringtrain.adminclient.data.user;

/**
 *
 * @author Marco Wilscher marco.wilscher@edu.htl-villach.at
 */
public enum UserRole {
    ROOT('R', 1),
    ADMIN('A', 2),
    MODERATOR('M', 4),
    USER('U', 8);
    
    private final Character value;
    private final int priority;
    
    private UserRole(Character value, int priority) {
        this.value = value;
        this.priority = priority;
    }

    public Character getValue() {
        return value;
    }

    public int getPriority() {
        return priority;
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
    
    public boolean isHighestPriority() {
        return priority == 1;
    }
}
