/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.tutoringtrain.data.search.offer;

import edu.tutoringtrain.data.search.DataType;
import static edu.tutoringtrain.data.search.DataType.*;
import edu.tutoringtrain.data.search.EntityProp;

/**
 *
 * @author Elias
 */
public enum OfferProp implements EntityProp {
    ID(NUMBER), 
    POSTEDON(DATE), 
    ISACTIVE(BOOLEAN), 
    DESCRIPTION(STRING), 
    SUBJECT(NUMBER),
    HEADLINE(STRING),
    USERNAME(STRING);
    
    private final DataType dataType;

    OfferProp(DataType dataType) {
        this.dataType = dataType;
    }

    @Override
    public DataType getDataType()  {
        return dataType;
    }
}
