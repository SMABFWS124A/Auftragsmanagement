package com.springboot.auftragsmanagement.controller;

import com.springboot.auftragsmanagement.dto.ArticleDto;
import com.springboot.auftragsmanagement.service.ArticleService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import com.springboot.auftragsmanagement.service.PdfExportService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/articles")
@CrossOrigin(origins = "*")
public class ArticleController {

    private final ArticleService articleService;
    private final PdfExportService pdfExportService;

    public ArticleController(ArticleService articleService, PdfExportService pdfExportService) {
        this.articleService = articleService;
        this.pdfExportService = pdfExportService;
    }

    @PostMapping
    public ResponseEntity<ArticleDto> createArticle(@RequestBody ArticleDto articleDto){
        ArticleDto savedArticle = articleService.createArticle(articleDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedArticle);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ArticleDto> getArticleById(@PathVariable Long id){
        ArticleDto articleDto = articleService.getArticleById(id);
        return ResponseEntity.ok(articleDto);
    }

    @GetMapping
    public ResponseEntity<List<ArticleDto>> getAllArticles(){
        List<ArticleDto> articles = articleService.getAllArticles();
        return ResponseEntity.ok(articles);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ArticleDto> updateArticle(@PathVariable Long id, @RequestBody ArticleDto articleDto){
        ArticleDto updatedArticle = articleService.updateArticle(id, articleDto);
        return ResponseEntity.ok(updatedArticle);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteArticle(@PathVariable Long id){
        articleService.deleteArticle(id);
        return ResponseEntity.ok("Artikel wurde erfolgreich gelöscht.");
    }

    @GetMapping("/{id}/margin")
    public ResponseEntity<Double> getArticleMargin(@PathVariable Long id) {
        double margin = articleService.calculateMargin(id);
        return ResponseEntity.ok(margin);
    }

    @GetMapping("/{id}/stock-check")
    public ResponseEntity<Boolean> checkStockLevel(
            @PathVariable Long id,
            @RequestParam int requiredQuantity) {

        boolean isAvailable = articleService.checkStockLevel(id, requiredQuantity);
        return ResponseEntity.ok(isAvailable);
    }

    @PatchMapping("/{id}/inventory")
    public ResponseEntity<ArticleDto> updateInventory(
            @PathVariable Long id,
            @RequestParam int inventoryChange) {

        ArticleDto updatedArticle = articleService.updateInventory(id, inventoryChange);
        return ResponseEntity.ok(updatedArticle);
    }
    @GetMapping(value = "/export/pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> exportArticlesToPdf() {
        byte[] pdfContent = pdfExportService.exportArticlesToPdf();

        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=artikel.pdf")
                .body(pdfContent);
    }
}