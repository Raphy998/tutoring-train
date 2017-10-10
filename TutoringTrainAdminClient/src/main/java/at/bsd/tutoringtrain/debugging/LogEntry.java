package at.bsd.tutoringtrain.debugging;

/**
 *
 * @author Marco Wilscher <marco.wilscher@edu.htl-villach.at>
 */
public class LogEntry {
    private final Class source;
    private final String message;
    
    /**
     * 
     * @param source
     * @param message 
     */
    public LogEntry(Class source, String message) {
        this.source = source;
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public Class getSource() {
        return source;
    }
}
