package com.tuorjp.wicket_java.wicket.pages;

import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.Url;
import org.apache.wicket.request.resource.UrlResourceReference;

import java.net.MalformedURLException;
import java.net.URL;

public class BasePage extends WebPage {
    public BasePage() {
        super();
        add(new Label("pageTitle", Model.of("Meu App de Tarefas")));
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);

        response.render(CssHeaderItem.forReference(new UrlResourceReference(Url.parse("https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css"))));

        response.render(JavaScriptHeaderItem.forReference(new UrlResourceReference(Url.parse("https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"))));
    }
}
