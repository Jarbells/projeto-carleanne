package model;

import java.io.Serializable;

public class ProductSummary implements Serializable {

	private static final long serialVersionUID = 1L;
	private String name;
    private String description;
    private double price;
    private int quantity;

    public ProductSummary(String name, String description, double price, int quantity) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.quantity = quantity;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public double getPrice() {
        return price;
    }

    public int getQuantity() {
        return quantity;
    }
    
    public void setName(String name) {
        this.name = name;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }



    public void decrementQuantity(int amount) {
        if (amount > quantity) {
            throw new IllegalArgumentException("Quantidade insuficiente no estoque.");
        }
        quantity -= amount;
    }
}