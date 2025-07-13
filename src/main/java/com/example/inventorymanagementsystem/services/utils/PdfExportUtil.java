package com.example.inventorymanagementsystem.services.utils;

import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class PdfExportUtil {

    public static void exportMultipleTablesToPdf(List<TableView<?>> tables, List<String> titles, File file) throws IOException {
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);

            float margin = 40;
            float yStart = page.getMediaBox().getHeight() - margin;
            float yPosition = yStart;
            float rowHeight = 18;
            float tableWidth = page.getMediaBox().getWidth() - 2 * margin;

            PDType0Font font;
            try (InputStream fontStream = PdfExportUtil.class.getResourceAsStream("/com/example/inventorymanagementsystem/fonts/DejaVuSans.ttf")) {
                if (fontStream == null) throw new IOException("Font file not found in resources");
                font = PDType0Font.load(document, fontStream);
            }

            PDPageContentStream contentStream = new PDPageContentStream(document, page);

            for (int i = 0; i < tables.size(); i++) {
                TableView<?> table = tables.get(i);
                String title = titles.get(i);
                ObservableList<? extends TableColumn<?, ?>> columns = table.getColumns();

                int columnCount = columns.size();
                float colWidth = tableWidth / columnCount;
                float tableStartX = (page.getMediaBox().getWidth() - tableWidth) / 2;

                // Title
                contentStream.beginText();
                contentStream.setFont(font, 14);
                contentStream.setNonStrokingColor(Color.BLACK);
                contentStream.newLineAtOffset(tableStartX, yPosition);
                contentStream.showText(title);
                contentStream.endText();
                yPosition -= 25;

                // Header background
                contentStream.setNonStrokingColor(new Color(0, 102, 204));
                contentStream.addRect(tableStartX, yPosition, tableWidth, rowHeight);
                contentStream.fill();

                // Header text and border
                contentStream.setNonStrokingColor(Color.WHITE);
                contentStream.setFont(font, 10);
                for (int c = 0; c < columnCount; c++) {
                    float cellX = tableStartX + c * colWidth;
                    TableColumn<?, ?> col = columns.get(c);

                    contentStream.beginText();
                    contentStream.newLineAtOffset(cellX + 2, yPosition + 4);
                    contentStream.showText(col.getText());
                    contentStream.endText();

                    contentStream.setStrokingColor(new Color(0, 102, 204));
                    contentStream.addRect(cellX, yPosition, colWidth, rowHeight);
                    contentStream.stroke();
                }

                yPosition -= rowHeight;

                // Table data
                contentStream.setNonStrokingColor(Color.BLACK);
                for (Object item : table.getItems()) {
                    for (int c = 0; c < columnCount; c++) {
                        float cellX = tableStartX + c * colWidth;
                        @SuppressWarnings("unchecked")
                        TableColumn<Object, ?> col = (TableColumn<Object, ?>) columns.get(c);
                        Object cellValue = col.getCellObservableValue(item).getValue();
                        String text = cellValue != null ? cellValue.toString() : "";

                        contentStream.beginText();
                        contentStream.setFont(font, 10);
                        contentStream.newLineAtOffset(cellX + 2, yPosition + 4);
                        contentStream.showText(text);
                        contentStream.endText();

                        contentStream.setStrokingColor(new Color(0, 102, 204));
                        contentStream.addRect(cellX, yPosition, colWidth, rowHeight);
                        contentStream.stroke();
                    }

                    yPosition -= rowHeight;

                    // New page handling
                    if (yPosition < margin + rowHeight * 3) {
                        contentStream.close();
                        page = new PDPage(PDRectangle.A4);
                        document.addPage(page);
                        contentStream = new PDPageContentStream(document, page);
                        yPosition = yStart;
                    }
                }

                yPosition -= 30;
            }

            contentStream.close();
            document.save(file);
        }
    }
}