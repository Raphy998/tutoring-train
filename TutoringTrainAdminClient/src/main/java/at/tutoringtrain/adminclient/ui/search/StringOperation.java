package at.tutoringtrain.adminclient.ui.search;

import at.tutoringtrain.adminclient.main.ApplicationManager;

/**
 *
 * @author Elias
 */
public enum StringOperation {
    EQ, CONTAINS, STARTS_WITH, ENDS_WITH;
    
    @Override
    public String toString() {
        return ApplicationManager.getLocalizedValueProvider().getString(name().toLowerCase());
    }
}
