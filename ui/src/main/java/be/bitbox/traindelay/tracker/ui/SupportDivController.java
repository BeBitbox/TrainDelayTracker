package be.bitbox.traindelay.tracker.ui;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.server.VaadinService;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
public class SupportDivController {
    Div asDiv() {
        var div = new Div();
        var locale = UI.getCurrent().getLocale();
        var contactDiv = new Div(
                new H2(translate("support.contact.title", locale)),
                new Paragraph(translate("support.contact.info", locale)),
                new Paragraph(
                        new Span(translate("support.contact.extra", locale)),
                        new Anchor("https://www.bitbox.be","BitBox"))
        );
        contactDiv.setId("contactDiv");
        var developDiv = new Div(
                new H2(translate("support.develop.title", locale)),
                new Paragraph(translate("support.develop.info", locale)),
                new Paragraph(
                        new Span(translate("support.develop.extra", locale)),
                        new Anchor("https://github.com/BeBitbox/TrainDelayTracker", "GitHub"))
        );
        developDiv.setId("developDiv");
        div.add(contactDiv, developDiv);
        return div;
    }

    private String translate(String key, Locale locale) {
        return VaadinService.getCurrent().getInstantiator().getI18NProvider().getTranslation(key, locale);
    }
}
