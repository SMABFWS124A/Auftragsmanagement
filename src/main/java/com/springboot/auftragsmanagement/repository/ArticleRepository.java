package com.springboot.auftragsmanagement.repository;

import com.springboot.auftragsmanagement.entity.Article;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArticleRepository extends JpaRepository<Article,Long> {
}
