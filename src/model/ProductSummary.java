package model;

public class ProductSummary {
    private final String name;
    private final String description;
    private final double price;
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

    public void decrementQuantity(int amount) {
        if (amount > quantity) {
            throw new IllegalArgumentException("Quantidade insuficiente no estoque.");
        }
        quantity -= amount;
    }
}