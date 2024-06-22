import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
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

public class CD_Rental_System extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private double ratePerDay = 0.1;

    public CD_Rental_System() {
        setTitle("Retro CD Rental System");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Create the main menu panel with padding
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(5, 1, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); // Add padding
        
        showHome(panel); // Pass mainMenuFrame instance
        add(panel);

        setVisible(true);
        
    }
    
    private JPanel showHome(JPanel panel) { // Corrected the parameter type to JPanel

        BufferedImage logo = null;
        try {
            logo = ImageIO.read(new File("image/Retro.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    
        panel.setLayout(new BorderLayout());
    
        // Header panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        JLabel welcomeLabel1 = new JLabel(new ImageIcon(logo), SwingConstants.CENTER);
        JLabel spacerLabel = new JLabel(" ");
        JLabel welcomeLabel2 = new JLabel("Retro CD Rental System", SwingConstants.CENTER);
        welcomeLabel2.setFont(new Font("SansSerif", Font.BOLD, 16));

        headerPanel.add(welcomeLabel1, BorderLayout.NORTH);
        headerPanel.add(spacerLabel, BorderLayout.CENTER);
        headerPanel.add(welcomeLabel2, BorderLayout.SOUTH);

    
        // Button panel with register, login, and exit buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JButton registerButton = new JButton("Register");
        JButton loginButton = new JButton("Login");
        JButton exitButton = new JButton("Exit");

        Dimension buttonSize = new Dimension(300, 100); // Example fixed size for all buttons
        registerButton.setMaximumSize(buttonSize);
        loginButton.setMaximumSize(buttonSize);
        exitButton.setMaximumSize(buttonSize);

        registerButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        exitButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        buttonPanel.add(registerButton);
        buttonPanel.add(Box.createVerticalStrut(10)); // Vertical spacing between buttons
        buttonPanel.add(loginButton);
        buttonPanel.add(Box.createVerticalStrut(10)); // Vertical spacing between buttons
        buttonPanel.add(exitButton);
    
        // Add panels to main panel
        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(buttonPanel, BorderLayout.CENTER);
    
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showRegisterDialog();
            }
        });
    
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showLoginDialog(new JFrame());
            }
        });
    
        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
    
        return panel;
    }

    private void showRegisterDialog() {
        JDialog registerDialog = new JDialog(this, "Register", true);
        registerDialog.setSize(300, 200);
        registerDialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Add padding

        JLabel usernameLabel = new JLabel("Username: ");
        usernameField = new JTextField();
        JLabel passwordLabel = new JLabel("Password: ");
        passwordField = new JPasswordField();

        JButton registerButton = new JButton("Register");

        panel.add(usernameLabel);
        panel.add(usernameField);
        panel.add(passwordLabel);
        panel.add(passwordField);
        panel.add(new JLabel()); // Empty cell to align the button
        panel.add(registerButton);

        registerDialog.add(panel);

        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());

                if (!username.isEmpty() && !password.isEmpty()) {
                    //create cust obj to check password and whether name has been taken or not
                    Customer cust = new Customer();
                    cust.setData(username, password);

                    if (cust.isUsernameTaken(username)) {
                        JOptionPane.showMessageDialog(registerDialog, "Username is already taken. Please choose another one.", "Error", JOptionPane.ERROR_MESSAGE);
                    } else if (!cust.isValidPassword(password)) {
                        JOptionPane.showMessageDialog(registerDialog, "Password must be at least 8 characters long and include at least one number, one special character, and one capital letter.", "Error", JOptionPane.ERROR_MESSAGE);
                    } else {
                        try {
                            // Check if the directory exists, if not, create it
                            File recordsDir = new File("records");
                            if (!recordsDir.exists()) {
                                recordsDir.mkdir();
                            }

                            // Create the customers.txt file inside records folder
                            File userFile = new File(recordsDir, "customers.txt");
                            if (!userFile.exists()) {
                                userFile.createNewFile();
                            }

                            // Write user data to the file
                            try (BufferedWriter writer = new BufferedWriter(new FileWriter(userFile, true))) {
                                writer.write(username + " " + password);
                                writer.newLine();
                                System.out.println("User registered: " + username + " " + password);  // Debug print
                                System.out.println("Data saved in: " + userFile.getAbsolutePath());  // Debug print for file path
                                JOptionPane.showMessageDialog(registerDialog, "User registered!");
                                registerDialog.dispose();
                            } catch (IOException ioException) {
                                ioException.printStackTrace();
                                JOptionPane.showMessageDialog(registerDialog, "Error registering user.", "Error", JOptionPane.ERROR_MESSAGE);
                            }
                        } catch (IOException ioException) {
                            ioException.printStackTrace();
                            JOptionPane.showMessageDialog(registerDialog, "Error creating user file.", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                } else {
                    JOptionPane.showMessageDialog(registerDialog, "Please fill in both fields.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        registerDialog.setVisible(true);
    }
    
    private String showLoginDialog(JFrame mainMenuFrame) {
        JDialog loginDialog = new JDialog(this, "Login", true);
        loginDialog.setSize(300, 200);
        loginDialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Add padding

        JLabel usernameLabel = new JLabel("Username: ");
        usernameField = new JTextField();
        JLabel passwordLabel = new JLabel("Password: ");
        passwordField = new JPasswordField();

        JButton loginButton = new JButton("Login");

        panel.add(usernameLabel);
        panel.add(usernameField);
        panel.add(passwordLabel);
        panel.add(passwordField);
        panel.add(new JLabel()); // Empty cell to align the button
        panel.add(loginButton);

        loginDialog.add(panel);

            loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());

                //create an obj cust
                Customer c1 = new Customer();
                c1.setData(username, password);
                c1.passFilename();//to authenticate

                boolean isuser = c1.passFilename();
                boolean isadmin = false;

                if (!isuser) {
                    //create admin obj
                    Admin a1 = new Admin();
                    a1.setData(username, password);
                    isadmin = a1.passFilename();
                }

                if (isuser) {
                    JOptionPane.showMessageDialog(loginDialog, "Login successful! Redirecting to main menu...");
                    loginDialog.dispose();
                    mainMenuFrame.setSize(900, 400);
                    mainMenuFrame.setLocationRelativeTo(null);
                    showMainMenu(mainMenuFrame, username);
                    setVisible(false);
                } 
                else if(isadmin){
                    JOptionPane.showMessageDialog(loginDialog, "Login successful! Redirecting to admin panel...");
                    loginDialog.dispose();
                    mainMenuFrame.setSize(900, 400);
                    mainMenuFrame.setLocationRelativeTo(null);
                    dispose();
                    
                    //go to admin panel
                    AdminPanel adminPanel = new AdminPanel(mainMenuFrame, username);
                    adminPanel.setVisible(true);
                }
                else {
                    JOptionPane.showMessageDialog(loginDialog, "Invalid username or password.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        loginDialog.setVisible(true);
        return usernameField.getText(); // Return the username
    }

    private void showMainMenu(JFrame mainMenuFrame, String username) {
        mainMenuFrame.getContentPane().removeAll(); // Clear current content
        mainMenuFrame.revalidate(); // Refresh frame
        mainMenuFrame.repaint(); // Repaint frame
    
        // Create panel for buttons with FlowLayout to center buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 70, 70));
    
        // Create buttons with fixed size
        JButton catalogButton = createImageButton("Catalog", "image/CD_icon.png");
        JButton cartButton = createImageButton("Cart", "image/cart_icon.png");
        JButton rentedButton = createImageButton("Rented", "image/rentedCDs_icon.png");
    
        // Set the preferred size of the buttons
        Dimension buttonSize = new Dimension(160, 170);
        catalogButton.setPreferredSize(buttonSize);
        cartButton.setPreferredSize(buttonSize);
        rentedButton.setPreferredSize(buttonSize);
    
        // Add buttons to panel
        buttonPanel.add(catalogButton);
        buttonPanel.add(cartButton);
        buttonPanel.add(rentedButton);
    
        // Add action listeners
        catalogButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JPanel catalogPanel = createCatalogPanel(mainMenuFrame, username); // Pass mainMenuFrame instance
                mainMenuFrame.getContentPane().removeAll(); // Clear previous content
                mainMenuFrame.add(catalogPanel, BorderLayout.CENTER); // Add catalog panel
                mainMenuFrame.revalidate(); // Refresh frame
                mainMenuFrame.repaint(); // Repaint frame
            }
        });
    
        cartButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JPanel cartPanel = createCartPanel(mainMenuFrame, username); // Pass mainMenuFrame instance
                mainMenuFrame.getContentPane().removeAll(); // Clear previous content
                mainMenuFrame.add(cartPanel, BorderLayout.CENTER); // Add catalog panel
                mainMenuFrame.revalidate(); // Refresh frame
                mainMenuFrame.repaint(); // Repaint frame
            }
        });
    
        rentedButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JPanel rentedPanel = RentedPanel(mainMenuFrame, username); // Pass mainMenuFrame instance
                mainMenuFrame.getContentPane().removeAll(); // Clear previous content
                mainMenuFrame.add(rentedPanel, BorderLayout.CENTER); // Add catalog panel
                mainMenuFrame.revalidate(); // Refresh frame
                mainMenuFrame.repaint(); // Repaint frame
            }
        });
    
        Runnable backButtonAction = () -> {
            int confirmDelete = JOptionPane.showConfirmDialog(null, "Are you sure you want to log out?", "Confirm Deletion", JOptionPane.YES_NO_OPTION);
            if (confirmDelete == JOptionPane.YES_OPTION) {
                mainMenuFrame.dispose(); // Close the current frame
                CD_Rental_System newSystem = new CD_Rental_System(); // Create a new instance of CD_Rental_System
                newSystem.setVisible(true); // Make the new frame visible
            }
        };
    
        showHeader(mainMenuFrame, "Main Menu", username, backButtonAction);
    
        // Add button panel to main frame
        mainMenuFrame.add(buttonPanel, BorderLayout.CENTER);
    
        mainMenuFrame.setVisible(true);
    }
    
    private JButton createImageButton(String text, String imagePath) {
        JButton button = new JButton(text);
        ImageIcon icon = new ImageIcon(imagePath);
    
        // Resize the image to fit the button
        Image image = icon.getImage();
        Image resizedImage = image.getScaledInstance(140, 140, java.awt.Image.SCALE_SMOOTH); // Set desired image size
        icon = new ImageIcon(resizedImage);
    
        button.setIcon(icon);
        button.setVerticalTextPosition(SwingConstants.BOTTOM);
        button.setHorizontalTextPosition(SwingConstants.CENTER);
    
        // Optional: customize button size and layout
        button.setPreferredSize(new Dimension(160, 160));
        button.setMargin(new Insets(5, 5, 5, 5));
        button.setFocusable(false); // Remove focus outline
        button.setFont(new Font("Arial", Font.BOLD, 16));

        button.setBackground(Color.WHITE);
        button.setForeground(Color.BLACK);
        button.setOpaque(true);
        button.setBorderPainted(false);
        button.setFocusPainted(false);

        
    
        return button;
    }
    
    
    private static void showHeader(JFrame frame, String title, String username, Runnable backButtonAction) {
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(Color.WHITE);

        // Create and add button to the left of the title
        ImageIcon icon = new ImageIcon("image/logout.png");
        JButton backButton = new JButton(icon);
        backButton.setPreferredSize(new Dimension(30, 30));
        backButton.setToolTipText("Log out");
        backButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        backButton.setBackground(Color.WHITE); 
        backButton.addActionListener(e -> backButtonAction.run()); // Trigger the provided function when the button is clicked
        titlePanel.add(backButton, BorderLayout.WEST);

        JLabel pageTitleLabel = new JLabel(title, SwingConstants.CENTER);
        pageTitleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titlePanel.add(pageTitleLabel, BorderLayout.CENTER);

        ImageIcon userIcon = new ImageIcon("image/usericon.png");
        JLabel welcomeLabel = new JLabel(username, userIcon, SwingConstants.RIGHT);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 13));
        titlePanel.add(welcomeLabel, BorderLayout.EAST);

        titlePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        frame.add(titlePanel, BorderLayout.NORTH);
    }
    
    private JPanel headerPanel(JFrame frame, String title, String username, Runnable backButtonAction) {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
    
        // Load and resize icon
        ImageIcon icon = new ImageIcon("image/back.png");
        Image image = icon.getImage(); // Transform ImageIcon to Image
        Image resizedImage = image.getScaledInstance(30, 30, java.awt.Image.SCALE_SMOOTH); // Resize image to fit button
        ImageIcon resizedIcon = new ImageIcon(resizedImage); // Transform Image back to ImageIcon
    
        JButton backButton = new JButton(resizedIcon);
        backButton.setPreferredSize(new Dimension(30, 30));
        backButton.setToolTipText("Log out");
        backButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        backButton.setBackground(Color.WHITE); 
        backButton.addActionListener(e -> backButtonAction.run()); // Trigger the provided function when the button is clicked
        headerPanel.add(backButton, BorderLayout.WEST);
    
        JLabel pageTitleLabel = new JLabel(title, SwingConstants.CENTER);
        pageTitleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        headerPanel.add(pageTitleLabel, BorderLayout.CENTER);
    
        ImageIcon userIcon = new ImageIcon("image/usericon.png");
        JLabel welcomeLabel = new JLabel(username, userIcon, SwingConstants.RIGHT);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 13));
        headerPanel.add(welcomeLabel, BorderLayout.EAST);
    
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    
        frame.add(headerPanel, BorderLayout.NORTH);
    
        return headerPanel;
    }
    
    private float getCDPrice(String cdName) {
        float cdPrice = 0;
        List<String[]> cdData = readCDData();

        for (String[] cd : cdData) {
            String cdNameFromFile = cd[0];
            if (cdName.equals(cdNameFromFile)) {
                cdPrice = Float.parseFloat(cd[1]);
                
                break;
            }
        }

        return cdPrice;
    }
    
    private int getCDStock(String cdName) {
        int stockQuantity = 0;
        List<String[]> cdData = readCDData();

        for (String[] cd : cdData) {
            String cdNameFromFile = cd[0];
            if (cdName.equals(cdNameFromFile)) {
                try {
                    stockQuantity = Integer.parseInt(cd[2]);
                } catch (NumberFormatException e) {
                    System.err.println("Invalid stock quantity format for CD: " + cdName);
                    e.printStackTrace();
                }
                break;
            }
        }

        return stockQuantity;
    }

    private JPanel createCatalogPanel(JFrame mainMenuFrame, String username) {
        Runnable backButtonAction = () -> {
            mainMenuFrame.getContentPane().removeAll(); // Clear current content
            showMainMenu(mainMenuFrame, username); // Pass the username when returning to the main menu
            mainMenuFrame.revalidate(); // Refresh frame
            mainMenuFrame.repaint(); // Repaint frame
        };
        

        JPanel catalogPanel = new JPanel(new BorderLayout());

        JPanel headerPanel = headerPanel(mainMenuFrame, "Catalog", username, backButtonAction);

        catalogPanel.add(headerPanel, BorderLayout.NORTH); // Add back button to the top

        // Add CD table
        String[] columnNames = {"CD Name", "Price (RM)", "Stock", "Genre", "Distributor"};
        List<String[]> data = readCDData();
        String[][] dataArray = data.toArray(new String[0][]);

        // Format prices with RM currency
        for (String[] row : dataArray) {
            row[1] = "RM " + row[1];
        }

        JTable table = new JTable(dataArray, columnNames);
        JScrollPane scrollPane = new JScrollPane(table);
        catalogPanel.add(scrollPane, BorderLayout.CENTER);

        // Set column sizes
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        table.setDefaultEditor(Object.class, null);
        table.getColumnModel().getColumn(0).setPreferredWidth(350); // CD Name column size increased
        table.getColumnModel().getColumn(1).setPreferredWidth(120); // Price column size adjusted
        table.getColumnModel().getColumn(2).setPreferredWidth(80); // Stock column size
        table.getColumnModel().getColumn(3).setPreferredWidth(100); // Genre column size
        table.getColumnModel().getColumn(4).setPreferredWidth(233); // Distributor column size

        // Add selection listener to the table
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

                        // Fields for user input
                        JTextField numCDsField = new JTextField();
                        JTextField numDaysField = new JTextField();

                        // Fields for user input
                        infoPanel.add(new JLabel("Quantity: "));
                        infoPanel.add(numCDsField);
                        infoPanel.add(new JLabel("Days to rent: "));
                        infoPanel.add(numDaysField);

                        JButton addtoCartButton = new JButton("Add to Cart");
                        infoPanel.add(new JLabel()); // Empty cell for alignment
                        infoPanel.add(addtoCartButton);

                        // Add action listener to the addtoCartButton
                        addtoCartButton.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                try {
                                    // Retrieve quantity and days rented
                                    int quantity = Integer.parseInt(numCDsField.getText());
                                    int daysRented = Integer.parseInt(numDaysField.getText());

                                    // Check if either quantity or days rented is zero
                                    if (quantity <= 0 || daysRented <= 0) {
                                        JOptionPane.showMessageDialog(cdInfoDialog, "Quantity and days rented must be greater than 0.", "Error", JOptionPane.ERROR_MESSAGE);
                                        return; // Exit the method
                                    }

                                    // Check if the requested quantity exceeds the available stock
                                    int availableStock = Integer.parseInt(stock);
                                    if (quantity > availableStock) {
                                        JOptionPane.showMessageDialog(cdInfoDialog, "Requested quantity exceeds available stock.", "Error", JOptionPane.ERROR_MESSAGE);
                                        return; // Exit the method
                                    }

                                    // Create the directory for the user's cart if it doesn't exist
                                    File cartDirectory = new File("records/cart/");
                                    if (!cartDirectory.exists()) {
                                        cartDirectory.mkdirs(); // Create the directory and its parents if they don't exist
                                    }

                                    // Create the file for the user's cart
                                    File cartFile = new File(cartDirectory, username + ".txt");

                                    // Write CD information to the cart file
                                    try (BufferedWriter writer = new BufferedWriter(new FileWriter(cartFile, true))) {
                                        writer.write('"' + cdName + '"' + " " + quantity + " " + daysRented); // Write CD name, quantity, and days rented
                                        writer.newLine(); // Add a new line for the next item
                                        JOptionPane.showMessageDialog(cdInfoDialog, "CD added to cart!");
                                    } catch (IOException ex) {
                                        ex.printStackTrace();
                                        JOptionPane.showMessageDialog(cdInfoDialog, "Error adding CD to cart.", "Error", JOptionPane.ERROR_MESSAGE);
                                    }
                                } catch (NumberFormatException ex) {
                                    JOptionPane.showMessageDialog(cdInfoDialog, "Please enter valid integer values.", "Error", JOptionPane.ERROR_MESSAGE);
                                }
                            }
                        });
                        table.clearSelection();

                        cdInfoDialog.add(infoPanel);
                        cdInfoDialog.setVisible(true);
                    }
                }
            }
        });

        return catalogPanel;
    }
    
    private JLabel cartTotalQuantityValueLabel;
    private JLabel cartTotalPriceValueLabel;
    
    private JPanel createCartPanel(JFrame mainMenuFrame, String username) {
        JPanel cartPanel = new JPanel(new BorderLayout());
        
        Runnable backButtonAction = () -> {
            mainMenuFrame.getContentPane().removeAll(); // Clear current content
            showMainMenu(mainMenuFrame, username); // Pass the username when returning to the main menu
            mainMenuFrame.revalidate(); // Refresh frame
            mainMenuFrame.repaint(); // Repaint frame
        };

        // Initialize instance variables
        cartTotalQuantityValueLabel = new JLabel();
        cartTotalPriceValueLabel = new JLabel();

        JPanel headerPanel = headerPanel(mainMenuFrame, "Cart", username, backButtonAction);
        cartPanel.add(headerPanel, BorderLayout.NORTH); // Add back button to the top

        // Table to display CDs information
        String[] columnNames = {"CD Name", "Quantity", "Days Rented", "Base Price", "Total Price"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);
        JTable table = new JTable(model);
        table.setDefaultEditor(Object.class, null);

        // Add a selection listener to the table
        table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    int selectedRow = table.getSelectedRow();
                    if (selectedRow != -1) { // If a row is selected
                        openEditDialog(selectedRow, model, username); // Pass the selected row index
                        table.clearSelection(); // Deselect the row after opening the edit dialog
                    }
                }
            }
        });

        // Scroll pane for table
        JScrollPane scrollPane = new JScrollPane(table);
        

        String ratePerDayFormatted = String.format("%.2f", ratePerDay);

        // Create the disclaimer label with the formatted rate per day
        JLabel disclaimerLabel = new JLabel("<html>**DISCLAIMER<br>Total fee is calculated based on the base price and additional charges. Additional charges are RM " + ratePerDayFormatted + "/day for each book rented.<br></html>");
        // Panel to hold the table and disclaimer label
        JPanel tableWithDisclaimerPanel = new JPanel(new BorderLayout());

        // Add table to the tableWithDisclaimerPanel
        tableWithDisclaimerPanel.add(scrollPane, BorderLayout.CENTER);

        // Add disclaimer label to the bottom of the tableWithDisclaimerPanel
        tableWithDisclaimerPanel.add(disclaimerLabel, BorderLayout.SOUTH);

        // Add tableWithDisclaimerPanel to the cartPanel
        cartPanel.add(tableWithDisclaimerPanel, BorderLayout.CENTER);

        // Panel for summary information
        JPanel summaryPanel = new JPanel(new GridLayout(4, 1));
        JLabel summaryLabel = new JLabel("");

        // Panel for total quantity
        JPanel totalQuantityPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel totalQuantityTextLabel = new JLabel("Total Quantity:");
        totalQuantityPanel.add(totalQuantityTextLabel);
        totalQuantityPanel.add(cartTotalQuantityValueLabel);

        // Panel for total price
        JPanel totalPricePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel totalPriceTextLabel = new JLabel("Total Price:");
        totalPricePanel.add(totalPriceTextLabel);
        totalPricePanel.add(cartTotalPriceValueLabel);

        JButton rentButton = new JButton("Rent");
        rentButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                rentCDs(username);
                // Refresh the cart panel after renting
                mainMenuFrame.getContentPane().removeAll();
                mainMenuFrame.getContentPane().add(createCartPanel(mainMenuFrame, username));
                mainMenuFrame.revalidate();
                mainMenuFrame.repaint();
            }
        });


        // Retrieve CDs information from file and populate table
        int totalQuantity = 0;
        double totalPrice = 0.0;
        try {
            File cartFile = new File("records/cart/" + username + ".txt");
            BufferedReader reader = new BufferedReader(new FileReader(cartFile));
            String line;
            while ((line = reader.readLine()) != null) {
                // Splitting the line into parts based on spaces, but keep the CD name within double quotations intact
                String[] parts = line.split("\"?( |$)(?=(([^\"]*\"){2})*[^\"]*$)\"?");
                if (parts.length == 3) {
                    // Trim extra spaces and remove surrounding double quotations from CD name
                    parts[0] = parts[0].trim().replaceAll("^\"|\"$", "");

                    // Convert quantity and days to integers
                    int quantity = Integer.parseInt(parts[1]);
                    int days = Integer.parseInt(parts[2]);

                    // Calculate total cost
                    double price = getCDPrice(parts[0]);
                    double totalCost = ((price + (days * ratePerDay)) * quantity);

                    // Add the calculated total cost and quantity to the parts array
                    String[] updatedParts = new String[5];
                    System.arraycopy(parts, 0, updatedParts, 0, 3);
                    updatedParts[3] = String.format("RM %.2f", price);
                    updatedParts[4] = String.format("RM %.2f", totalCost);

                    // Update total quantity and price
                    totalQuantity += quantity;
                    totalPrice += totalCost;

                    // Adding the updated parts to the table model
                    model.addRow(updatedParts);
                }
            }
            reader.close();
        } catch (IOException ex) {
        }
        
        // Set total quantity and price labels
        cartTotalQuantityValueLabel.setText(String.valueOf(totalQuantity));
        cartTotalPriceValueLabel.setText(String.format("RM %.2f", totalPrice));

        // Add components to summary panel
        summaryPanel.add(summaryLabel);
        summaryPanel.add(totalQuantityPanel);
        summaryPanel.add(totalPricePanel);
        summaryPanel.add(rentButton);
        
        // Add summary panel to cart panel
        cartPanel.add(summaryPanel, BorderLayout.SOUTH);

        return cartPanel;
    }

    
    // Method to open a dialog for editing quantity and days rented
    private void openEditDialog(int row, DefaultTableModel model, String username) {
        JPanel editPanel = new JPanel();
        editPanel.setLayout(new GridLayout(5, 2));

        JLabel nameLabel = new JLabel("CD Name:");
        JLabel quantityLabel = new JLabel("Quantity:");
        JLabel daysLabel = new JLabel("Days Rented:");

        JTextField quantityField = new JTextField(5);
        JTextField daysField = new JTextField(5);

        // Get CD name from the table model using the row index
        String cdName = (String) model.getValueAt(row, 0);

        // Set initial values for quantity and days rented
        if (row != -1) {
            quantityField.setText((String) model.getValueAt(row, 1));
            daysField.setText((String) model.getValueAt(row, 2));
        }

        editPanel.add(nameLabel);
        editPanel.add(new JLabel(cdName));
        editPanel.add(quantityLabel);
        editPanel.add(quantityField);
        editPanel.add(daysLabel);
        editPanel.add(daysField);

        // Add delete button
        JButton deleteButton = new JButton("Remove");
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int confirmDelete = JOptionPane.showConfirmDialog(null, "Are you sure you want to remove this item from your cart?", "Confirm Deletion", JOptionPane.YES_NO_OPTION);
                if (confirmDelete == JOptionPane.YES_OPTION) {
                    if (row != -1) {
                        model.removeRow(row);
                        // Rewrite the file without the deleted row
                        try {
                            File cartFile = new File("records/cart/" + username + ".txt");
                            FileWriter writer = new FileWriter(cartFile);
                            for (int i = 0; i < model.getRowCount(); i++) {
                                String cd = (String) model.getValueAt(i, 0);
                                String quantity = (String) model.getValueAt(i, 1);
                                String days = (String) model.getValueAt(i, 2);
                                writer.write("\"" + cd + "\" " + quantity + " " + days + "\n");
                            }
                            writer.close();
                        } catch (IOException ex) {
                            ex.printStackTrace();
                            JOptionPane.showMessageDialog(null, "Error updating cart information.", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                        // Update the summary after deletion
                        updateSummary(model);
                        // Close the dialog after deleting
                        JOptionPane.getRootFrame().dispose();
                        JOptionPane.showMessageDialog(null, "Item successfully removed from cart!");
                    }
                }
            }
        });

        // Add an empty placeholder for alignment
        editPanel.add(new JLabel());
        editPanel.add(deleteButton);

        while (true) {
            int result = JOptionPane.showConfirmDialog(null, editPanel, "Edit Item",
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

            if (result == JOptionPane.OK_OPTION) {
                try {
                    int quantity = Integer.parseInt(quantityField.getText());
                    int days = Integer.parseInt(daysField.getText());

                    if (quantity <= 0 || days <= 0) {
                        JOptionPane.showMessageDialog(null, "Quantity and Days Rented must be greater than zero.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
                    } else {
                        int stockQuantity = getCDStock(cdName);

                        if (quantity > stockQuantity) {
                            JOptionPane.showMessageDialog(null, "Not enough stock available! " + stockQuantity + " left!");
                            continue; // Restart the loop to allow the user to enter valid input
                        }

                        JOptionPane.showMessageDialog(null, "Item updated!");
                        // Update the table model with the edited information
                        if (row != -1) {
                            model.setValueAt(quantityField.getText(), row, 1);
                            model.setValueAt(daysField.getText(), row, 2);
                            // Recalculate the price and total price
                            double price = getCDPrice(cdName);
                            double totalCost = ((price + (ratePerDay * days)) * quantity);
                            model.setValueAt(String.format("RM %.2f", price), row, 3);
                            model.setValueAt(String.format("RM %.2f", totalCost), row, 4);
                            // Rewrite the file with the updated information
                            try {
                                File cartFile = new File("records/cart/" + username + ".txt");
                                FileWriter writer = new FileWriter(cartFile);
                                for (int i = 0; i < model.getRowCount(); i++) {
                                    String cd = (String) model.getValueAt(i, 0);
                                    String updatedQuantity = (String) model.getValueAt(i, 1);
                                    String updatedDays = (String) model.getValueAt(i, 2);
                                    writer.write("\"" + cd + "\" " + updatedQuantity + " " + updatedDays + "\n");
                                }
                                writer.close();
                            } catch (IOException ex) {
                                ex.printStackTrace();
                                JOptionPane.showMessageDialog(null, "Error updating cart information.", "Error", JOptionPane.ERROR_MESSAGE);
                            }
                            // Update the summary after editing
                            updateSummary(model);
                            break; // Exit the loop after successful update
                        }
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null, "Please enter valid numeric values for Quantity and Days Rented.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                // User clicked cancel or closed the dialog
                break; // Exit the loop
            }
        }
    }

    private void updateSummary(DefaultTableModel model) {
        int totalQuantity = 0;
        double totalPrice = 0.0;

        for (int i = 0; i < model.getRowCount(); i++) {
            int quantity = Integer.parseInt((String) model.getValueAt(i, 1));
            int days = Integer.parseInt((String) model.getValueAt(i, 2));
            double price = Double.parseDouble(((String) model.getValueAt(i, 3)).substring(3)); // Extract price from formatted string

            // Calculate total cost including daily rental rate
            double totalCost = ((price + (ratePerDay * days)) * quantity);

            totalQuantity += quantity;
            totalPrice += totalCost;
        }

        // Update the summary labels
        cartTotalQuantityValueLabel.setText(String.valueOf(totalQuantity));
        cartTotalPriceValueLabel.setText(String.format("RM %.2f", totalPrice));
    }

    private void rentCDs(String username) {
    File cartDir = new File("records/cart");
    File rentedDir = new File("records/rented");
    if (!rentedDir.exists()) {
        rentedDir.mkdir();
    }

    File userCartFile = new File(cartDir, username + ".txt");
    File userRentedFile = new File(rentedDir, username + ".txt");
    List<String> rentedCDs = new ArrayList<>();

    // Read CDs from user's cart
    try (BufferedReader reader = new BufferedReader(new FileReader(userCartFile))) {
        String line;
        while ((line = reader.readLine()) != null) {
            // Splitting the line to get the CD name, quantity, and days rented
            String[] parts = line.split("\"?( |$)(?=(([^\"]*\"){2})*[^\"]*$)\"?");
            parts[0] = parts[0].trim().replaceAll("^\"|\"$", "");
            String cdName = parts[0];
            int quantity = Integer.parseInt(parts[1]);
            int daysRented = Integer.parseInt(parts[2]);

            // Calculate due date by adding days rented to the current date
            LocalDate dueDate = LocalDate.now().plusDays(daysRented);

            if (cdName.contains(" ")) {
                cdName = "\"" + cdName + "\"";
            }

            // Construct the line to be written to rented file (CD name, quantity, due date)
            String rentedLine = cdName + " " + quantity + " " + dueDate;

            // Add the constructed line to the list of rented CDs
            rentedCDs.add(rentedLine);
        }
    } catch (IOException e) {
        JOptionPane.showMessageDialog(this, "An error occurred while reading the cart file.", "Error", JOptionPane.ERROR_MESSAGE);
        e.printStackTrace();
        return;
    }

    if (rentedCDs.isEmpty()) {
        JOptionPane.showMessageDialog(this, "Cart is empty!", "Error", JOptionPane.ERROR_MESSAGE);
        return;
    }

    // Write rented CDs to user's rented file
    try (BufferedWriter writer = new BufferedWriter(new FileWriter(userRentedFile, true))) {
        for (String rentedCD : rentedCDs) {
            writer.write(rentedCD);
            writer.newLine();
        }
    } catch (IOException e) {
        JOptionPane.showMessageDialog(this, "An error occurred while storing the rented CDs into file.", "Error", JOptionPane.ERROR_MESSAGE);
        e.printStackTrace();
        return;
    }

    updateInventory(rentedCDs);

    // Clear the cart file
    try (BufferedWriter writer = new BufferedWriter(new FileWriter(userCartFile))) {
        writer.write("");
    } catch (IOException e) {
        JOptionPane.showMessageDialog(this, "An error occurred while clearing the cart.", "Error", JOptionPane.ERROR_MESSAGE);
        e.printStackTrace();
    }

    JOptionPane.showMessageDialog(this, "CDs rented successfully!");
}    

private void updateInventory(List<String> rentedCDs) {
    File cdsFile = new File("records/CDs.txt");

    try (BufferedReader reader = new BufferedReader(new FileReader(cdsFile))) {
        List<String> updatedLines = new ArrayList<>();
        String line;
        while ((line = reader.readLine()) != null) {
            String[] parts = line.split("\"?( |$)(?=(([^\"]*\"){2})*[^\"]*$)\"?");
            parts[0] = parts[0].trim().replaceAll("^\"|\"$", "");
            String cdName = parts[0];
            double price = Double.parseDouble(parts[1]);
            int stock = Integer.parseInt(parts[2]);
            String genre = parts[3];
            String distributor = parts[4];

            // Check if the CD is rented and update its stock
            for (String rentedCD : rentedCDs) {
                String[] rentedParts = rentedCD.split("\"?( |$)(?=(([^\"]*\"){2})*[^\"]*$)\"?");
                rentedParts[0] = rentedParts[0].trim().replaceAll("^\"|\"$", "");
                String rentedCDName = rentedParts[0];
                int quantityRented = Integer.parseInt(rentedParts[1]);

                if (cdName.equals(rentedCDName)) {
                    stock -= quantityRented; // Deduct the rented quantity from stock
                }
            }
            
            // Construct the updated line
            String updatedLine = "\"" + cdName + "\" " + String.format("%.2f", price) + " " + stock + " " + genre + " \"" + distributor + "\"";

            // Add the updated line to the list
            updatedLines.add(updatedLine);
        }

        // Rewrite the contents of the CDs file with updated stock
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(cdsFile))) {
            for (String updatedLine : updatedLines) {
                writer.write(updatedLine);
                writer.newLine();
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error writing to the CDs file.", "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    } catch (IOException e) {
        JOptionPane.showMessageDialog(this, "Error reading the CDs file.", "Error", JOptionPane.ERROR_MESSAGE);
        e.printStackTrace();
    }
}

private JPanel RentedPanel(JFrame mainMenuFrame, String username) {
    JPanel rentedPanel = new JPanel(new BorderLayout());

    Runnable backButtonAction = () -> {
        mainMenuFrame.getContentPane().removeAll(); // Clear current content
        showMainMenu(mainMenuFrame, username); // Pass the username when returning to the main menu
        mainMenuFrame.revalidate(); // Refresh frame
        mainMenuFrame.repaint(); // Repaint frame
    };

    JPanel headerPanel = headerPanel(mainMenuFrame, "Rented CDs", username, backButtonAction);
    rentedPanel.add(headerPanel, BorderLayout.NORTH); // Add back button to the top

    // Table to display rented CDs information
    String[] columnNames = {"CD Name", "Quantity", "Due Date", "Status"};
    DefaultTableModel model = new DefaultTableModel(columnNames, 0);
    JTable table = new JTable(model);
    table.setDefaultEditor(Object.class, null);

    // Custom renderer for center alignment and coloring the status
    DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer() {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            if (column == 3) { // Status column
                String status = (String) value;
                if ("Overdue".equals(status)) {
                    c.setForeground(Color.RED);
                } else {
                    c.setForeground(Color.GREEN);
                }
            } else {
                c.setForeground(Color.BLACK); // Default color for other columns
            }
            ((JLabel) c).setHorizontalAlignment(SwingConstants.CENTER); // Center align
            return c;
        }
    };

    // Apply the custom renderer to all columns
    for (int i = 0; i < table.getColumnCount(); i++) {
        table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
    }

    // Add a selection listener to the table
    table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
        @Override
        public void valueChanged(ListSelectionEvent e) {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = table.getSelectedRow();
                if (selectedRow != -1) { // If a row is selected
                    openReturnDialog(selectedRow, model, username); // Pass the selected row index
                    table.clearSelection(); // Deselect the row after opening the return dialog
                }
            }
        }
    });

    // Scroll pane for table
    JScrollPane scrollPane = new JScrollPane(table);

    // Panel to hold the table
    JPanel tablePanel = new JPanel(new BorderLayout());
    tablePanel.add(scrollPane, BorderLayout.CENTER);

    // Retrieve rented CDs information from file and populate table
    try {
        File rentedFile = new File("records/rented/" + username + ".txt");
        BufferedReader reader = new BufferedReader(new FileReader(rentedFile));
        String line;
        while ((line = reader.readLine()) != null) {
            // Splitting the line to get the CD name, quantity, and due date
            String[] parts = line.split("\"?( |$)(?=(([^\"]*\"){2})*[^\"]*$)\"?");
            parts[0] = parts[0].trim().replaceAll("^\"|\"$", "");
            String cdName = parts[0];
            int quantity = Integer.parseInt(parts[1]);
            LocalDate dueDate = LocalDate.parse(parts[2]);

            String status;
            // Determine the status based on the due date and current date
            LocalDate currentDate = LocalDate.now();
            if (currentDate.isAfter(dueDate)) {
                status = "Overdue";
            } else {
                status = "Active";
            }

            // Add the rented CD information to the table model
            model.addRow(new Object[]{cdName, quantity, dueDate, status});
        }
        reader.close();
    } catch (IOException ex) {
      
    } catch (DateTimeParseException ex) {
        JOptionPane.showMessageDialog(this, "Error parsing the due date in the rented CDs file for user: " + username, "Error", JOptionPane.ERROR_MESSAGE);
        ex.printStackTrace();
    } catch (ArrayIndexOutOfBoundsException ex) {
        JOptionPane.showMessageDialog(this, "Error processing the rented CDs file format for user: " + username, "Error", JOptionPane.ERROR_MESSAGE);
        ex.printStackTrace();
    }

    // Add tablePanel to the rentedPanel
    rentedPanel.add(tablePanel, BorderLayout.CENTER);

    JLabel disclaimerLabel = new JLabel("<html>**DISCLAIMER<br>Select the CDs to return the specific CD(s).<br> Late returns are fined RM 0.50/day for each CD rented.<br><br></html>");
    JPanel DisclaimerPanel = new JPanel(new BorderLayout());
    DisclaimerPanel.add(disclaimerLabel);

    rentedPanel.add(DisclaimerPanel, BorderLayout.SOUTH);

    return rentedPanel;
}

