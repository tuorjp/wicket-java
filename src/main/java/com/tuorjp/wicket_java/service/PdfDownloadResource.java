package com.tuorjp.wicket_java.service;

import com.tuorjp.wicket_java.model.Todo;
import org.apache.wicket.request.resource.AbstractResource;
import org.apache.wicket.request.resource.ContentDisposition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.Duration;
import java.util.List;

@Component("pdfDownloadResource") //Spring Bean
public class PdfDownloadResource extends AbstractResource {

    @Autowired
    private MongoDBService mongoDBService;

    @Autowired
    private PdfGeneratorService pdfGeneratorService;

    @Override
    protected ResourceResponse newResourceResponse(Attributes attributes) {
        ResourceResponse response = new ResourceResponse();

        response.setWriteCallback(new WriteCallback() {
            @Override
            public void writeData(Attributes attributes) throws IOException {
                try {
                    List<Todo> todos = mongoDBService.fetchAllItems();
                    try (ByteArrayOutputStream baos = pdfGeneratorService.createdPdf(todos)) {
                        baos.writeTo(attributes.getResponse().getOutputStream());
                    }
                } catch (Exception e) {
                    throw new IOException("Erro ao gerar o arquivo PDF", e);
                }
            }
        });

        response.disableCaching();
        response.setFileName("tarefas_" + System.currentTimeMillis() + ".pdf");
        response.setContentType("application/pdf");
        response.setCacheDuration(Duration.ZERO);
        response.setContentDisposition(ContentDisposition.ATTACHMENT);
        return response;
    }
}