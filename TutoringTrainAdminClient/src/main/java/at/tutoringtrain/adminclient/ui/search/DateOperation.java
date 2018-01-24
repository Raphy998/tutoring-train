package at.tutoringtrain.adminclient.ui.search;

import at.tutoringtrain.adminclient.main.ApplicationManager;

/**
 *
 * @author Elias
 */
public enum DateOperation {
    EQ, BEFORE, AFTER, BEFORE_EQ, AFTER_EQ, IGNORE;
    
    @Override
    public String toString() {
        return ApplicationManager.getLocalizedValueProvider().getString(name().toLowerCase());
    }
}
