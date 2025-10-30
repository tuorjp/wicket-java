package com.tuorjp.wicket_java.wicket.pages.home;

import com.giffing.wicket.spring.boot.context.scan.WicketHomePage;
import com.tuorjp.wicket_java.model.Todo;
import com.tuorjp.wicket_java.service.MongoDBService;
import com.tuorjp.wicket_java.wicket.pages.BasePage;
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
import org.apache.wicket.request.resource.IResource;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.List;

@WicketHomePage
public class HomePage extends BasePage {

    @SpringBean
    private MongoDBService mongoDBService;

    @SpringBean(name = "excelDownloadResource")
    private IResource excelDownloadResource;

    @SpringBean(name = "pdfDownloadResource")
    private IResource pdfDownloadResource;

    //Models
    private LoadableDetachableModel<List<Todo>> todoListModel;
    private LoadableDetachableModel<Todo> newTodoModel;

    //Wicket
    private FeedbackPanel feedbackPanel;
    private WebMarkupContainer sectionForm;
    private Form<Todo> form;
    private AjaxLink<Void> downloadExcelBtn;
    private AjaxLink<Void> downloadPdfBtn;

    //Construtor
    public HomePage() {
        add(new Label("welcomeMessage", "Aplicação Lista de Tarefas"));
        feedbackPanel = new FeedbackPanel("feedbackPanel");
        feedbackPanel.setOutputMarkupId(true);
        add(feedbackPanel);
    }

    //onInitialize é executado após os beans do Spring serem injetados
    @Override
    protected void onInitialize() {
        super.onInitialize();

        //inicializa modelos
        initializeModels();

        //formulário principal e a seção
        sectionForm = new WebMarkupContainer("sectionForm");
        sectionForm.setOutputMarkupId(true);
        add(sectionForm);

        form = new Form<>("form", new CompoundPropertyModel<>(newTodoModel));
        sectionForm.add(form);

        //adiciona componentes
        addToolbarButtons();
        addNewTodoPanel();
        addDownloadBehaviors();
        addTodoListView();
        addSelectionButtons();
    }

    private void initializeModels() {
        //modelo para a lista principal
        todoListModel = new LoadableDetachableModel<List<Todo>>() {
            @Override
            protected List<Todo> load() {
                return mongoDBService.fetchAllItems();
            }
        };

        //modelo para o formulário de "Novo Item"
        newTodoModel = new LoadableDetachableModel<Todo>() {
            @Override
            protected Todo load() {
                return new Todo();
            }
        };
    }

    private void addToolbarButtons() {
        //botão "Adicionar"
        AjaxLink<Void> btnAdd = new AjaxLink<>("addItemLink") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                //formNew é criado em addNewTodoPanel
                form.get("formNew").setVisible(!form.get("formNew").isVisible());
                target.add(form.get("formNew"));
            }
        };

        //botão "Remover"
        AjaxSubmitLink btnRemove = new AjaxSubmitLink("remove", form) {
            @Override
            protected void onSubmit(AjaxRequestTarget target) {
                List<Todo> todos = todoListModel.getObject();
                List<Todo> todosToRemove = todos.stream().filter(Todo::isSelected).toList();

                if (todosToRemove.isEmpty()) {
                    showInfo(target, "Nenhum item selecionado.");
                    return;
                }

                mongoDBService.removeItems(todosToRemove);
                todoListModel.detach(); //recarrega a lista

                //esconde os botões de download se a lista estiver vazia
                if (mongoDBService.fetchAllItems().isEmpty()) {
                    downloadExcelBtn.setVisible(false);
                    downloadPdfBtn.setVisible(false);
                    target.add(downloadExcelBtn, downloadPdfBtn);
                }

                showInfo(target, todosToRemove.size() + " itens selecionados foram removidos.");
                target.add(sectionForm); // atualiza o formulário inteiro
            }
        };

        form.add(btnAdd, btnRemove);
    }

    private void addNewTodoPanel() {
        //container "Novo Item"
        WebMarkupContainer formNew = new WebMarkupContainer("formNew");
        formNew.setOutputMarkupPlaceholderTag(true);
        formNew.setVisible(false);
        form.add(formNew);

        //campos (vinculados ao CompoundPropertyModel do 'form')
        formNew.add(new TextField<>("title"));
        formNew.add(new TextField<>("body"));

        //botão "Salvar"
        AjaxSubmitLink btnSave = new AjaxSubmitLink("save", form) {
            @Override
            protected void onSubmit(AjaxRequestTarget target) {
                Todo todoToSave = form.getModelObject();
                mongoDBService.save(todoToSave);
                todoListModel.detach(); // Força a lista a recarregar

                form.getModel().detach(); // Limpa o formulário (cria um novo Todo)
                formNew.setVisible(false);

                // Mostra os botões de download
                downloadExcelBtn.setVisible(true);
                downloadPdfBtn.setVisible(true);
                target.add(downloadExcelBtn, downloadPdfBtn);

                showInfo(target, "Tarefa adicionada com sucesso");
                target.add(sectionForm); // Atualiza tudo
            }
        };
        formNew.add(btnSave);
    }

    private void addDownloadBehaviors() {
        //cria os Behaviors usando os IResources injetados
        AjaxDownloadBehavior downloadExcel = new AjaxDownloadBehavior(excelDownloadResource);
        AjaxDownloadBehavior downloadPDF = new AjaxDownloadBehavior(pdfDownloadResource);

        //botão de Download PDF
        downloadPdfBtn = new AjaxLink<Void>("downloadPdfBtn") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                downloadPDF.initiate(target);
            }
        };

        //botão de Download Excel
        downloadExcelBtn = new AjaxLink<Void>("downloadExcelBtn") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                downloadExcel.initiate(target);
            }
        };

        //adiciona os botões e os behaviors ao formulário
        form.add(downloadPdfBtn, downloadExcelBtn);
        form.add(downloadPDF, downloadExcel);

        //esconde os botões se a lista estiver vazia no carregamento
        if (todoListModel.getObject().isEmpty()) {
            downloadPdfBtn.setVisible(false);
            downloadExcelBtn.setVisible(false);
        }
    }

    private void addTodoListView() {
        ListView<Todo> todoList = new ListView<>("todoList", todoListModel) {
            @Override
            protected void populateItem(ListItem<Todo> item) {
                item.add(new CheckBox("selected", new PropertyModel<>(item.getModel(), "selected")));
                item.add(new Label("title", () -> item.getModelObject().getTitle()));
                item.add(new Label("body", () -> item.getModelObject().getBody()));
            }
        };
        todoList.setReuseItems(true);
        form.add(todoList);
    }

    private void addSelectionButtons() {
        //botões fora do form
        AjaxLink<Void> btnSelectAll = new AjaxLink<>("btnSelectAll") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                todoListModel.getObject().forEach(todo -> todo.setSelected(true));
                target.add(sectionForm);
            }
        };

        AjaxLink<Void> btnDeselectAll = new AjaxLink<>("btnDeselectAll") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                todoListModel.getObject().forEach(todo -> todo.setSelected(false));
                target.add(sectionForm);
            }
        };

        add(btnSelectAll, btnDeselectAll);
    }

    private void showInfo(AjaxRequestTarget target, String msg) {
        info(msg);
        target.add(feedbackPanel);
    }
}