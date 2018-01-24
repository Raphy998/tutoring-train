package at.tutoringtrain.adminclient.ui.search;

import java.util.List;

/**
 *
 * @author Elias
 * @param <T>
 */
public interface Search<T extends EntityProp> {
    public List<SearchCriteria<T>> getCriteria();

    public List<OrderElement<T>> getOrder();
}
