package com.tuorjp.wicket_java.wicket.pages.home;

import com.giffing.wicket.spring.boot.context.scan.WicketHomePage;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;

@WicketHomePage
public class HomePage extends WebPage {
    public HomePage() {
        super();

        String message = "Aplicação rodando!";

        Label welcomeLabel = new Label("welcomeMessage", message);

        add(welcomeLabel);
    }
}
