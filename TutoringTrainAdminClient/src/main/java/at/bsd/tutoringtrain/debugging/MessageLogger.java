package at.bsd.tutoringtrain.debugging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 *
 * @author Marco Wilscher marco.wilscher@edu.htl-villach.at
 */
public final class MessageLogger {  
    private static MessageLogger INSTANCE;
    private static SimpleDateFormat FORMAT;
    public static MessageLogger getINSTANCE() {
        if (INSTANCE == null) {
            FORMAT = new SimpleDateFormat("HH:mm:ss dd.MM.yyyy");
            INSTANCE = new MessageLogger();
        }
        return INSTANCE;
    }
    
    private final TreeMap<LogLevel, TreeMap<Timestamp, LogEntry>> entries;
    private TreeSet<LogLevel> displayLevels;
    private boolean consoleOutput;
    
    /**
     * 
     */
    public MessageLogger() {
        entries = new TreeMap<>();
        for (LogLevel level : LogLevel.values()) {
            entries.put(level, new TreeMap<>());
        }
        displayLevels = new TreeSet<>(Arrays.asList(LogLevel.values())); 
        consoleOutput = true;
    }
    
    /**
     * 
     * @param text
     * @param colour
     * @return 
     */
    private String getColouredText(String text, AnsiColour colour) {
        return colour.getValue() + text;
    }
    
    /**
     * 
     * @param entry 
     */
    private void writeToConsole(LogEntry entry) {
        LogLevel level;
        StringBuilder text;
        level = entry.getLevel();
        if (consoleOutput && displayLevels.contains(level)) {
            text = new StringBuilder();
            text.append(">> ");
            text.append(FORMAT.format(entry.getTimestamp()));
            text.append(" ");
            text.append(level.toString());
            text.append(" at ");
            text.append(entry.getSource().getCanonicalName());
            text.append(":\n *\t");
            text.append(entry.getMessage());
            if (level == LogLevel.EXCEPTION) {
                text.append("\n *\t");
                text.append(entry.getException().getMessage());
            }
            text.append("\n");
            if (level == LogLevel.ERROR || level == LogLevel.EXCEPTION) {
                System.err.println(getColouredText(text.toString(), level.getColour()));
            } else {
                System.out.println(getColouredText(text.toString(), level.getColour()));
            }
        }
    }

    /**
     * 
     * @return 
     */
    public TreeMap<LogLevel, TreeMap<Timestamp, LogEntry>> getEntries() {
        return new TreeMap<>(entries);
    }
    
    /**
     * 
     * @param level
     * @return 
     */
    public TreeMap<Timestamp, LogEntry> getEntries(LogLevel level) {
        return new TreeMap<>(entries.get(level));
    }
    
    /**
     * 
     * @param entry 
     */
    private void addEntry(LogEntry entry) {
        entries.get(entry.getLevel()).put(entry.getTimestamp(), entry);
    }

    /**
     * 
     * @param info 
     * @param source 
     */
    public void info(String info, Class source) {
        LogEntry entry;
        entry = new LogEntry(source, LogLevel.INFO, info);
        addEntry(entry);
        if (consoleOutput) {
            writeToConsole(entry);
        }
    }
    
    /**
     * 
     * @param debug
     * @param source 
     */
    public void debug(String debug, Class source) {
        LogEntry entry;
        entry = new LogEntry(source, LogLevel.DEBUG, debug);
        addEntry(entry);
        if (consoleOutput) {
            writeToConsole(entry);
        }
    }
    
    /**
     * 
     * @param warning
     * @param source 
     */
    public void warning(String warning, Class source) {
        LogEntry entry;
        entry = new LogEntry(source, LogLevel.WARNING, warning);
        addEntry(entry);
        if (consoleOutput) {
            writeToConsole(entry);
        }
    }
    
    /**
     * 
     * @param error
     * @param source 
     */
    public void error(String error, Class source) {
        LogEntry entry;
        entry = new LogEntry(source, LogLevel.ERROR, error);
        addEntry(entry);
        if (consoleOutput) {
            writeToConsole(entry);
        }
    }
    
    /**
     * 
     * @param exception
     * @param source 
     */
    public void exception(Exception exception, Class source) {
        LogEntry entry;
        entry = new LogEntry(source, LogLevel.EXCEPTION, "", exception);
        addEntry(entry);
        if (consoleOutput) {
            writeToConsole(entry);
            exception.printStackTrace();
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
     * @param levels 
     */
    public void setDisplayLevels(LogLevel... levels) {
        this.displayLevels = new TreeSet<>(Arrays.asList(levels));
    }

    /**
     * 
     * @return 
     */
    public TreeSet<LogLevel> getDisplayLevels() {
        return new TreeSet<>(displayLevels);
    }
    
    /**
     * 
     * @return 
     */
    public int getTotalEntries() {
        return entries.size();
    }
    
    /**
     * 
     * @param level
     * @return 
     */
    public int getTotalEntries(LogLevel level) {
        return entries.get(level).size();
    }  
    
    /**
     * 
     * @return
     * @throws JsonProcessingException 
     */
    public String getEntriesAsJson() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(entries);
    }
}