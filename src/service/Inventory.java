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

    // Adiciona um produto ao inventário
    public void addProduct(String name, String description, double price, int quantity) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("O nome do produto não pode estar vazio.");
        }
        if (description == null || description.trim().isEmpty()) {
            throw new IllegalArgumentException("A descrição do produto não pode estar vazia.");
        }
        if (price <= 0) {
            throw new IllegalArgumentException("O preço do produto deve ser maior que zero.");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("A quantidade do produto deve ser maior que zero.");
        }

        String key = name.trim() + "|" + description.trim() + "|" + price;
        ProductSummary summary = productSummaries.getOrDefault(key, new ProductSummary(name.trim(), description.trim(), price, 0));
        summary.decrementQuantity(-quantity); // Adiciona ao estoque
        productSummaries.put(key, summary);

        for (int i = 0; i < quantity; i++) {
            products.add(new Product(name.trim(), description.trim(), price));
        }
    }


    // Realiza a venda de produtos
    public void sellProducts(List<ProductSummary> productSummaries, List<Integer> quantities, String customerName) {
        System.out.println("sellProducts chamado com " + productSummaries.size() + " produtos.");

        if (productSummaries.size() != quantities.size()) {
            throw new IllegalArgumentException("Produtos e quantidades não correspondem.");
        }

        String transactionId = "TX-" + System.nanoTime(); // Gera um ID único para a transação
        LocalDateTime saleDate = LocalDateTime.now();

        for (int i = 0; i < productSummaries.size(); i++) {
            ProductSummary summary = productSummaries.get(i);
            int quantity = quantities.get(i);

            if (summary.getQuantity() < quantity) {
                throw new IllegalArgumentException("Quantidade insuficiente para o produto: " + summary.getName());
            }

            int count = 0;
            List<Product> toRemove = new ArrayList<>();

            System.out.println("Tentando vender " + quantity + " unidades de " + summary.getName());
            for (Product product : products) {
                boolean matches = product.getName().trim().equalsIgnoreCase(summary.getName().trim()) &&
                                  product.getDescription().trim().equalsIgnoreCase(summary.getDescription().trim()) &&
                                  Math.abs(product.getPrice() - summary.getPrice()) < 0.001;

                if (matches && count < quantity) {
                    toRemove.add(product);
                    sales.add(new Sale(product, customerName, saleDate, transactionId, 1)); // Registra a venda de 1 unidade
                    count++;
                }
            }

            if (count < quantity) {
                System.out.println("Produtos encontrados: " + count + ". Estoque insuficiente ou dados não correspondem.");
                throw new IllegalStateException("Não foi possível encontrar todos os produtos para venda: " + summary.getName());
            }

            products.removeAll(toRemove); // Remove os produtos vendidos do estoque
            summary.decrementQuantity(count); // Atualiza a quantidade no resumo do produto
            System.out.println("Quantidade vendida: " + count + ". Estoque restante: " + summary.getQuantity());
        }

        System.out.println("Vendas totais após venda: " + sales.size());
    }




    public Map<String, ProductSummary> getProductSummary() {
        return productSummaries.entrySet().stream()
            .filter(entry -> entry.getValue().getQuantity() > 0) // Apenas produtos com quantidade disponível
            .collect(Collectors.toMap(
                Map.Entry::getKey,
                Map.Entry::getValue,
                (e1, e2) -> e1,
                LinkedHashMap::new // Mantém a ordem de inserção
            ));
    }




    // Retorna as transações de vendas agrupadas por ID da transação
    public Map<String, List<Sale>> getSalesTransactions() {
        System.out.println("Chamando getSalesTransactions, vendas registradas: " + sales.size());
        return sales.stream().collect(Collectors.groupingBy(Sale::getTransactionId));
    }
    
    
    public void updateProductAttributes(String oldKey, String newName, String newDescription, double newPrice) {
        ProductSummary summary = productSummaries.remove(oldKey);

        if (summary == null) {
            throw new IllegalArgumentException("Produto não encontrado no inventário!");
        }

        // Atualiza os atributos no resumo
        summary.setName(newName);
        summary.setDescription(newDescription);
        summary.setPrice(newPrice);

        String newKey = newName + "|" + newDescription + "|" + newPrice;
        productSummaries.put(newKey, summary);

        // Atualiza os produtos no estoque
        for (Product product : products) {
            if (product.getName().equals(summary.getName()) &&
                product.getDescription().equals(summary.getDescription()) &&
                Math.abs(product.getPrice() - summary.getPrice()) < 0.001) {
                product.setName(newName);
                product.setDescription(newDescription);
                product.setPrice(newPrice);
            }
        }
    }



}
