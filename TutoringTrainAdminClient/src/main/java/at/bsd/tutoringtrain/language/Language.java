package at.bsd.tutoringtrain.language;

import org.apache.commons.lang.StringUtils;

/**
 *
 * @author Marco Wilscher marco.wilscher@edu.htl-villach.at
 */
public enum Language {
    EN,
    DE;
    
    public String getStringValue() {
        return StringUtils.lowerCase(this.toString());
    }
}
