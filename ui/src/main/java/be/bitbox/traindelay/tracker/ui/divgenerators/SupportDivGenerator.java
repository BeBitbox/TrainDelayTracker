package be.bitbox.traindelay.tracker.ui.divgenerators;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.*;
import org.springframework.stereotype.Component;

@Component
public class SupportDivGenerator extends DivGenerator {

    @Override
    public Div asDiv() {
        var div = new Div();
        var locale = UI.getCurrent().getLocale();
        var contactDiv = new Div(
                new H2(translate("support.contact.title", locale)),
                new Paragraph(translate("support.contact.info", locale)),
                new Paragraph(
                        new Span(translate("support.contact.extra", locale)),
                        new Anchor("https://www.bitbox.be", "BitBox"))
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
}
