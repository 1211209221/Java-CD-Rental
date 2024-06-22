import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

public class AdminPanel extends JFrame{
    String username;
    private JTable table;
    private List<String[]> allData;

    public AdminPanel(JFrame mainMenuFrame, String username){
        this.username = username;

        setTitle("Retro CD Rental System");
        setSize(910, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel mainp = new JPanel(new BorderLayout());
        
        //Logout Button
        Runnable backButtonAction = () -> {
            int confirmDelete = JOptionPane.showConfirmDialog(null, "Are you sure you want to log out?", "Confirm Logout", JOptionPane.YES_NO_OPTION);
            if (confirmDelete == JOptionPane.YES_OPTION) {
                dispose(); // Close the current frame
                CD_Rental_System newSystem = new CD_Rental_System(); // Create a new instance of CD_Rental_System
                newSystem.setVisible(true); // Make the new frame visible
            }
        };
        
        JPanel headerPanel = showHeader(mainMenuFrame, "Admin Panel", username, backButtonAction);
        mainp.add(headerPanel, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.setBackground(Color.LIGHT_GRAY);

        JButton addButton = new JButton("Add New CD");
        addButton.addActionListener(e -> {
            // Handle add button action
            System.out.println("Add button clicked");
        });
        buttonPanel.add(addButton);

        // View Rental Record Button
        JButton viewButton = new JButton("View Rental Record");
        viewButton.addActionListener(e -> {
            // Handle view rental record button action
            System.out.println("View rental record button clicked");
        });
        buttonPanel.add(viewButton);

        mainp.add(buttonPanel, BorderLayout.SOUTH);

        // Catalog Panel (Center)
        JPanel catalogPanel = catalogPanel(mainMenuFrame);
        mainp.add(catalogPanel, BorderLayout.CENTER);
        // Add mainPanel to JFrame
        add(mainp);

        setVisible(true);

    }

    //cd data
    private JPanel catalogPanel(JFrame mainMenuFrame){
        JPanel catalogPanel = new JPanel(new BorderLayout());

        JPanel searchButtonPanel = new JPanel(new BorderLayout());
        searchButtonPanel.setBorder(BorderFactory.createEmptyBorder(3, 5, 3, 5)); 
        JLabel infoLabel = new JLabel(" Click any row to update information or delete.");
        infoLabel.setHorizontalAlignment(SwingConstants.LEFT);

        ImageIcon refreshIcon = new ImageIcon("image/refresh.png");
        JButton refreshButton = new JButton(refreshIcon);
        refreshButton.setPreferredSize(new Dimension(30, 30));
        refreshButton.setToolTipText("Refresh");
        refreshButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        refreshButton.setBackground(Color.WHITE); 

        refreshButton.addActionListener(e -> {
            // refresh table
            reset();
        });

        ImageIcon searchIcon = new ImageIcon("image/search.png");
        JButton searchButton = new JButton(searchIcon);
        searchButton.setPreferredSize(new Dimension(30, 30));
        searchButton.setToolTipText("Search");
        searchButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        searchButton.setBackground(Color.WHITE); 

        searchButton.addActionListener(e -> {
            // search function
            String searchInput = JOptionPane.showInputDialog(mainMenuFrame, "Enter CD Name to search:", "Search CD", JOptionPane.PLAIN_MESSAGE);
            if (searchInput != null) { 
                searchInput = searchInput.trim(); 
                if (!searchInput.isEmpty()) { 
                    filter(searchInput, mainMenuFrame);
                } else {
                    JOptionPane.showMessageDialog(mainMenuFrame, "Search input cannot be empty.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        JPanel combinebtn = new JPanel();
        combinebtn.add(refreshButton);
        combinebtn.add(searchButton);
        searchButtonPanel.add(infoLabel, BorderLayout.WEST);
        searchButtonPanel.add(combinebtn, BorderLayout.EAST);

        String[] columnNames = {"CD Name", "Price (RM)", "Stock", "Genre", "Distributor"};
        allData = readCDData();
        String[][] dataArray = allData.toArray(new String[0][]);

        // Format prices with RM currency
        for (String[] row : dataArray) {
            row[1] = "RM " + row[1];
        }

        table = new JTable(dataArray, columnNames);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        table.setDefaultEditor(Object.class, null);
        table.getColumnModel().getColumn(0).setPreferredWidth(300);
        table.getColumnModel().getColumn(1).setPreferredWidth(120);
        table.getColumnModel().getColumn(2).setPreferredWidth(80);
        table.getColumnModel().getColumn(3).setPreferredWidth(140);
        table.getColumnModel().getColumn(4).setPreferredWidth(240);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.BLACK), // Add a black line border
            BorderFactory.createEmptyBorder(0, 5, 0, 5) 
        ));
        catalogPanel.add(searchButtonPanel, BorderLayout.NORTH);
        catalogPanel.add(scrollPane, BorderLayout.CENTER);
        table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) { // Ensure the event is not fired twice
                    int selectedRow = table.getSelectedRow();
                    if (selectedRow != -1) { // Ensure a row is selected
                        // Extract CD information from the selected row
                        String cdName = (String) table.getValueAt(selectedRow, 0);
                        String price = (String) table.getValueAt(selectedRow, 1);
                        String stock = (String) table.getValueAt(selectedRow, 2);
                        String genre = (String) table.getValueAt(selectedRow, 3);
                        String distributor = (String) table.getValueAt(selectedRow, 4);

                        // Create and display CD information window
                        JDialog cdInfoDialog = new JDialog(mainMenuFrame, "CD Information", true);
                        cdInfoDialog.setSize(400, 300);
                        cdInfoDialog.setLocationRelativeTo(mainMenuFrame);

                        JPanel infoPanel = new JPanel(new GridLayout(8, 2, 10, 5)); // Adjust rows, columns, and gaps
                        infoPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Add padding

                        JTextField cdNameField = new JTextField(cdName);
                        JTextField priceField = new JTextField(price.replace("RM ", ""));
                        JTextField stockField = new JTextField(stock);
                        JComboBox<String> genreComboBox = new JComboBox<>(new String[]{"Comedy", "Crime", "Sci-Fi", "Drame", "Action", "Historical", "Music", "Classical", "Folk"}); 
                        genreComboBox.setSelectedItem(genre);
                        JTextField distributorField = new JTextField(distributor);

                       
                        infoPanel.add(new JLabel("CD Name: "));
                        infoPanel.add(cdNameField);
                        infoPanel.add(new JLabel("Price (RM) : "));
                        infoPanel.add(priceField);
                        infoPanel.add(new JLabel("Stock: "));
                        infoPanel.add(stockField);
                        infoPanel.add(new JLabel("Genre: "));
                        infoPanel.add(genreComboBox);
                        infoPanel.add(new JLabel("Distributor: "));
                        infoPanel.add(distributorField);

                        JButton updateButton = new JButton("Update");
                        JButton deleteButton = new JButton("Remove");

                        updateButton.addActionListener(new ActionListener() {
                            public void actionPerformed(ActionEvent e) {
                                int confirmUpdate = JOptionPane.showConfirmDialog(null, "Are you sure you want to update the changes?", "Confirm Updation", JOptionPane.YES_NO_OPTION);
                                if (confirmUpdate == JOptionPane.YES_OPTION) {
                                    String newCdName = cdNameField.getText().trim();
                                    String newPrice = priceField.getText().trim();
                                    String newStock = stockField.getText().trim();
                                    String newGenre = (String) genreComboBox.getSelectedItem();
                                    String newDistributor = distributorField.getText().trim();

                                    if (newCdName.isEmpty() || newPrice.isEmpty() || newStock.isEmpty() || newDistributor.isEmpty()) {
                                        JOptionPane.showMessageDialog(cdInfoDialog, "All fields are required.", "Error", JOptionPane.ERROR_MESSAGE);
                                        return; // Stop further processing
                                    }

                                    //validate price and stock
                                    double price;
                                    try {
                                        price = Double.parseDouble(newPrice);
                                        if (price < 0) {
                                            JOptionPane.showMessageDialog(cdInfoDialog, "Price CANNOT be in zero/negative value.", "Error", JOptionPane.ERROR_MESSAGE);
                                            return; 
                                        }
                                    } catch (NumberFormatException ex) {
                                        JOptionPane.showMessageDialog(cdInfoDialog, "Invalid price format.", "Error", JOptionPane.ERROR_MESSAGE);
                                        return; 
                                    }

                                    int stock;
                                    try {
                                        stock = Integer.parseInt(newStock);
                                        if (stock <= 0) {
                                            JOptionPane.showMessageDialog(cdInfoDialog, "Stock must be a positive value.", "Error", JOptionPane.ERROR_MESSAGE);
                                            return; 
                                        }
                                    } catch (NumberFormatException ex) {
                                        JOptionPane.showMessageDialog(cdInfoDialog, "Invalid stock format.", "Error", JOptionPane.ERROR_MESSAGE);
                                        return; 
                                    }

                                    // Update data in memory
                                    String[] updatedRow = {newCdName, newPrice, newStock, newGenre, newDistributor};
                                    allData.set(selectedRow, updatedRow);

                                    // Save updated data to file (you need to implement this)
                                    saveDataToFile(allData);

                                    // Refresh table with updated data
                                    reset();

                                    // Show success message
                                    JOptionPane.showMessageDialog(cdInfoDialog, "CD information updated successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);

                                    // Close the dialog
                                    cdInfoDialog.dispose();
                                }
                            }
                        });

                        deleteButton.addActionListener(new ActionListener() {
                            public void actionPerformed(ActionEvent e) {
                                int confirmDelete = JOptionPane.showConfirmDialog(null, "Are you sure you want to remove this CD?", "Confirm Deletion", JOptionPane.YES_NO_OPTION);
                                if (confirmDelete == JOptionPane.YES_OPTION) {
                                    
                                }
                            }
                        });

                        JPanel buttongrp = new JPanel();
                        buttongrp.add(updateButton); buttongrp.add(deleteButton);

                        cdInfoDialog.add(infoPanel, BorderLayout.CENTER);
                        cdInfoDialog.add(buttongrp, BorderLayout.SOUTH);
                        cdInfoDialog.setVisible(true);
                    }
                }
            }
        });

        return catalogPanel;
    }
    //for update cd information
    private void saveDataToFile(List<String[]> data) {

        try (BufferedWriter putin = new BufferedWriter(new FileWriter("records/CDs.txt"))) {
            for (String[] row : data) {
                String priceString = row[1].replace("RM ", "");
                double price = Double.parseDouble(priceString);
                int stock = Integer.parseInt(row[2]);

                putin.write(String.format("\"%s\" %.2f %d %s \"%s\"",
                row[0], price, stock, row[3], row[4]));
                putin.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error saving data to file.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    private void filter(String searchInput, JFrame mainMenuFrame) {
        // Retrieve data again
        String[][] filteredDataArray = allData.stream()
            .filter(row -> row[0].toLowerCase().contains(searchInput.toLowerCase()))
            .toArray(String[][]::new);

        if (filteredDataArray.length == 0) {
            JOptionPane.showMessageDialog(mainMenuFrame, "No results found.", "Search Result", JOptionPane.INFORMATION_MESSAGE);
            reset();
            return;
        }
    
        // Update table model
        table.setModel(new DefaultTableModel(filteredDataArray, new String[] {"CD Name", "Price (RM)", "Stock", "Genre", "Distributor"}));
    
        // Adjust column widths
        table.getColumnModel().getColumn(0).setPreferredWidth(300);
        table.getColumnModel().getColumn(1).setPreferredWidth(120);
        table.getColumnModel().getColumn(2).setPreferredWidth(80);
        table.getColumnModel().getColumn(3).setPreferredWidth(140);
        table.getColumnModel().getColumn(4).setPreferredWidth(240);
    }
    private void reset() {
        String[][] dataArray = allData.toArray(new String[0][]);

        for (String[] row : dataArray) {
            if (!row[1].startsWith("RM")) {
                row[1] = "RM " + row[1];
            }
        }

        // Update table model
        table.setModel(new DefaultTableModel(dataArray, new String[]{"CD Name", "Price (RM)", "Stock", "Genre", "Distributor"}));

        // Adjust column widths
        table.getColumnModel().getColumn(0).setPreferredWidth(300);
        table.getColumnModel().getColumn(1).setPreferredWidth(120);
        table.getColumnModel().getColumn(2).setPreferredWidth(80);
        table.getColumnModel().getColumn(3).setPreferredWidth(140);
        table.getColumnModel().getColumn(4).setPreferredWidth(240);
    }
    private List<String[]> readCDData() {
        List<String[]> data = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader("records/CDs.txt"))) { // Corrected file name
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = parseLine(line);
                if (parts.length == 5) {
                    data.add(parts);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }
    private String[] parseLine(String line) {
        List<String> parts = new ArrayList<>();
        Matcher matcher = Pattern.compile("\"([^\"]*)\"|(\\S+)").matcher(line);
        while (matcher.find()) {
            if (matcher.group(1) != null) {
                parts.add(matcher.group(1));
            } else {
                parts.add(matcher.group(2));
            }
        }
        return parts.toArray(new String[0]);
    }

    //header
    private JPanel showHeader(JFrame mainMenuFrame, String title, String username, Runnable backButtonAction) {
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(Color.WHITE);
        
        ImageIcon icon = new ImageIcon("image/logout.png");
        JButton backButton = new JButton(icon);
        backButton.setPreferredSize(new Dimension(30, 30));
        backButton.setToolTipText("Log out");
        backButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        backButton.setBackground(Color.WHITE); 
        backButton.addActionListener(e -> backButtonAction.run());
        titlePanel.add(backButton, BorderLayout.WEST);
    
        JLabel pageTitleLabel = new JLabel(title, SwingConstants.CENTER);
        pageTitleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titlePanel.add(pageTitleLabel, BorderLayout.CENTER);
        
        ImageIcon usericon = new ImageIcon("image/usericon.png");
        JLabel welcomeLabel = new JLabel(username, usericon, SwingConstants.RIGHT);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 13));
        titlePanel.add(welcomeLabel, BorderLayout.EAST);
    
        titlePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    
        mainMenuFrame.add(titlePanel, BorderLayout.NORTH); // Add titlePanel to mainMenuFrame
    
        return titlePanel; // Return titlePanel if needed
    }
    
    void addCD() {
        System.out.println("CD added");
    }

    void editCD() {
        System.out.println("CD edited");
    }

    void deleteCD() {
        System.out.println("CD deleted");
    }
    
}