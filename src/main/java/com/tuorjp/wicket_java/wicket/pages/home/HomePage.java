package com.tuorjp.wicket_java.wicket.pages.home;

import com.giffing.wicket.spring.boot.context.scan.WicketHomePage;
import com.tuorjp.wicket_java.model.Todo;
import com.tuorjp.wicket_java.service.MongoDBService;
import com.tuorjp.wicket_java.wicket.pages.BasePage;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormSubmitBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.List;

@WicketHomePage
public class HomePage extends BasePage {

    @SpringBean
    MongoDBService mongoDBService;

    FeedbackPanel feedbackPanel;

    public HomePage() {
        Label welcomeLabel = new Label("welcomeMessage", "Aplicação Lista de Tarefas ");
        add(welcomeLabel);

        feedbackPanel = new FeedbackPanel("feedbackPanel");
        feedbackPanel.setOutputMarkupId(true);
        add(feedbackPanel);

        WebMarkupContainer sectionForm = new WebMarkupContainer("sectionForm");
        sectionForm.setOutputMarkupId(true);
        add(sectionForm);

        //formulário principal
        Form<Void> form = new Form("form");
        sectionForm.add(form);

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

        //vincula o model aos campos do formulário
        Todo todoItem = new Todo();
        form.setDefaultModel(new CompoundPropertyModel<Object>(todoItem));
        TextField<String> title = new TextField<>("title");
        TextField<String> body = new TextField<>("body");
        AjaxLink<Void> btnSave = new AjaxLink<Void>("save") {
            @Override
            public void onClick(AjaxRequestTarget ajaxRequestTarget) {
                Todo todo = new Todo();
                todo.setTitle(title.getValue());
                todo.setBody(body.getValue());
                mongoDBService.save(todo);

                todoItem.setTitle("");
                todoItem.setBody("");

                formNew.setVisible(false);
                showInfo(ajaxRequestTarget,"Tarefa adicionada com sucesso");
                ajaxRequestTarget.add(sectionForm);
            }
        };
        btnSave.add(new AjaxFormSubmitBehavior(form, "click") {});
        formNew.add(title, body, btnSave);

        //lista de tarefas
        LoadableDetachableModel<List<Todo>> todoListModel = new LoadableDetachableModel<List<Todo>>() {
            @Override
            protected List<Todo> load() {
                return mongoDBService.fetchAllItems();
            }
        };

        ListView<Todo> todoList = new ListView<>("todoList", todoListModel) {
            @Override
            protected void populateItem(ListItem<Todo> listItem) {
                listItem.add(new CheckBox("selected", new PropertyModel<>(listItem.getModel(), "selected")));
                listItem.add(new Label("title", () -> listItem.getModelObject().getTitle()));
                listItem.add(new Label("body", () -> listItem.getModelObject().getBody()));
            }
        };

        todoList.setReuseItems(true);
        form.add(todoList);
    }

    private void showInfo(AjaxRequestTarget target, String msg) {
        info(msg);
        target.add(feedbackPanel);
    }
}
