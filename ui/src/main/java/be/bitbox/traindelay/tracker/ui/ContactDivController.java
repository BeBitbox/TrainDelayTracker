package be.bitbox.traindelay.tracker.ui;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Paragraph;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ContactDivController {

    @Autowired
    public ContactDivController() {
    }

    Div asDiv() {
        var div = new Div();
        div.add(new Paragraph("HELLO"));
        return div;
    }
}
