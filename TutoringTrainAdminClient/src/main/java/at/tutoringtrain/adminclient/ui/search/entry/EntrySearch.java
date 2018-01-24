/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.tutoringtrain.adminclient.ui.search.entry;

import at.tutoringtrain.adminclient.ui.search.OrderElement;
import at.tutoringtrain.adminclient.ui.search.Search;
import at.tutoringtrain.adminclient.ui.search.SearchCriteria;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Elias
 */
public class EntrySearch implements Search<EntryProp> {
    List<SearchCriteria<EntryProp>> criteria;
    List<OrderElement<EntryProp>> order;

    public EntrySearch() {
        criteria = new ArrayList<>();
        order = new ArrayList<>();
    }
    
    public EntrySearch(List<SearchCriteria<EntryProp>> criteria, List<OrderElement<EntryProp>> order) {
        this.criteria = criteria;
        this.order = order;
    }

    @Override
    public List<SearchCriteria<EntryProp>> getCriteria() {
        return criteria;
    }

    @Override
    public List<OrderElement<EntryProp>> getOrder() {
        return order;
    }

    @Override
    public String toString() {
        return "UserSearch{" + "criteria=" + criteria + ", order=" + order + '}';
    }
}
