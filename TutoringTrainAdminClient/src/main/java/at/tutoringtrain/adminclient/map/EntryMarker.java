package at.tutoringtrain.adminclient.map;

import at.tutoringtrain.adminclient.data.entry.Entry;
import com.lynden.gmapsfx.javascript.object.Marker;
import com.lynden.gmapsfx.javascript.object.MarkerOptions;

/**
 *
 * @author Marco Wilscher marco.wilscher@edu.htl-villach.at
 */
public final class EntryMarker extends Marker {
    private Entry entry;
    
    public EntryMarker(MarkerOptions markerOptions, Entry entry) {
        super(markerOptions);    
        setEntry(entry);
    }

    public Entry getEntry() {
        return entry;
    }

    public void setEntry(Entry entry) {
        this.entry = entry;
    }
}
