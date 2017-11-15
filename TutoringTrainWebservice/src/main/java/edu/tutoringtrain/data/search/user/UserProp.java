/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.tutoringtrain.data.search.user;

import edu.tutoringtrain.data.search.DataType;
import edu.tutoringtrain.data.search.EntityProp;
import static edu.tutoringtrain.data.search.DataType.*;

/**
 *
 * @author Elias
 */
public enum UserProp implements EntityProp {
    USERNAME(STRING), 
    NAME(STRING), 
    ROLE(CHAR), 
    EDUCATION(STRING), 
    GENDER(CHAR);
    
    private final DataType dataType;

    UserProp(DataType dataType) {
        this.dataType = dataType;
    }

    @Override
    public DataType getDataType()  {
        return dataType;
    }
}
