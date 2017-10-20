package at.bsd.tutoringtrain.debugging;

import java.sql.Timestamp;

/**
 *
 * @author Marco Wilscher marco.wilscher@edu.htl-villach.at
 */
public class LogEntry {
    private final LogLevel level;
    private final Class source;
    private final String message;
    private final Exception exception;
    private final Timestamp timestamp;
    
    /**
     * 
     * @param source
     * @param level
     * @param message 
     */
    public LogEntry(Class source, LogLevel level, String message) {
        this(source, level, message, null);
    }
    
    public LogEntry(Class source, LogLevel level, String message, Exception exception) {
        this.timestamp = new Timestamp(System.currentTimeMillis());
        this.source = source;
        this.level = level;
        this.message = message;
        this.exception = exception;
    }
    

    public String getMessage() {
        return message;
    }

    public Class getSource() {
        return source;
    }

    public LogLevel getLevel() {
        return level;
    }

    public Exception getException() {
        return exception;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }
}
