package service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import model.Product;
import model.ProductSummary;
import model.Sale;

public class Inventory {
    private final List<Product> products = new ArrayList<>();
    private final List<Sale> sales = new ArrayList<>();

    public void addProduct(String name, String description, double price, int quantity) {
        for (int i = 0; i < quantity; i++) {
            products.add(new Product(name, description, price));
        }
    }

    public void sellProduct(int productId, int quantity, String customerName) {
        int count = 0;
        List<Product> toRemove = new ArrayList<>();
        for (Product product : products) {
            if (product.getId() == productId && count < quantity) {
                toRemove.add(product);
                sales.add(new Sale(product, customerName));
                count++;
            }
        }
        products.removeAll(toRemove);
    }

    public List<Product> getProducts() {
        return products;
    }

    public List<Sale> getSales() {
        return sales;
    }

    public Map<String, ProductSummary> getProductSummary() {
        Map<String, ProductSummary> summary = new HashMap<>();

        for (Product product : products) {
            String key = product.getName() + "|" + product.getDescription() + "|" + product.getPrice();
            summary.putIfAbsent(key, new ProductSummary(product.getName(), product.getDescription(), product.getPrice(), 0));
            summary.get(key).incrementQuantity();
        }

        return summary;
    }
}