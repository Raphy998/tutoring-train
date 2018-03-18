package at.tutoringtrain.adminclient.ui.search.user;

import at.tutoringtrain.adminclient.main.ApplicationManager;
import at.tutoringtrain.adminclient.ui.search.DataType;
import static at.tutoringtrain.adminclient.ui.search.DataType.*;
import at.tutoringtrain.adminclient.ui.search.EntityProp;

/**
 *
 * @author Elias
 */
public enum UserProp implements EntityProp {
    USERNAME(STRING), 
    NAME(STRING), 
    ROLE(CHAR), 
    EDUCATION(STRING), 
    GENDER(CHAR),
    RATING(NUMBER);
    
    private final DataType dataType;

    UserProp(DataType dataType) {
        this.dataType = dataType;
    }

    @Override
    public DataType getDataType()  {
        return dataType;
    }
    
    @Override
    public String toString() {
        return ApplicationManager.getLocalizedValueProvider().getString(name().toLowerCase());
    }
}
