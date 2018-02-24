/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.tutoringtrain.data.search.entry;

import com.mysema.query.types.OrderSpecifier;
import com.mysema.query.types.Predicate;
import com.mysema.query.types.expr.ComparableExpressionBase;
import edu.tutoringtrain.data.search.BooleanSearchCriteria;
import edu.tutoringtrain.data.search.DateSearchCriteria;
import edu.tutoringtrain.data.search.EntityProp;
import edu.tutoringtrain.data.search.NumberSearchCriteria;
import edu.tutoringtrain.data.search.OrderDirection;
import edu.tutoringtrain.data.search.OrderElement;
import edu.tutoringtrain.data.search.QueryGenerator;
import edu.tutoringtrain.data.search.Search;
import edu.tutoringtrain.data.search.SearchCriteria;
import edu.tutoringtrain.data.search.SpatialSearchCriteria;
import edu.tutoringtrain.data.search.StringSearchCriteria;
import edu.tutoringtrain.entities.QEntry;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Elias
 */
public class EntryQueryGenerator extends QueryGenerator<EntryProp> {
    private final QEntry entry = QEntry.entry;
    
    @Override
    public Predicate[] getPredicates(Search<EntryProp> s) throws ParseException, IOException {
        List<Predicate> preds = new ArrayList<>();
        
        for (SearchCriteria<EntryProp> crit: s.getCriteria()) {            
            if (crit instanceof StringSearchCriteria) {
                preds.add(getPredicate((StringSearchCriteria<EntryProp>) crit));
            }
            else if (crit instanceof BooleanSearchCriteria) {
                preds.add(getPredicate((BooleanSearchCriteria<EntryProp>) crit));
            }
            else if (crit instanceof DateSearchCriteria) {
                preds.add(getPredicate((DateSearchCriteria<EntryProp>) crit));
            }
            else if (crit instanceof NumberSearchCriteria) {
                preds.add(getPredicate((NumberSearchCriteria<EntryProp>) crit));
            }
            else if (crit instanceof SpatialSearchCriteria) {
                preds.add(getPredicate((SpatialSearchCriteria<EntryProp>) crit));
            }
        }
        
        return preds.toArray(new Predicate[0]);
    }
    
    @Override
    public OrderSpecifier[] getOrders(Search<EntryProp> s) throws IOException {
        List<OrderSpecifier> orders = new ArrayList<>();
        
        for (OrderElement o: s.getOrder()) {      
            if (o.getKey() == EntryProp.LOCATION) {
                throw new IOException("cannot use 'LOCATION' for order");
            }
            
            OrderSpecifier os;
            if (o.getDirection() == OrderDirection.ASC) os = getKey(o.getKey()).asc();
            else os = getKey(o.getKey()).desc();
            orders.add(os);
        }
        
        return orders.toArray(new OrderSpecifier[0]);
    }
    
    @Override
    protected ComparableExpressionBase getKey(EntityProp k) {
        ComparableExpressionBase var = null;
        EntryProp key = (EntryProp) k;
        
        switch (key) {
            case ID:
                var = entry.id;
                break;
            case HEADLINE:
                var = entry.headline;
                break;
            case DESCRIPTION:
                var = entry.description;
                break;
            case ISACTIVE:
                var = entry.isactive;
                break;
            case POSTEDON:
                var = entry.postedon;
                break;
            case SUBJECT:
                var = entry.subject.id;
                break;
            case USERNAME:
                var = entry.user.username;
                break;
            case PRICE:
                var = entry.price;
                break;
        }
        
        return var;
    }
}
