package com.tuorjp.wicket_java.wicket.pages.home;

import com.giffing.wicket.spring.boot.context.scan.WicketHomePage;
import com.tuorjp.wicket_java.model.Todo;
import com.tuorjp.wicket_java.service.ExcelGeneratorService;
import com.tuorjp.wicket_java.service.MongoDBService;
import com.tuorjp.wicket_java.wicket.pages.BasePage;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.extensions.ajax.AjaxDownloadBehavior;
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
import org.apache.wicket.request.resource.AbstractResource;
import org.apache.wicket.request.resource.ContentDisposition;
import org.apache.wicket.request.resource.IResource;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.io.IOException;
import java.util.List;

@WicketHomePage
public class HomePage extends BasePage {

    @SpringBean
    private MongoDBService mongoDBService;

    @SpringBean
    private ExcelGeneratorService excelGeneratorService;

    private FeedbackPanel feedbackPanel;

    LoadableDetachableModel<List<Todo>> todoListModel;

    public HomePage() {
        Label welcomeLabel = new Label("welcomeMessage", "Aplicação Lista de Tarefas ");
        add(welcomeLabel);

        feedbackPanel = new FeedbackPanel("feedbackPanel");
        feedbackPanel.setOutputMarkupId(true);
        add(feedbackPanel);

        WebMarkupContainer sectionForm = new WebMarkupContainer("sectionForm");
        sectionForm.setOutputMarkupId(true);
        add(sectionForm);

        LoadableDetachableModel<Todo> newTodoModel = new LoadableDetachableModel<Todo>() {
            @Override
            protected Todo load() {
                return new Todo();
            }
        };

        //formulário principal
        Form<Todo> form = new Form<>("form", new CompoundPropertyModel<>(newTodoModel));
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

        AjaxSubmitLink btnRemove = new AjaxSubmitLink("remove", form){
            @Override
            protected void onSubmit(AjaxRequestTarget ajaxRequestTarget) {
                List<Todo> todos = todoListModel.getObject();

                List<Todo> todosToRemove = todos.stream()
                        .filter(Todo::isSelected)
                        .toList();

                if (todosToRemove.isEmpty()) {
                    showInfo(ajaxRequestTarget, "Nenhum item selecionado.");
                    return;
                }

                mongoDBService.removeItems(todosToRemove);

                todoListModel.detach();

                showInfo(ajaxRequestTarget, todosToRemove.size() + " itens selecionados foram removidos.");
                ajaxRequestTarget.add(sectionForm);
            }
        };

        IResource dynamicExcelResource = new AbstractResource() {
            @Override
            protected ResourceResponse newResourceResponse(Attributes attributes) {
                ResourceResponse response = new ResourceResponse();

                response.setFileName("excel-todos.xlsx");
                response.setContentDisposition(ContentDisposition.ATTACHMENT);

                response.setWriteCallback(new WriteCallback() {
                    @Override
                    public void writeData(Attributes attributes) throws IOException {
                        try {
                            List<Todo> todos = todoListModel.getObject();
                            Workbook wb = excelGeneratorService.createExcelFile(todos);

                            wb.write(attributes.getResponse().getOutputStream());
                            wb.close();
                        } catch (Exception e) {
                            throw new IOException("Erro ao gerar o arquivo Excel", e);
                        }
                    }
                });

                return response;
            }
        };

        AjaxDownloadBehavior downloadExcel = new AjaxDownloadBehavior(dynamicExcelResource);


        AjaxLink<Void> downloadExcelBtn = new AjaxLink<Void>("downloadExcelBtn") {
            @Override
            public void onClick(AjaxRequestTarget ajaxRequestTarget) {
                downloadExcel.initiate(ajaxRequestTarget);
            }
        };

        form.add(btnAdd, btnRemove, downloadExcelBtn);
        form.add(downloadExcel);

        //adicionando formulário invisível de novo item dentro do form principal
        formNew.setOutputMarkupPlaceholderTag(true);
        formNew.setVisible(false);
        form.add(formNew);

        //vincula o model aos campos do formulário
        form.setDefaultModel(new CompoundPropertyModel<>(newTodoModel));
        TextField<String> title = new TextField<>("title");
        TextField<String> body = new TextField<>("body");

        AjaxSubmitLink btnSave = new AjaxSubmitLink("save", form) {
            @Override
            protected void onSubmit(AjaxRequestTarget ajaxRequestTarget) {
                Todo todoToSave = form.getModelObject();

                mongoDBService.save(todoToSave);

                todoListModel.detach();

                formNew.setVisible(false);
                showInfo(ajaxRequestTarget,"Tarefa adicionada com sucesso");
                ajaxRequestTarget.add(sectionForm);
            }
        };
        formNew.add(title, body, btnSave);

        //lista de tarefas
        todoListModel = new LoadableDetachableModel<List<Todo>>() {
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

        AjaxLink<Void> btnSelectAll = new AjaxLink<>("btnSelectAll") {
            @Override
            public void onClick(AjaxRequestTarget ajaxRequestTarget) {
                List<Todo> todos = todoListModel.getObject();
                for(Todo todo : todos) {
                    todo.setSelected(true);
                }
                ajaxRequestTarget.add(sectionForm);
            }
        };
        AjaxLink<Void> btnDeselectAll = new AjaxLink<>("btnDeselectAll") {
            @Override
            public void onClick(AjaxRequestTarget ajaxRequestTarget) {
                List<Todo> todos = todoListModel.getObject();
                for(Todo todo : todos) {
                    todo.setSelected(false);
                }
                ajaxRequestTarget.add(sectionForm);
            }
        };

        add(btnSelectAll, btnDeselectAll);
    }

    private void showInfo(AjaxRequestTarget target, String msg) {
        info(msg);
        target.add(feedbackPanel);
    }
}
