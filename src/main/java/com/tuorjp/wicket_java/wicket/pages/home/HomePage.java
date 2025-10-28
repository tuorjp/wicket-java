package com.tuorjp.wicket_java.wicket.pages.home;

import com.giffing.wicket.spring.boot.context.scan.WicketHomePage;
import com.tuorjp.wicket_java.wicket.pages.BasePage;
import org.apache.wicket.markup.html.basic.Label;

@WicketHomePage
public class HomePage extends BasePage {
    public HomePage() {
        super();

        String message = "Aplicação rodando!";

        Label welcomeLabel = new Label("welcomeMessage", message);

        add(welcomeLabel);
    }
}
