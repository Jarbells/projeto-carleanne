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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
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
        JPanel buttonPanel = new JPanel(new GridLayout(6, 1, 10, 10)); // Espaçamento entre botões
        buttonPanel.setBackground(backgroundColor);

        JButton addProductButton = new JButton("Adicionar Produtos");
        addProductButton.setFont(fonteGrande);

        JButton viewInventoryButton = new JButton("Ver Estoque");
        viewInventoryButton.setFont(fonteGrande);

        JButton sellProductButton = new JButton("Vender");
        sellProductButton.setFont(fonteGrande);

        JButton viewSalesButton = new JButton("Consultar Vendas");
        viewSalesButton.setFont(fonteGrande);

        JButton editProductButton = new JButton("Alterar Produtos");
        editProductButton.setFont(fonteGrande);

        JButton consultarVendasPorPeriodoButton = new JButton("Consultar Vendas Por Período");
        consultarVendasPorPeriodoButton.setFont(fonteGrande);

        buttonPanel.add(addProductButton);
        buttonPanel.add(viewInventoryButton);
        buttonPanel.add(sellProductButton);
        buttonPanel.add(viewSalesButton);
        buttonPanel.add(editProductButton); // Adiciona o botão "Alterar Produtos"
        buttonPanel.add(consultarVendasPorPeriodoButton); // Adiciona o botão "Consultar Vendas Por Período"

        // Adiciona o título e os botões ao painel principal
        panel.add(titleLabel, BorderLayout.NORTH); // Adiciona o título no topo
        panel.add(buttonPanel, BorderLayout.CENTER); // Adiciona os botões no centro

        frame.add(panel);

        // Ações dos botões
        addProductButton.addActionListener(e -> showAddProductDialog(frame));
        viewInventoryButton.addActionListener(e -> showInventoryDialog(frame));
        sellProductButton.addActionListener(e -> showSellProductDialog(frame));
        viewSalesButton.addActionListener(e -> showSalesDialog(frame));
        editProductButton.addActionListener(e -> showEditProductDialog(frame)); // Ação para o botão "Alterar Produtos"
        consultarVendasPorPeriodoButton.addActionListener(e -> showSalesByPeriodInputDialog(frame)); // Ação para o botão "Consultar Vendas Por Período"

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
                String name = nameField.getText().trim();
                String description = descriptionField.getText().trim();
                String priceText = priceField.getText().trim().replace(",", ".");
                String quantityText = quantityField.getText().trim();

                if (name.isEmpty() || description.isEmpty() || priceText.isEmpty() || quantityText.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Todos os campos devem ser preenchidos.", "Erro", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                double price = Double.parseDouble(priceText);
                int quantity = Integer.parseInt(quantityText);

                inventory.addProduct(name, description, price, quantity);
                saveInventory();

                JOptionPane.showMessageDialog(dialog, "Produto adicionado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Por favor, insira valores válidos para preço e quantidade.", "Erro", JOptionPane.ERROR_MESSAGE);
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(dialog, ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
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

                    // Adiciona os produtos com quantidade selecionada ao inventário
                    selectedProducts.forEach((productSummary, quantity) -> {
                        if (quantity > 0) { // Apenas produtos com quantidade selecionada
                            productsToSell.add(productSummary);
                            quantities.add(quantity);
                        }
                    });

                    // Realiza a venda apenas se houver produtos válidos
                    if (!productsToSell.isEmpty()) {
                        inventory.sellProducts(productsToSell, quantities, customerName.isEmpty() ? null : customerName);
                        saveInventory();

                        JOptionPane.showMessageDialog(
                            dialog, 
                            new JLabel("<html><body style='font-size:20px;'>Compra finalizada com sucesso!</body></html>"),
                            "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                        dialog.dispose();
                    } else {
                        JOptionPane.showMessageDialog(dialog, 
                            new JLabel("<html><body style='font-size:20px;'>Nenhum produto selecionado para venda.</body></html>"),
                            "Aviso", JOptionPane.WARNING_MESSAGE);
                    }
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
        Font fontGrande = new Font("Arial", Font.PLAIN, 18);
        String[] colunas = {"Data", "Cliente", "Produto", "Quantidade", "Valor Unitário", "Total"};
        List<Object[]> linhas = new ArrayList<>();

        // Obtenha todas as vendas e ordene pela data (mais recente para mais antiga)
        List<Sale> allSales = inventory.getSalesTransactions().values().stream()
            .flatMap(List::stream)
            .sorted((s1, s2) -> s2.getDateTime().compareTo(s1.getDateTime())) // Ordenação decrescente por data
            .toList();

        // Mapeia cliente e data para produtos e suas quantidades
        Map<String, Map<String, Integer>> salesByClientAndDate = new LinkedHashMap<>();
        Map<String, String> clientAndDateMap = new HashMap<>();
        Map<String, Double> productPrices = new HashMap<>();

        for (Sale sale : allSales) {
            String cliente = sale.getCustomerName() != null ? sale.getCustomerName() : "Cliente não registrado";
            String data = sale.getDateTime().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
            String produto = sale.getProduct().getName();
            String key = cliente + "|" + data;

            salesByClientAndDate.putIfAbsent(key, new LinkedHashMap<>());
            salesByClientAndDate.get(key).put(produto, salesByClientAndDate.get(key).getOrDefault(produto, 0) + 1);

            clientAndDateMap.put(key, cliente);
            productPrices.put(produto, sale.getProduct().getPrice());
        }

        // Construir as linhas da tabela
        salesByClientAndDate.forEach((key, products) -> {
            String[] keyParts = key.split("\\|");
            String cliente = keyParts[0];
            String data = keyParts[1];

            boolean primeiraLinha = true;
            for (Map.Entry<String, Integer> entry : products.entrySet()) {
                String produto = entry.getKey();
                int quantidade = entry.getValue();
                double precoUnitario = productPrices.get(produto);
                double total = quantidade * precoUnitario;

                if (primeiraLinha) {
                    linhas.add(new Object[]{data, cliente, produto, quantidade, String.format("%.2f", precoUnitario).replace('.', ','), String.format("%.2f", total).replace('.', ',')});
                    primeiraLinha = false;
                } else {
                    linhas.add(new Object[]{"", "", produto, quantidade, String.format("%.2f", precoUnitario).replace('.', ','), String.format("%.2f", total).replace('.', ',')});
                }
            }
        });

        Object[][] dados = linhas.toArray(new Object[0][]);
        JTable tabela = new JTable(dados, colunas);
        tabela.setFont(fontGrande);
        tabela.setRowHeight(25);
        tabela.getTableHeader().setFont(new Font("Arial", Font.BOLD, 20));

        JScrollPane scrollPane = new JScrollPane(tabela);
        JFrame dialog = new JFrame("Histórico de Vendas");
        dialog.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        dialog.setSize(900, 600);
        dialog.setResizable(true);
        dialog.add(scrollPane);
        dialog.setVisible(true);
    }
    
    


    private static void showSalesByPeriodInputDialog(JFrame parentFrame) {
        Font fonteGrande = new Font("Arial", Font.PLAIN, 18);

        JDialog dialog = new JDialog(parentFrame, "Selecionar Período", true);
        dialog.setSize(400, 300);
        dialog.setLayout(new GridLayout(3, 2, 10, 10));

        JLabel startDateLabel = new JLabel("Data Inicial (dd/MM/yyyy):");
        startDateLabel.setFont(fonteGrande);
        JTextField startDateField = new JTextField();
        startDateField.setFont(fonteGrande);

        JLabel endDateLabel = new JLabel("Data Final (dd/MM/yyyy):");
        endDateLabel.setFont(fonteGrande);
        JTextField endDateField = new JTextField();
        endDateField.setFont(fonteGrande);

        JButton okButton = new JButton("OK");
        okButton.setFont(fonteGrande);

        JButton cancelButton = new JButton("Cancelar");
        cancelButton.setFont(fonteGrande);

        dialog.add(startDateLabel);
        dialog.add(startDateField);
        dialog.add(endDateLabel);
        dialog.add(endDateField);
        dialog.add(okButton);
        dialog.add(cancelButton);

        okButton.addActionListener(e -> {
            try {
                String startDateText = startDateField.getText().trim();
                String endDateText = endDateField.getText().trim();

                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                LocalDateTime startDate = LocalDate.parse(startDateText, formatter).atStartOfDay();
                LocalDateTime endDate = LocalDate.parse(endDateText, formatter).atTime(23, 59, 59);

                dialog.dispose();

                // Chama o método para exibir as vendas por período
                showSalesByPeriodDialog(parentFrame, startDate, endDate);

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Datas inválidas! Por favor, insira no formato dd/MM/yyyy.", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        dialog.setVisible(true);
    }



    private static void showSalesByPeriodDialog(JFrame parentFrame, LocalDateTime startDate, LocalDateTime endDate) {
        Font fontGrande = new Font("Arial", Font.PLAIN, 18);
        String[] colunas = {"Data", "Cliente", "Produto", "Quantidade", "Valor Unitário", "Total"};
        
        // Filtra as vendas por período
        List<Sale> filteredSales = inventory.getSalesTransactions().values().stream()
            .flatMap(List::stream)
            .filter(sale -> !sale.getDateTime().isBefore(startDate) && !sale.getDateTime().isAfter(endDate)) // Filtra pelo período
            .collect(Collectors.toList());

        // Prepara os dados para a tabela
        Object[][] dados = filteredSales.stream()
            .map(sale -> new Object[]{
                sale.getDateTime().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")),
                sale.getCustomerName(),
                sale.getProduct().getName(),
                sale.getQuantity(),
                String.format("%.2f", sale.getProduct().getPrice()).replace('.', ','),
                String.format("%.2f", sale.getQuantity() * sale.getProduct().getPrice()).replace('.', ',')
            })
            .toArray(Object[][]::new);

        JTable tabela = new JTable(dados, colunas);
        tabela.setFont(fontGrande);
        tabela.setRowHeight(25);
        tabela.getTableHeader().setFont(new Font("Arial", Font.BOLD, 20));

        JScrollPane scrollPane = new JScrollPane(tabela);
        JFrame dialog = new JFrame("Vendas Por Período");
        dialog.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        dialog.setSize(900, 600);
        dialog.add(scrollPane);

        // Exibe o total geral no rodapé
        double totalVendas = filteredSales.stream()
            .mapToDouble(sale -> sale.getQuantity() * sale.getProduct().getPrice())
            .sum();

        JLabel totalLabel = new JLabel(String.format("Total de vendas: R$ %.2f", totalVendas).replace('.', ','));
        totalLabel.setFont(fontGrande);
        totalLabel.setHorizontalAlignment(SwingConstants.CENTER);

        dialog.add(totalLabel, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }




    private static void showEditProductDialog(JFrame parentFrame) {
        Font fonteGrande = new Font("Arial", Font.PLAIN, 20);

        JFrame dialog = new JFrame("Alterar Produtos");
        dialog.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        dialog.setSize(800, 600);
        dialog.setResizable(true);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        JComboBox<String> productSelector = new JComboBox<>(
            inventory.getProductSummary().keySet().toArray(new String[0])
        );
        productSelector.setFont(fonteGrande);

        JTextField nameField = new JTextField();
        nameField.setFont(fonteGrande);

        JTextField descriptionField = new JTextField();
        descriptionField.setFont(fonteGrande);

        JTextField priceField = new JTextField();
        priceField.setFont(fonteGrande);

        JLabel nameLabel = new JLabel("Novo Nome:");
        nameLabel.setFont(fonteGrande);

        JLabel descriptionLabel = new JLabel("Nova Descrição:");
        descriptionLabel.setFont(fonteGrande);

        JLabel priceLabel = new JLabel("Novo Preço:");
        priceLabel.setFont(fonteGrande);

        mainPanel.add(new JLabel("Selecione o Produto:"));
        mainPanel.add(productSelector);
        mainPanel.add(nameLabel);
        mainPanel.add(nameField);
        mainPanel.add(descriptionLabel);
        mainPanel.add(descriptionField);
        mainPanel.add(priceLabel);
        mainPanel.add(priceField);

        JButton saveButton = new JButton("Salvar Alterações");
        saveButton.setFont(fonteGrande);

        JButton cancelButton = new JButton("Cancelar");
        cancelButton.setFont(fonteGrande);

        saveButton.addActionListener(e -> {
            try {
                String selectedProductKey = (String) productSelector.getSelectedItem();
                ProductSummary summary = inventory.getProductSummary().get(selectedProductKey);

                if (summary == null) {
                    JOptionPane.showMessageDialog(dialog, "Produto não encontrado!", "Erro", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                String newName = nameField.getText().trim();
                String newDescription = descriptionField.getText().trim();
                double newPrice = Double.parseDouble(priceField.getText().trim().replace(",", "."));

                if (newName.isEmpty() || newDescription.isEmpty() || newPrice <= 0) {
                    JOptionPane.showMessageDialog(dialog, "Preencha todos os campos corretamente!", "Erro", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Atualiza os atributos do produto
                inventory.updateProductAttributes(selectedProductKey, newName, newDescription, newPrice);

                JOptionPane.showMessageDialog(dialog, "Produto atualizado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Preço inválido!", "Erro", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        dialog.add(mainPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }



}

