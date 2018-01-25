package at.tutoringtrain.adminclient.main;

/**
 *
 * @author Marco Wilscher marco.wilscher@edu.htl-villach.at
 */
public class MessageCodes {
    //GENERAL
    public final static int UNDEFINED = -1111;
    public final static int INFO = 100;
    public final static int NOT_IMPLEMENTED_YET = -9999;
    public final static int SEE_APPLICATION_LOG = -8888;
    
    //SUCCESS
    public final static int OK = 0;
   
    //ERROR
    public final static int EXCEPTION = -40;
    public final static int LOGIN_REQUIRED = -60;
    public final static int NULL = -80;
    public final static int MAPPING_FAILED = -100;
    public final static int REQUEST_FAILED = -120;
    public final static int LOADING_FAILED = -140;
}
