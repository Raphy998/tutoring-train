package at.bsd.tutoringtrain.ui.validators;

import com.jfoenix.validation.base.ValidatorBase;
import de.jensd.fx.glyphs.GlyphsBuilder;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.scene.control.TextInputControl;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.validator.routines.EmailValidator;
/**
 *
 * @author Marco Wilscher marco.wilscher@edu.htl-villach.at
 */
public class EmailFieldValidator extends ValidatorBase {
    private static final int MAX_LENGTH = 50;
    
    @Override
    protected void eval() {
        TextInputControl textField;
        setMessage("Required! Address not valid!");
        setIcon(GlyphsBuilder.create(FontAwesomeIconView.class).glyph(FontAwesomeIcon.WARNING).build());    
        if (srcControl.get() instanceof TextInputControl) {
            textField = (TextInputControl) srcControl.get();
            if (textField.getText() == null || StringUtils.isBlank(textField.getText()) || !EmailValidator.getInstance().isValid(textField.getText()) || textField.getLength() > MAX_LENGTH) {
                hasErrors.set(true);
            } else {
                hasErrors.set(false);
            }
        }
    }
}
