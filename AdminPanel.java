import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import java.math.BigDecimal;

class IconHeaderRenderer extends DefaultTableCellRenderer {
    private ImageIcon icon;

    public IconHeaderRenderer(ImageIcon icon) {
        this.icon = icon;
        setHorizontalAlignment(JLabel.CENTER);
        setHorizontalTextPosition(JLabel.LEFT);
        //Preserving original table style
        setBackground(UIManager.getColor("TableHeader.background"));
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        setText((String) value);
        setIcon(icon);
        return this;
    }
} 

public class AdminPanel extends JFrame{
    String username;
    private JTable table;
    private List<String[]> allData;

    public AdminPanel(JFrame menuFrame, String username){
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
        
        JPanel headerPanel = showHeader(menuFrame, "Admin Panel", username, backButtonAction);
        headerPanel.setBackground(Color.WHITE);
        mainp.add(headerPanel, BorderLayout.NORTH);

        JPanel buttonPanel = createButtonPanel(menuFrame);
        mainp.add(buttonPanel, BorderLayout.SOUTH);

        JPanel catalogPanel = catalogPanel(menuFrame);
        mainp.add(catalogPanel, BorderLayout.CENTER);
        // Add mainPanel to JFrame
        add(mainp);

        setVisible(true);

    }

    //for button below
    private JPanel createButtonPanel(JFrame menuFrame) {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.setBackground(Color.WHITE);

        // Add New CD Button
        ImageIcon addIcon = new ImageIcon("image/add.png");
        JButton addButton = new JButton("Add New CD", addIcon);
        addButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        addButton.addActionListener(e -> showAddCDDialog(menuFrame));
        buttonPanel.add(addButton);

        // View Rental Record Button
        ImageIcon viewIcon = new ImageIcon("image/record.png");
        JButton viewButton = new JButton("View Rental Record", viewIcon);
        viewButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        viewButton.addActionListener(e -> {
            viewrentalrecord(menuFrame);
        });
        buttonPanel.add(viewButton);

        // Manage Fee Button
        ImageIcon feeIcon = new ImageIcon("image/fee.png");
        JButton feeButton = new JButton("Manage Fee", feeIcon);
        feeButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        feeButton.addActionListener(e -> showFeeManagement(menuFrame));
        feeButton.setHorizontalAlignment(SwingConstants.RIGHT);
        buttonPanel.add(feeButton);

        return buttonPanel;
    }

