package de.home.vs.model.order;

import de.home.vs.model.article.Article;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Order {
    private int orderId;
    private List<Article> articles;


    private Order() {
    }

    public Order(int orderId, List<Article> articles) {
        this.orderId = orderId;
        this.articles = articles;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public List<Article> getArticles() {
        return articles;
    }

    public void setArticles(List<Article> articles) {
        this.articles = articles;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Order)) return false;
        Order order = (Order) o;
        return orderId == order.orderId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(orderId);
    }
}
