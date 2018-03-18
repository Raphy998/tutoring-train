package at.tutoringtrain.adminclient.data.entry;

/**
 *
 * @author Marco Wilscher marco.wilscher@edu.htl-villach.at
 */
public enum EntryType {
    OFFER, REQUEST;

    @Override
    public String toString() {
        return name().toLowerCase();
    }
}
