package model;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Sale implements Serializable {

    private static final long serialVersionUID = 1L;

    private Product product; // Produto vendido
    private String customerName; // Nome do cliente
    private LocalDateTime dateTime; // Data e hora da venda
    private String transactionId; // ID único da transação
    private int quantity; // Quantidade vendida

    // Construtor com todos os atributos
    public Sale(Product product, String customerName, LocalDateTime dateTime, String transactionId, int quantity) {
        if (product == null) {
            throw new IllegalArgumentException("O produto não pode ser nulo.");
        }
        if (customerName == null || customerName.trim().isEmpty()) {
            throw new IllegalArgumentException("O nome do cliente não pode estar vazio.");
        }
        if (transactionId == null || transactionId.trim().isEmpty()) {
            throw new IllegalArgumentException("O ID da transação não pode estar vazio.");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("A quantidade deve ser maior que zero.");
        }

        this.product = product;
        this.customerName = customerName.trim();
        this.dateTime = dateTime != null ? dateTime : LocalDateTime.now();
        this.transactionId = transactionId.trim();
        this.quantity = quantity;
    }

    // Getters
    public Product getProduct() {
        return product;
    }

    public String getCustomerName() {
        return customerName;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public int getQuantity() {
        return quantity;
    }

    // toString para depuração
    @Override
    public String toString() {
        return "Sale{" +
                "product=" + product +
                ", customerName='" + customerName + '\'' +
                ", dateTime=" + dateTime +
                ", transactionId='" + transactionId + '\'' +
                ", quantity=" + quantity +
                '}';
    }
}
