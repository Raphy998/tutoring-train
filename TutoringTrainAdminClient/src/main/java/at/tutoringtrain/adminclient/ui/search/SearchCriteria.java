package at.tutoringtrain.adminclient.ui.search;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 *
 * @author Elias
 * @param <T>
 */
public abstract class SearchCriteria<T extends EntityProp> {
    private T key;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssZ")
    private Object value;
    private boolean not;

    public SearchCriteria() {
    }
    
    public SearchCriteria(T key, Object value) {
        this.key = key;
        this.value = value;
    }

    public SearchCriteria(T key, Object value, boolean not) {
        this.key = key;
        this.value = value;
        this.not = not;
    }

    public T getKey() {
        return key;
    }

    public void setKey(T key) {
        this.key = key;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public boolean isNot() {
        return not;
    }

    public void setNot(boolean not) {
        this.not = not;
    }
}
