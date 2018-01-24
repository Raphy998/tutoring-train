package at.tutoringtrain.adminclient.ui.search;

/**
 *
 * @author Elias
 * @param <T>
 */
public class DateSearchCriteria<T extends EntityProp> extends SearchCriteria<T> {
    private DateOperation operation;
    
    public DateSearchCriteria() {
        super();
    }
    
    public DateSearchCriteria(T key, DateOperation operation, Object value) {
        this(key, operation, value, false);
    }

    public DateSearchCriteria(T key, DateOperation operation, Object value, boolean not) {
        super(key, value, not);
        checkDataType(key);
        this.operation = operation;
    }

    public DateOperation getOperation() {
        return operation;
    }

    public void setOperation(DateOperation operation) {
        this.operation = operation;
    }
    
    @Override
    public void setKey(T key) {
        checkDataType(key);
        super.setKey(key);
    }
    
    private void checkDataType(T key) throws IllegalArgumentException {
        if (key.getDataType() != DataType.DATE) {
            throw new IllegalArgumentException("key '" + key + "' is not of type " + DataType.DATE);
        }
    }
}
