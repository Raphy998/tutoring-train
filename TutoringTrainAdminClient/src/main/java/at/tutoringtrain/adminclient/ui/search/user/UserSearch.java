package at.tutoringtrain.adminclient.ui.search.user;

import at.tutoringtrain.adminclient.ui.search.OrderElement;
import at.tutoringtrain.adminclient.ui.search.Search;
import at.tutoringtrain.adminclient.ui.search.SearchCriteria;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Elias
 */
public class UserSearch implements Search<UserProp> {
    List<SearchCriteria<UserProp>> criteria;
    List<OrderElement<UserProp>> order;

    public UserSearch() {
        criteria = new ArrayList<>();
        order = new ArrayList<>();
    }
    
    public UserSearch(List<SearchCriteria<UserProp>> criteria, List<OrderElement<UserProp>> order) {
        this.criteria = criteria;
        this.order = order;
    }

    @Override
    public List<SearchCriteria<UserProp>> getCriteria() {
        return criteria;
    }

    @Override
    public List<OrderElement<UserProp>> getOrder() {
        return order;
    }

    @Override
    public String toString() {
        return "UserSearch{" + "criteria=" + criteria + ", order=" + order + '}';
    }
}
