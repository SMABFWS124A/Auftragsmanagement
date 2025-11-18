package com.springboot.auftragsmanagement.entity;

public class NullArticle extends Article {

    private static final NullArticle INSTANCE = new NullArticle();

    private NullArticle() {
        this.setId(0L);
        this.setArticleName("Unbekannter Artikel");
        this.setArticleNumber("N/A");
        this.setPurchasePrice(0.0);
    }

    public static NullArticle getInstance() {
        return INSTANCE;
    }

    public boolean isNull() {
        return true;
    }
}