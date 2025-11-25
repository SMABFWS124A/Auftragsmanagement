package com.springboot.auftragsmanagement.service;

import com.springboot.auftragsmanagement.export.ArticlePdfExporter;
import com.springboot.auftragsmanagement.service.ArticleService;
import org.springframework.stereotype.Service;

@Service
public class PdfExportService {

    private final ArticleService articleService;
    private final ArticlePdfExporter articlePdfExporter = new ArticlePdfExporter();

    public PdfExportService(ArticleService articleService) {
        this.articleService = articleService;
    }

    public byte[] exportArticlesToPdf() {
        return articlePdfExporter.export(articleService.getAllArticles());
    }
}