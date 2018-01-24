package at.tutoringtrain.adminclient.ui.search.entry;

import at.tutoringtrain.adminclient.main.ApplicationManager;

/**
 *
 * @author Marco Wilscher marco.wilscher@edu.htl-villach.at
 */
public enum EntryActiveState {
    TRUE, FALSE, ALL;

    @Override
    public String toString() {
        return ApplicationManager.getLocalizedValueProvider().getString(name().toLowerCase());
    }
}
