package at.tutoringtrain.adminclient.ui.search;

/**
 *
 * @author Elias
 * @param <T>
 */
public class CharacterSearchCriteria<T extends EntityProp> extends SearchCriteria<T> {
    private CharacterOperation operation;
    private boolean ignoreCase;

    public CharacterSearchCriteria() {
        super();
    }
    
    public CharacterSearchCriteria(T key, CharacterOperation operation, Object value) {
        this(key, operation, value, false, false);
    }

    public CharacterSearchCriteria(T key, CharacterOperation operation, Object value, boolean ignoreCase) {
        this(key, operation, value, ignoreCase, false);
    }
    
    public CharacterSearchCriteria(T key, CharacterOperation operation, Object value, boolean ignoreCase, boolean not) {
        super(key, value, not);
        checkDataType(key);
        this.operation = operation;
        this.ignoreCase = ignoreCase;
    }

    public CharacterOperation getOperation() {
        return operation;
    }

    public void setOperation(CharacterOperation operation) {
        this.operation = operation;
    }

    public boolean isIgnoreCase() {
        return ignoreCase;
    }

    public void setIgnoreCase(boolean ignoreCase) {
        this.ignoreCase = ignoreCase;
    }
    
    @Override
    public void setKey(T key) {
        checkDataType(key);
        super.setKey(key);
    }
    
    private void checkDataType(T key) throws IllegalArgumentException {
        if (key.getDataType() != DataType.CHAR) {
            throw new IllegalArgumentException("key '" + key + "' is not of type " + DataType.CHAR);
        }
    }
}
