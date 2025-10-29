package com.tuorjp.wicket_java.wicket.pages.home;

import com.giffing.wicket.spring.boot.context.scan.WicketHomePage;
import com.tuorjp.wicket_java.model.Todo;
import com.tuorjp.wicket_java.service.MongoDBService;
import com.tuorjp.wicket_java.wicket.pages.BasePage;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
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

        //formulário principal
        Form<Void> form = new Form("form");
        add(form);

        WebMarkupContainer formNew = new WebMarkupContainer("formNew");
        AjaxLink<Void> btnAdd = new AjaxLink<>("addItemLink") {
            @Override
            public void onClick(AjaxRequestTarget ajaxRequestTarget) {
                formNew.setVisible(!formNew.isVisible());
                //a linha acima muda o status no backend e a linha abaixo atualiza a UI
                ajaxRequestTarget.add(formNew);
            }
        };
        form.add(btnAdd);

        //adicionando formulário invisível de novo item dentro do form principal
        formNew.setOutputMarkupPlaceholderTag(true);
        formNew.setVisible(false);
        form.add(formNew);

        TextField<String> title = new TextField<>("title");
        TextField<String> body = new TextField<>("body");
        AjaxLink<Void> btnSave = new AjaxLink<Void>("save") {
            @Override
            public void onClick(AjaxRequestTarget ajaxRequestTarget) {

            }
        };
        formNew.add(title, body, btnSave);

        //lista de tarefas
        List<Todo> todos = mongoDBService.fetchAllItems();

        ListView<Todo> todoList = new ListView<>("todoList", todos) {
            @Override
            protected void populateItem(ListItem<Todo> listItem) {
                listItem.add(new Label("title", new PropertyModel<String>(listItem.getModel(), "title")));
                listItem.add(new Label("body", () -> listItem.getModelObject().getBody()));
            }
        };

        add(todoList);
    }
}
