package at.tutoringtrain.adminclient.main;

import at.tutoringtrain.adminclient.data.user.Gender;
import at.tutoringtrain.adminclient.data.user.UserRole;
import at.tutoringtrain.adminclient.internationalization.Language;
import at.tutoringtrain.adminclient.security.HashAlgorithm;
import at.tutoringtrain.adminclient.ui.validators.ValidationPattern;
import at.tutoringtrain.adminclient.user.BlockDuration;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.TreeMap;
import java.util.TreeSet;
import javafx.scene.image.Image;
import javafx.stage.FileChooser;
import javax.imageio.ImageIO;
import okhttp3.MediaType;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author Marco Wilscher marco.wilscher@edu.htl-villach.at
 */
public final class DefaultValueProvider {
    
    private static DefaultValueProvider INSTANCE;

    public static DefaultValueProvider getINSTANCE() {
        if (INSTANCE == null) {
            INSTANCE = new DefaultValueProvider();
        }
        return INSTANCE;
    }

    private final Logger logger;
    private final TreeMap<Character, Gender> defaultGenders;
    private final TreeSet<BlockDuration> defaultBlockDurations;
    private final Language defaultLanguage;
    private final HashAlgorithm defaultHashAlgorithm;
    private final int defaultRandomPasswortLength;
    private final FileChooser.ExtensionFilter defaultImageFileExtensionFilter;
    private final String defaultTutoringTrainDirectoryPath;
    private final String defaultTutoringTrainApplicationConfigurationPath;
    private final String defaultTutoringTrainSessionTokenPath;
    private final WebserviceHostInfo defaultWebserviceHostInfo;
    private final ArrayList<WebserviceHostInfo> defaultWebserviceFallbackHosts;
    private final String defaultWebServiceProtokoll;
    private final String defaultWebServiceRootPath;
    private final String defaultWebServiceContextPath;
    private final MediaType jsonMediaType;
    private final MediaType pngImageMediaType;
    private final MediaType jpgImageMediaType;
    private final UserRole minimumRequiredUserRoleForAdminClient;
    private final TreeMap<String, ValidationPattern> defaultValidationPatterns;
    
    private BufferedImage defaultAvatar;
    private BufferedImage defaultSubjectIcon;
    private BufferedImage defaultOfferIcon;
    private BufferedImage defaultRequestIcon;
    private Image defaultApplicationIcon;
    
    private DefaultValueProvider() {
        this.logger = LogManager.getLogger(this);
        this.defaultGenders = new TreeMap<>();
        this.defaultBlockDurations = new TreeSet<>();
        this.defaultLanguage = Language.EN;
        this.defaultHashAlgorithm = HashAlgorithm.MD5;
        this.defaultRandomPasswortLength = 6; 
        this.defaultImageFileExtensionFilter = new FileChooser.ExtensionFilter("Image", "*.jpg", "*.png");
        this.defaultTutoringTrainDirectoryPath = FileUtils.getUserDirectoryPath() + IOUtils.DIR_SEPARATOR + "TutoringTrain";
        this.defaultTutoringTrainApplicationConfigurationPath = this.defaultTutoringTrainDirectoryPath + IOUtils.DIR_SEPARATOR + "config.json";
        this.defaultTutoringTrainSessionTokenPath = this.defaultTutoringTrainDirectoryPath + IOUtils.DIR_SEPARATOR + "session.token";   
        this.jsonMediaType = MediaType.parse("application/json; charset=utf-8");
        this.pngImageMediaType = MediaType.parse("image/png");
        this.jpgImageMediaType = MediaType.parse("image/jpg");
        this.defaultWebserviceHostInfo = new WebserviceHostInfo("tutoringtrain.hopto.org", 8080);
        this.defaultWebserviceFallbackHosts = new ArrayList<>();
        this.defaultWebServiceProtokoll = "http";
        this.defaultWebServiceRootPath = "/TutoringTrainWebservice/services/";
        this.defaultWebServiceContextPath = "/TutoringTrainWebservice/";
        this.minimumRequiredUserRoleForAdminClient = UserRole.ADMIN;
        this.defaultValidationPatterns = new TreeMap<>();
        this.initializeDefaultValidationPatterns();
        this.initializeDefaultAvatar();
        this.initializeDefaultApplicationIcon();
        this.initializeDefaultSubjectIcon();
        this.initializeDefaultRequestIcon();
        this.initializeDefaultOfferIcon();
        this.initializeDefaultGenders();
        this.initializeDefaultBlockDurations();
        this.initializeDefaultWebserviceFallbackHosts();
        this.logger.debug("DefaultValueProvider initialized");
    }
    
    private void initializeDefaultGenders() {
        defaultGenders.put('M', new Gender("M", "Male"));
        defaultGenders.put('F', new Gender("F", "Female"));
        defaultGenders.put('N', new Gender("N", "Others"));
    }
    
    private void initializeDefaultWebserviceFallbackHosts() {
        //defaultWebserviceFallbackHosts.add(new WebserviceHostInfo("tutoringtrain.hopto.org", 8080));
        //defaultWebserviceFallbackHosts.add(new WebserviceHostInfo("localhost", 8080));
    }

    private void initializeDefaultBlockDurations() {
        defaultBlockDurations.addAll(Arrays.asList(BlockDuration.values()));
    }
    
