package model;

import java.time.LocalDateTime;

public class Sale {
    private final Product product;
    private final String customerName;
    private final LocalDateTime dateTime;

    public Sale(Product product, String customerName) {
        this.product = product;
        this.customerName = customerName;
        this.dateTime = LocalDateTime.now();
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

    @Override
    public String toString() {
        return "Product: " + product + ", Customer: " + (customerName == null ? "Anonymous" : customerName) + ", Date: " + dateTime;
    }
}