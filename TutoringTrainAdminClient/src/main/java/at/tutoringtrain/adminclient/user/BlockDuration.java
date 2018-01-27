package at.tutoringtrain.adminclient.user;

import at.tutoringtrain.adminclient.internationalization.LocalizedValueProvider;
import java.util.Comparator;

/**
 *
 * @author Marco Wilscher marco.wilscher@edu.htl-villach.at
 */
public enum BlockDuration implements Comparator<BlockDuration> {
    MINUTES_30(30),
    HOUR_1(60),
    HOUR_2(120),
    HOUR_6(360),
    HOUR_12(720),
    DAY_1(1440),
    DAY_2(2880),
    DAY_5(7200),
    WEEK_1(10080),
    WEEK_2(20160),
    WEEK_3(30240),
    MONTH_1(43800),
    MONTH_2(87600),
    MONTH_6(262800),
    YEAR_1(525600),
    UNLIMITED(-1);
    
    private final int minutes;

    private BlockDuration(int minutes) {
        this.minutes = minutes;
    }

    public int getMinutes() {
        return minutes;
    }
    
    public String getName() {
        return LocalizedValueProvider.getINSTANCE().getString(name());
    }

    @Override
    public String toString() {
        return getName();
    }
    
    @Override
    public int compare(BlockDuration o1, BlockDuration o2) {
        return o2.getMinutes() - o1.getMinutes();
    }
}