private void openReturnDialog(int selectedRow, DefaultTableModel model, String username) {
    String cdName = (String) model.getValueAt(selectedRow, 0);
    int quantity = (int) model.getValueAt(selectedRow, 1);
    LocalDate dueDate = (LocalDate) model.getValueAt(selectedRow, 2);
    String status = (String) model.getValueAt(selectedRow, 3);

    LocalDate currentDate = LocalDate.now();
    int overdueDays = Period.between(dueDate, currentDate).getDays();
    double penaltyFee = 0.5 * overdueDays;

    String message;
    if ("Overdue".equals(status)) {
        message = String.format("Overdue by %d day(s). Total penalty fee is RM %.2f. Pay penalty fee and return %d copy/copies of '%s'?", overdueDays, penaltyFee, quantity, cdName);
    } else {
        message = String.format("Do you want to return %d copies of '%s'?", quantity, cdName);
    }

    int confirm = JOptionPane.showConfirmDialog(null, message, "Confirm Return", JOptionPane.YES_NO_OPTION);
    if (confirm == JOptionPane.YES_OPTION) {
        model.removeRow(selectedRow);
        updateInventoryAfterReturn(cdName, quantity);
        removeRentedRecord(username, cdName, quantity);
    }
}

private void updateInventoryAfterReturn(String cdName, int quantity) {
    File cdsFile = new File("records/CDs.txt");
    List<String> updatedLines = new ArrayList<>();

    try (BufferedReader reader = new BufferedReader(new FileReader(cdsFile))) {
        String line;
        while ((line = reader.readLine()) != null) {
            String[] parts = line.split("\"?( |$)(?=(([^\"]*\"){2})*[^\"]*$)\"?");
            parts[0] = parts[0].trim().replaceAll("^\"|\"$", "");
            String inventoryCDName = parts[0];
            double price = Double.parseDouble(parts[1]);
            int stock = Integer.parseInt(parts[2]);
            String genre = parts[3];
            String author = parts[4];

            if (inventoryCDName.equals(cdName)) {
                stock += quantity; // Add the returned quantity back to stock
            }

            String updatedLine = "\"" + inventoryCDName + "\" " + String.format("%.2f", price) + " " + stock + " " + genre + " \"" + author + "\"";
            updatedLines.add(updatedLine);
        }
    } catch (IOException ex) {
        JOptionPane.showMessageDialog(this, "Error reading the CDs file.", "Error", JOptionPane.ERROR_MESSAGE);
        ex.printStackTrace();
    }

    // Rewrite the CDs file with updated stock
    try (BufferedWriter writer = new BufferedWriter(new FileWriter(cdsFile))) {
        for (String updatedLine : updatedLines) {
            writer.write(updatedLine);
            writer.newLine();
        }
    } catch (IOException ex) {
        JOptionPane.showMessageDialog(this, "Error reading the CDs file.", "Error", JOptionPane.ERROR_MESSAGE);
        ex.printStackTrace();
    }
}

