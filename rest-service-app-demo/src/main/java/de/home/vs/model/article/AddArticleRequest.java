package de.home.vs.model.article;

public class AddArticleRequest {
    private String name;
    private String description;
    private long price;

    public AddArticleRequest(String name, String description, long price) {
        this.name = name;
        this.description = description;
        this.price = price;
    }

    private AddArticleRequest(){

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getPrice() {
        return price;
    }

    public void setPrice(long price) {
        this.price = price;
    }
}
