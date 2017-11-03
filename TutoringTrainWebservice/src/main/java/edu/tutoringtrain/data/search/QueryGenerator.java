/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.tutoringtrain.data.search;

import com.mysema.query.types.OrderSpecifier;
import com.mysema.query.types.Predicate;
import com.mysema.query.types.expr.ComparableExpressionBase;
import com.mysema.query.types.path.ComparablePath;
import com.mysema.query.types.path.NumberPath;
import com.mysema.query.types.path.StringPath;

/**
 *
 * @author Elias
 * @param <T>
 */
public abstract class QueryGenerator<T extends EntityProp> {
    public abstract Predicate[] getPredicates(Search<T> s);
    public abstract OrderSpecifier[] getOrders(Search<T> s);
    
    protected Predicate getPredicate(StringSearchCriteria crit) {
        Predicate pred = null;
        StringPath key = (StringPath) getKey(crit.getKey());
        
        switch (crit.getOperation()) {
            case EQ:
                if (crit.isIgnoreCase()) pred = key.equalsIgnoreCase(crit.getValue().toString());
                else pred = key.eq(crit.getValue().toString());
                break;
            case CONTAINS:
                if (crit.isIgnoreCase()) pred = key.containsIgnoreCase(crit.getValue().toString());
                else pred = key.contains(crit.getValue().toString());
                break;
            case ENDS_WITH:
                if (crit.isIgnoreCase()) pred = key.endsWithIgnoreCase(crit.getValue().toString());
                else pred = key.endsWith(crit.getValue().toString());
                break;
            case STARTS_WITH:
                if (crit.isIgnoreCase()) pred = key.startsWithIgnoreCase(crit.getValue().toString());
                else pred = key.startsWith(crit.getValue().toString());
                break;
        }

        if (crit.isNot()) {
            pred = pred.not();
        }
        
        return pred;
    }
    
    protected Predicate getPredicate(CharacterSearchCriteria crit) {
        Predicate pred = null;
        ComparablePath<Character> key = (ComparablePath<Character>) getKey(crit.getKey());
        
        switch (crit.getOperation()) {
            case EQ:
                if (crit.isIgnoreCase()) pred = key.in(crit.getValue().toString().toLowerCase().charAt(0),
                        crit.getValue().toString().toUpperCase().charAt(0));
                else pred = key.eq(crit.getValue().toString().charAt(0));
                break;
        }

        if (crit.isNot()) {
            pred = pred.not();
        }
        
        return pred;
    }
    
    protected Predicate getPredicate(NumberSearchCriteria crit) {
        Predicate pred = null;
        NumberPath key = (NumberPath) getKey(crit.getKey());
        
        switch (crit.getOperation()) {
            case EQ:
                key.eq(crit.getValue().toString());
                break;
            case GT:
                pred = key.gt(Integer.valueOf(crit.getValue().toString()));
                break;
            case LT:
                pred = key.lt(Integer.valueOf(crit.getValue().toString()));
                break;
            case GTE:
                pred = key.goe(Integer.valueOf(crit.getValue().toString()));
                break;
            case LTE:
                pred = key.loe(Integer.valueOf(crit.getValue().toString()));
                break;
        }

        if (crit.isNot()) {
            pred = pred.not();
        }
        
        return pred;
    }
    
    protected abstract ComparableExpressionBase getKey(EntityProp key);
}
