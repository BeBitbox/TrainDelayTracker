package be.bitbox.traindelay.tracker.ui;

import com.vaadin.flow.i18n.I18NProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Locale;

@Component
public class BelgianProvider implements I18NProvider {

    public static final Locale BELGIAN_LOCAL = new Locale("nl", "be");

    @Autowired
    public BelgianProvider() {
        Locale.setDefault(BELGIAN_LOCAL);
    }

    @Override
    public List<Locale> getProvidedLocales() {
        return List.of(BELGIAN_LOCAL);
    }

    @Override
    public String getTranslation(String s, Locale locale, Object... objects) {
        return null;
    }
}
