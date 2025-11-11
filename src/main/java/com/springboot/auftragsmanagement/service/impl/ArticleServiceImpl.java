package com.springboot.auftragsmanagement.service.impl;

import com.springboot.auftragsmanagement.dto.ArticleDto;
import com.springboot.auftragsmanagement.entity.Article;
import com.springboot.auftragsmanagement.exception.ResourceNotFoundException;
import com.springboot.auftragsmanagement.exception.StockExceededException;
import com.springboot.auftragsmanagement.mapper.ArticleMapper;
import com.springboot.auftragsmanagement.repository.ArticleRepository;
import com.springboot.auftragsmanagement.service.ArticleService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ArticleServiceImpl implements ArticleService {

    private final ArticleRepository articleRepository;
    private final ArticleMapper articleMapper;

    public ArticleServiceImpl(ArticleRepository articleRepository, ArticleMapper articleMapper){
        this.articleRepository = articleRepository;
        this.articleMapper = articleMapper;
    }


    @Override
    public ArticleDto createArticle(ArticleDto articleDto) {
        Article article = articleMapper.toEntity(articleDto);
        Article savedArticle = articleRepository.save(article);
        return articleMapper.toDto(savedArticle);
    }

    @Override
    public ArticleDto getArticleById(Long id) {
        Article article = articleRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("Artikel mit der ID " +id+ " wurde nicht gefunden"));

        return articleMapper.toDto(article);
    }

    @Override
    public List<ArticleDto> getAllArticles() {
        List <Article> articles = articleRepository.findAll();
        return articles.stream()
                .map(articleMapper::toDto)
                .toList();
    }

    @Override
    public ArticleDto updateArticle(Long id, ArticleDto articleDto) {
        Article article = articleRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("Artikel mit der ID " +id+ " wurde nicht gefunden"));

        article.setArticleNumber(articleDto.articleNumber());
        article.setArticleName(articleDto.articleName());
        article.setPurchasePrice(articleDto.purchasePrice());
        article.setSalesPrice(articleDto.salesPrice());
        article.setCategory(articleDto.category());
        article.setInventory(articleDto.inventory());
        article.setActive(articleDto.active());
        article.setDescription(articleDto.description());

        Article updatedArticle = articleRepository.save(article);

        return articleMapper.toDto(updatedArticle);
    }

    @Override
    public void deleteArticle(Long id) {
        Article article = articleRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("Artikel mit der ID " +id+ " wurde nicht gefunden"));

        articleRepository.delete(article);
    }


    @Override
    public double calculateMargin(Long id) {
        Article article = articleRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("Artikel mit der ID " +id+ " wurde nicht gefunden"));

        double purchase = article.getPurchasePrice();
        double sales = article.getSalesPrice();

        if (sales <= 0) {
            return 0.0;
        }

        return ((sales - purchase) / sales) * 100.0;
    }

    @Override
    public boolean checkStockLevel(Long id, int requiredQuantity) {
        Article article = articleRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("Artikel mit der ID " +id+ " wurde nicht gefunden"));

        return article.getInventory() >= requiredQuantity;
    }

    @Override
    @Transactional
    public ArticleDto updateInventory(Long id, int inventoryChange) {
        Article article = articleRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("Artikel mit der ID " +id+ " wurde nicht gefunden"));

        int currentInventory = article.getInventory();
        int newInventory = currentInventory + inventoryChange;

        if (newInventory < 0) {
            int required = -inventoryChange;

            throw new StockExceededException(
                    "Nicht genügend Bestand für Artikel " + article.getArticleNumber() + " (" + article.getArticleName() + ")." +
                            " Benötigt: " + required + ", Verfügbar: " + currentInventory
            );
        }


        article.setInventory(newInventory);

        Article updatedArticle = articleRepository.save(article);
        return articleMapper.toDto(updatedArticle);
    }
}