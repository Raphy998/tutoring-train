package at.bsd.tutoringtrain.io.file;

import at.bsd.tutoringtrain.data.configuration.Configuration;
import at.bsd.tutoringtrain.data.mapper.DataMapper;
import at.bsd.tutoringtrain.security.Encrypter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

/**
 * File operations
 * @author Marco Wilscher marco.wilscher@edu.htl-villach.at
 */
public class FileService {
    private static final String SESSION_TOKEN_AES_KEY = "QfTjWnZr4u7x!A%D";
    private static final String SESSION_TOKEN_CBC_IV = "QfTjWnZr4u7x!A%D";
    private static FileService INSTANCE;
    
    public static FileService getINSTANCE() {
        if (INSTANCE == null) {
            INSTANCE = new FileService();
        }
        return INSTANCE;
    }
    
    private final String defaultPath;
    private final String configPath;
    private final String tokenPath;
    
    private FileService() {
        defaultPath = FileUtils.getUserDirectoryPath() + IOUtils.DIR_SEPARATOR + "TutoringTrain";
        configPath = defaultPath + IOUtils.DIR_SEPARATOR + "config.json";
        tokenPath = defaultPath + IOUtils.DIR_SEPARATOR + "session.token";
    }
    
    /**
     * 
     * @param file
     * @return 
     */
    private boolean isAvailable(File file) {
        return file.exists();
    }
    
    /**
     * 
     * @param file
     * @return 
     */
    private boolean deleteFile(File file) {
        return FileUtils.deleteQuietly(file);
    }
    
    /**
     * 
     * @param file
     * @return
     * @throws IOException 
     */
    private String readFile(File file) throws IOException {
        return FileUtils.readFileToString(file, Charset.forName("UTF-8"));
    }
    
    /**
     * 
     * @param file
     * @param data
     * @throws IOException 
     */
    private void writeFile(File file, String data) throws IOException {
        FileUtils.writeStringToFile(file, data, Charset.forName("UTF-8"));
    }
    
    /**
     * Determines wheather an configuration file is available on the computer
     * @return existence
     */
    public boolean isConfigFileAvailable() {
        return isAvailable(new File(configPath));
    }
    
    /**
     * 
     * @return 
     */
    public boolean isTokenFileAvailable() {      
        return isAvailable(new File(tokenPath));
    }
    
    /**
     * Writes the configuration object to the specified configuration path
     * @param configuration configuration object
     * @throws IOException 
     */
    public void writeConfig(Configuration configuration) throws IOException {
        writeFile(new File(configPath), DataMapper.toJson(configuration));
    }
    
    /**
     * 
     * @param token
     * @throws IOException 
     */
    public void writeTokenFile(String token) throws Exception { 
        writeFile(new File(tokenPath), Encrypter.encrypt(SESSION_TOKEN_AES_KEY, SESSION_TOKEN_CBC_IV, token));
    }
    
    /**
     * Reads the value of the configuration file if available 
     * @return 
     */
    public Configuration readConfig() {
        File file;
        Configuration tmpConfig;
        tmpConfig = new Configuration();
        file = new File(configPath);
        if (isAvailable(file)) {
            try {
                tmpConfig = DataMapper.toConfiguration(readFile(file)); 
            } catch (IOException ioex) {
                tmpConfig = new Configuration();
            }
        }
        return tmpConfig;
    }
    
    /**
     * 
     * @return
     * @throws IOException 
     */
    public String readTokenFile() throws Exception {
        return Encrypter.decrypt(SESSION_TOKEN_AES_KEY, SESSION_TOKEN_CBC_IV, readFile(new File(tokenPath)));
    }

    /**
     * 
     * @return 
     */
    public boolean deleteTokenFile() {
        return deleteFile(new File(tokenPath));
    }
    
    /**
     * 
     * @return 
     */
    public boolean deleteConfigFile() {
        return deleteFile(new File(configPath));
    }
}
