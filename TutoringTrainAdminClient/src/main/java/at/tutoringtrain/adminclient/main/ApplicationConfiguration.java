package at.tutoringtrain.adminclient.main;

/**
 * Application configuration
 * @author Marco Wilscher marco.wilscher@edu.htl-villach.at
 */
public class ApplicationConfiguration {
    private Language language;

    public ApplicationConfiguration() {
        this.language = Language.EN;
    }

    public ApplicationConfiguration(Language language) {
        this.language = language;
    }

    public Language getLanguage() {
        return language;
    }

    public void setLanguage(Language language) {
        this.language = language;
    }
}