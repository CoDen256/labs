package de.home.vs.model;

import java.util.LinkedHashSet;
import java.util.Set;

public class DataSource {

	private static DataSource instance = null;
	private final Set<Article> articles = new LinkedHashSet<Article>();
	private final Set<Order> orders = new LinkedHashSet<Order>();

	private DataSource() {
	}
	
	public static DataSource getInstance() {
		if (instance == null) {
			instance = new DataSource();
		}
		return instance;
	}
	
	public void prefillData() {
		addArticle(new Article(0, "Alpha", "AAA", 1000));
		addArticle(new Article(1, "Beta", "BBB", 0));
		addArticle(new Article(2, "Gamma", "GGG", 13000));
		addArticle(new Article(3, "Delta", "DDD", 50));
		addArticle(new Article(4, "Epsilon", "EEE", 9000));
		addOrder(new Order(
				0,
				articles
		));
	}

	public Article findArticleById(int id){
		for (Article a: articles){
			if (a.getId() == id){
				return a;
			}
		}
		return null;
	}

	public Set<Article> getArticles(){
		return new LinkedHashSet<Article>(articles);
	}


	public void addOrder(Order a){
		if (findOrderById(a.getId()) != null)
			throw new OrderAlreadyExistsException(String.format("Article with id %s already exists", a.getId()));
		orders.add(a);
	}

	public Order findOrderById(int id){
		for (Order o: orders){
			if (o.getId() == id){
				return o;
			}
		}
		return null;
	}

	public Set<Article> getOrders(){
		return new LinkedHashSet<Article>(articles);
	}

	public void addArticle(Article a){
		if (findOrderById(a.getId()) != null)
			throw new ArticleAlreadyExistsException(String.format("Article with id %s already exists", a.getId()));
		articles.add(a);
	}


	public static class ArticleAlreadyExistsException extends RuntimeException{
		public ArticleAlreadyExistsException(String s) {
			super(s);
		}
	}

	public static class OrderAlreadyExistsException extends RuntimeException{
		public OrderAlreadyExistsException(String s) {
			super(s);
		}
	}
}
