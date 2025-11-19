package com.springboot.auftragsmanagement.service.impl;

import com.springboot.auftragsmanagement.dto.ArticleDto;
import com.springboot.auftragsmanagement.entity.Article;
import com.springboot.auftragsmanagement.exception.ResourceNotFoundException;
import com.springboot.auftragsmanagement.exception.StockExceededException;
import com.springboot.auftragsmanagement.factory.ArticleDtoFactory;
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
    private ArticleDtoFactory articleDtoFactory;

    @Mock
    private ArticleMapper articleMapper;

    @InjectMocks
    private ArticleServiceImpl articleService;

    private static final LocalDateTime NOW = LocalDateTime.now();

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
                NOW,
                "Test Description"
        );
    }

    @Test
    void createArticle_ShouldSaveAndReturnDto() {
        Long articleId = 10L;
        String articleNumber = "TEST-001";
        ArticleDto inputDto = createTestDto(null, articleNumber, "New Item", 10.0, 20.0, 5, true);
        Article entityToSave = new Article();
        Article savedEntity = new Article();
        savedEntity.setId(articleId);
        savedEntity.setArticleNumber(articleNumber);
        savedEntity.setCreationDate(NOW);

        ArticleDto outputDto = createTestDto(articleId, articleNumber, "New Item", 10.0, 20.0, 5, true);

        when(articleMapper.toEntity(inputDto)).thenReturn(entityToSave);
        when(articleRepository.save(entityToSave)).thenReturn(savedEntity);
        when(articleDtoFactory.createArticle(
                savedEntity.getId(),
                savedEntity.getArticleNumber(),
                savedEntity.getArticleName(),
                savedEntity.getPurchasePrice(),
                savedEntity.getSalesPrice(),
                savedEntity.getCategory(),
                savedEntity.getInventory(),
                savedEntity.isActive(),
                savedEntity.getCreationDate(),
                savedEntity.getDescription()
        )).thenReturn(outputDto);

        ArticleDto result = articleService.createArticle(inputDto);

        assertNotNull(result);
        assertEquals(articleId, result.id());
        assertEquals(articleNumber, result.articleNumber());
        assertEquals(NOW, result.creationDate());
        verify(articleRepository).save(entityToSave);
        verify(articleMapper).toEntity(inputDto);
        verify(articleDtoFactory).createArticle(any(), any(), any(), anyDouble(), anyDouble(), any(), anyInt(), anyBoolean(), any(), any());
    }

    @Test
    void getArticleById_ShouldReturnDto_WhenFound() {
        Long articleId = 1L;
        Article article = new Article();
        ArticleDto expectedDto = createTestDto(articleId, "A-1", "Found Item", 1.0, 2.0, 1, true);

        when(articleRepository.findById(articleId)).thenReturn(Optional.of(article));
        when(articleDtoFactory.createArticle(
                article.getId(),
                article.getArticleNumber(),
                article.getArticleName(),
                article.getPurchasePrice(),
                article.getSalesPrice(),
                article.getCategory(),
                article.getInventory(),
                article.isActive(),
                article.getCreationDate(),
                article.getDescription()
        )).thenReturn(expectedDto);

        ArticleDto result = articleService.getArticleById(articleId);

        assertNotNull(result);
        assertEquals(articleId, result.id());
        assertEquals("Found Item", result.articleName());
        verify(articleDtoFactory).createArticle(any(), any(), any(), anyDouble(), anyDouble(), any(), anyInt(), anyBoolean(), any(), any());
    }

    @Test
    void getArticleById_ShouldThrowResourceNotFoundException_WhenNotFound() {
        Long articleId = 99L;
        when(articleRepository.findById(articleId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> articleService.getArticleById(articleId));
    }

    @Test
    void getAllArticles_ShouldReturnListOfDtos() {
        Article article1 = new Article();
        Article article2 = new Article();
        List<Article> articles = Arrays.asList(article1, article2);

        ArticleDto dto1 = createTestDto(1L, "A1", "Item 1", 10.0, 20.0, 5, true);
        ArticleDto dto2 = createTestDto(2L, "A2", "Item 2", 15.0, 30.0, 10, true);

        when(articleRepository.findAll()).thenReturn(articles);
        when(articleDtoFactory.createArticle(
                eq(article1.getId()), any(), any(), anyDouble(), anyDouble(), any(), anyInt(), anyBoolean(), any(), any()
        )).thenReturn(dto1);
        when(articleDtoFactory.createArticle(
                eq(article2.getId()), any(), any(), anyDouble(), anyDouble(), any(), anyInt(), anyBoolean(), any(), any()
        )).thenReturn(dto2);

        List<ArticleDto> result = articleService.getAllArticles();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).id());
        assertEquals("Item 2", result.get(1).articleName());
        verify(articleDtoFactory, times(2)).createArticle(any(), any(), any(), anyDouble(), anyDouble(), any(), anyInt(), anyBoolean(), any(), any());
    }

    @Test
    void updateArticle_ShouldUpdateAllFieldsAndReturnDto() {
        Long articleId = 1L;
        LocalDateTime oldDate = NOW.minusDays(1);

        Article existingArticle = new Article();
        existingArticle.setId(articleId);
        existingArticle.setArticleNumber("OLD-001");
        existingArticle.setCreationDate(oldDate);

        ArticleDto inputDto = createTestDto(articleId, "NEW-002", "Updated Name", 50.0, 100.0, 20, true);

        Article updatedAndSavedArticle = new Article();
        updatedAndSavedArticle.setId(articleId);
        updatedAndSavedArticle.setArticleNumber("NEW-002");
        updatedAndSavedArticle.setCreationDate(oldDate);

        ArticleDto expectedDto = new ArticleDto(
                articleId, "NEW-002", "Updated Name", 50.0, 100.0, "DefaultCategory", 20, true, oldDate, "Test Description"
        );

        when(articleRepository.findById(articleId)).thenReturn(Optional.of(existingArticle));
        when(articleRepository.save(existingArticle)).thenReturn(updatedAndSavedArticle);
        when(articleDtoFactory.createArticle(
                updatedAndSavedArticle.getId(),
                updatedAndSavedArticle.getArticleNumber(),
                updatedAndSavedArticle.getArticleName(),
                updatedAndSavedArticle.getPurchasePrice(),
                updatedAndSavedArticle.getSalesPrice(),
                updatedAndSavedArticle.getCategory(),
                updatedAndSavedArticle.getInventory(),
                updatedAndSavedArticle.isActive(),
                updatedAndSavedArticle.getCreationDate(),
                updatedAndSavedArticle.getDescription()
        )).thenReturn(expectedDto);

        ArticleDto result = articleService.updateArticle(articleId, inputDto);

        verify(articleRepository).save(existingArticle);
        assertEquals(expectedDto.articleNumber(), result.articleNumber());
        assertEquals(oldDate, result.creationDate());

        assertEquals("NEW-002", existingArticle.getArticleNumber());
        assertEquals(50.0, existingArticle.getPurchasePrice());
        verify(articleDtoFactory).createArticle(any(), any(), any(), anyDouble(), anyDouble(), any(), anyInt(), anyBoolean(), any(), any());
    }

    @Test
    void updateArticle_ShouldThrowResourceNotFoundException_WhenNotFound() {
        Long articleId = 99L;
        ArticleDto inputDto = createTestDto(articleId, "ANY", "Any", 1.0, 2.0, 1, true);

        when(articleRepository.findById(articleId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> articleService.updateArticle(articleId, inputDto));
        verify(articleRepository, never()).save(any(Article.class));
    }

    @Test
    void deleteArticle_ShouldDeleteArticle_WhenFound() {
        Long articleId = 1L;
        Article article = new Article();
        when(articleRepository.findById(articleId)).thenReturn(Optional.of(article));
        doNothing().when(articleRepository).delete(article);

        articleService.deleteArticle(articleId);

        verify(articleRepository).delete(article);
    }

    @Test
    void deleteArticle_ShouldThrowResourceNotFoundException_WhenNotFound() {
        Long articleId = 99L;
        when(articleRepository.findById(articleId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> articleService.deleteArticle(articleId));
        verify(articleRepository, never()).delete(any(Article.class));
    }

    @Test
    void calculateMargin_ShouldReturnCorrectMargin() {
        Long articleId = 1L;
        Article article = new Article();
        article.setPurchasePrice(50.0);
        article.setSalesPrice(100.0);

        when(articleRepository.findById(articleId)).thenReturn(Optional.of(article));

        double result = articleService.calculateMargin(articleId);
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

    @Test
    void calculateMargin_ShouldThrowResourceNotFoundException_WhenNotFound() {
        Long articleId = 99L;
        when(articleRepository.findById(articleId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> articleService.calculateMargin(articleId));
    }

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

    @Test
    void checkStockLevel_ShouldThrowResourceNotFoundException_WhenNotFound() {
        Long articleId = 99L;
        when(articleRepository.findById(articleId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> articleService.checkStockLevel(articleId, 1));
    }

    @Test
    void updateInventory_ShouldIncreaseStock() {
        Long articleId = 1L;
        Article article = new Article();
        article.setId(articleId);
        article.setInventory(10);
        article.setCreationDate(NOW);

        Article updatedArticle = new Article();
        updatedArticle.setId(articleId);
        updatedArticle.setInventory(15);
        updatedArticle.setCreationDate(NOW);

        ArticleDto expectedDto = createTestDto(articleId, "A-1", "Test", 10.0, 20.0, 15, true);

        when(articleRepository.findById(articleId)).thenReturn(Optional.of(article));
        when(articleRepository.save(article)).thenReturn(updatedArticle);
        when(articleDtoFactory.createArticle(
                updatedArticle.getId(),
                updatedArticle.getArticleNumber(),
                updatedArticle.getArticleName(),
                updatedArticle.getPurchasePrice(),
                updatedArticle.getSalesPrice(),
                updatedArticle.getCategory(),
                updatedArticle.getInventory(),
                updatedArticle.isActive(),
                updatedArticle.getCreationDate(),
                updatedArticle.getDescription()
        )).thenReturn(expectedDto);

        ArticleDto result = articleService.updateInventory(articleId, 5);

        verify(articleRepository).save(article);
        assertEquals(15, result.inventory());
        assertEquals(15, article.getInventory());
        verify(articleDtoFactory).createArticle(any(), any(), any(), anyDouble(), anyDouble(), any(), anyInt(), anyBoolean(), any(), any());
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

        StockExceededException thrown = assertThrows(StockExceededException.class, () -> articleService.updateInventory(articleId, -5));

        assertTrue(thrown.getMessage().contains("Benötigt: 5, Verfügbar: 2"));

        verify(articleRepository, never()).save(article);
    }

    @Test
    void updateInventory_ShouldThrowResourceNotFoundException_WhenNotFound() {
        Long articleId = 99L;
        when(articleRepository.findById(articleId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> articleService.updateInventory(articleId, 1));
        verify(articleRepository, never()).save(any(Article.class));
    }
}