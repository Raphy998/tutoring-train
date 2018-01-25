package at.tutoringtrain.adminclient.io.file;

import at.tutoringtrain.adminclient.datamapper.DataMapper;
import at.tutoringtrain.adminclient.exception.InitializationFailedException;
import at.tutoringtrain.adminclient.main.ApplicationConfiguration;
import at.tutoringtrain.adminclient.main.ApplicationManager;
import at.tutoringtrain.adminclient.main.DefaultValueProvider;
import at.tutoringtrain.adminclient.security.EncryptionService;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * File operations
 * @author Marco Wilscher marco.wilscher@edu.htl-villach.at
 */
public class FileService {
    //INSECURE
    private static final String SESSION_TOKEN_AES_KEY = "QfTjWnZr4u7x!A%D";
    private static final String SESSION_TOKEN_CBC_IV = "QfTjWnZr4u7x!A%D";
    private static FileService INSTANCE;
    
    public static FileService getINSTANCE() {
        if (INSTANCE == null) {
            INSTANCE = new FileService();
        }
        return INSTANCE;
    }
    
    private final ApplicationManager applicationManager;
    private final Logger logger;   
    private final DefaultValueProvider defaultValueProvider;
    private final DataMapper dataMapper;
    
    private EncryptionService encryptionService;
    
    private FileService() {
        this.applicationManager = ApplicationManager.getInstance();
        this.logger = LogManager.getLogger(this);
        this.defaultValueProvider = ApplicationManager.getDefaultValueProvider();
        this.dataMapper = ApplicationManager.getDataMapper();
        this.encryptionService = null;
        this.logger.debug("FileService initialized");
    }
    
    private boolean isAvailable(File file) {
        return file.exists();
    }

    private boolean deleteFile(File file) {
        return FileUtils.deleteQuietly(file);
    }
    
    private String readFile(File file) throws IOException {
        return FileUtils.readFileToString(file, Charset.forName("UTF-8"));
    }

    private void writeFile(File file, String data) throws IOException {
        FileUtils.writeStringToFile(file, data, Charset.forName("UTF-8"));
    }
    
    public boolean isConfigFileAvailable() {
        return isAvailable(new File(defaultValueProvider.getDefaultTutoringTrainApplicationConfigurationPath()));
    }

    public boolean isTokenFileAvailable() {
        return isAvailable(new File(defaultValueProvider.getDefaultTutoringTrainSessionTokenPath()));
    }
    
    private boolean isEncryptionServiceInitialized() {
        return encryptionService != null;
    }
    
    private void initializeEncryptionService() {
        if (!isEncryptionServiceInitialized()) {
            try {
                encryptionService = new EncryptionService(SESSION_TOKEN_AES_KEY, SESSION_TOKEN_CBC_IV);
            } catch (Exception ex) {
                encryptionService = null;
            }
        }
    }
    
    public void writeConfig(ApplicationConfiguration configuration) throws IOException {
        writeFile(new File(defaultValueProvider.getDefaultTutoringTrainApplicationConfigurationPath()), dataMapper.toJson(configuration));
    }
    
    public void writeTokenFile(String token) throws Exception { 
        initializeEncryptionService();
        if (isEncryptionServiceInitialized()) {
            writeFile(new File(defaultValueProvider.getDefaultTutoringTrainSessionTokenPath()), encryptionService.encrypt(token));
        } else {
            throw new InitializationFailedException();
        }
    }

    public ApplicationConfiguration readConfig() {
        File file = new File(defaultValueProvider.getDefaultTutoringTrainApplicationConfigurationPath());
        ApplicationConfiguration config = null; 
        if (isAvailable(file)) {
            try {
                config = dataMapper.toApplicationConfiguration(readFile(file)); 
            } catch (IOException ioex) {
                config = null;
            }
        }
        return config;
    }
    
    public String readTokenFile() throws Exception {
        String value = null;
        initializeEncryptionService();
        if (isEncryptionServiceInitialized()) {
            value = encryptionService.decrypt(readFile(new File(defaultValueProvider.getDefaultTutoringTrainSessionTokenPath())));
        } else {
            throw new InitializationFailedException();
        }
        return value;
    }

    public boolean deleteTokenFile() {
        return deleteFile(new File(defaultValueProvider.getDefaultTutoringTrainSessionTokenPath()));
    }
    
    public boolean deleteConfigFile() {
        return deleteFile(new File(defaultValueProvider.getDefaultTutoringTrainApplicationConfigurationPath()));
    }
}
