package com.tuorjp.wicket_java.wicket.pages.home;

import com.giffing.wicket.spring.boot.context.scan.WicketHomePage;
import com.tuorjp.wicket_java.model.Todo;
import com.tuorjp.wicket_java.service.MongoDBService;
import com.tuorjp.wicket_java.wicket.pages.BasePage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.List;

@WicketHomePage
public class HomePage extends BasePage {

    @SpringBean
    MongoDBService mongoDBService;

    public HomePage() {
        Label welcomeLabel = new Label("welcomeMessage", "Aplicação rodando! ");
        add(welcomeLabel);

        List<Todo> todos = mongoDBService.fetchAllItems();

        ListView<Todo> todoList = new ListView<>("todoList", todos) {
            @Override
            protected void populateItem(ListItem listItem) {
                listItem.add(new Label("title", new PropertyModel<String>(listItem.getModel(), "title")));
                listItem.add(new Label("body", new PropertyModel<String>(listItem.getModel(), "body")));
            }
        };

        add(todoList);
    }
}
