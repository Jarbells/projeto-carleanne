package view;

import java.awt.GridLayout;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import model.ProductSummary;
import service.Inventory;

public class InventoryManagementApp {
    private static Inventory inventory = new Inventory();

    public static void main(String[] args) {
        SwingUtilities.invokeLater(InventoryManagementApp::createAndShowGUI);
    }

    private static void createAndShowGUI() {
        JFrame frame = new JFrame("Inventory Management");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 400);

        JPanel panel = new JPanel(new GridLayout(3, 1));

        JButton addProductButton = new JButton("Add Product");
        JButton viewInventoryButton = new JButton("View Inventory");
        JButton sellProductButton = new JButton("Sell Product");

        panel.add(addProductButton);
        panel.add(viewInventoryButton);
        panel.add(sellProductButton);

        frame.add(panel);

        addProductButton.addActionListener(e -> showAddProductDialog(frame));
        viewInventoryButton.addActionListener(e -> showInventoryDialog(frame));
        sellProductButton.addActionListener(e -> showSellProductDialog(frame));

        frame.setVisible(true);
    }

    private static void showAddProductDialog(JFrame parentFrame) {
        JTextField nameField = new JTextField();
        JTextField descriptionField = new JTextField();
        JTextField priceField = new JTextField();
        JTextField quantityField = new JTextField();

        Object[] message = {
            "Name:", nameField,
            "Description:", descriptionField,
            "Price:", priceField,
            "Quantity:", quantityField
        };

        int option = JOptionPane.showConfirmDialog(parentFrame, message, "Add Product", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            String name = nameField.getText();
            String description = descriptionField.getText();
            double price = Double.parseDouble(priceField.getText());
            int quantity = Integer.parseInt(quantityField.getText());

            inventory.addProduct(name, description, price, quantity);
            JOptionPane.showMessageDialog(parentFrame, "Product added successfully!");

            int nextAction = JOptionPane.showOptionDialog(parentFrame, "Do you want to continue adding products?", "Continue or Cancel", 
                    JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, new String[]{"Continue", "Cancel"}, "Continue");

            if (nextAction == JOptionPane.YES_OPTION) {
                showAddProductDialog(parentFrame);
            }
        }
    }

    private static void showInventoryDialog(JFrame parentFrame) {
        StringBuilder inventoryDisplay = new StringBuilder();
        double totalValue = 0;

        Map<String, ProductSummary> summary = inventory.getProductSummary();

        for (ProductSummary productSummary : summary.values()) {
            inventoryDisplay.append("---------------------------------\n")
                    .append("Produto: ").append(productSummary.getName()).append("\n")
                    .append("Descrição: ").append(productSummary.getDescription()).append("\n")
                    .append("Quantidade disponível: ").append(productSummary.getQuantity()).append("\n")
                    .append("Preço unitário R$: ").append(String.format("%.2f", productSummary.getPrice())).append("\n")
                    .append("Valor total R$: ").append(String.format("%.2f", productSummary.getTotalValue())).append("\n");
            totalValue += productSummary.getTotalValue();
        }

        inventoryDisplay.append("-----------------------------------\n")
                .append("Total em mercadorias R$: ").append(String.format("%.2f", totalValue)).append("\n")
                .append("-----------------------------------");

        JOptionPane.showMessageDialog(parentFrame, inventoryDisplay.toString());
    }

    private static void showSellProductDialog(JFrame parentFrame) {
        JTextField productIdField = new JTextField();
        JTextField quantityField = new JTextField();
        JTextField customerField = new JTextField();

        Object[] message = {
            "Product ID:", productIdField,
            "Quantity:", quantityField,
            "Customer Name (Optional):", customerField
        };

        int option = JOptionPane.showConfirmDialog(parentFrame, message, "Sell Product", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            int productId = Integer.parseInt(productIdField.getText());
            int quantity = Integer.parseInt(quantityField.getText());
            String customerName = customerField.getText();

            inventory.sellProduct(productId, quantity, customerName.isEmpty() ? null : customerName);
            JOptionPane.showMessageDialog(parentFrame, "Product sold successfully!");
        }
    }
}