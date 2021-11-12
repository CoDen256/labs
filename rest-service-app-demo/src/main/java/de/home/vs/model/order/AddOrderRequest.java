package de.home.vs.model.order;

import de.home.vs.model.article.Article;
import java.util.Set;

public class AddOrderRequest {
    private Set<Article> articles;

    public AddOrderRequest(Set<Article> articles) {
        this.articles = articles;
    }

    private AddOrderRequest() {
    }

    public Set<Article> getArticles() {
        return articles;
    }

    public void setArticles(Set<Article> articles) {
        this.articles = articles;
    }
}
