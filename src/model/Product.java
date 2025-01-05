package model;

import java.io.Serializable;

public class Product implements Serializable {

    private static final long serialVersionUID = 1L;
    private String name;
    private String description;
    private double price;

    public Product(String name, String description, double price) {
        setName(name);
        setDescription(description);
        setPrice(price);
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

    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("O nome do produto não pode estar vazio.");
        }
        this.name = name.trim();
    }

    public void setDescription(String description) {
        if (description == null || description.trim().isEmpty()) {
            throw new IllegalArgumentException("A descrição do produto não pode estar vazia.");
        }
        this.description = description.trim();
    }

    public void setPrice(double price) {
        if (price <= 0) {
            throw new IllegalArgumentException("O preço do produto deve ser maior que zero.");
        }
        this.price = price;
    }

    @Override
    public String toString() {
        return "Product{" +
               "name='" + name + '\'' +
               ", description='" + description + '\'' +
               ", price=" + price +
               '}';
    }
}
