package com.example.inventorymanagementsystem.services.utils;

import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
//import org.apache.poi.ss.usermodel.*;
//import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ExcelExportUtil {

    public static void exportMultipleTablesToExcel(List<TableView<?>> tables, List<String> titles, File file) throws Exception {
//        try (Workbook workbook = new XSSFWorkbook()) {
//            for (int i = 0; i < tables.size(); i++) {
//                TableView<?> table = tables.get(i);
//                String sheetName = titles.get(i);
//                Sheet sheet = workbook.createSheet(sheetName);
//
//                List<TableColumn<?, ?>> columns = getLeafColumns(table);
//
//                // Create header row
//                Row headerRow = sheet.createRow(0);
//                for (int colIndex = 0; colIndex < columns.size(); colIndex++) {
//                    TableColumn<?, ?> col = columns.get(colIndex);
//                    Cell cell = headerRow.createCell(colIndex);
//                    cell.setCellValue(col.getText());
//                }
//
//                // Export rows
//                List<?> items = new ArrayList<>(table.getItems()); // defensive copy
//                for (int rowIndex = 0; rowIndex < items.size(); rowIndex++) {
//                    Row row = sheet.createRow(rowIndex + 1);
//                    Object item = items.get(rowIndex);
//
//                    for (int colIndex = 0; colIndex < columns.size(); colIndex++) {
//                        @SuppressWarnings("unchecked")
//                        TableColumn<Object, Object> col = (TableColumn<Object, Object>) columns.get(colIndex);
//                        Cell cell = row.createCell(colIndex);
//
//                        Object cellData = col.getCellData(item);
//                        cell.setCellValue(cellData != null ? cellData.toString() : "");
//                    }
//                }
//
//                // Auto-size columns
//                for (int colIndex = 0; colIndex < columns.size(); colIndex++) {
//                    sheet.autoSizeColumn(colIndex);
//                }
//
//                // Debug log (optional)
//                System.out.println("Exported sheet: " + sheetName + " — Rows: " + items.size() + ", Columns: " + columns.size());
//            }
//
//            // Write the workbook to file
//            try (FileOutputStream fos = new FileOutputStream(file)) {
//                workbook.write(fos);
//            }
//        }
    }

    private static List<TableColumn<?, ?>> getLeafColumns(TableView<?> table) {
        List<TableColumn<?, ?>> leafColumns = new ArrayList<>();
        for (TableColumn<?, ?> column : table.getColumns()) {
            getLeafColumnsRecursive(column, leafColumns);
        }
        return leafColumns;
    }

    private static void getLeafColumnsRecursive(TableColumn<?, ?> column, List<TableColumn<?, ?>> list) {
        if (column.getColumns().isEmpty()) {
            list.add(column);
            System.out.println("Leaf column: " + column.getText());
        } else {
            for (TableColumn<?, ?> subColumn : column.getColumns()) {
                getLeafColumnsRecursive(subColumn, list);
            }
        }
    }
}
