package at.bsd.tutoringtrain.debugging;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.TreeMap;

/**
 *
 * @author Marco Wilscher <marco.wilscher@edu.htl-villach.at>
 */
public final class MessageLogger {
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_BLUE = "\u001B[34m";
    
    private static MessageLogger INSTANCE;
    private static SimpleDateFormat FORMAT;
    public static MessageLogger getINSTANCE() {
        if (INSTANCE == null) {
            FORMAT = new SimpleDateFormat("HH:mm:ss dd.MM.yyyy");
            INSTANCE = new MessageLogger();
        }
        return INSTANCE;
    }
    
    private final TreeMap<Timestamp, LogEntry> infoLevelMessages;
    private final TreeMap<Timestamp, LogEntry> debugLevelMessages;
    private final TreeMap<Timestamp, LogEntry> errorLevelMessages;
    private boolean consoleOutput;
    
    /**
     * 
     */
    public MessageLogger() {
        infoLevelMessages = new TreeMap<>();
        debugLevelMessages = new TreeMap<>();
        errorLevelMessages = new TreeMap<>();
        consoleOutput = true;
        
        objectInitialized(this, getClass());
    }

    /**
     * 
     * @return 
     */
    public TreeMap<Timestamp, LogEntry> getInfoLevelMessages() {
        return new TreeMap<>(infoLevelMessages);
    }

    /**
     * 
     * @return 
     */
    public TreeMap<Timestamp, LogEntry> getDebugLevelMessages() {
        return new TreeMap<>(debugLevelMessages);
    }

    /**
     * 
     * @return 
     */
    public TreeMap<Timestamp, LogEntry> getErrorLevelMessages() {
        return new TreeMap<>(errorLevelMessages);
    }
    
    /**
     * 
     * @param info 
     * @param source 
     */
    public void info(String info, Class source) {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        infoLevelMessages.put(timestamp, new LogEntry(source, info));
        if (consoleOutput) {
            System.out.println(ANSI_GREEN + ">> " + FORMAT.format(timestamp) + " INFO at " + source.getCanonicalName() + ":\n\t" + info + "\n" + ANSI_RESET);
        }
    }
    
    public void objectInitialized(Object object, Class source) {
        try {
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            String info = object.getClass().getCanonicalName() + " initialized";
            infoLevelMessages.put(timestamp, new LogEntry(source, info));
            if (consoleOutput) {
                System.out.println(ANSI_GREEN + ">> " + FORMAT.format(timestamp) + " INFO at " + source.getCanonicalName() + ":\n\t" + info + "\n" + ANSI_RESET);
            }
        } catch (Exception ex) {
            error(ex.getMessage(), getClass());
        }
    }
    
    /**
     * 
     * @param debug 
     */
    public void debug(String debug, Class source) {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        debugLevelMessages.put(timestamp, new LogEntry(source, debug));
        if (consoleOutput) {
            System.out.println(ANSI_BLUE + ">> " + FORMAT.format(timestamp) + " DEBUG at " + source.getCanonicalName() + ":\n\t" + debug + "\n" + ANSI_RESET);
        }
    }
    
    /**
     * 
     * @param error 
     * @param source 
     */
    public void error(String error, Class source) {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        errorLevelMessages.put(timestamp, new LogEntry(source, error));
        if (consoleOutput) {
            System.err.println(ANSI_RED + ">> " + FORMAT.format(timestamp) + " ERROR at " + source.getCanonicalName() + ":\n\t" + error + "\n" + ANSI_RESET);
        }
    }

    /**
     * 
     * @param consoleOutput 
     */
    public void setConsoleOutput(boolean consoleOutput) {
        this.consoleOutput = consoleOutput;
    }
    
    /**
     * 
     * @return 
     */
    public boolean isConsoleOutput() {
        return consoleOutput;
    }
    
    /**
     * 
     * @return 
     */
    public int getTotalLogEntries() {
        return errorLevelMessages.size() + infoLevelMessages.size() + debugLevelMessages.size();
    }
    
    /**
     * 
     * @return 
     */
    public int getTotalInfoLogEntries() {
        return infoLevelMessages.size();
    }
    
    /**
     * 
     * @return 
     */
    public int getTotalDebugLogEntries() {
        return debugLevelMessages.size();
    }
    
    /**
     * 
     * @return 
     */
    public int getTotalErrorLogEntries() {
        return errorLevelMessages.size();
    }
}