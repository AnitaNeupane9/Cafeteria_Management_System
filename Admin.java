import java.io.*;
import java.util.List;
import java.util.ArrayList;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.table.DefaultTableModel;

public class Admin {
    private JFrame frame;
    private CardLayout cardLayout;
    private JPanel cardPanel;

    public Admin(JFrame frame, CardLayout cardLayout, JPanel cardPanel) {
        this.frame = frame;
        this.cardLayout = cardLayout;
        this.cardPanel = cardPanel;
        initializeMenuFile();
    }

    public JPanel createAdminPanel() {
        JPanel adminPanel = new JPanel(new BorderLayout());
        DefaultTableModel model = new DefaultTableModel(readMenuDataFromFile("menus.txt"),
                new String[]{"Item Name", "Price", "Category"});

        JTable menuTable = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(menuTable);

        JPanel centerPanel = new JPanel(new BorderLayout());

        JLabel menuItemLabel = new JLabel("Menu Items");
        customizeLabel(menuItemLabel);

        JButton addItemButton = new JButton("Add Menu Item");
        customizeButton(addItemButton);

        JButton updateItemButton = new JButton("Update Menu Item");
        customizeButton(updateItemButton);

        JButton viewPaymentsButton = new JButton("View Payments");
        customizeButton(viewPaymentsButton);

        JButton viewFeedbackButton = new JButton("View Feedback");
        customizeButton(viewFeedbackButton);

        addItemButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleAddMenuItem(model);
            }
        });

        updateItemButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleUpdateMenuItem(model, menuTable);
            }
        });

        viewPaymentsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleViewPayments();
            }
        });

        viewFeedbackButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleViewFeedback();
            }
        });

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(addItemButton);
        buttonPanel.add(updateItemButton);
        buttonPanel.add(viewPaymentsButton);
        buttonPanel.add(viewFeedbackButton);

        centerPanel.add(menuItemLabel, BorderLayout.NORTH);
        centerPanel.add(scrollPane, BorderLayout.CENTER);
        centerPanel.add(buttonPanel, BorderLayout.SOUTH);

        centerPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
        adminPanel.add(centerPanel, BorderLayout.CENTER);

        JButton logoutButton = new JButton("Logout");
        customizeButton(logoutButton);

        logoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleLogout();
            }
        });

        adminPanel.add(logoutButton, BorderLayout.SOUTH);
        return adminPanel;
    }

    private void handleViewFeedback() {

        File feedbacksFile = new File("feedbacks.txt");
        if (!feedbacksFile.exists()) {
            try {
                feedbacksFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(frame, "Error creating feedbacks file.");
                return;
            }
        }

        JTextArea feedbackArea = new JTextArea();
        feedbackArea.setEditable(false);

        JScrollPane scrollPane = new JScrollPane(feedbackArea);

        try (BufferedReader reader = new BufferedReader(new FileReader("feedbacks.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                feedbackArea.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Error reading feedbacks from file.");
        }

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(scrollPane, BorderLayout.CENTER);

        JOptionPane.showMessageDialog(frame, panel, "View Feedback", JOptionPane.INFORMATION_MESSAGE);
    }

    private void handleViewPayments() {

        File paymentsFile = new File("payments.txt");
        if (!paymentsFile.exists()) {
            try {
                paymentsFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(frame, "Error creating payments file.");
                return;
            }
        }

        JTextArea paymentsArea = new JTextArea();
        paymentsArea.setEditable(false);

        JScrollPane scrollPane = new JScrollPane(paymentsArea);

        try (BufferedReader reader = new BufferedReader(new FileReader("payments.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                paymentsArea.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Error reading payments from file.");
        }

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(scrollPane, BorderLayout.CENTER);

        JOptionPane.showMessageDialog(frame, panel, "View Payments", JOptionPane.INFORMATION_MESSAGE);
    }

    private void addMenuItemToFile(String menuItem, String fileName) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName, true))) {
            writer.write(menuItem);
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Error adding menu item to file.");
        }
    }

    private void handleUpdateMenuItem(DefaultTableModel model, JTable menuTable) {
        int selectedRow = menuTable.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(frame, "Please select a row to update.");
            return;
        }

        JTextField itemNameField = new JTextField(model.getValueAt(selectedRow, 0).toString());
        JTextField priceField = new JTextField(model.getValueAt(selectedRow, 1).toString());
        JTextField categoryField = new JTextField(model.getValueAt(selectedRow, 2).toString());

        JPanel panel = new JPanel(new GridLayout(3, 2));
        panel.add(new JLabel("Item Name:"));
        panel.add(itemNameField);
        panel.add(new JLabel("Price:"));
        panel.add(priceField);
        panel.add(new JLabel("Category:"));
        panel.add(categoryField);

        int result = JOptionPane.showConfirmDialog(frame, panel, "Update Menu Item", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            String itemName = itemNameField.getText();
            String price = priceField.getText();
            String category = categoryField.getText();

            if (!itemName.isEmpty() && !price.isEmpty() && !category.isEmpty()) {
                // Update the values in the table model
                model.setValueAt(itemName, selectedRow, 0);
                model.setValueAt(price, selectedRow, 1);
                model.setValueAt(category, selectedRow, 2);

                // Update the data in the file or database
                updateMenuItemInFile(selectedRow, itemName + "," + price + "," + category, "menus.txt");
            } else {
                JOptionPane.showMessageDialog(frame, "Please fill in all fields.");
            }
        }
    }

    private void updateMenuItemInFile(int rowIndex, String menuItem, String fileName) {
        List<String> lines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            int currentLine = 0;
            while ((line = reader.readLine()) != null) {
                if (currentLine == rowIndex) {
                    lines.add(menuItem);
                } else {
                    lines.add(line);
                }
                currentLine++;
            }
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Error updating menu item in file.");
            return;
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            for (String line : lines) {
                writer.write(line);
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Error updating menu item in file.");
        }
    }

    private void handleAddMenuItem(DefaultTableModel model) {
        JTextField itemNameField = new JTextField();
        JTextField priceField = new JTextField();
        JTextField categoryField = new JTextField();

        JPanel panel = new JPanel(new GridLayout(3, 2));
        panel.add(new JLabel("Item Name:"));
        panel.add(itemNameField);
        panel.add(new JLabel("Price:"));
        panel.add(priceField);
        panel.add(new JLabel("Category:"));
        panel.add(categoryField);

        int result = JOptionPane.showConfirmDialog(frame, panel, "Add Menu Item", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            String itemName = itemNameField.getText();
            String price = priceField.getText();
            String category = categoryField.getText();

            if (!itemName.isEmpty() && !price.isEmpty() && !category.isEmpty()) {
                addMenuItemToFile(itemName + "," + price + "," + category, "menus.txt");
                updateMenuTable(model, "menus.txt");
            } else {
                JOptionPane.showMessageDialog(frame, "Please fill in all fields.");
            }
        }
    }

    private void customizeLabel(JLabel label) {
        label.setFont(new Font("Arial", Font.BOLD, 16));
        label.setHorizontalAlignment(JLabel.CENTER);
        label.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
    }

    private void customizeButton(JButton button) {
        button.setFont(new Font("Arial", Font.PLAIN, 14));
    }

    private void handleLogout() {
        JOptionPane.showMessageDialog(frame, "Logged out!");
        AppState.setUserLoggedIn(false);
        cardLayout.show(cardPanel, "home");
        frame.setTitle("APU Cafeteria Management System");
    }

    private void initializeMenuFile() {
        File menuFile = new File("menus.txt");

        if (!menuFile.exists()) {
            // default menu items
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(menuFile))) {
                writer.write("Burger,5.99,Main Course\n");
                writer.write("Pizza,8.99,Main Course\n");
                writer.write("Salad,3.99,Appetizer\n");
                writer.write("Ice Cream,2.99,Dessert\n");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void updateMenuTable(DefaultTableModel model, String fileName) {
        Object[][] menuData = readMenuDataFromFile(fileName);
        model.setDataVector(menuData, new String[]{"Item Name", "Price", "Category"});
    }

    private Object[][] readMenuDataFromFile(String fileName) {
        List<Object[]> menuItems = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                menuItems.add(data);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return menuItems.toArray(new Object[0][]);
    }
}
