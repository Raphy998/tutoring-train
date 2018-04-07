package at.tutoringtrain.adminclient.data.entry;

import at.tutoringtrain.adminclient.main.ApplicationManager;

/**
 *
 * @author Marco Wilscher marco.wilscher@edu.htl-villach.at
 */
public enum EntryType {
    OFFER, REQUEST;

    @Override
    public String toString() {
        return ApplicationManager.getLocalizedValueProvider().getString(name().toLowerCase());
    }
}
