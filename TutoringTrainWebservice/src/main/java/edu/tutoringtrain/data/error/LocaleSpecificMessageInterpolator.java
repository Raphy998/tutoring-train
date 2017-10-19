/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.tutoringtrain.data.error;

import java.util.Locale;
import javax.validation.MessageInterpolator;

/**
 *
 * @author Elias
 */
public class LocaleSpecificMessageInterpolator implements MessageInterpolator {
    private final MessageInterpolator defaultInterpolator;
    private final Locale defaultLocale;

    public LocaleSpecificMessageInterpolator(MessageInterpolator interpolator, Locale locale) {
        this.defaultLocale = locale;
        this.defaultInterpolator = interpolator;
    }

    /**
     * enforces the locale passed to the interpolator
     */
    public String interpolate(String message, 
                              Context context) {
        return defaultInterpolator.interpolate(message, 
                                               context, 
                                               this.defaultLocale);
    }

    // no real use, implemented for completeness
    public String interpolate(String message,
                              Context context,
                              Locale locale) {
        return defaultInterpolator.interpolate(message, context, locale);
    }
}
