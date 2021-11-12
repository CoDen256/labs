package de.home.vs.model.order;

public class MultipleArticles {
    private int articleId;
    private int count;

    public MultipleArticles(int articleId, int count) {
        this.articleId = articleId;
        this.count = count;
    }

    private MultipleArticles(){
        // constructor for deserialization
    }

    public int getArticleId() {
        return articleId;
    }

    public void setArticleId(int articleId) {
        this.articleId = articleId;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
