package com.tuorjp.wicket_java.service;

import com.tuorjp.wicket_java.model.Todo;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.wicket.request.resource.AbstractResource;
import org.apache.wicket.request.resource.ContentDisposition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.time.Duration;
import java.util.List;

@Component("excelDownloadResource") //Spring Bean
public class ExcelDownloadResource extends AbstractResource {

    @Autowired
    private MongoDBService mongoDBService;

    @Autowired
    private ExcelGeneratorService excelGeneratorService;

    @Override
    protected ResourceResponse newResourceResponse(Attributes attributes) {
        ResourceResponse response = new ResourceResponse();

        response.setWriteCallback(new WriteCallback() {
            @Override
            public void writeData(Attributes attributes) throws IOException {
                try {
                    List<Todo> todos = mongoDBService.fetchAllItems();
                    Workbook wb = excelGeneratorService.createExcelFile(todos);
                    wb.write(attributes.getResponse().getOutputStream());
                    wb.close();
                } catch (Exception e) {
                    throw new IOException("Erro ao gerar o arquivo Excel", e);
                }
            }
        });

        response.disableCaching();
        response.setFileName("tarefas_" + System.currentTimeMillis() + ".xlsx");
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCacheDuration(Duration.ZERO);
        response.setContentDisposition(ContentDisposition.ATTACHMENT);
        return response;
    }
}