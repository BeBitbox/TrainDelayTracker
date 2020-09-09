package be.bitbox.traindelay.tracker.ui;

import com.vaadin.flow.i18n.I18NProvider;
import com.vaadin.flow.spring.annotation.SpringComponent;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

@SpringComponent
public class BelgianProvider implements I18NProvider {
    private final ResourceBundle dutch;
    private final ResourceBundle french;
    private final ResourceBundle english;

    @Autowired
    public BelgianProvider() {
        dutch = ResourceBundle.getBundle("translation", LanguageOptionSpan.DUTCH.getLocale());
        french = ResourceBundle.getBundle("translation", LanguageOptionSpan.FRENCH.getLocale());
        english = ResourceBundle.getBundle("translation", LanguageOptionSpan.ENGLISH.getLocale());
    }

    @Override
    public List<Locale> getProvidedLocales() {
        return List.of(LanguageOptionSpan.DUTCH.getLocale(), LanguageOptionSpan.FRENCH.getLocale(), LanguageOptionSpan.ENGLISH.getLocale());
    }

    @Override
    public String getTranslation(String key, Locale locale, Object... objects) {
        if ("nl".equals(locale.getLanguage())) {
            return dutch.getString(key);
        } else if ("fr".equals(locale.getLanguage())) {
            return french.getString(key);
        } else {
            return english.getString(key);
        }
    }
}
