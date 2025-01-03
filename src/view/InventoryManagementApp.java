package view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import model.ProductSummary;
import model.Sale;
import service.Inventory;

public class InventoryManagementApp {
    private static final String DATA_DIRECTORY = "C:\\Vendas";
    private static final String INVENTORY_FILE = DATA_DIRECTORY + "\\inventory_data.bin";
    private static Inventory inventory;

    public static void main(String[] args) {
        loadInventory();
        SwingUtilities.invokeLater(InventoryManagementApp::createAndShowGUI);
    }   
    

    private static void saveInventory() {
        try {
            File directory = new File(DATA_DIRECTORY);
            if (!directory.exists()) {
                directory.mkdirs();
            }

            File tempFile = new File(INVENTORY_FILE + ".tmp");
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(tempFile))) {
                oos.writeObject(inventory);
            }

            File originalFile = new File(INVENTORY_FILE);
            if (originalFile.exists() && !originalFile.delete()) {
                throw new IOException("Falha ao substituir o arquivo original.");
            }

            if (!tempFile.renameTo(originalFile)) {
                throw new IOException("Falha ao renomear o arquivo temporário.");
            }

        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Erro ao salvar os dados do inventário: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    
    
    private static void loadInventory() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(INVENTORY_FILE))) {
            inventory = (Inventory) ois.readObject();
        } catch (FileNotFoundException e) {
            inventory = new Inventory(); // Caso não exista o arquivo, inicia um novo inventário
        } catch (IOException | ClassNotFoundException e) {
            JOptionPane.showMessageDialog(null, "Erro ao carregar os dados do inventário: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            inventory = new Inventory();
        }
    }

    
    
    private static void createAndShowGUI() {
        JFrame frame = new JFrame("AROMA MULTIMARCAS - Gestão de Estoque");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 400);

        // Configuração de fonte global
        Font fonteGrande = new Font("Arial", Font.PLAIN, 24);
        Font fonteTitulo = new Font("Arial", Font.BOLD, 28);

        // Painel principal
        JPanel panel = new JPanel(new BorderLayout());

        // Adiciona o título com o tema do programa
        JLabel titleLabel = new JLabel("AROMA MULTIMARCAS", JLabel.CENTER);
        titleLabel.setFont(fonteTitulo);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0)); // Adiciona espaçamento

        // Painel para os botões
        JPanel buttonPanel = new JPanel(new GridLayout(4, 1, 10, 10)); // Espaçamento entre botões
        JButton addProductButton = new JButton("Adicionar Produtos");
        addProductButton.setFont(fonteGrande);

        JButton viewInventoryButton = new JButton("Ver Estoque");
        viewInventoryButton.setFont(fonteGrande);

        JButton sellProductButton = new JButton("Vender");
        sellProductButton.setFont(fonteGrande);

        JButton viewSalesButton = new JButton("Consultar Vendas");
        viewSalesButton.setFont(fonteGrande);

        buttonPanel.add(addProductButton);
        buttonPanel.add(viewInventoryButton);
        buttonPanel.add(sellProductButton);
        buttonPanel.add(viewSalesButton);

        // Adiciona o título e os botões ao painel principal
        panel.add(titleLabel, BorderLayout.NORTH); // Adiciona o título no topo
        panel.add(buttonPanel, BorderLayout.CENTER); // Adiciona os botões no centro

        frame.add(panel);

        // Ações dos botões
        addProductButton.addActionListener(e -> showAddProductDialog(frame));
        viewInventoryButton.addActionListener(e -> showInventoryDialog(frame));
        sellProductButton.addActionListener(e -> showSellProductDialog(frame));
        viewSalesButton.addActionListener(e -> showSalesDialog(frame));

        frame.setVisible(true);
    }

    

    private static void showAddProductDialog(JFrame parentFrame) {
        Font fonteGrande = new Font("Arial", Font.PLAIN, 20); // Fonte com tamanho 20

        // Criação de um JFrame para total controle da janela
        JFrame dialog = new JFrame("Adicionar Produto");
        dialog.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Permite fechar
        dialog.setSize(600, 400); // Tamanho inicial
        dialog.setResizable(true); // Torna a janela redimensionável

        // Painel principal
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        // Campos e rótulos
        JTextField nameField = new JTextField();
        nameField.setFont(fonteGrande);

        JTextField descriptionField = new JTextField();
        descriptionField.setFont(fonteGrande);

        JTextField priceField = new JTextField();
        priceField.setFont(fonteGrande);

        JTextField quantityField = new JTextField();
        quantityField.setFont(fonteGrande);

        JLabel nameLabel = new JLabel("Nome do Produto:");
        nameLabel.setFont(fonteGrande);

        JLabel descriptionLabel = new JLabel("Descrição:");
        descriptionLabel.setFont(fonteGrande);

        JLabel priceLabel = new JLabel("Preço:");
        priceLabel.setFont(fonteGrande);

        JLabel quantityLabel = new JLabel("Quantidade:");
        quantityLabel.setFont(fonteGrande);

        // Adiciona os componentes ao painel principal
        mainPanel.add(nameLabel);
        mainPanel.add(nameField);
        mainPanel.add(descriptionLabel);
        mainPanel.add(descriptionField);
        mainPanel.add(priceLabel);
        mainPanel.add(priceField);
        mainPanel.add(quantityLabel);
        mainPanel.add(quantityField);

        // Adiciona barras de rolagem
        JScrollPane scrollPane = new JScrollPane(mainPanel);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        // Painel para botões
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton okButton = new JButton("OK");
        okButton.setFont(fonteGrande);

        JButton cancelButton = new JButton("Cancelar");
        cancelButton.setFont(fonteGrande);

        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);

        // Adiciona os componentes ao JFrame
        dialog.setLayout(new BorderLayout());
        dialog.add(scrollPane, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        // Ação do botão OK
        okButton.addActionListener(e -> {
            try {
                String name = nameField.getText();
                String description = descriptionField.getText();
                double price = Double.parseDouble(priceField.getText());
                int quantity = Integer.parseInt(quantityField.getText());
                inventory.addProduct(name, description, price, quantity);
                saveInventory();

                // Mensagem de sucesso ampliada
                JOptionPane.showMessageDialog(
                    dialog, 
                    new JLabel("<html><body style='font-size:18px;'>Produto adicionado com sucesso!</body></html>"),
                    "Sucesso",
                    JOptionPane.INFORMATION_MESSAGE
                );

                dialog.dispose(); // Fecha o diálogo

                // Perguntar se deseja cadastrar outro produto
                int continueOption = JOptionPane.showOptionDialog(
                    parentFrame,
                    new JLabel("<html><body style='font-size:18px;'>Deseja adicionar outro produto?</body></html>"),
                    "Continuar Cadastro",
                    JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    new String[]{"OK", "Cancelar"},
                    "OK"
                );

                if (continueOption == JOptionPane.OK_OPTION) {
                    showAddProductDialog(parentFrame); // Reabre a janela de cadastro
                }
            } catch (NumberFormatException ex) {
                // Mensagem de erro ampliada
                JOptionPane.showMessageDialog(
                    dialog,
                    new JLabel("<html><body style='font-size:18px;'>Por favor, insira valores válidos.</body></html>"),
                    "Erro",
                    JOptionPane.ERROR_MESSAGE
                );
            }
        });

        // Ação do botão Cancelar
        cancelButton.addActionListener(e -> dialog.dispose());

        // Torna a janela visível
        dialog.setVisible(true);
    }


    
    private static void showSellProductDialog(JFrame parentFrame) {
        Font fonteGrande = new Font("Arial", Font.PLAIN, 18); // Fonte ajustada para texto legível
        JPanel mainPanel = new JPanel(new BorderLayout());
        JPanel productPanel = new JPanel();
        productPanel.setLayout(new BoxLayout(productPanel, BoxLayout.Y_AXIS));

        Map<String, ProductSummary> summary = inventory.getProductSummary();
        Map<ProductSummary, Integer> selectedProducts = new HashMap<>();
        JLabel totalLabel = new JLabel("Total da compra: R$ 0.00");
        totalLabel.setFont(fonteGrande);

        for (ProductSummary productSummary : summary.values()) {
            JPanel individualProductPanel = new JPanel(new BorderLayout());
            individualProductPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

            // Painel para texto (produto, descrição, preço, quantidade disponível)
            JPanel textPanel = new JPanel();
            textPanel.setLayout(new GridLayout(0, 1));
            JLabel productLabel = new JLabel("Produto: " + productSummary.getName());
            productLabel.setFont(fonteGrande);
            JLabel descriptionLabel = new JLabel("Descrição: " + productSummary.getDescription());
            descriptionLabel.setFont(fonteGrande);
            JLabel quantityLabel = new JLabel("Qtde. disp.: " + productSummary.getQuantity());
            quantityLabel.setFont(fonteGrande);
            JLabel priceLabel = new JLabel("Preço unitário: R$ " + String.format("%.2f", productSummary.getPrice()));
            priceLabel.setFont(fonteGrande);
            JLabel selectedQuantityLabel = new JLabel("Selecionado: 0");
            selectedQuantityLabel.setFont(fonteGrande);
            selectedProducts.put(productSummary, 0);

            textPanel.add(productLabel);
            textPanel.add(descriptionLabel);
            textPanel.add(quantityLabel);
            textPanel.add(priceLabel);
            textPanel.add(selectedQuantityLabel);

            // Painel para botões (+ e -)
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            JButton addButton = new JButton("+");
            addButton.setFont(fonteGrande);
            JButton removeButton = new JButton("-");
            removeButton.setFont(fonteGrande);

            addButton.addActionListener(e -> {
                int selectedQuantity = selectedProducts.get(productSummary);
                if (selectedQuantity < productSummary.getQuantity()) {
                    selectedQuantity++;
                    selectedProducts.put(productSummary, selectedQuantity);
                    selectedQuantityLabel.setText("Selecionado: " + selectedQuantity);
                    quantityLabel.setText("Qtde. disp.: " + (productSummary.getQuantity() - selectedQuantity));
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
                    quantityLabel.setText("Qtde. disp.: " + (productSummary.getQuantity() - selectedQuantity));
                    updateTotalLabel(selectedProducts, totalLabel);
                }
            });

            buttonPanel.add(addButton);
            buttonPanel.add(removeButton);

            individualProductPanel.add(textPanel, BorderLayout.CENTER);
            individualProductPanel.add(buttonPanel, BorderLayout.EAST);
            productPanel.add(individualProductPanel);
        }

        JScrollPane scrollPane = new JScrollPane(productPanel);
        scrollPane.setPreferredSize(new Dimension(800, 600));

        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(totalLabel, BorderLayout.SOUTH);

        int result = JOptionPane.showConfirmDialog(parentFrame, mainPanel, "Vender Produtos", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            double totalPrice = selectedProducts.entrySet().stream()
                    .mapToDouble(entry -> entry.getKey().getPrice() * entry.getValue())
                    .sum();

            JTextField customerField = new JTextField();
            customerField.setFont(fonteGrande);
            Object[] message = {
                    new JLabel(String.format("<html><body style='font-size:18px;'>Total da compra: R$ %.2f</body></html>", totalPrice)),
                    new JLabel("<html><body style='font-size:18px;'>Nome do cliente (opcional):</body></html>"),
                    customerField
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
                    }
                }

                if (!productsToSell.isEmpty()) {
                    inventory.sellProducts(
                            productsToSell,
                            quantities,
                            customerName.isEmpty() ? null : customerName
                    );
                    saveInventory();

                    JOptionPane.showMessageDialog(
                        parentFrame, 
                        new JLabel("<html><body style='font-size:18px;'>Compra finalizada com sucesso!</body></html>"),
                        "Sucesso", 
                        JOptionPane.INFORMATION_MESSAGE
                    );
                } else {
                    JOptionPane.showMessageDialog(
                        parentFrame, 
                        new JLabel("<html><body style='font-size:18px;'>Nenhum produto selecionado para venda.</body></html>"),
                        "Aviso", 
                        JOptionPane.WARNING_MESSAGE
                    );
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
 

    
    private static void showInventoryDialog(JFrame parentFrame) {
        Font fonteGrande = new Font("Arial", Font.PLAIN, 20); // Fonte maior

        // Painel principal para exibir o estoque
        JPanel inventoryPanel = new JPanel();
        inventoryPanel.setLayout(new BoxLayout(inventoryPanel, BoxLayout.Y_AXIS));

        // Adiciona os produtos ao painel
        inventory.getProductSummary().values().forEach(summary -> {
            // Exibe as informações de cada produto em linhas separadas
            inventoryPanel.add(createProductInfoLabel("Produto: " + summary.getName(), fonteGrande));
            inventoryPanel.add(createProductInfoLabel("Descrição: " + summary.getDescription(), fonteGrande));
            inventoryPanel.add(createProductInfoLabel("Qtde. disp.: " + summary.getQuantity(), fonteGrande));
            inventoryPanel.add(createProductInfoLabel("Preço unitário: R$ " + String.format("%.2f", summary.getPrice()), fonteGrande));

            // Adiciona um espaço entre os produtos
            inventoryPanel.add(Box.createVerticalStrut(20));
        });

        // Adiciona o painel em um JScrollPane para barra de rolagem
        JScrollPane scrollPane = new JScrollPane(inventoryPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        // Configuração do diálogo
        JDialog dialog = new JDialog(parentFrame, "Estoque", true);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setLayout(new BorderLayout());
        dialog.add(scrollPane, BorderLayout.CENTER);

        // Botão "OK" para fechar a janela
        JButton okButton = new JButton("OK");
        okButton.setFont(fonteGrande); // Ajusta a fonte do botão
        okButton.addActionListener(e -> dialog.dispose());
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(okButton);

        dialog.add(buttonPanel, BorderLayout.SOUTH);

        // Define tamanho inicial e torna a janela redimensionável
        dialog.setSize(800, 600); // Tamanho inicial maior
        dialog.setResizable(true); // Permite redimensionamento
        dialog.setVisible(true);
    }

    // Método auxiliar para criar rótulos com fonte ajustada
    private static JLabel createProductInfoLabel(String text, Font font) {
        JLabel label = new JLabel(text);
        label.setFont(font);
        return label;
    }

    
    
    private static void showSalesDialog(JFrame parentFrame) {
        Font fonteGrande = new Font("Arial", Font.PLAIN, 20); // Fonte para o texto
        JPanel salesPanel = new JPanel();
        salesPanel.setLayout(new BoxLayout(salesPanel, BoxLayout.Y_AXIS));

        Map<String, List<Sale>> salesTransactions = inventory.getSalesTransactions();

        // Ordenar as transações por data (mais recente para a mais antiga)
        List<Map.Entry<String, List<Sale>>> sortedSalesTransactions = salesTransactions.entrySet()
            .stream()
            .sorted((entry1, entry2) -> {
                LocalDateTime date1 = entry1.getValue().get(0).getDateTime();
                LocalDateTime date2 = entry2.getValue().get(0).getDateTime();
                return date2.compareTo(date1); // Ordena do mais recente para o mais antigo
            })
            .toList();

        for (Map.Entry<String, List<Sale>> entry : sortedSalesTransactions) {
            List<Sale> sales = entry.getValue();
            Sale firstSale = sales.get(0);

            String customerName = firstSale.getCustomerName();
            String formattedDate = firstSale.getDateTime().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));

            // Cabeçalho da transação
            salesPanel.add(createStyledLabel("--------------------------------------------------------------------", fonteGrande));
            salesPanel.add(createStyledLabel("Data: " + formattedDate, fonteGrande));
            salesPanel.add(createStyledLabel("Cliente: " + (customerName != null ? customerName : "Não informado"), fonteGrande));
            salesPanel.add(createStyledLabel("Produto(s):", fonteGrande));

            // Detalhes dos produtos
            Map<String, Integer> productSummary = sales.stream()
                .collect(Collectors.groupingBy(sale -> sale.getProduct().getName(), Collectors.summingInt(sale -> 1)));

            double total = 0.0;

            for (Map.Entry<String, Integer> productEntry : productSummary.entrySet()) {
                String productName = productEntry.getKey();
                int quantity = productEntry.getValue();
                double unitPrice = sales.stream()
                    .filter(sale -> sale.getProduct().getName().equals(productName))
                    .findFirst()
                    .get()
                    .getProduct()
                    .getPrice();

                total += quantity * unitPrice;

                salesPanel.add(createStyledLabel(
                    quantity + " x " + productName + "  |  Valor Unitário: R$ " + String.format("%.2f", unitPrice),
                    fonteGrande
                ));
            }

            // Total da compra
            salesPanel.add(createStyledLabel("Total: R$ " + String.format("%.2f", total), fonteGrande));
            salesPanel.add(createStyledLabel("--------------------------------------------------------------------", fonteGrande));
        }

        JScrollPane scrollPane = new JScrollPane(salesPanel);
        scrollPane.setPreferredSize(new Dimension(800, 600));

        JDialog dialog = new JDialog(parentFrame, "Histórico de Vendas", true);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setSize(900, 700); // Define tamanho inicial maior
        dialog.setResizable(true); // Permite redimensionar
        dialog.setLayout(new BorderLayout());
        dialog.add(scrollPane, BorderLayout.CENTER);

        // Botão "OK" para fechar
        JButton okButton = new JButton("OK");
        okButton.setFont(fonteGrande); // Fonte maior no botão
        okButton.addActionListener(e -> dialog.dispose());
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(okButton);

        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    
    
    private static JLabel createStyledLabel(String text, Font font) {
        JLabel label = new JLabel(text);
        label.setFont(font);
        return label;
    }
}

