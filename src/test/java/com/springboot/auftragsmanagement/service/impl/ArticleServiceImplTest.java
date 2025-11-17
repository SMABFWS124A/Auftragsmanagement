package com.springboot.auftragsmanagement.service.impl;

import com.springboot.auftragsmanagement.dto.ArticleDto;
import com.springboot.auftragsmanagement.entity.Article;
import com.springboot.auftragsmanagement.exception.ResourceNotFoundException;
import com.springboot.auftragsmanagement.exception.StockExceededException;
import com.springboot.auftragsmanagement.mapper.ArticleMapper;
import com.springboot.auftragsmanagement.repository.ArticleRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ArticleServiceImplTest {

    @Mock
    private ArticleRepository articleRepository;

    @Mock
    private ArticleMapper articleMapper;

    @InjectMocks
    private ArticleServiceImpl articleService;

    // Zeitstempel für konsistente DTO-Erstellung
    private static final LocalDateTime NOW = LocalDateTime.now();

    /**
     * Hilfsmethode zur Erstellung eines DTOs entsprechend der ArticleDto Record-Definition.
     */
    private ArticleDto createTestDto(Long id, String articleNumber, String name, double purchase, double sales, int inventory, boolean active) {
        return new ArticleDto(
                id,
                articleNumber,
                name,
                purchase,
                sales,
                "DefaultCategory",
                inventory,
                active,
                NOW, // creationDate
                "Test Description"
        );
    }

    // --- Tests for createArticle ---

    @Test
    void createArticle_ShouldSaveAndReturnDto() {
        // Arrange
        Long articleId = 10L;
        String articleNumber = "TEST-001";
        // ID ist null beim Erstellen
        ArticleDto inputDto = createTestDto(null, articleNumber, "New Item", 10.0, 20.0, 5, true);
        Article entityToSave = new Article();
        Article savedEntity = new Article();
        savedEntity.setId(articleId);
        savedEntity.setArticleNumber(articleNumber);

        // Output DTO hat die generierte ID
        ArticleDto outputDto = createTestDto(articleId, articleNumber, "New Item", 10.0, 20.0, 5, true);

        when(articleMapper.toEntity(inputDto)).thenReturn(entityToSave);
        when(articleRepository.save(entityToSave)).thenReturn(savedEntity);
        when(articleMapper.toDto(savedEntity)).thenReturn(outputDto);

        // Act
        ArticleDto result = articleService.createArticle(inputDto);

        // Assert
        assertNotNull(result);
        assertEquals(articleId, result.id());
        assertEquals(articleNumber, result.articleNumber());
        assertEquals(NOW, result.creationDate()); // Prüfen des neuen Feldes
        verify(articleRepository).save(entityToSave);
    }

    // --- Tests for getArticleById ---

    @Test
    void getArticleById_ShouldReturnDto_WhenFound() {
        // Arrange
        Long articleId = 1L;
        Article article = new Article();
        ArticleDto expectedDto = createTestDto(articleId, "A-1", "Found Item", 1.0, 2.0, 1, true);

        when(articleRepository.findById(articleId)).thenReturn(Optional.of(article));
        when(articleMapper.toDto(article)).thenReturn(expectedDto);

        // Act
        ArticleDto result = articleService.getArticleById(articleId);

        // Assert
        assertNotNull(result);
        assertEquals(articleId, result.id());
        assertEquals("Found Item", result.articleName());
    }

    // --- Tests for getAllArticles ---

    @Test
    void getAllArticles_ShouldReturnListOfDtos() {
        // Arrange
        Article article1 = new Article();
        Article article2 = new Article();
        List<Article> articles = Arrays.asList(article1, article2);

        ArticleDto dto1 = createTestDto(1L, "A1", "Item 1", 10.0, 20.0, 5, true);
        ArticleDto dto2 = createTestDto(2L, "A2", "Item 2", 15.0, 30.0, 10, true);

        when(articleRepository.findAll()).thenReturn(articles);
        when(articleMapper.toDto(article1)).thenReturn(dto1);
        when(articleMapper.toDto(article2)).thenReturn(dto2);

        // Act
        List<ArticleDto> result = articleService.getAllArticles();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).id());
        assertEquals("Item 2", result.get(1).articleName());
    }

    // --- Tests for updateArticle ---

    @Test
    void updateArticle_ShouldUpdateAllFieldsExceptCreationDateAndReturnDto() {
        // Arrange
        Long articleId = 1L;
        Article existingArticle = new Article();
        existingArticle.setId(articleId);
        existingArticle.setArticleNumber("OLD-001");
        existingArticle.setCreationDate(NOW.minusDays(1)); // Altes Datum

        // Das Input DTO (von außen) enthält die neuen Werte
        ArticleDto inputDto = createTestDto(articleId, "NEW-002", "Updated Name", 50.0, 100.0, 20, true);
        // Hinweis: Das DTO kann auch das CreationDate enthalten, aber der Service ändert es nicht.

        Article updatedAndSavedArticle = new Article();
        updatedAndSavedArticle.setId(articleId);
        updatedAndSavedArticle.setArticleNumber("NEW-002");
        updatedAndSavedArticle.setCreationDate(NOW.minusDays(1)); // Wichtig: Datum bleibt unverändert

        ArticleDto expectedDto = createTestDto(articleId, "NEW-002", "Updated Name", 50.0, 100.0, 20, true);

        when(articleRepository.findById(articleId)).thenReturn(Optional.of(existingArticle));
        when(articleRepository.save(existingArticle)).thenReturn(updatedAndSavedArticle);
        when(articleMapper.toDto(updatedAndSavedArticle)).thenReturn(expectedDto);

        // Act
        articleService.updateArticle(articleId, inputDto);

        // Assert
        // Prüfen, ob die Entity-Felder aktualisiert wurden
        assertEquals("NEW-002", existingArticle.getArticleNumber());
        assertEquals(100.0, existingArticle.getSalesPrice());
        assertEquals(20, existingArticle.getInventory());
        // Prüfen, ob das CreationDate nicht überschrieben wurde (wenn die Entity es hält)
        assertEquals(NOW.minusDays(1), existingArticle.getCreationDate());

        verify(articleRepository).save(existingArticle);
    }

    @Test
    void updateArticle_ShouldThrowResourceNotFoundException_WhenNotFound() {
        Long articleId = 99L;
        ArticleDto inputDto = createTestDto(articleId, "ANY", "Any", 1.0, 2.0, 1, true);

        when(articleRepository.findById(articleId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> articleService.updateArticle(articleId, inputDto));
        verify(articleRepository, never()).save(any(Article.class));
    }


    // --- Tests for calculateMargin ---

    @Test
    void calculateMargin_ShouldReturnCorrectMargin() {
        // Arrange
        Long articleId = 1L;
        Article article = new Article();
        article.setPurchasePrice(50.0);
        article.setSalesPrice(100.0);

        when(articleRepository.findById(articleId)).thenReturn(Optional.of(article));

        // Act & Assert
        double result = articleService.calculateMargin(articleId);
        // 50% Marge
        assertEquals(50.0, result, 0.001);
    }

    @Test
    void calculateMargin_ShouldReturnZero_WhenSalesPriceIsZero() {
        Long articleId = 1L;
        Article article = new Article();
        article.setPurchasePrice(50.0);
        article.setSalesPrice(0.0);

        when(articleRepository.findById(articleId)).thenReturn(Optional.of(article));

        double result = articleService.calculateMargin(articleId);
        assertEquals(0.0, result, 0.001);
    }

    // --- Tests for checkStockLevel ---

    @Test
    void checkStockLevel_ShouldReturnTrue_WhenStockIsSufficient() {
        Long articleId = 1L;
        Article article = new Article();
        article.setInventory(10);
        int requiredQuantity = 5;

        when(articleRepository.findById(articleId)).thenReturn(Optional.of(article));

        assertTrue(articleService.checkStockLevel(articleId, requiredQuantity));
    }

    @Test
    void checkStockLevel_ShouldReturnFalse_WhenStockIsNotSufficient() {
        Long articleId = 1L;
        Article article = new Article();
        article.setInventory(3);
        int requiredQuantity = 5;

        when(articleRepository.findById(articleId)).thenReturn(Optional.of(article));

        assertFalse(articleService.checkStockLevel(articleId, requiredQuantity));
    }

    // --- Tests for updateInventory ---

    @Test
    void updateInventory_ShouldIncreaseStock() {
        Long articleId = 1L;
        Article article = new Article();
        article.setId(articleId);
        article.setInventory(10);

        Article updatedArticle = new Article();
        updatedArticle.setInventory(15);

        when(articleRepository.findById(articleId)).thenReturn(Optional.of(article));
        when(articleRepository.save(article)).thenReturn(updatedArticle);
        when(articleMapper.toDto(updatedArticle)).thenReturn(createTestDto(articleId, "A-1", "Test", 10.0, 20.0, 15, true));

        // Act
        ArticleDto result = articleService.updateInventory(articleId, 5);

        // Assert
        assertEquals(15, article.getInventory()); // Prüft, ob die Entity aktualisiert wurde
        assertEquals(15, result.inventory());     // Prüft, ob das zurückgegebene DTO korrekt ist
        verify(articleRepository).save(article);
    }

    @Test
    void updateInventory_ShouldThrowStockExceededException_WhenNotEnoughStock() {
        Long articleId = 1L;
        Article article = new Article();
        article.setId(articleId);
        article.setInventory(2);
        article.setArticleNumber("A-002");
        article.setArticleName("Item");

        when(articleRepository.findById(articleId)).thenReturn(Optional.of(article));

        // Act & Assert
        StockExceededException thrown = assertThrows(StockExceededException.class, () -> articleService.updateInventory(articleId, -5));

        assertTrue(thrown.getMessage().contains("Benötigt: 5, Verfügbar: 2"));

        verify(articleRepository, never()).save(article);
    }
}