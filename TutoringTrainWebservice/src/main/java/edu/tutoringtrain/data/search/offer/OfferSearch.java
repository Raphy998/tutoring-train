/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.tutoringtrain.data.search.offer;

import edu.tutoringtrain.data.search.OrderElement;
import edu.tutoringtrain.data.search.Search;
import edu.tutoringtrain.data.search.SearchCriteria;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Elias
 */
public class OfferSearch implements Search<OfferProp> {
    List<SearchCriteria<OfferProp>> criteria;
    List<OrderElement<OfferProp>> order;

    public OfferSearch() {
        criteria = new ArrayList<>();
        order = new ArrayList<>();
    }
    
    public OfferSearch(List<SearchCriteria<OfferProp>> criteria, List<OrderElement<OfferProp>> order) {
        this.criteria = criteria;
        this.order = order;
    }

    @Override
    public List<SearchCriteria<OfferProp>> getCriteria() {
        return criteria;
    }

    @Override
    public List<OrderElement<OfferProp>> getOrder() {
        return order;
    }

    @Override
    public String toString() {
        return "UserSearch{" + "criteria=" + criteria + ", order=" + order + '}';
    }
}
