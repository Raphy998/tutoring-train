package at.bsd.tutoringtrain.data.configuration;

import at.bsd.tutoringtrain.language.Language;

/**
 * 
 * @author Marco Wilscher marco.wilscher@edu.htl-villach.at
 */
public class Configuration {
    private Language language;

    public Configuration() {
       language = Language.EN;
    }

    public Language getLanguage() {
        return language;
    }

    public void setLanguage(Language language) {
        this.language = language;
    }
}