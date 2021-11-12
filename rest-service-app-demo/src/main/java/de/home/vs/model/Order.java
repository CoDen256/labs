package de.home.vs.model;

public class Order {
    private int id;
    private Article[] articles;

    public Order(int id, Article[] articles) {
        this.id = id;
        this.articles = articles;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Article[] getArticles() {
        return articles;
    }

    public void setArticles(Article[] articles) {
        this.articles = articles;
    }
}
