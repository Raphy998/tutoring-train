package at.bsd.tutoringtrain.debugging;

/**
 *
 * @author Marco Wilscher marco.wilscher@edu.htl-villach.at
 */
public enum LogLevel {
    DEBUG(AnsiColour.YELLOW),
    INFO(AnsiColour.GREEN),
    WARNING(AnsiColour.CYAN),
    ERROR(AnsiColour.RED),
    EXCEPTION(AnsiColour.RED);
    
    private final AnsiColour colour;

    /**
     * 
     * @param colour 
     */
    private LogLevel(AnsiColour colour) {
        this.colour = colour;
    }

    /**
     * 
     * @return 
     */
    public AnsiColour getColour() {
        return colour;
    }
}
