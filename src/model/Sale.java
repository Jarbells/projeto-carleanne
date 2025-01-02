package model;

import java.time.LocalDateTime;

public class Sale {
    private final Product product;
    private final String customerName;
    private final LocalDateTime dateTime;
    private final String transactionId;

    public Sale(Product product, String customerName, LocalDateTime dateTime, String transactionId) {
        this.product = product;
        this.customerName = customerName;
        this.dateTime = dateTime;
        this.transactionId = transactionId;
    }

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
}