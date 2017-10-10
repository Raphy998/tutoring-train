package at.bsd.tutoringtrain.network;

import at.bsd.tutoringtrain.network.enumeration.ResultType;
import java.util.ArrayList;
import java.util.Arrays;
import org.apache.commons.lang.StringUtils;

/**
 *
 * @author Marco Wilscher <marco.wilscher@edu.htl-villach.at>
 */
public class Result {
    private final ResultType type;
    private final String[] errorMessages;
    private final int statusCode;
    private final String value;
    
    /**
     * 
     * @param code
     * @param type
     * @param value 
     * @param errorMessage 
     */
    public Result(int code, ResultType type, String value, String... errorMessages) {
        this.statusCode = code;
        this.type = type;
        this.errorMessages = errorMessages;
        this.value = (value == null ? "" : value);
    }

    /**
     * 
     * @return 
     */
    public ResultType getType() {
        return type;
    }

    /**
     * 
     * @return 
     */
    public ArrayList<String> getErrorMessages() {
        return new ArrayList<>(Arrays.asList(errorMessages));
    }

    /**
     * 
     * @return 
     */
    public int getStatusCode() {
        return statusCode;
    }

    /**
     * 
     * @return 
     */
    public String getResultValue() {
        return value;
    }
    
    /**
     * 
     * @return 
     */
    public boolean isError() {
        return type != ResultType.OK;
    }

    /**
     * 
     * @return 
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("(");
        builder.append(statusCode);
        builder.append(") ");
        builder.append(getType().toString());
        if (!StringUtils.isEmpty(value)) {
            builder.append(" Value:'");
            builder.append(value);
            builder.append("'");
        }
        if (errorMessages.length > 0) {
            builder.append(" Error:");
            builder.append("'");
            builder.append(errorMessages[0]);
            builder.append("'");
            for (int i = 1; i < errorMessages.length; i++) {
                builder.append(", '");
                builder.append(errorMessages[i]);
                builder.append("'");
            }
        }
        return builder.toString();
    }
}
