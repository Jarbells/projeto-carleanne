package service;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import model.Product;
import model.ProductSummary;
import model.Sale;

public class Inventory implements Serializable {

	private static final long serialVersionUID = 1L;
	private final List<Product> products = new ArrayList<>();
    private final List<Sale> sales = new ArrayList<>();
    private final Map<String, ProductSummary> productSummaries = new HashMap<>();

    public void addProduct(String name, String description, double price, int quantity) {
        String key = name + "|" + description + "|" + price;
        ProductSummary summary = productSummaries.getOrDefault(key, new ProductSummary(name, description, price, 0));
        summary.decrementQuantity(-quantity); // Adiciona ao estoque
        productSummaries.put(key, summary);

        for (int i = 0; i < quantity; i++) {
            products.add(new Product(name, description, price));
        }
    }

    public Map<String, ProductSummary> getProductSummary() {
        // Filtra os produtos com quantidade maior que 0
        return productSummaries.entrySet().stream()
            .filter(entry -> entry.getValue().getQuantity() > 0)
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
    }


    public void sellProducts(List<ProductSummary> productSummaries, List<Integer> quantities, String customerName) {
        if (productSummaries.size() != quantities.size()) {
            throw new IllegalArgumentException("Produtos e quantidades não correspondem.");
        }

        String transactionId = "TX-" + System.nanoTime();
        LocalDateTime saleDate = LocalDateTime.now();

        for (int i = 0; i < productSummaries.size(); i++) {
            ProductSummary summary = productSummaries.get(i);
            int quantity = quantities.get(i);

            if (summary.getQuantity() < quantity) {
                throw new IllegalArgumentException("Quantidade insuficiente para o produto: " + summary.getName());
            }

            int count = 0;
            List<Product> toRemove = new ArrayList<>();
            for (Product product : products) {
                if (product.getName().equals(summary.getName()) &&
                    product.getDescription().equals(summary.getDescription()) &&
                    product.getPrice() == summary.getPrice() && count < quantity) {
                    toRemove.add(product);
                    sales.add(new Sale(product, customerName, saleDate, transactionId));
                    count++;
                }
            }

            products.removeAll(toRemove);
            summary.decrementQuantity(count);
        }
    }

    public Map<String, List<Sale>> getSalesTransactions() {
        return sales.stream().collect(Collectors.groupingBy(Sale::getTransactionId));
    }
}