/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.tutoringtrain.adminclient.ui.search.entry;

import at.tutoringtrain.adminclient.main.ApplicationManager;
import at.tutoringtrain.adminclient.ui.search.DataType;
import static at.tutoringtrain.adminclient.ui.search.DataType.*;
import at.tutoringtrain.adminclient.ui.search.EntityProp;

/**
 *
 * @author Elias
 */
public enum EntryProp implements EntityProp {
    ID(NUMBER), 
    POSTEDON(DATE), 
    ISACTIVE(BOOLEAN), 
    DESCRIPTION(STRING), 
    SUBJECT(NUMBER),
    HEADLINE(STRING),
    USERNAME(STRING);
    
    private final DataType dataType;

    EntryProp(DataType dataType) {
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
