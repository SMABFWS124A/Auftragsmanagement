package com.springboot.auftragsmanagement.export;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Font;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.springboot.auftragsmanagement.dto.ArticleDto;

import java.util.List;

public class ArticlePdfExporter extends AbstractPdfExporter<ArticleDto> {

    private static final Font HEADER_FONT = new Font(Font.HELVETICA, 12, Font.BOLD);
    private static final Font CELL_FONT = new Font(Font.HELVETICA, 11);

    @Override
    protected String getTitle() {
        return "Artikelübersicht";
    }

    @Override
    protected void addTable(Document document, List<ArticleDto> items) throws DocumentException {
        PdfPTable table = new PdfPTable(new float[]{1.2f, 2.2f, 3f, 2f, 1.6f, 1.4f});
        table.setWidthPercentage(100);
        table.setSpacingBefore(6);

        addHeader(table);
        addRows(table, items);

        document.add(table);
    }

    private void addHeader(PdfPTable table) {
        addHeaderCell(table, "ID");
        addHeaderCell(table, "Artikelnummer");
        addHeaderCell(table, "Name");
        addHeaderCell(table, "VK-Preis (€)");
        addHeaderCell(table, "Bestand");
        addHeaderCell(table, "Status");
    }

    private void addHeaderCell(PdfPTable table, String text) {
        PdfPCell cell = new PdfPCell(new Phrase(text, HEADER_FONT));
        cell.setPadding(6f);
        table.addCell(cell);
    }

    private void addRows(PdfPTable table, List<ArticleDto> items) {
        items.forEach(article -> {
            table.addCell(createCell(String.valueOf(article.id())));
            table.addCell(createCell(article.articleNumber()));
            table.addCell(createCell(article.articleName()));
            table.addCell(createCell(String.format("%.2f", article.salesPrice())));
            table.addCell(createCell(String.valueOf(article.inventory())));
            table.addCell(createCell(article.active() ? "Aktiv" : "Inaktiv"));
        });
    }

    private PdfPCell createCell(String text) {
        PdfPCell cell = new PdfPCell(new Phrase(text, CELL_FONT));
        cell.setPadding(5f);
        return cell;
    }
}