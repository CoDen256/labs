package de.home.vs.model.order;

import java.util.Set;

public class AddOrderRequest {
    private Set<MultipleArticles> multipleArticles;

    public AddOrderRequest(Set<MultipleArticles> multipleArticles) {
        this.multipleArticles = multipleArticles;
    }

    private AddOrderRequest() {
    }

    public Set<MultipleArticles> getArticles() {
        return multipleArticles;
    }

    public void setArticles(Set<MultipleArticles> multipleArticles) {
        this.multipleArticles = multipleArticles;
    }
}
