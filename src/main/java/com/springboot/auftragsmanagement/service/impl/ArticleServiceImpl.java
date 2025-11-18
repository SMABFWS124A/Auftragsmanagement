package com.springboot.auftragsmanagement.service.impl;

import com.springboot.auftragsmanagement.dto.ArticleDto;
import com.springboot.auftragsmanagement.entity.Article;
import com.springboot.auftragsmanagement.exception.ResourceNotFoundException;
import com.springboot.auftragsmanagement.exception.StockExceededException;
import com.springboot.auftragsmanagement.factory.ArticleDtoFactory;
import com.springboot.auftragsmanagement.mapper.ArticleMapper;
import com.springboot.auftragsmanagement.repository.ArticleRepository;
import com.springboot.auftragsmanagement.service.ArticleService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ArticleServiceImpl implements ArticleService {

    private final ArticleRepository articleRepository;
    private final ArticleMapper articleMapper;
    private final ArticleDtoFactory articleDtoFactory;

    public ArticleServiceImpl(ArticleRepository articleRepository, ArticleMapper articleMapper, ArticleDtoFactory articleDtoFactory) {
        this.articleRepository = articleRepository;
        this.articleMapper = articleMapper;
        this.articleDtoFactory = articleDtoFactory;
    }

    @Override
    public ArticleDto createArticle(ArticleDto articleDto) {
        Article article = articleMapper.toEntity(articleDto);
        Article savedArticle = articleRepository.save(article);
        return articleDtoFactory.createArticle(
                savedArticle.getId(),
                savedArticle.getArticleNumber(),
                savedArticle.getArticleName(),
                savedArticle.getPurchasePrice(),
                savedArticle.getSalesPrice(),
                savedArticle.getCategory(),
                savedArticle.getInventory(),
                savedArticle.isActive(),
                savedArticle.getCreationDate(),
                savedArticle.getDescription()
        );
    }

    @Override
    public ArticleDto getArticleById(Long id) {
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Artikel mit der ID " + id + " wurde nicht gefunden"));

        return articleDtoFactory.createArticle(
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
        );
    }

    @Override
    public List<ArticleDto> getAllArticles() {
        return articleRepository.findAll().stream()
                .map(article -> articleDtoFactory.createArticle(
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
                ))
                .collect(Collectors.toList());
    }

    @Override
    public ArticleDto updateArticle(Long id, ArticleDto articleDto) {
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Artikel mit der ID " + id + " wurde nicht gefunden"));

        article.setArticleNumber(articleDto.articleNumber());
        article.setArticleName(articleDto.articleName());
        article.setPurchasePrice(articleDto.purchasePrice());
        article.setSalesPrice(articleDto.salesPrice());
        article.setCategory(articleDto.category());
        article.setInventory(articleDto.inventory());
        article.setActive(articleDto.active());
        article.setDescription(articleDto.description());

        Article updatedArticle = articleRepository.save(article);

        return articleDtoFactory.createArticle(
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
        );
    }

    @Override
    public void deleteArticle(Long id) {
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Artikel mit der ID " + id + " wurde nicht gefunden"));

        articleRepository.delete(article);
    }

    @Override
    public double calculateMargin(Long id) {
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Artikel mit der ID " + id + " wurde nicht gefunden"));

        double purchasePrice = article.getPurchasePrice();
        double salesPrice = article.getSalesPrice();

        if (salesPrice <= 0) {
            return 0.0;
        }

        return ((salesPrice - purchasePrice) / salesPrice) * 100.0;
    }

    @Override
    public boolean checkStockLevel(Long id, int requiredQuantity) {
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Artikel mit der ID " + id + " wurde nicht gefunden"));

        return article.getInventory() >= requiredQuantity;
    }

    @Override
    @Transactional
    public ArticleDto updateInventory(Long id, int inventoryChange) {
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Artikel mit der ID " + id + " wurde nicht gefunden"));

        int currentInventory = article.getInventory();
        int newInventory = currentInventory + inventoryChange;

        if (newInventory < 0) {
            throw new StockExceededException(
                    "Nicht genügend Bestand für Artikel " + article.getArticleNumber() + " (" + article.getArticleName() + ")." +
                            " Benötigt: " + (-inventoryChange) + ", Verfügbar: " + currentInventory
            );
        }

        article.setInventory(newInventory);
        Article updatedArticle = articleRepository.save(article);

        return articleDtoFactory.createArticle(
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
        );
    }
}