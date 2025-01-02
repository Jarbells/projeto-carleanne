package view;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import model.Product;
import model.ProductSummary;
import model.Sale;
import service.Inventory;

public class InventoryManagementApp {
    private static Inventory inventory = new Inventory();

    public static void main(String[] args) {
        SwingUtilities.invokeLater(InventoryManagementApp::createAndShowGUI);
    }

    private static void createAndShowGUI() {
        JFrame frame = new JFrame("Gestão de Inventário");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 400);

        JPanel panel = new JPanel(new GridLayout(4, 1));

        JButton addProductButton = new JButton("Adicionar Produtos");
        JButton viewInventoryButton = new JButton("Ver Estoque");
        JButton sellProductButton = new JButton("Vender");
        JButton viewSalesButton = new JButton("Consultar Vendas");

        panel.add(addProductButton);
        panel.add(viewInventoryButton);
        panel.add(sellProductButton);
        panel.add(viewSalesButton);

        frame.add(panel);

        addProductButton.addActionListener(e -> showAddProductDialog(frame));
        viewInventoryButton.addActionListener(e -> showInventoryDialog(frame));
        sellProductButton.addActionListener(e -> showSellProductDialog(frame));
        viewSalesButton.addActionListener(e -> showSalesDialog(frame));

        frame.setVisible(true);
    }

    private static void showAddProductDialog(JFrame parentFrame) {
        JTextField nameField = new JTextField();
        JTextField descriptionField = new JTextField();
        JTextField priceField = new JTextField();
        JTextField quantityField = new JTextField();

        Object[] message = {
            "Nome do Produto:", nameField,
            "Descrição:", descriptionField,
            "Preço:", priceField,
            "Quantidade:", quantityField
        };

        int option = JOptionPane.showConfirmDialog(parentFrame, message, "Adicionar Produto", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            try {
                String name = nameField.getText();
                String description = descriptionField.getText();
                double price = Double.parseDouble(priceField.getText());
                int quantity = Integer.parseInt(quantityField.getText());
                inventory.addProduct(name, description, price, quantity);
                JOptionPane.showMessageDialog(parentFrame, "Produto adicionado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(parentFrame, "Por favor, insira valores válidos.", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private static void showInventoryDialog(JFrame parentFrame) {
        StringBuilder inventoryDisplay = new StringBuilder();
        inventory.getProductSummary().values().forEach(summary -> {
            inventoryDisplay.append(String.format("Produto: %s\nDescrição: %s\nQuantidade disponível: %d\nPreço unitário: R$ %.2f\n\n",
                    summary.getName(), summary.getDescription(), summary.getQuantity(), summary.getPrice()));
        });

        JTextArea textArea = new JTextArea(inventoryDisplay.toString());
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);

        JOptionPane.showMessageDialog(parentFrame, scrollPane, "Estoque", JOptionPane.INFORMATION_MESSAGE);
    }

    private static void showSellProductDialog(JFrame parentFrame) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        Map<String, ProductSummary> summary = inventory.getProductSummary();
        Map<ProductSummary, Integer> selectedProducts = new HashMap<>();
        JLabel totalLabel = new JLabel("Total da compra: R$ 0.00");

        for (ProductSummary productSummary : summary.values()) {
            JPanel productPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

            JLabel productLabel = new JLabel(
                    "Produto: " + productSummary.getName() +
                            " | Quantidade disponível: " + productSummary.getQuantity() +
                            " | Preço: R$ " + productSummary.getPrice()
            );

            JButton addButton = new JButton("+");
            JButton removeButton = new JButton("-");

            JLabel selectedQuantityLabel = new JLabel("Selecionado: 0");
            selectedProducts.put(productSummary, 0);

            addButton.addActionListener(e -> {
                int selectedQuantity = selectedProducts.get(productSummary);
                if (selectedQuantity < productSummary.getQuantity()) {
                    selectedQuantity++;
                    selectedProducts.put(productSummary, selectedQuantity);
                    selectedQuantityLabel.setText("Selecionado: " + selectedQuantity);
                    productLabel.setText(
                            "Produto: " + productSummary.getName() +
                                    " | Quantidade disponível: " + (productSummary.getQuantity() - selectedQuantity) +
                                    " | Preço: R$ " + productSummary.getPrice()
                    );
                    updateTotalLabel(selectedProducts, totalLabel);
                } else {
                    JOptionPane.showMessageDialog(parentFrame, "Não há mais produtos disponíveis!", "Erro", JOptionPane.ERROR_MESSAGE);
                }
            });

            removeButton.addActionListener(e -> {
                int selectedQuantity = selectedProducts.get(productSummary);
                if (selectedQuantity > 0) {
                    selectedQuantity--;
                    selectedProducts.put(productSummary, selectedQuantity);
                    selectedQuantityLabel.setText("Selecionado: " + selectedQuantity);
                    productLabel.setText(
                            "Produto: " + productSummary.getName() +
                                    " | Quantidade disponível: " + (productSummary.getQuantity() - selectedQuantity) +
                                    " | Preço: R$ " + productSummary.getPrice()
                    );
                    updateTotalLabel(selectedProducts, totalLabel);
                }
            });

            productPanel.add(productLabel);
            productPanel.add(addButton);
            productPanel.add(removeButton);
            productPanel.add(selectedQuantityLabel);

            panel.add(productPanel);
        }

        panel.add(Box.createVerticalStrut(10));
        panel.add(totalLabel);

        JScrollPane scrollPane = new JScrollPane(panel);
        scrollPane.setPreferredSize(new Dimension(600, 400));

        int result = JOptionPane.showConfirmDialog(parentFrame, scrollPane, "Vender Produtos", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            double totalPrice = 0;

            JTextField customerField = new JTextField();
            Object[] message = {
                    "Total da compra: R$ " + String.format("%.2f", totalPrice),
                    "Nome do cliente (opcional):", customerField
            };

            int confirm = JOptionPane.showConfirmDialog(parentFrame, message, "Finalizar Compra", JOptionPane.OK_CANCEL_OPTION);

            if (confirm == JOptionPane.OK_OPTION) {
                String customerName = customerField.getText();

                List<ProductSummary> productsToSell = new ArrayList<>();
                List<Integer> quantities = new ArrayList<>();

                for (Map.Entry<ProductSummary, Integer> entry : selectedProducts.entrySet()) {
                    ProductSummary productSummary = entry.getKey();
                    int selectedQuantity = entry.getValue();

                    if (selectedQuantity > 0) {
                        productsToSell.add(productSummary);
                        quantities.add(selectedQuantity);
                        totalPrice += selectedQuantity * productSummary.getPrice();
                    }
                }

                if (!productsToSell.isEmpty()) {
                    inventory.sellProducts(
                        productsToSell,
                        quantities,
                        customerName.isEmpty() ? null : customerName // Passa o nome do cliente ou null
                    );

                    JOptionPane.showMessageDialog(parentFrame, "Compra finalizada com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(parentFrame, "Nenhum produto selecionado para venda.", "Aviso", JOptionPane.WARNING_MESSAGE);
                }
            }
        }
    }

    private static void updateTotalLabel(Map<ProductSummary, Integer> selectedProducts, JLabel totalLabel) {
        double total = selectedProducts.entrySet().stream()
                .mapToDouble(entry -> entry.getKey().getPrice() * entry.getValue())
                .sum();
        totalLabel.setText("Total da compra: R$ " + String.format("%.2f", total));
    }

    private static void showSalesDialog(JFrame parentFrame) {
        StringBuilder salesDisplay = new StringBuilder();

        Map<String, List<Sale>> salesTransactions = inventory.getSalesTransactions();

        for (Map.Entry<String, List<Sale>> entry : salesTransactions.entrySet()) {
            List<Sale> sales = entry.getValue();

            if (!sales.isEmpty()) {
                String customerName = sales.get(0).getCustomerName() != null ? sales.get(0).getCustomerName() : "Anônimo";
                LocalDateTime dateTime = sales.get(0).getDateTime();

                salesDisplay.append("Cliente: ").append(customerName).append("\n");
                salesDisplay.append("Data: ").append(dateTime).append("\n");
                salesDisplay.append("--------------------------------------------------------------\n");
                salesDisplay.append(String.format("%-20s | %-10s | %-13s | %-10s\n", "Produto(s)", "Qtde.", "Preço Und.", "Total"));
                salesDisplay.append("--------------------------------------------------------------\n");

                double totalAmount = 0;
                for (Sale sale : sales) {
                    Product product = sale.getProduct();
                    salesDisplay.append(String.format("%-20s | %-10d | %-13.2f | %-10.2f\n",
                            product.getName(), 1, product.getPrice(), product.getPrice()));
                    totalAmount += product.getPrice();
                }

                salesDisplay.append("--------------------------------------------------------------\n");
                salesDisplay.append(String.format("Total.......................................R$: %.2f\n\n", totalAmount));
            }
        }

        JTextArea textArea = new JTextArea(salesDisplay.toString());
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);

        JOptionPane.showMessageDialog(parentFrame, scrollPane, "Histórico de Vendas", JOptionPane.INFORMATION_MESSAGE);
    }

}