    private void initializeDefaultValidationPatterns() {
        this.defaultValidationPatterns.put("username", new ValidationPattern("^.{1,20}$", "messageValidationUsername"));
        this.defaultValidationPatterns.put("password", new ValidationPattern(".+", "messageValidationPassword"));
        this.defaultValidationPatterns.put("search", new ValidationPattern(".+", "messageValidationSearch"));
        this.defaultValidationPatterns.put("email", new ValidationPattern("^.{1,50}$", "messageValidationEmail"));
        this.defaultValidationPatterns.put("education", new ValidationPattern("^.{1,50}$", "messageValidationEducation"));
        this.defaultValidationPatterns.put("name", new ValidationPattern("^.{1,30}$", "messageValidationName"));
        this.defaultValidationPatterns.put("reason", new ValidationPattern("^.{1,100}$", "messageValidationReason"));
        this.defaultValidationPatterns.put("subjectname", new ValidationPattern("^.{1,25}$", "messageValidationSubjectName"));
        this.defaultValidationPatterns.put("headline", new ValidationPattern("^.{1,50}$", "messageValidationHeadline"));
        this.defaultValidationPatterns.put("description", new ValidationPattern("^.{1,500}$", "messageValidationDescription"));
        this.defaultValidationPatterns.put("serverip", new ValidationPattern("^.+$", "messageValidationServerIp"));
        this.defaultValidationPatterns.put("serverport", new ValidationPattern("^[1-9][0-9]*$", "messageValidationServerPort"));
    }
    
    private void initializeDefaultAvatar() {
        BufferedImage tmpImage = null;
        try {   
            tmpImage = ImageIO.read(getClass().getResource("/images/default_avatar.png"));
        } catch (IOException ioex) {
            logger.error("Loading default avatar failed", ioex);
        } finally {
            this.defaultAvatar = tmpImage;
        }
    }
    
    private void initializeDefaultSubjectIcon() {
        BufferedImage tmpImage = null;
        try {   
            tmpImage = ImageIO.read(getClass().getResource("/images/default_subject.png"));
        } catch (IOException ioex) {
            logger.error("Loading default subject icon failed", ioex);
        } finally {
            this.defaultSubjectIcon = tmpImage;
        }
    }
    
    private void initializeDefaultOfferIcon() {
        BufferedImage tmpImage = null;
        try {   
            tmpImage = ImageIO.read(getClass().getResource("/images/default_offer.png"));
        } catch (IOException ioex) {
            logger.error("Loading default offer icon failed", ioex);
        } finally {
            this.defaultOfferIcon = tmpImage;
        }
    }
    
    private void initializeDefaultRequestIcon() {
        BufferedImage tmpImage = null;
        try {   
            tmpImage = ImageIO.read(getClass().getResource("/images/default_request.png"));
        } catch (IOException ioex) {
            logger.error("Loading default request icon failed", ioex);
        } finally {
            this.defaultRequestIcon = tmpImage;
        }
    }
    
    private void initializeDefaultApplicationIcon() {
        this.defaultApplicationIcon = new Image(getClass().getResource("/images/icon.png").toString());
        
    }
    
    public Language getDefaultLanguage() {
        return defaultLanguage;
    }

    public TreeMap<Character, Gender> getDefaultGenders() {
        return new TreeMap<>(defaultGenders);
    }

    public TreeSet<BlockDuration> getDefaultBlockDurations() {
        return new TreeSet<>(defaultBlockDurations);
    }
    
    public HashAlgorithm getDefaultHashAlgorithm() {
        return defaultHashAlgorithm;
    }

    public int getDefaultRandomPasswortLength() {
        return defaultRandomPasswortLength;
    }

    public FileChooser.ExtensionFilter getDefaultImageFileExtensionFilter() {
        return defaultImageFileExtensionFilter;
    }    

    public String getDefaultTutoringTrainDirectoryPath() {
        return defaultTutoringTrainDirectoryPath;
    }

    public String getDefaultTutoringTrainApplicationConfigurationPath() {
        return defaultTutoringTrainApplicationConfigurationPath;
    }

    public String getDefaultTutoringTrainSessionTokenPath() {
        return defaultTutoringTrainSessionTokenPath;
    }

    public MediaType getJsonMediaType() {
        return jsonMediaType;
    }

    public MediaType getJpgImageMediaType() {
        return jpgImageMediaType;
    }

    public MediaType getPngImageMediaType() {
        return pngImageMediaType;
    }
    
    public WebserviceHostInfo getDefaultWebserviceHostInfo() {
        return new WebserviceHostInfo(defaultWebserviceHostInfo.getHost(), defaultWebserviceHostInfo.getPort());
    }
    
    public ArrayList<WebserviceHostInfo> getDefaultWebserviceHostFallbacks() {
        return new ArrayList<>(defaultWebserviceFallbackHosts);
    }

    public String getDefaultWebServiceProtokoll() {
        return defaultWebServiceProtokoll;
    }

    public String getDefaultWebServiceRootPath() {
        return defaultWebServiceRootPath;
    }

    public String getDefaultWebServiceContextPath() {
        return defaultWebServiceContextPath;
    }

    public UserRole getMinimumRequiredUserRoleForAdminClient() {
        return minimumRequiredUserRoleForAdminClient;
    }

    public BufferedImage getDefaultAvatar() {
        return defaultAvatar;
    }

    public TreeMap<String, ValidationPattern> getDefaultValidationPatterns() {
        return new TreeMap<>(defaultValidationPatterns);
    }
    
    public ValidationPattern getDefaultValidationPattern(String key) {
        return defaultValidationPatterns.get(key);
    }

    public Image getDefaultApplicationIcon() {
        return defaultApplicationIcon;
    }

    public BufferedImage getDefaultSubjectIcon() {
        return defaultSubjectIcon;
    }

    public BufferedImage getDefaultOfferIcon() {
        return defaultOfferIcon;
    }

    public BufferedImage getDefaultRequestIcon() {
        return defaultRequestIcon;
    }
}
