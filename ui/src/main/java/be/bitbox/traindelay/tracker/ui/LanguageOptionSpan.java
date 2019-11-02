package be.bitbox.traindelay.tracker.ui;

import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;

import java.util.Locale;
import java.util.Objects;

public class LanguageOptionSpan extends Span {
    static final LanguageOptionSpan DUTCH = new LanguageOptionSpan(new Image("frontend/nl_flag.png", "Nederlands"), "Nederlands", Locale.forLanguageTag("nl"));
    static final LanguageOptionSpan FRENCH = new LanguageOptionSpan(new Image("frontend/fr_flag.png", "Français"), "Français", Locale.FRENCH);
    static final LanguageOptionSpan ENGLISH = new LanguageOptionSpan(new Image("frontend/uk_flag.png", "English"), "English", Locale.ENGLISH);

    private final Image image;
    private final String language;
    private final Locale locale;

    private LanguageOptionSpan(Image image, String language, Locale locale) {
        this.image = image;
        this.language = language;
        this.locale = locale;
        add(image);
        add(language);
    }

    static LanguageOptionSpan from(Locale locale) {
        switch (locale.getLanguage()) {
            case "nl":
                return DUTCH;
            case "fr":
                return FRENCH;
            default:
                return ENGLISH;
        }
    }

    Image getImage() {
        return image;
    }

    String getLanguage() {
        return language;
    }

    @Override
    public Locale getLocale() {
        return locale;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LanguageOptionSpan that = (LanguageOptionSpan) o;
        return language.equals(that.language);
    }

    @Override
    public int hashCode() {
        return Objects.hash(language);
    }

    @Override
    public String toString() {
        return language;
    }

}
