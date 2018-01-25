package at.tutoringtrain.adminclient.ui.search;

/**
 *
 * @author Elias
 * @param <T>
 */
public class BooleanSearchCriteria<T extends EntityProp> extends SearchCriteria<T> {
    private BooleanOperation operation;

    public BooleanSearchCriteria() {
        super();
    }
    
    public BooleanSearchCriteria(T key, BooleanOperation operation, Object value) {
        this(key, operation, value, false);
    }
    
    public BooleanSearchCriteria(T key, BooleanOperation operation, Object value, boolean not) {
        super(key, value, not);
        checkDataType(key);
        this.operation = operation;
    }

    public BooleanOperation getOperation() {
        return operation;
    }

    public void setOperation(BooleanOperation operation) {
        this.operation = operation;
    }
    
    @Override
    public void setKey(T key) {
        checkDataType(key);
        super.setKey(key);
    }
    
    private void checkDataType(T key) throws IllegalArgumentException {
        if (key.getDataType() != DataType.BOOLEAN) {
            throw new IllegalArgumentException("key '" + key + "' is not of type " + DataType.BOOLEAN);
        }
    }
}
