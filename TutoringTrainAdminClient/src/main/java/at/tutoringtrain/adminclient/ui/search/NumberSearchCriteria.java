package at.tutoringtrain.adminclient.ui.search;

/**
 *
 * @author Elias
 * @param <T>
 */
public class NumberSearchCriteria<T extends EntityProp>  extends SearchCriteria<T> {
    private NumberOperation operation;

    public NumberSearchCriteria() {
        super();
    }

    public NumberSearchCriteria(T key, NumberOperation operation, Object value) {
        super(key, value);
        checkDataType(key);
        this.operation = operation;
    }

    public NumberOperation getOperation() {
        return operation;
    }

    public void setOperation(NumberOperation operation) {
        this.operation = operation;
    }
    
    @Override
    public void setKey(T key) {
        checkDataType(key);
        super.setKey(key);
    }
    
    private void checkDataType(T key) throws IllegalArgumentException {
        if (key.getDataType() != DataType.NUMBER) {
            throw new IllegalArgumentException("key '" + key + "' is not of type " + DataType.NUMBER);
        }
    }
}
