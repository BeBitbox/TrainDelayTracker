package be.bitbox.traindelay.tracker.ui.divgenerators;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.server.VaadinService;

import java.util.Locale;

abstract class DivGenerator {
    public abstract  Div asDiv();

    protected String translate(String key, Locale locale) {
        return VaadinService.getCurrent().getInstantiator().getI18NProvider().getTranslation(key, locale);
    }
}
