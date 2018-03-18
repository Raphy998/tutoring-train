package at.tutoringtrain.adminclient.ui.search;

import at.tutoringtrain.adminclient.main.ApplicationManager;

/**
 *
 * @author Elias
 */
public enum BooleanOperation {
    EQ;
    
    @Override
    public String toString() {
        return ApplicationManager.getLocalizedValueProvider().getString(name().toLowerCase());
    }
}
