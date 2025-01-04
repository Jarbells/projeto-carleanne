package view;

import java.awt.BorderLayout;
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
import java.time.LocalDate;
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
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
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

        // Define a cor de fundo para a janela
        java.awt.Color backgroundColor = new java.awt.Color(138, 106, 45); // RGB: (138, 106, 45)
        frame.getContentPane().setBackground(backgroundColor);

        // Configuração de fonte global
        Font fonteGrande = new Font("Arial", Font.PLAIN, 24);
        Font fonteTitulo = new Font("Arial", Font.BOLD, 28);

        // Painel principal
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(backgroundColor);

        // Adiciona o título com o tema do programa
        JLabel titleLabel = new JLabel("AROMAS MULTIMARCAS", JLabel.CENTER);
        titleLabel.setFont(fonteTitulo);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0)); // Adiciona espaçamento
        titleLabel.setForeground(java.awt.Color.WHITE);

        // Painel para os botões
        JPanel buttonPanel = new JPanel(new GridLayout(4, 1, 10, 10)); // Espaçamento entre botões
        buttonPanel.setBackground(backgroundColor);

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
        
        // Adiciona o botão para consultar vendas por período
        addConsultarVendasPorPeriodoButton(frame);

        frame.setVisible(true);
    }


    

    private static void showAddProductDialog(JFrame parentFrame) {
        Font fonteGrande = new Font("Arial", Font.PLAIN, 20);

        JFrame dialog = new JFrame("Adicionar Produto");
        dialog.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        dialog.setSize(600, 400);
        dialog.setResizable(true);

        java.awt.Color backgroundColor = new java.awt.Color(138, 106, 45);
        dialog.getContentPane().setBackground(backgroundColor);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(backgroundColor);

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
        nameLabel.setForeground(java.awt.Color.WHITE);

        JLabel descriptionLabel = new JLabel("Descrição:");
        descriptionLabel.setFont(fonteGrande);
        descriptionLabel.setForeground(java.awt.Color.WHITE);

        JLabel priceLabel = new JLabel("Preço:");
        priceLabel.setFont(fonteGrande);
        priceLabel.setForeground(java.awt.Color.WHITE);

        JLabel quantityLabel = new JLabel("Quantidade:");
        quantityLabel.setFont(fonteGrande);
        quantityLabel.setForeground(java.awt.Color.WHITE);

        mainPanel.add(nameLabel);
        mainPanel.add(nameField);
        mainPanel.add(descriptionLabel);
        mainPanel.add(descriptionField);
        mainPanel.add(priceLabel);
        mainPanel.add(priceField);
        mainPanel.add(quantityLabel);
        mainPanel.add(quantityField);

        JScrollPane scrollPane = new JScrollPane(mainPanel);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.getViewport().setBackground(backgroundColor);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(backgroundColor);

        JButton okButton = new JButton("OK");
        okButton.setFont(fonteGrande);

        JButton cancelButton = new JButton("Cancelar");
        cancelButton.setFont(fonteGrande);

        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);

        dialog.setLayout(new BorderLayout());
        dialog.add(scrollPane, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        okButton.addActionListener(e -> {
            try {
                String name = nameField.getText();
                String description = descriptionField.getText();
                
                // Ajusta o preço para aceitar `,` ou `.` como separador decimal
                String priceText = priceField.getText().replace(",", ".");
                double price = Double.parseDouble(priceText);

                int quantity = Integer.parseInt(quantityField.getText());
                inventory.addProduct(name, description, price, quantity);
                saveInventory();

                JOptionPane.showMessageDialog(
                    dialog, 
                    new JLabel("<html><body style='font-size:18px;'>Produto adicionado com sucesso!</body></html>"),
                    "Sucesso",
                    JOptionPane.INFORMATION_MESSAGE
                );

                dialog.dispose();

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
                    showAddProductDialog(parentFrame);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(
                    dialog,
                    new JLabel("<html><body style='font-size:18px;'>Por favor, insira valores válidos.</body></html>"),
                    "Erro",
                    JOptionPane.ERROR_MESSAGE
                );
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        dialog.setVisible(true);
    }



    

    



    
    private static void showInventoryDialog(JFrame parentFrame) {
        Font fonteGrande = new Font("Arial", Font.PLAIN, 20); // Fonte maior

        // Define a cor de fundo
        java.awt.Color backgroundColor = new java.awt.Color(138, 106, 45); // RGB: (138, 106, 45)

        // Painel principal para exibir o estoque
        JPanel inventoryPanel = new JPanel();
        inventoryPanel.setLayout(new BoxLayout(inventoryPanel, BoxLayout.Y_AXIS));
        inventoryPanel.setBackground(backgroundColor);

        // Ordena os produtos em ordem alfabética (ignorando acentuação)
        inventory.getProductSummary().values().stream()
            .sorted((p1, p2) -> java.text.Collator.getInstance(java.util.Locale.getDefault()).compare(p1.getName(), p2.getName()))
            .forEach(summary -> {
                // Exibe as informações de cada produto em linhas separadas
                inventoryPanel.add(createProductInfoLabel("Produto: " + summary.getName(), fonteGrande, backgroundColor));
                inventoryPanel.add(createProductInfoLabel("Descrição: " + summary.getDescription(), fonteGrande, backgroundColor));
                inventoryPanel.add(createProductInfoLabel("Qtde. disp.: " + summary.getQuantity(), fonteGrande, backgroundColor));
                inventoryPanel.add(createProductInfoLabel("Preço unitário: R$ " + String.format("%.2f", summary.getPrice()), fonteGrande, backgroundColor));

                // Adiciona um espaço entre os produtos
                inventoryPanel.add(Box.createVerticalStrut(20));
            });

        // Adiciona o painel em um JScrollPane para barra de rolagem
        JScrollPane scrollPane = new JScrollPane(inventoryPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.getViewport().setBackground(backgroundColor);

        // Configuração do diálogo
        JDialog dialog = new JDialog(parentFrame, "Estoque", true);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setLayout(new BorderLayout());
        dialog.add(scrollPane, BorderLayout.CENTER);
        dialog.getContentPane().setBackground(backgroundColor);

        // Botão "OK" para fechar a janela
        JButton okButton = new JButton("OK");
        okButton.setFont(fonteGrande); // Ajusta a fonte do botão
        okButton.addActionListener(e -> dialog.dispose());
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(backgroundColor);
        buttonPanel.add(okButton);

        dialog.add(buttonPanel, BorderLayout.SOUTH);

        // Define tamanho inicial e torna a janela redimensionável
        dialog.setSize(800, 600); // Tamanho inicial maior
        dialog.setResizable(true); // Permite redimensionamento
        dialog.setVisible(true);
    }

    private static void showSellProductDialog(JFrame parentFrame) {
        Font fonteGrande = new Font("Arial", Font.PLAIN, 18);
        JFrame dialog = new JFrame("Vender Produtos");
        dialog.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        dialog.setSize(600, 700);
        dialog.setResizable(true);

        java.awt.Color backgroundColor = new java.awt.Color(138, 106, 45);
        dialog.getContentPane().setBackground(backgroundColor);

        JLabel totalLabel = new JLabel("Total da compra: R$ 0.00", JLabel.CENTER);
        totalLabel.setFont(fonteGrande);
        totalLabel.setOpaque(true);
        totalLabel.setBackground(backgroundColor);
        totalLabel.setForeground(java.awt.Color.WHITE);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(backgroundColor);

        JPanel productPanel = new JPanel();
        productPanel.setLayout(new BoxLayout(productPanel, BoxLayout.Y_AXIS));
        productPanel.setBackground(backgroundColor);

        // Declarar `selectedProducts` fora do loop
        Map<ProductSummary, Integer> selectedProducts = new HashMap<>();

        inventory.getProductSummary().values().stream()
            .sorted((p1, p2) -> java.text.Collator.getInstance(java.util.Locale.getDefault()).compare(p1.getName(), p2.getName()))
            .forEach(productSummary -> {
                JPanel individualProductPanel = new JPanel(new BorderLayout());
                individualProductPanel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createEmptyBorder(10, 10, 10, 10),
                    BorderFactory.createLineBorder(java.awt.Color.LIGHT_GRAY)
                ));
                individualProductPanel.setBackground(backgroundColor);

                JPanel textPanel = new JPanel();
                textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
                textPanel.setBackground(backgroundColor);

                JLabel productLabel = new JLabel("Produto: " + productSummary.getName());
                productLabel.setFont(fonteGrande);
                productLabel.setForeground(java.awt.Color.WHITE);
                JLabel descriptionLabel = new JLabel("Descrição: " + productSummary.getDescription());
                descriptionLabel.setFont(fonteGrande);
                descriptionLabel.setForeground(java.awt.Color.WHITE);
                JLabel quantityLabel = new JLabel("Qtde. disp.: " + productSummary.getQuantity());
                quantityLabel.setFont(fonteGrande);
                quantityLabel.setForeground(java.awt.Color.WHITE);
                JLabel priceLabel = new JLabel("Preço unitário: R$ " + String.format("%.2f", productSummary.getPrice()));
                priceLabel.setFont(fonteGrande);
                priceLabel.setForeground(java.awt.Color.WHITE);
                JLabel selectedQuantityLabel = new JLabel("Selecionado: 0");
                selectedQuantityLabel.setFont(fonteGrande);
                selectedQuantityLabel.setForeground(java.awt.Color.WHITE);

                textPanel.add(productLabel);
                textPanel.add(descriptionLabel);
                textPanel.add(quantityLabel);
                textPanel.add(priceLabel);
                textPanel.add(selectedQuantityLabel);

                JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
                buttonPanel.setBackground(backgroundColor);
                JButton addButton = new JButton("+");
                addButton.setFont(fonteGrande);
                JButton removeButton = new JButton("-");
                removeButton.setFont(fonteGrande);

                buttonPanel.add(addButton);
                buttonPanel.add(removeButton);

                selectedProducts.put(productSummary, 0);

                addButton.addActionListener(e -> {
                    int selectedQuantity = selectedProducts.get(productSummary);
                    if (selectedQuantity < productSummary.getQuantity()) {
                        selectedQuantity++;
                        selectedProducts.put(productSummary, selectedQuantity);

                        selectedQuantityLabel.setText("Selecionado: " + selectedQuantity);
                        quantityLabel.setText("Qtde. disp.: " + (productSummary.getQuantity() - selectedQuantity));
                        updateTotalLabel(selectedProducts, totalLabel);
                    } else {
                        JOptionPane.showMessageDialog(dialog, 
                            new JLabel("<html><body style='font-size:20px;'>Não há mais produtos disponíveis!</body></html>"),
                            "Erro", JOptionPane.ERROR_MESSAGE);
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

                individualProductPanel.add(textPanel, BorderLayout.CENTER);
                individualProductPanel.add(buttonPanel, BorderLayout.EAST);

                productPanel.add(individualProductPanel);
                productPanel.add(Box.createVerticalStrut(10));
            });

        JScrollPane scrollPane = new JScrollPane(productPanel);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.getViewport().setBackground(backgroundColor);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(backgroundColor);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(backgroundColor);
        JButton okButton = new JButton("OK");
        okButton.setFont(fonteGrande);
        JButton cancelButton = new JButton("Cancelar");
        cancelButton.setFont(fonteGrande);

        okButton.addActionListener(e -> {
            double totalPrice = selectedProducts.entrySet().stream()
                .mapToDouble(entry -> entry.getKey().getPrice() * entry.getValue())
                .sum();

            if (totalPrice > 0) {
                JTextField customerField = new JTextField();
                customerField.setFont(fonteGrande);
                Object[] message = {
                    new JLabel(String.format("<html><body style='font-size:18px;'>Total da compra: R$ %.2f</body></html>", totalPrice)),
                    new JLabel("<html><body style='font-size:18px;'>Nome do cliente (opcional):</body></html>"),
                    customerField
                };

                int confirm = JOptionPane.showConfirmDialog(dialog, message, "Finalizar Compra", JOptionPane.OK_CANCEL_OPTION);

                if (confirm == JOptionPane.OK_OPTION) {
                    String customerName = customerField.getText();
                    List<ProductSummary> productsToSell = new ArrayList<>();
                    List<Integer> quantities = new ArrayList<>();

                    for (Map.Entry<ProductSummary, Integer> entry : selectedProducts.entrySet()) {
                        if (entry.getValue() > 0) {
                            productsToSell.add(entry.getKey());
                            quantities.add(entry.getValue());
                        }
                    }

                    inventory.sellProducts(productsToSell, quantities, customerName.isEmpty() ? null : customerName);
                    saveInventory();

                    JOptionPane.showMessageDialog(
                        dialog, 
                        new JLabel("<html><body style='font-size:20px;'>Compra finalizada com sucesso!</body></html>"),
                        "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                    dialog.dispose();
                }
            } else {
                JOptionPane.showMessageDialog(dialog, 
                    new JLabel("<html><body style='font-size:20px;'>Nenhum produto selecionado para venda.</body></html>"),
                    "Aviso", JOptionPane.WARNING_MESSAGE);
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);

        bottomPanel.add(totalLabel, BorderLayout.NORTH);
        bottomPanel.add(buttonPanel, BorderLayout.SOUTH);

        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        dialog.add(mainPanel);
        dialog.setVisible(true);
    }

    
    

    private static void updateTotalLabel(Map<ProductSummary, Integer> selectedProducts, JLabel totalLabel) {
        double total = selectedProducts.entrySet().stream()
                .mapToDouble(entry -> entry.getKey().getPrice() * entry.getValue())
                .sum();
        totalLabel.setText("Total da compra: R$ " + String.format("%.2f", total));
    }

    private static JLabel createProductInfoLabel(String text, Font font, java.awt.Color backgroundColor) {
        JLabel label = new JLabel(text);
        label.setFont(font);
        label.setForeground(java.awt.Color.WHITE);
        label.setOpaque(true);
        label.setBackground(backgroundColor);
        return label;
    }





    
    
    private static void showSalesDialog(JFrame parentFrame) {
        Font fonteGrande = new Font("Arial", Font.PLAIN, 20);

        // Define a cor de fundo
        java.awt.Color backgroundColor = new java.awt.Color(138, 106, 45);

        // Colunas e dados da tabela
        String[] colunas = {"Data", "Cliente", "Produto", "Quantidade", "Valor Unitário", "Total"};
        List<Object[]> linhas = new ArrayList<>();

        Map<String, List<Sale>> salesTransactions = inventory.getSalesTransactions();

        // Ordenar as transações por data (mais recente para a mais antiga)
        List<Map.Entry<String, List<Sale>>> sortedSalesTransactions = salesTransactions.entrySet()
            .stream()
            .sorted((entry1, entry2) -> {
                LocalDateTime date1 = entry1.getValue().get(0).getDateTime();
                LocalDateTime date2 = entry2.getValue().get(0).getDateTime();
                return date2.compareTo(date1);
            })
            .toList();

        for (Map.Entry<String, List<Sale>> entry : sortedSalesTransactions) {
            List<Sale> sales = entry.getValue();
            Sale firstSale = sales.get(0);

            String customerName = firstSale.getCustomerName();
            if (customerName == null || customerName.isEmpty()) {
                customerName = "Cliente não registrado";
            }

            String formattedDate = firstSale.getDateTime().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));

            Map<String, Integer> productSummary = sales.stream()
                .collect(Collectors.groupingBy(sale -> sale.getProduct().getName(), Collectors.summingInt(sale -> 1)));

            boolean firstRow = true;
            for (Map.Entry<String, Integer> productEntry : productSummary.entrySet()) {
                String productName = productEntry.getKey();
                int quantity = productEntry.getValue();
                double unitPrice = sales.stream()
                    .filter(sale -> sale.getProduct().getName().equals(productName))
                    .findFirst()
                    .get()
                    .getProduct()
                    .getPrice();

                double total = quantity * unitPrice;

                if (firstRow) {
                    linhas.add(new Object[]{formattedDate, customerName, productName, quantity, String.format("%.2f", unitPrice), String.format("%.2f", total)});
                    firstRow = false;
                } else {
                    linhas.add(new Object[]{"", "", productName, quantity, String.format("%.2f", unitPrice), String.format("%.2f", total)});
                }
            }
        }

        // Conversão de dados para a JTable
        Object[][] dados = linhas.toArray(new Object[0][]);

        JTable tabela = new JTable(dados, colunas);
        tabela.setFont(fonteGrande);
        tabela.setRowHeight(30);
        tabela.getTableHeader().setFont(new Font("Arial", Font.BOLD, 22));
        tabela.getTableHeader().setBackground(backgroundColor);
        tabela.getTableHeader().setForeground(java.awt.Color.WHITE);
        tabela.setBackground(backgroundColor);
        tabela.setForeground(java.awt.Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(tabela);

        JDialog dialog = new JDialog(parentFrame, "Histórico de Vendas", true);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setSize(900, 600);
        dialog.setResizable(true);
        dialog.setLayout(new BorderLayout());
        dialog.getContentPane().setBackground(backgroundColor);
        dialog.add(scrollPane, BorderLayout.CENTER);

        // Botão "OK" para fechar
        JButton okButton = new JButton("OK");
        okButton.setFont(fonteGrande);
        okButton.addActionListener(e -> dialog.dispose());
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(backgroundColor);
        buttonPanel.add(okButton);

        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }
    
    private static void addConsultarVendasPorPeriodoButton(JFrame frame) {
        Font fonteGrande = new Font("Arial", Font.PLAIN, 20);

        JButton consultarVendasPorPeriodoButton = new JButton("Consultar Vendas Por Período");
        consultarVendasPorPeriodoButton.setFont(fonteGrande);
        consultarVendasPorPeriodoButton.addActionListener(e -> showSalesByPeriodDialog(frame));

        JPanel mainPanel = (JPanel) frame.getContentPane().getComponent(0);
        JPanel buttonPanel = (JPanel) mainPanel.getComponent(1);
        buttonPanel.add(consultarVendasPorPeriodoButton);
        buttonPanel.revalidate();
        buttonPanel.repaint();
    }

    private static void showSalesByPeriodDialog(JFrame parentFrame) {
        Font fonteGrande = new Font("Arial", Font.PLAIN, 20);
        java.awt.Color backgroundColor = new java.awt.Color(138, 106, 45);

        JDialog dialog = new JDialog(parentFrame, "Consultar Vendas Por Período", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(500, 300);
        dialog.setResizable(false);
        dialog.getContentPane().setBackground(backgroundColor);

        JPanel inputPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        inputPanel.setBackground(backgroundColor);

        JLabel labelStartDate = new JLabel("Data Inicial (dd/mm/yyyy):");
        labelStartDate.setFont(fonteGrande);
        labelStartDate.setForeground(java.awt.Color.WHITE);
        JTextField startDateField = new JTextField();
        startDateField.setFont(fonteGrande);

        JLabel labelEndDate = new JLabel("Data Final (dd/mm/yyyy):");
        labelEndDate.setFont(fonteGrande);
        labelEndDate.setForeground(java.awt.Color.WHITE);
        JTextField endDateField = new JTextField();
        endDateField.setFont(fonteGrande);

        inputPanel.add(labelStartDate);
        inputPanel.add(startDateField);
        inputPanel.add(labelEndDate);
        inputPanel.add(endDateField);

        dialog.add(inputPanel, BorderLayout.CENTER);

        JButton consultarButton = new JButton("Consultar");
        consultarButton.setFont(fonteGrande);
        consultarButton.addActionListener(e -> {
            try {
                LocalDate startDate = LocalDate.parse(startDateField.getText(), DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                LocalDate endDate = LocalDate.parse(endDateField.getText(), DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                LocalDate today = LocalDate.now();

                if (startDate.isAfter(endDate)) {
                    throw new IllegalArgumentException("A data inicial não pode ser maior que a data final.");
                }

                if (endDate.isAfter(today)) {
                    throw new IllegalArgumentException("A data final não pode ser maior que a data de hoje.");
                }

                showFilteredSalesDialog(parentFrame, startDate, endDate);
                dialog.dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Erro: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(backgroundColor);
        buttonPanel.add(consultarButton);

        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private static void showFilteredSalesDialog(JFrame parentFrame, LocalDate startDate, LocalDate endDate) {
        Font fonteGrande = new Font("Arial", Font.PLAIN, 20);
        java.awt.Color backgroundColor = new java.awt.Color(138, 106, 45);

        String[] colunas = {"Data", "Cliente", "Produto", "Quantidade", "Valor Unitário", "Total"};
        List<Object[]> linhas = new ArrayList<>();

        Map<String, List<Sale>> salesTransactions = inventory.getSalesTransactions();
        double totalSales = 0.0;

        for (List<Sale> sales : salesTransactions.values()) {
            String lastCustomerName = null;
            for (Sale sale : sales) {
                LocalDate saleDate = sale.getDateTime().toLocalDate();
                if (!saleDate.isBefore(startDate) && !saleDate.isAfter(endDate)) {
                    String customerName = sale.getCustomerName();
                    if (customerName == null || customerName.isEmpty()) {
                        customerName = "Cliente não registrado";
                    }
                    String formattedDate = sale.getDateTime().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));

                    double unitPrice = sale.getProduct().getPrice();
                    double total = unitPrice;
                    totalSales += total;

                    linhas.add(new Object[]{
                        formattedDate,
                        customerName.equals(lastCustomerName) ? "" : customerName,
                        sale.getProduct().getName(),
                        1,
                        String.format("%.2f", unitPrice),
                        String.format("%.2f", total)
                    });

                    lastCustomerName = customerName;
                }
            }
        }

        Object[][] dados = linhas.toArray(new Object[0][]);

        JTable tabela = new JTable(dados, colunas);
        tabela.setFont(fonteGrande);
        tabela.setRowHeight(30);
        tabela.getTableHeader().setFont(new Font("Arial", Font.BOLD, 22));
        tabela.getTableHeader().setBackground(backgroundColor);
        tabela.getTableHeader().setForeground(java.awt.Color.WHITE);
        tabela.setBackground(backgroundColor);
        tabela.setForeground(java.awt.Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(tabela);

        JDialog dialog = new JDialog(parentFrame, "Vendas Por Período", true);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setSize(900, 600);
        dialog.setResizable(true);
        dialog.setLayout(new BorderLayout());
        dialog.getContentPane().setBackground(backgroundColor);
        dialog.add(scrollPane, BorderLayout.CENTER);

        JLabel totalLabel = new JLabel("Total de vendas: R$ " + String.format("%.2f", totalSales));
        totalLabel.setFont(fonteGrande);
        totalLabel.setHorizontalAlignment(SwingConstants.CENTER);
        totalLabel.setForeground(java.awt.Color.WHITE);

        dialog.add(totalLabel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }





}