private void removeRentedRecord(String username, String cdName, int quantity) {
    File rentedFile = new File("records/rented/" + username + ".txt");
    List<String> remainingCDs = new ArrayList<>();

    try (BufferedReader reader = new BufferedReader(new FileReader(rentedFile))) {
        String line;
        while ((line = reader.readLine()) != null) {
            String[] parts = line.split("\"?( |$)(?=(([^\"]*\"){2})*[^\"]*$)\"?");
            parts[0] = parts[0].trim().replaceAll("^\"|\"$", "");
            String rentedCDName = parts[0];
            int rentedQuantity = Integer.parseInt(parts[1]);
            LocalDate dueDate = LocalDate.parse(parts[2]);

            if (!rentedCDName.equals(cdName) || rentedQuantity != quantity) {
                remainingCDs.add(line);
            } else if (rentedQuantity == quantity) {
                // if the rented quantity matches the quantity to be removed, skip this line (it gets removed)
                quantity -= rentedQuantity;
            } else {
                // if the rented quantity is larger, decrease the quantity and keep the line with updated quantity
                rentedQuantity -= quantity;
                String updatedLine = "\"" + rentedCDName + "\" " + rentedQuantity + " " + dueDate;
                remainingCDs.add(updatedLine);
            }
        }
    } catch (IOException ex) {
        JOptionPane.showMessageDialog(this, "Error reading the rented CDs file for user: " + username, "Error", JOptionPane.ERROR_MESSAGE);
        ex.printStackTrace();
    }

    try (BufferedWriter writer = new BufferedWriter(new FileWriter(rentedFile))) {
        for (String cd : remainingCDs) {
            writer.write(cd);
            writer.newLine();
        }
    } catch (IOException ex) {
        JOptionPane.showMessageDialog(this, "Error reading the rented CDs file for user: " + username, "Error", JOptionPane.ERROR_MESSAGE);
        ex.printStackTrace();
    }
}

    // Helper method to find the row index by CD name
    private int findRowByCDName(String cdName, DefaultTableModel model) {
        for (int i = 0; i < model.getRowCount(); i++) {
            if (model.getValueAt(i, 0).equals(cdName)) {
                return i;
            }
        }
        return -1; // Not found
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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new CD_Rental_System().setVisible(true);
            }
        });
    }
}