    //cd data, inside include update and remove function
    private JPanel catalogPanel(JFrame menuFrame){
        JPanel catalogPanel = new JPanel(new BorderLayout());

        JPanel searchButtonPanel = new JPanel(new BorderLayout());
        searchButtonPanel.setBorder(BorderFactory.createEmptyBorder(3, 5, 3, 5)); 
        JLabel infoLabel = new JLabel(" Click any row to edit information or delete.");
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
            String searchInput = JOptionPane.showInputDialog(menuFrame, "Enter CD Name to search:", "Search CD", JOptionPane.PLAIN_MESSAGE);
            if (searchInput != null) { 
                searchInput = searchInput.trim(); 
                if (!searchInput.isEmpty()) { 
                    filter(searchInput, menuFrame);
                } else {
                    JOptionPane.showMessageDialog(menuFrame, "Search input cannot be empty.", "Error", JOptionPane.ERROR_MESSAGE);
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
        table.setDefaultEditor(Object.class, null);//

        ImageIcon originalIcon = new ImageIcon("image/sort_icon.png");
        Image image = originalIcon.getImage().getScaledInstance(15, 15, Image.SCALE_SMOOTH);
        ImageIcon sortIcon = new ImageIcon(image);
        table.getTableHeader().setDefaultRenderer(new IconHeaderRenderer(sortIcon));

        TableColumnModel columnModel = table.getColumnModel();
        columnModel.getColumn(0).setPreferredWidth(300);
        columnModel.getColumn(1).setPreferredWidth(120);
        columnModel.getColumn(2).setPreferredWidth(80);
        columnModel.getColumn(3).setPreferredWidth(140);
        columnModel.getColumn(4).setPreferredWidth(220);

        // Enable sorting
        table.setModel(new DefaultTableModel(dataArray, columnNames));
        TableRowSorter<TableModel> sorter = new TableRowSorter<>(table.getModel());
        table.setRowSorter(sorter);
        reset();

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
                        JDialog cdInfoDialog = new JDialog(menuFrame, "CD Information", true);
                        cdInfoDialog.setSize(400, 300);
                        cdInfoDialog.setLocationRelativeTo(menuFrame);

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
                        JButton cancelButton = new JButton("Cancel");

                        //this part for update
                        updateButton.addActionListener(new ActionListener() {
                            public void actionPerformed(ActionEvent e) {
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
                                    if (price <= 0) {
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
                                    if (stock < 0) {
                                        JOptionPane.showMessageDialog(cdInfoDialog, "Stock must be a positive value.", "Error", JOptionPane.ERROR_MESSAGE);
                                        return; 
                                    }
                                } catch (NumberFormatException ex) {
                                    JOptionPane.showMessageDialog(cdInfoDialog, "Invalid stock format.", "Error", JOptionPane.ERROR_MESSAGE);
                                    return; 
                                }

                                int confirmUpdate = JOptionPane.showConfirmDialog(null, "Are you sure you want to update the changes?", "Confirm Updation", JOptionPane.YES_NO_OPTION);
                                if (confirmUpdate == JOptionPane.YES_OPTION) {

                                    //original data
                                    String originalCdName = (String) table.getValueAt(selectedRow, 0);
                                    String originalPrice = (String) table.getValueAt(selectedRow, 1);
                                    String originalStock = (String) table.getValueAt(selectedRow, 2);
                                    String originalGenre = (String) table.getValueAt(selectedRow, 3);
                                    String originalDistributor = (String) table.getValueAt(selectedRow, 4);
                                    // Read the file to find the line number of the original data
                                    int lineNumber = findLineNumber(originalCdName, originalPrice, originalStock, originalGenre, originalDistributor);
                                    if (lineNumber == -1) {
                                        JOptionPane.showMessageDialog(null, "Original CD information not found in file.", "Error", JOptionPane.ERROR_MESSAGE);
                                        return;
                                    }

                                    // Update data in memory
                                    String[] updatedRow = {newCdName, newPrice, newStock, newGenre, newDistributor};

                                    // Save updated data to file (you need to implement this)
                                    saveDataToFile(updatedRow, lineNumber);

                                    // Refresh table with updated data
                                    reset();

                                    // Show success message
                                    JOptionPane.showMessageDialog(cdInfoDialog, "CD information updated successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);

                                    // Close the dialog
                                    cdInfoDialog.dispose();
                                }
                            }
                        });

                        //this part for delete
                        deleteButton.addActionListener(new ActionListener() {
                            public void actionPerformed(ActionEvent e) {
                                int confirmDelete = JOptionPane.showConfirmDialog(null, "Are you sure you want to remove this CD?", "Confirm Deletion", JOptionPane.YES_NO_OPTION);
                                if (confirmDelete == JOptionPane.YES_OPTION) {
                                    // Get original CD data from the table
                                    String originalCdName = (String) table.getValueAt(selectedRow, 0);
                                    String originalPrice = (String) table.getValueAt(selectedRow, 1);
                                    String originalStock = (String) table.getValueAt(selectedRow, 2);
                                    String originalGenre = (String) table.getValueAt(selectedRow, 3);
                                    String originalDistributor = (String) table.getValueAt(selectedRow, 4);

                                    // Find the line number of the original data in the file
                                    int lineNumber = findLineNumber(originalCdName, originalPrice, originalStock, originalGenre, originalDistributor);
                                    if (lineNumber == -1) {
                                        JOptionPane.showMessageDialog(null, "Original CD information not found in file.", "Error", JOptionPane.ERROR_MESSAGE);
                                        return;
                                    }

                                    boolean deletionSuccess = deleteDataFromFile(lineNumber);
                                    if (deletionSuccess) {
                                        reset(); // Refresh table with updated data
                                        JOptionPane.showMessageDialog(cdInfoDialog, "CD removed successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                                    } else {
                                        JOptionPane.showMessageDialog(cdInfoDialog, "Failed to remove CD from file.", "Error", JOptionPane.ERROR_MESSAGE);
                                    }
                                    // Refresh table with updated data
                                    reset();

                                    // Close the dialog
                                    cdInfoDialog.dispose();
                                
                                }
                            }
                        });

                        cancelButton.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                cdInfoDialog.dispose(); 
                            }
                        });

                        JPanel buttongrp = new JPanel();
                        buttongrp.add(updateButton); buttongrp.add(deleteButton); buttongrp.add(cancelButton);

                        cdInfoDialog.add(infoPanel, BorderLayout.CENTER);
                        cdInfoDialog.add(buttongrp, BorderLayout.SOUTH);
                        cdInfoDialog.setVisible(true);
                    }
                }
            }
        });

        return catalogPanel;
    }
    // Method to find the line number in the file that matches the original data
    private int findLineNumber(String originalCdName, String originalPrice, String originalStock, String originalGenre, String originalDistributor) {
        try (BufferedReader reader = new BufferedReader(new FileReader("records/CDs.txt"))) {
            String line;
            int lineNumber = 0;
            while ((line = reader.readLine()) != null) {
                List<String> parts = parseLineforUpdateDelete(line);
                if (parts.size() >= 5) {
                    String cdName = parts.get(0);
                    String price = parts.get(1);
                    String stock = parts.get(2);
                    String genre = parts.get(3);
                    String distributor = parts.get(4);

                    originalPrice = price.replace("RM ", "");

                    if (cdName.equals(originalCdName) && price.equals(originalPrice) && stock.equals(originalStock)
                            && genre.equals(originalGenre) && distributor.equals(originalDistributor)) {
                                
                        return lineNumber; // Found the line number
                    }
                }
                lineNumber++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        return -1; // Original data not found
    }
    // Method to parse a line from the file into parts
    private List<String> parseLineforUpdateDelete(String line) {
        List<String> parts = new ArrayList<>();
        Matcher matcher = Pattern.compile("\"([^\"]*)\"|(\\S+)").matcher(line);
        while (matcher.find()) {
            if (matcher.group(1) != null) {
                parts.add(matcher.group(1));
            } else {
                parts.add(matcher.group(2));
            }
        }
        return parts;
    }

    //for update cd information
    private void saveDataToFile(String[] updatedRow, int lineNumber) {
        File file = new File("records/CDs.txt");
        File tempFile = new File("records/CDs_temp.txt");
    
        try (BufferedReader reader = new BufferedReader(new FileReader(file));
             BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {
    
            String line;
            int currentLine = 0;
    
            // Copy all lines from original file to temp file, updating the specified line
            while ((line = reader.readLine()) != null) {
                if (currentLine == lineNumber) {
                    // Update the line with new data
                    String priceString = updatedRow[1].replace("RM ", "");
                    double price = Double.parseDouble(priceString);
                    int stock = Integer.parseInt(updatedRow[2]);
    
                    writer.write(String.format("\"%s\" %.2f %d %s \"%s\"",
                            updatedRow[0], price, stock, updatedRow[3], updatedRow[4]));
                    writer.newLine();
                } else {
                    // Copy unchanged line to temp file
                    writer.write(line);
                    writer.newLine();
                }
                currentLine++;
            }
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error saving data to file.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
    
        // Replace the original file with the temp file
        if (!file.delete()) {
            JOptionPane.showMessageDialog(null, "Error updating data in file.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (!tempFile.renameTo(file)) {
            JOptionPane.showMessageDialog(null, "Error updating data in file.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    //for delete
    private boolean deleteDataFromFile(int lineNumber) {
        File inputFile = new File("records/CDs.txt");
        File tempFile = new File("records/CDs_temp.txt");
    
        BufferedReader reader = null;
        BufferedWriter writer = null;
    
        try {
            reader = new BufferedReader(new FileReader(inputFile));
            writer = new BufferedWriter(new FileWriter(tempFile));
    
            String lineToRemove = "";
            String currentLine;
            int lineNum = 0;
    
            while ((currentLine = reader.readLine()) != null) {
                if (lineNum == lineNumber) {
                    lineToRemove = currentLine;
                    break;
                }
                lineNum++;
            }
    
            // If lineToRemove is empty, the line was not found
            if (lineToRemove.isEmpty()) {
                System.err.println("Line to delete not found.");
                return false;
            }
    
            // Reset reader to beginning of the file
            reader.close();
            reader = new BufferedReader(new FileReader(inputFile));
            lineNum = 0;
    
            // Rewrite the file excluding the line to be deleted
            while ((currentLine = reader.readLine()) != null) {
                // Don't write the line to remove to the temporary file
                if (lineNum != lineNumber) {
                    writer.write(currentLine + System.getProperty("line.separator"));
                }
                lineNum++;
            }
    
            // Close resources
            reader.close();
            writer.close();
    
            // Delete original file
            if (!inputFile.delete()) {
                System.err.println("Failed to delete the original file.");
                return false;
            }
    
            // Rename the temporary file to the original file name
            if (!tempFile.renameTo(inputFile)) {
                System.err.println("Failed to rename the temporary file.");
                return false;
            }
    
            return true; // Deletion successful
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            // Close resources in finally block to ensure they are always closed
            try {
                if (reader != null) {
                    reader.close();
                }
                if (writer != null) {
                    writer.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    //for search 
    private void filter(String searchInput, JFrame menuFrame) {
        // Retrieve data again
        String[][] filteredDataArray = allData.stream()
            .filter(row -> row[0].toLowerCase().contains(searchInput.toLowerCase()))
            .toArray(String[][]::new);

        if (filteredDataArray.length == 0) {
            JOptionPane.showMessageDialog(menuFrame, "No results found.", "Search Result", JOptionPane.INFORMATION_MESSAGE);
            reset();
            return;
        }
    
        // Update table model
        DefaultTableModel newModel = new DefaultTableModel(filteredDataArray, new String[] {"CD Name", "Price (RM)", "Stock", "Genre", "Distributor"});
        table.setModel(newModel);

        // Re-apply TableRowSorter to the new model
        TableRowSorter<TableModel> newSorter = new TableRowSorter<>(newModel);
        table.setRowSorter(newSorter);
    
        // Adjust column widths
        table.getColumnModel().getColumn(0).setPreferredWidth(300);
        table.getColumnModel().getColumn(1).setPreferredWidth(120);
        table.getColumnModel().getColumn(2).setPreferredWidth(80);
        table.getColumnModel().getColumn(3).setPreferredWidth(140);
        table.getColumnModel().getColumn(4).setPreferredWidth(220);
    }
    //for reset table
    private void reset() {
        allData = readCDData();
        String[][] dataArray = allData.toArray(new String[0][]);
        DecimalFormat decimalFormat = new DecimalFormat("#0.00");
        
        for (String[] row : dataArray) {
            if (!row[1].startsWith("RM")) {
                try {
                    double value = Double.parseDouble(row[1]);
                    // Format the value to 2 decimal places
                    row[1] = "RM " + decimalFormat.format(value);
                } catch (NumberFormatException e) {
                    // Handle the case where row[1] is not a valid number
                    System.err.println("Invalid number format: " + row[1]);
                }
            }
        }

        // Update table model
        DefaultTableModel newModel = new DefaultTableModel(dataArray, new String[] {"CD Name", "Price (RM)", "Stock", "Genre", "Distributor"});
        table.setModel(newModel);

        // Re-apply TableRowSorter to the new model
        TableRowSorter<TableModel> newSorter = new TableRowSorter<>(newModel);
        table.setRowSorter(newSorter);

        // Adjust column widths
        table.getColumnModel().getColumn(0).setPreferredWidth(300);
        table.getColumnModel().getColumn(1).setPreferredWidth(120);
        table.getColumnModel().getColumn(2).setPreferredWidth(80);
        table.getColumnModel().getColumn(3).setPreferredWidth(140);
        table.getColumnModel().getColumn(4).setPreferredWidth(220);
    }
    //for take out cd info
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

    //for add cd dialog
    public void showAddCDDialog(JFrame menuFrame){
        JDialog addcdDialog = new JDialog(menuFrame, "Add New CD", true);
        addcdDialog.setSize(400, 300);
        addcdDialog.setLocationRelativeTo(menuFrame);

        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        titlePanel.setBackground(Color.WHITE);
        ImageIcon logoIcon = new ImageIcon("image/add.png"); 
        JLabel titleLabel = new JLabel("Add New CD", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titlePanel.add(new JLabel(logoIcon));
        titlePanel.add(titleLabel);

        JPanel infoPanel = new JPanel(new GridLayout(6, 2, 10, 5)); // Adjust rows, columns, and gaps
        infoPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Add padding

        JTextField cdNameField = new JTextField();
        JTextField priceField = new JTextField();
        JTextField stockField = new JTextField();
        JComboBox<String> genreComboBox = new JComboBox<>(new String[]{"Comedy", "Crime", "Sci-Fi", "Drame", "Action", "Historical", "Music", "Classical", "Folk"}); 
        JTextField distributorField = new JTextField();

                       
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

        JButton addButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");

        //this part for add
        addButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String newCdName = cdNameField.getText().trim();
                String newPrice = priceField.getText().trim();
                String newStock = stockField.getText().trim();
                String newGenre = (String) genreComboBox.getSelectedItem();
                String newDistributor = distributorField.getText().trim();

                if (newCdName.isEmpty() || newPrice.isEmpty() || newStock.isEmpty() || newDistributor.isEmpty()) {
                    JOptionPane.showMessageDialog(addcdDialog, "All fields are required.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                //validate price and stock
                double price;
                try {
                    price = Double.parseDouble(newPrice);
                    if (price <= 0) {
                        JOptionPane.showMessageDialog(addcdDialog, "Price CANNOT be in zero/negative value.", "Error", JOptionPane.ERROR_MESSAGE);
                        return; 
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(addcdDialog, "Invalid price format.", "Error", JOptionPane.ERROR_MESSAGE);
                    return; 
                }

                int stock;
                try {
                    stock = Integer.parseInt(newStock);
                    if (stock < 0) {
                        JOptionPane.showMessageDialog(addcdDialog, "Stock must be a positive value.", "Error", JOptionPane.ERROR_MESSAGE);
                        return; 
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(addcdDialog, "Invalid stock format.", "Error", JOptionPane.ERROR_MESSAGE);
                    return; 
                }

                int confirmUpdate = JOptionPane.showConfirmDialog(null, "Are you sure you want to add this CD?", "Confirm Updation", JOptionPane.YES_NO_OPTION);
                if (confirmUpdate == JOptionPane.YES_OPTION) {
                //save data in memory
                String[] newbookdata = {newCdName, newPrice, newStock, newGenre, newDistributor};
                allData.add(newbookdata);

                //go to a fucntion to add in cds.txt
                addCD(newbookdata);

                //reset table
                reset();

                // Show success message
                JOptionPane.showMessageDialog(addcdDialog, "CD is added successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);

                // Close the dialog
                addcdDialog.dispose();
            }
        }
    });

                        

    JPanel buttongrp = new JPanel();
    buttongrp.add(addButton); buttongrp.add(cancelButton);
    cancelButton.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            addcdDialog.dispose(); 
        }
    });   
    addcdDialog.add(titlePanel, BorderLayout.NORTH);
    addcdDialog.add(infoPanel, BorderLayout.CENTER);
    addcdDialog.add(buttongrp, BorderLayout.SOUTH);
    addcdDialog.setVisible(true);
}
    //for view rental record
    private void viewrentalrecord(JFrame menuFrame) {
        int recordcount = 0;

        File rentedFolder = new File("records/rented");
        File[] rentedFiles = rentedFolder.listFiles();
        List<String[]> rentalData = new ArrayList<>();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        Pattern pattern = Pattern.compile("\"([^\"]+)\" (\\d+) (\\d{4}-\\d{2}-\\d{2})");
        Pattern patternWithoutQuotes = Pattern.compile("([^\\d]+) (\\d+) (\\d{4}-\\d{2}-\\d{2})");

        if (rentedFiles != null) {
            for (File file : rentedFiles) {
                if (file.isFile()) {
                    //take the user name from filename
                    String username = file.getName().replace(".txt", "");
                    try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                        String line;
                        while ((line = reader.readLine()) != null) {
                            recordcount++;
                            Matcher matcherWithQuotes = pattern.matcher(line);
                            Matcher matcherWithoutQuotes = patternWithoutQuotes.matcher(line);
                            if (matcherWithQuotes.matches()) {
                                String cdName = matcherWithQuotes.group(1);
                                String quantity = matcherWithQuotes.group(2);
                                String dueDate = matcherWithQuotes.group(3);
                                LocalDate dueDateParsed = LocalDate.parse(dueDate, dateFormatter);
                                String status = dueDateParsed.isBefore(LocalDate.now()) ? "Overdue" : "Active";
                                rentalData.add(new String[]{username, cdName, quantity, dueDate, status});
                            } else if (matcherWithoutQuotes.matches()) {
                                String cdName = matcherWithoutQuotes.group(1).trim();
                                String quantity = matcherWithoutQuotes.group(2);
                                String dueDate = matcherWithoutQuotes.group(3);
                                LocalDate dueDateParsed = LocalDate.parse(dueDate, dateFormatter);
                                String status = dueDateParsed.isBefore(LocalDate.now()) ? "Overdue" : "Active";
                                rentalData.add(new String[]{username, cdName, quantity, dueDate, status});
                            }else {
                                System.err.println("Invalid data format in file: " + file.getName());
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        JOptionPane.showMessageDialog(menuFrame, "Error reading file: " + file.getName(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        }

        //after read the current rental record, read the past record
        File passRecordFile = new File("records/pastrecord.txt");
        pattern = Pattern.compile("\"([^\"]+)\" (\\d+) (\\d{4}-\\d{2}-\\d{2}) \"([^\"]+)\"");
        patternWithoutQuotes = Pattern.compile("([^\\d]+) (\\d+) (\\d{4}-\\d{2}-\\d{2}) \"([^\"]+)\"");
        try (BufferedReader passRecordReader = new BufferedReader(new FileReader(passRecordFile))) {
            String line;
            while ((line = passRecordReader.readLine()) != null) {
                recordcount++;
                Matcher matcherWithQuotes = pattern.matcher(line);
                Matcher matcherWithoutQuotes = patternWithoutQuotes.matcher(line);
                if (matcherWithQuotes.find()) {
                    String cdName = matcherWithQuotes.group(1);
                    String quantity = matcherWithQuotes.group(2);
                    String dueDate = matcherWithQuotes.group(3);
                    String user = matcherWithQuotes.group(4);
                    String status = "Completed";
                    rentalData.add(new String[]{user, cdName, quantity, dueDate, status});
                } else if (matcherWithoutQuotes.find()) {
                    String cdName = matcherWithoutQuotes.group(1).trim();
                    String quantity = matcherWithoutQuotes.group(2);
                    String dueDate = matcherWithoutQuotes.group(3);
                    String user = matcherWithoutQuotes.group(4);
                    String status = "Completed";
                    rentalData.add(new String[]{user, cdName, quantity, dueDate, status});
                }else {
                    System.err.println("Invalid data format in line: " + line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(menuFrame, "Error reading pastrecord file.", "Error", JOptionPane.ERROR_MESSAGE);
        }


        // Create the table with rental data
        String[] columnNames = {"User", "CD Name", "Quantity", "Due Date", "Status"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);

        for (String[] data : rentalData) {
            model.addRow(data);
        }   

        JTable table = new JTable(model);
        table.setDefaultEditor(Object.class, null);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(new EmptyBorder(0, 10,0, 10));

        table.getColumnModel().getColumn(0).setPreferredWidth(100);
        table.getColumnModel().getColumn(1).setPreferredWidth(250);
        table.getColumnModel().getColumn(2).setPreferredWidth(70);
        table.getColumnModel().getColumn(3).setPreferredWidth(90);
        table.getColumnModel().getColumn(4).setPreferredWidth(70);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                //alignment
                if (column == 2 || column == 3 || column == 4) { 
                    setHorizontalAlignment(SwingConstants.CENTER);
                } else {
                    setHorizontalAlignment(SwingConstants.LEFT); 
                }
                //status 
                if (column == 4) { // Status column
                    String status = (String) value;
                    if ("Overdue".equals(status)) {
                        c.setForeground(Color.RED);
                    } else if("Active".equals(status)) {
                        c.setForeground(Color.GREEN);
                    }else{
                        c.setForeground(Color.BLUE);
                    }
                } else {
                    c.setForeground(Color.BLACK); // Default color for other columns
                }
                return c; 
                
            }
        };
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        // Create and set up the window
        JFrame rentalFrame = new JFrame("Rental Records");
        rentalFrame.setSize(600, 400);
        rentalFrame.setLocationRelativeTo(null);
        rentalFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        //title for this frame
        JPanel topPanel = new JPanel(new BorderLayout());
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        ImageIcon logoIcon = new ImageIcon("image/record.png"); 
        JLabel titleLabel = new JLabel("Rental Record List", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titlePanel.setBackground(Color.WHITE);
        titlePanel.add(new JLabel(logoIcon));
        titlePanel.add(titleLabel);
        JLabel countLabel= new JLabel(" Total Rental Records: " + recordcount);
        JPanel countPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        countPanel.add(countLabel);

        topPanel.add(titlePanel,BorderLayout.NORTH);
        topPanel.add(countPanel,BorderLayout.SOUTH);
        

        //buttons
        // Create buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(Color.WHITE);
        JButton allButton = new JButton("All");
        JButton activeButton = new JButton("Active");
        JButton overdueButton = new JButton("Overdue");
        JButton completedButton = new JButton("Completed");

        buttonPanel.add(allButton);
        buttonPanel.add(activeButton);
        buttonPanel.add(overdueButton);
        buttonPanel.add(completedButton);

        //for the button event listener
        allButton.addActionListener(e -> filterrentedTable(model, rentalData, "All"));
        activeButton.addActionListener(e -> filterrentedTable(model, rentalData, "Active"));
        overdueButton.addActionListener(e -> filterrentedTable(model, rentalData, "Overdue"));
        completedButton.addActionListener(e -> filterrentedTable(model, rentalData, "Completed"));

        JPanel contentPane = new JPanel(new BorderLayout());
        contentPane.add(topPanel, BorderLayout.NORTH);
        contentPane.add(scrollPane, BorderLayout.CENTER);
        contentPane.add(buttonPanel, BorderLayout.SOUTH);

        rentalFrame.setContentPane(contentPane);
        rentalFrame.setVisible(true);
    }
    //filter rented table
    private void filterrentedTable(DefaultTableModel model, List<String[]> rentalData, String filter) {
        model.setRowCount(0); // Clear all rows from the table
        //filter is rental status
        for (String[] data : rentalData) {
            if ("All".equals(filter)) {
                model.addRow(data); // Add all data
            } else if (data[4].equals(filter)) {
                model.addRow(data); // Add chosen status data
            }
        }
    }

    private void showFeeManagement(JFrame menuFrame) {
        //Create new Dialog
        JDialog feeDialog = new JDialog(menuFrame, "Fee Management", true);
        feeDialog.setSize(400, 300);
        feeDialog.setLocationRelativeTo(menuFrame);

        //title panel 
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        titlePanel.setBackground(Color.WHITE);
        ImageIcon logoIcon = new ImageIcon("image/fee.png"); 
        JLabel titleLabel = new JLabel("Fee Management", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titlePanel.add(new JLabel(logoIcon));
        titlePanel.add(titleLabel);

        //Create new Panel
        // Button panel for the buttons
        JPanel buttonPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 0, 10, 0); // Add space between buttons
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Set button names
        JButton rentalFeeButton = new JButton("View Rental Fee");
        JButton penaltyFeeButton = new JButton("View Penalty Fee");
        JButton viewTotalRevenueButton = new JButton("View Total Revenue");

        // Set button size
        Dimension buttonSize = new Dimension(200, 50);
        rentalFeeButton.setPreferredSize(buttonSize);
        penaltyFeeButton.setPreferredSize(buttonSize);
        viewTotalRevenueButton.setPreferredSize(buttonSize);

        // Add buttons to the button panel with constraints
        gbc.gridy = 0;
        buttonPanel.add(rentalFeeButton, gbc);
        gbc.gridy = 1;
        buttonPanel.add(penaltyFeeButton, gbc);
        gbc.gridy = 2;
        buttonPanel.add(viewTotalRevenueButton, gbc);

        // Add buttonPanel to the dialog
        feeDialog.add(buttonPanel, BorderLayout.CENTER);
        feeDialog.add(titlePanel, BorderLayout.NORTH);
        //Event Listener for Fee Management
        rentalFeeButton.addActionListener(e -> {
            String currentFee = "";
            String filePath = "records/fee.txt";
            try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
                String line = reader.readLine();
                if (line != null) {
                    currentFee = line.split(" ")[0];
                }
                double fee = Double.parseDouble(currentFee);
                String formattedFee = String.format("%.2f", fee);
                String message = "The current rental fee is: RM" + formattedFee + ". Do you want to change it?";
                Object[] options = {"Yes", "No"};
                
                int choice = JOptionPane.showOptionDialog(feeDialog, message, "Rental Fee",
                        JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[1]);
                if (choice == JOptionPane.YES_OPTION) {
                    String newFee = JOptionPane.showInputDialog(feeDialog, "Enter the new rental fee:");
                    if (newFee != null)
                    {
                        // Validate the new fee
                        try {
                            new BigDecimal(newFee); // This checks if the new fee is a valid decimal number
                            String[] columns = line.split(" "); // Assuming columns represent parts of the line
                            columns[0] = newFee; // Update the fee in the array
                            String newLine = String.join(" ", columns); // Join the array back into a string
                            // Write the updated line back to the file, overwriting the existing content
                            try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, false))) {
                                writer.write(newLine);
                            } catch (IOException writeFail) {
                                writeFail.printStackTrace();
                            }
                            JOptionPane.showMessageDialog(feeDialog, "Rental fee successfully edited.", "Success", JOptionPane.INFORMATION_MESSAGE);
                        } catch (NumberFormatException ex) {
                            JOptionPane.showMessageDialog(feeDialog, "Invalid fee format. Please enter a valid number.", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                }
            } catch (IOException readfail) {
                readfail.printStackTrace();
            }
        });
        penaltyFeeButton.addActionListener(e -> {
            String currentPenalty = "";
            String filePath = "records/fee.txt";
            try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
                String line = reader.readLine();
                if (line != null) {
                    currentPenalty = line.split(" ")[1];
                }
                double fee = Double.parseDouble(currentPenalty);
                String formattedFee = String.format("%.2f", fee);
                String message = "The current penalty fee is: RM" + formattedFee + ". Do you want to change it?";
                Object[] options = {"Yes", "No"};
                
                int choice = JOptionPane.showOptionDialog(feeDialog, message, "Penalty Fee",
                        JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[1]);
                if (choice == JOptionPane.YES_OPTION) {
                    String newFee = JOptionPane.showInputDialog(feeDialog, "Enter the new penalty fee:");
                    if (newFee != null)
                    {
                        try {
                            new BigDecimal(newFee);
                            String[] columns = line.split(" ");
                            columns[1] = newFee;
                            String newLine = String.join(" ", columns);
                            try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, false))) {
                                writer.write(newLine);
                            } catch (IOException writeFail) {
                                writeFail.printStackTrace();
                            }
                            JOptionPane.showMessageDialog(feeDialog, "Penalty fee successfully edited.", "Success", JOptionPane.INFORMATION_MESSAGE);
                        } catch (NumberFormatException ex) {
                            JOptionPane.showMessageDialog(feeDialog, "Invalid fee format. Please enter a valid number.", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                }
            } catch (IOException readfail) {
                readfail.printStackTrace();
            }
        });
        viewTotalRevenueButton.addActionListener(e -> {
            String total = "";
            String ototal = "";
            String filePath = "records/fee.txt";
            try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
                String line = reader.readLine();
                if (line != null) {
                    ototal = line.split(" ")[2];
                    total = line.split(" ")[3];
                }
                double fee = Double.parseDouble(total);
                double ofee = Double.parseDouble(ototal);
                double totalfee = fee + ofee;
                String formattedFee = String.format("%.2f", fee);
                String oformattedFee = String.format("%.2f", ofee);
                String totalformattedFee = String.format("%.2f", totalfee);
                String message = "The Total Incurred Penalty Fees is RM " + oformattedFee + "\n" +
                                 "The Total Money Earned From Renting is RM " + formattedFee + "\n" +
                                 "Total Money Earned (including Penalty Fees) is RM " + totalformattedFee;
                JOptionPane.showMessageDialog(feeDialog, message, "Revenue Summary", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException readfail) {
                readfail.printStackTrace();
            }
        });
        feeDialog.setVisible(true);
    }

    //header
    private JPanel showHeader(JFrame menuFrame, String title, String username, Runnable backButtonAction) {
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
    
        menuFrame.add(titlePanel, BorderLayout.NORTH); // Add titlePanel to menuFrame
    
        return titlePanel; // Return titlePanel if needed
    }
    
    public void addCD(String [] newbookdata) {
        //add new book to the file
        String cdData = String.format("\"%s\" %.2f %d %s \"%s\"",
                newbookdata[0], Double.parseDouble(newbookdata[1]), Integer.parseInt(newbookdata[2]), newbookdata[3], newbookdata[4]);
        // Write the formatted CD data to the file
        try (BufferedWriter addin = new BufferedWriter(new FileWriter("records/CDs.txt", true))) {
            addin.write(cdData);
            addin.newLine();
            System.out.println("CD data added to file successfully.");
            return;
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error writing CD data to file.");
        }
    }

    
}