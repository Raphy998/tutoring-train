package at.tutoringtrain.adminclient.ui.validators;

import de.jensd.fx.glyphs.GlyphsBuilder;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.scene.control.TextInputControl;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.validator.routines.InetAddressValidator;
import org.apache.commons.validator.routines.RegexValidator;
/**
 *
 * @author Marco Wilscher marco.wilscher@edu.htl-villach.at
 */
public class IpFieldValidator extends TextFieldValidator {
    public IpFieldValidator(ValidationPattern validationPattern) {
        super(validationPattern);
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
            if (textField.getText() == null || StringUtils.isBlank(textField.getText()) || !regexValidator.isValid(textField.getText()) || !InetAddressValidator.getInstance().isValidInet4Address(textField.getText())) {
                hasErrors.set(true);
            } else {
                hasErrors.set(false);
            }
        }
    }
}
