package view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.HashMap;
import java.util.Map;

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

        JButton addProductButton = new JButton("Adicionar Produtos");
        JButton viewInventoryButton = new JButton("Ver Estoque");
        JButton sellProductButton = new JButton("Vender");

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
            "Nome:", nameField,
            "Descrição:", descriptionField,
            "Preço:", priceField,
            "Quantidade:", quantityField
        };

        int option = JOptionPane.showConfirmDialog(parentFrame, message, "Adicionar Produto", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            try {
                String name = nameField.getText();
                String description = descriptionField.getText();

                if (name.isEmpty() || description.isEmpty()) {
                    throw new IllegalArgumentException("Nome e Descrição não podem estar vazios.");
                }

                double price = Double.parseDouble(priceField.getText());
                if (price <= 0) {
                    throw new IllegalArgumentException("O preço deve ser maior que 0.");
                }

                int quantity = Integer.parseInt(quantityField.getText());
                if (quantity <= 0) {
                    throw new IllegalArgumentException("A quantidade deve ser maior que 0.");
                }

                inventory.addProduct(name, description, price, quantity);
                JOptionPane.showMessageDialog(parentFrame, "Produto adicionado com sucesso!");

                int nextAction = JOptionPane.showOptionDialog(parentFrame, "Deseja continuar adicionando produtos?", "Continuar ou Cancelar", 
                        JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, new String[]{"Continuar", "Cancelar"}, "Continuar");

                if (nextAction == JOptionPane.YES_OPTION) {
                    showAddProductDialog(parentFrame);
                }

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(parentFrame, "O preço e a quantidade devem ser números válidos.", "Erro de Entrada", JOptionPane.ERROR_MESSAGE);
                showAddProductDialog(parentFrame);
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(parentFrame, ex.getMessage(), "Erro de Entrada", JOptionPane.ERROR_MESSAGE);
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

        JTextArea textArea = new JTextArea(inventoryDisplay.toString());
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(500, 400));

        JOptionPane.showMessageDialog(parentFrame, scrollPane, "Inventory", JOptionPane.INFORMATION_MESSAGE);
    }

    private static void showSellProductDialog(JFrame parentFrame) {
        JPanel mainPanel = new JPanel(new BorderLayout());

        JPanel productPanelContainer = new JPanel();
        productPanelContainer.setLayout(new BoxLayout(productPanelContainer, BoxLayout.Y_AXIS));

        Map<String, ProductSummary> summary = inventory.getProductSummary();
        Map<ProductSummary, Integer> selectedProducts = new HashMap<>();
        JLabel totalLabel = new JLabel("Total da compra: R$ 0.00");

        for (ProductSummary productSummary : summary.values()) {
            JPanel productPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

            JLabel productLabel = new JLabel(
                    "Produto: " + productSummary.getName() + " | Quantidade disponível: " + productSummary.getQuantity() + " | Preço: R$ " + productSummary.getPrice()
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
                            "Produto: " + productSummary.getName() + " | Quantidade disponível: " + (productSummary.getQuantity() - selectedQuantity) + " | Preço: R$ " + productSummary.getPrice()
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
                            "Produto: " + productSummary.getName() + " | Quantidade disponível: " + (productSummary.getQuantity() - selectedQuantity) + " | Preço: R$ " + productSummary.getPrice()
                    );
                    updateTotalLabel(selectedProducts, totalLabel);
                }
            });

            productPanel.add(productLabel);
            productPanel.add(addButton);
            productPanel.add(removeButton);
            productPanel.add(selectedQuantityLabel);

            productPanelContainer.add(productPanel);
        }

        JScrollPane scrollPane = new JScrollPane(productPanelContainer);
        scrollPane.setPreferredSize(new Dimension(600, 400));

        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        footerPanel.add(totalLabel);

        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(footerPanel, BorderLayout.SOUTH);

        int result = JOptionPane.showConfirmDialog(parentFrame, mainPanel, "Sell Products", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            double totalPrice = 0;
            for (Map.Entry<ProductSummary, Integer> entry : selectedProducts.entrySet()) {
                ProductSummary productSummary = entry.getKey();
                int selectedQuantity = entry.getValue();

                if (selectedQuantity > 0) {
                    inventory.sellProduct(productSummary.getName().hashCode(), selectedQuantity, null);
                    totalPrice += selectedQuantity * productSummary.getPrice();
                }
            }

            JTextField customerField = new JTextField();
            Object[] message = {
                "Total da compra: R$ " + String.format("%.2f", totalPrice),
                "Nome do cliente (opcional):", customerField
            };

            int confirm = JOptionPane.showConfirmDialog(parentFrame, message, "Finalizar Compra", JOptionPane.OK_CANCEL_OPTION);

            if (confirm == JOptionPane.OK_OPTION) {
                String customerName = customerField.getText();
                if (customerName.isEmpty()) {
                    customerName = null;
                }
                JOptionPane.showMessageDialog(parentFrame, "Compra finalizada com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    private static void updateTotalLabel(Map<ProductSummary, Integer> selectedProducts, JLabel totalLabel) {
        double totalPrice = 0;
        for (Map.Entry<ProductSummary, Integer> entry : selectedProducts.entrySet()) {
            ProductSummary productSummary = entry.getKey();
            int selectedQuantity = entry.getValue();
            totalPrice += selectedQuantity * productSummary.getPrice();
        }
        totalLabel.setText("Total da compra: R$ " + String.format("%.2f", totalPrice));
    }

}