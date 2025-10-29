package com.tuorjp.wicket_java.wicket.pages.home;

import com.giffing.wicket.spring.boot.context.scan.WicketHomePage;
import com.tuorjp.wicket_java.service.MongoDBService;
import com.tuorjp.wicket_java.wicket.pages.BasePage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.spring.injection.annot.SpringBean;

@WicketHomePage
public class HomePage extends BasePage {

    @SpringBean
    MongoDBService mongoDBService;

    public HomePage() {
        super();

        String message = "Aplicação rodando!";

        Label welcomeLabel = new Label("welcomeMessage", message + mongoDBService.getTodoRepository().count());

        add(welcomeLabel);
    }
}
