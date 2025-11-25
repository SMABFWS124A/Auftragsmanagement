package com.springboot.auftragsmanagement.export;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Font;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * Template-Method-Basisklasse für PDF Exporte.
 * Unterklassen definieren nur die tabellenspezifische Darstellung,
 * während das Dokumentgerüst (Seitenaufbau, Titel) hier vorgegeben wird.
 */
public abstract class AbstractPdfExporter<T> {

    private static final Font TITLE_FONT = new Font(Font.HELVETICA, 18, Font.BOLD);

    public byte[] export(List<T> items) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            Document document = new Document(PageSize.A4);
            PdfWriter.getInstance(document, outputStream);
            document.open();

            addTitle(document);
            addTable(document, items);

            document.close();
            return outputStream.toByteArray();
        } catch (DocumentException | IOException e) {
            throw new IllegalStateException("PDF Export fehlgeschlagen", e);
        }
    }

    private void addTitle(Document document) throws DocumentException {
        Paragraph title = new Paragraph(getTitle(), TITLE_FONT);
        title.setSpacingAfter(14);
        document.add(title);
    }

    protected abstract String getTitle();

    protected abstract void addTable(Document document, List<T> items) throws DocumentException;
}