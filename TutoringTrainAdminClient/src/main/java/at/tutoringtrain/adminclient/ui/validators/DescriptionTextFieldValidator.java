package at.tutoringtrain.adminclient.ui.validators;

import at.tutoringtrain.adminclient.internationalization.LocalizedValueProvider;
import at.tutoringtrain.adminclient.main.ApplicationManager;
import com.jfoenix.validation.base.ValidatorBase;
import de.jensd.fx.glyphs.GlyphsBuilder;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.scene.control.TextInputControl;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.validator.routines.RegexValidator;

/**
 *
 * @author Marco Wilscher marco.wilscher@edu.htl-villach.at
 */
public class DescriptionTextFieldValidator extends ValidatorBase {
    protected final ValidationPattern validationPattern;
    protected final LocalizedValueProvider localizedValueProvider;
    
    public DescriptionTextFieldValidator(ValidationPattern validationPattern) {
        super();
        this.validationPattern = validationPattern;
        this.localizedValueProvider = ApplicationManager.getLocalizedValueProvider();
    }
    
    @Override
    protected void eval() {
        TextInputControl textField;
        RegexValidator regexValidator;
        setMessage(localizedValueProvider.getString(validationPattern.getMessageKey()));
        setIcon(GlyphsBuilder.create(FontAwesomeIconView.class).glyph(FontAwesomeIcon.WARNING).build());    
        if (srcControl.get() instanceof TextInputControl) {
            textField = (TextInputControl) srcControl.get();
            regexValidator = new RegexValidator(validationPattern.getPattern(), false);
            if (textField.getText() == null || StringUtils.isBlank(textField.getText()) || textField.getLength() > 500) {
                hasErrors.set(true);
            } else {
                hasErrors.set(false);
            }
        }
    }
}
