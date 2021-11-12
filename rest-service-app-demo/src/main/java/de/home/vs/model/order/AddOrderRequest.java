package de.home.vs.model.order;

import java.util.List;

public class AddOrderRequest {
    private List<MultipleArticles> articles;

    public AddOrderRequest(List<MultipleArticles> articles) {
        this.articles = articles;
    }

    private AddOrderRequest() {
    }

    public List<MultipleArticles> getArticles() {
        return articles;
    }

    public void setArticles(List<MultipleArticles> multipleArticles) {
        this.articles = multipleArticles;
    }
}
