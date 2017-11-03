/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.tutoringtrain.data.search;

import com.mysema.query.types.OrderSpecifier;
import com.mysema.query.types.Predicate;
import com.mysema.query.types.expr.ComparableExpression;
import com.mysema.query.types.expr.ComparableExpressionBase;
import edu.tutoringtrain.entities.QUser;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Elias
 */
public class UserQueryGenerator extends QueryGenerator<UserProp> {
    private final QUser user = QUser.user;
    
    @Override
    public Predicate[] getPredicates(Search<UserProp> s) {
        List<Predicate> preds = new ArrayList<>();
        
        for (SearchCriteria crit: s.getCriteria()) {            
            if (crit instanceof StringSearchCriteria) {
                preds.add(getPredicate((StringSearchCriteria<UserProp>) crit));
            }
            else if (crit instanceof CharacterSearchCriteria) {
                preds.add(getPredicate((CharacterSearchCriteria<UserProp>) crit));
            }
            else if (crit instanceof NumberSearchCriteria) {
                preds.add(getPredicate((CharacterSearchCriteria<UserProp>) crit));
            }
        }
        
        return preds.toArray(new Predicate[0]);
    }
    
    @Override
    public OrderSpecifier[] getOrders(Search<UserProp> s) {
        List<OrderSpecifier> orders = new ArrayList<>();
        
        for (OrderElement o: s.getOrder()) {      
            OrderSpecifier os;
            if (o.getDirection() == OrderDirection.ASC) os = getKey(o.getProp()).asc();
            else os = getKey(o.getProp()).desc();
            orders.add(os);
        }
        
        return orders.toArray(new OrderSpecifier[0]);
    }
    
    @Override
    protected ComparableExpressionBase getKey(EntityProp k) {
        ComparableExpression var = null;
        UserProp key = (UserProp) k;
        
        switch (key) {
            case USERNAME:
                var = user.username;
                break;
            case NAME:
                var = user.name;
                break;
            case EDUCATION:
                var = user.education;
                break;
            case GENDER:
                var = user.gender;
                break;
            case ROLE:
                var = user.role;
                break;
        }
        
        return var;
    }
}
