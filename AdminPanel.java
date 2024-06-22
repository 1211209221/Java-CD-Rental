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

    public AdminPanel(JFrame mainMenuFrame, String username){
        this.username = username;

        setTitle("Retro CD Rental System");
        setSize(1000, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel mainp = new JPanel(new BorderLayout());
        
        //Logout Button
        Runnable backButtonAction = () -> {
            int confirmDelete = JOptionPane.showConfirmDialog(null, "Are you sure you want to log out?", "Confirm Deletion", JOptionPane.YES_NO_OPTION);
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

        //Edit CD Button
        JButton editButton = new JButton("Edit CD");
        editButton.addActionListener(e-> {
            // Handle edit button action
            System.out.println("Edit button clicked");
        });
        buttonPanel.add(editButton);

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

        JPanel searchButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        ImageIcon searchIcon = new ImageIcon("image/search.png");
        JButton searchButton = new JButton(searchIcon);
        searchButton.setPreferredSize(new Dimension(24, 24));
        searchButton.setToolTipText("Search");
        searchButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        searchButton.setBackground(Color.WHITE); 

        searchButton.addActionListener(e -> {
            // Implement search functionality here
            System.out.println("Search button clicked");
        });
        searchButtonPanel.add(searchButton);

        String[] columnNames = {"CD Name", "Price (RM)", "Stock", "Genre", "Distributor"};
        List<String[]> data = readCDData();
        String[][] dataArray = data.toArray(new String[0][]);

        // Format prices with RM currency
        for (String[] row : dataArray) {
            row[1] = "RM " + row[1];
        }

        JTable table = new JTable(dataArray, columnNames);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        table.setDefaultEditor(Object.class, null);
        table.getColumnModel().getColumn(0).setPreferredWidth(300);
        table.getColumnModel().getColumn(1).setPreferredWidth(120);
        table.getColumnModel().getColumn(2).setPreferredWidth(80);
        table.getColumnModel().getColumn(3).setPreferredWidth(140);
        table.getColumnModel().getColumn(4).setPreferredWidth(240);
        //table.getColumnModel().getColumn(0).setPreferredWidth(20);

        JScrollPane scrollPane = new JScrollPane(table);
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

                        // Use a GridLayout with 8 rows and 2 columns for the infoPanel
                        JPanel infoPanel = new JPanel(new GridLayout(8, 2, 10, 5)); // Adjust rows, columns, and gaps
                        infoPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Add padding

                        int rowNum = 1;

                        infoPanel.add(new JLabel("No: "));
                        infoPanel.add(new JLabel(String.valueOf(rowNum++)));
                        infoPanel.add(new JLabel("CD Name: "));
                        infoPanel.add(new JLabel(cdName));
                        infoPanel.add(new JLabel("Price: "));
                        infoPanel.add(new JLabel(price));
                        infoPanel.add(new JLabel("Stock: "));
                        infoPanel.add(new JLabel(stock));
                        infoPanel.add(new JLabel("Genre: "));
                        infoPanel.add(new JLabel(genre));
                        infoPanel.add(new JLabel("Distributor: "));
                        infoPanel.add(new JLabel(distributor));

                        JButton updateButton = new JButton("Update");
                        JButton deleteButton = new JButton("Remove");

                        updateButton.addActionListener(new ActionListener() {
                            public void actionPerformed(ActionEvent e) {
                                int confirmUpdate = JOptionPane.showConfirmDialog(null, "Are you sure you want to update the changes?", "Confirm Updation", JOptionPane.YES_NO_OPTION);
                                if (confirmUpdate == JOptionPane.YES_OPTION) {
                                    
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