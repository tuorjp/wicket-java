package com.tuorjp.wicket_java.service;

import com.itextpdf.text.DocumentException;
import com.tuorjp.wicket_java.model.Todo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@Service
public class PdfGeneratorService {
    @Autowired
    SpringTemplateEngine springTemplateEngine;

    public ByteArrayOutputStream createdPdf(List<Todo> todos) {
        String html = renderHtmlForPdf(todos);
        return createPdf(html);
    }

    private ByteArrayOutputStream createPdf(String html) {
        ITextRenderer renderer = new ITextRenderer();
        renderer.setDocumentFromString(html);
        renderer.layout();

        ByteArrayOutputStream fs = new ByteArrayOutputStream();
        try {
            renderer.createPDF(fs);
            return fs;
        } catch (DocumentException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String renderHtmlForPdf(List<Todo> todos) {
        Context context = new Context();

        context.setVariable("items", todos);
        context.setVariable("title", "Todos list :" + todos.size());


        return springTemplateEngine.process("pdf/todos.html", context);
    }
}
