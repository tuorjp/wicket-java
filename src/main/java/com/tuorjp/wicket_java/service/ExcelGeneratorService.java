package com.tuorjp.wicket_java.service;

import com.tuorjp.wicket_java.model.Todo;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ExcelGeneratorService {
    public Workbook createExcelFile(List<Todo> todos) {
        XSSFWorkbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Tarefas");

        CellStyle headerStyle = workbook.createCellStyle();
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerStyle.setFont(headerFont);
        headerStyle.setAlignment(HorizontalAlignment.CENTER);

        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("ID");
        headerRow.createCell(1).setCellValue("Título");
        headerRow.createCell(2).setCellValue("Descrição");
        headerRow.createCell(3).setCellValue("Selecionado");

        for (int i = 0; i < 4; i++) {
            headerRow.getCell(i).setCellStyle(headerStyle);
        }

        int rowNum = 1;
        for (Todo todo : todos) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(todo.getId() != null ? todo.getId() : "");
            row.createCell(1).setCellValue(todo.getTitle() != null ? todo.getTitle() : "");
            row.createCell(2).setCellValue(todo.getBody() != null ? todo.getBody() : "");
            row.createCell(3).setCellValue(todo.isSelected());
        }

        for (int i = 0; i < 4; i++) {
            sheet.autoSizeColumn(i);
        }

        return workbook;
    }
}
