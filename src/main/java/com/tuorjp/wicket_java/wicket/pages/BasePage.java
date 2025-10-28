package com.tuorjp.wicket_java.wicket.pages;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;

public class BasePage extends WebPage {
    public BasePage() {
        super();
        add(new Label("pageTitle", "Aplicativo de tarefas"));
    }
}
