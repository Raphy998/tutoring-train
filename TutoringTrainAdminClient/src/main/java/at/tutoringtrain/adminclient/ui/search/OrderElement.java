package at.tutoringtrain.adminclient.ui.search;

/**
 *
 * @author Elias
 * @param <T>
 */
public class OrderElement<T extends EntityProp> {
    private T key;
    private OrderDirection direction;

    public OrderElement() {
    }

    public OrderElement(T key, OrderDirection direction) {
        this.key = key;
        this.direction = direction;
    }

    public T getKey() {
        return key;
    }

    public void setKey(T prop) {
        this.key = prop;
    }

    public OrderDirection getDirection() {
        return direction;
    }

    public void setDirection(OrderDirection direction) {
        this.direction = direction;
    }
}
