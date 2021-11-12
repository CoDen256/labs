package de.home.vs.model.order;

import de.home.vs.model.article.Article;
import java.util.Objects;
import java.util.Set;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Order {
    private int id;
    private Set<Article> articles;


    private Order() {
    }

    public Order(int id, Set<Article> articles) {
        this.id = id;
        this.articles = articles;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Set<Article> getArticles() {
        return articles;
    }

    public void setArticles(Set<Article> articles) {
        this.articles = articles;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Order)) return false;
        Order order = (Order) o;
        return id == order.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
