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

        setTitle("Admin Panel");
        setSize(900, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel catalogPanel = new JPanel(new BorderLayout());
        
        Runnable backButtonAction = () -> {
            int confirmDelete = JOptionPane.showConfirmDialog(null, "Are you sure you want to log out?", "Confirm Deletion", JOptionPane.YES_NO_OPTION);
            if (confirmDelete == JOptionPane.YES_OPTION) {
                dispose(); // Close the current frame
                CD_Rental_System newSystem = new CD_Rental_System(); // Create a new instance of CD_Rental_System
                newSystem.setVisible(true); // Make the new frame visible
            }
        };

        JPanel headerPanel = showHeader(mainMenuFrame, "Admin Panel", username, backButtonAction);
        catalogPanel.add(headerPanel, BorderLayout.NORTH);

        add(catalogPanel);
        setVisible(true);

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
        table.getColumnModel().getColumn(0).setPreferredWidth(300); // CD Name column size increased
        table.getColumnModel().getColumn(1).setPreferredWidth(120); // Price column size adjusted
        table.getColumnModel().getColumn(2).setPreferredWidth(80); // Stock column size
        table.getColumnModel().getColumn(3).setPreferredWidth(100); // Genre column size
        table.getColumnModel().getColumn(4).setPreferredWidth(200); // Distributor column size
    }

    //cd data
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