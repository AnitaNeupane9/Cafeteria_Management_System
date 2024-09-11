import java.awt.*;
import java.io.BufferedWriter;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

public class CustomerGui {
    private JFrame frame;
    private CardLayout cardLayout;
    private JPanel cardPanel;
    private JTable ordersTable;

    public CustomerGui(JFrame frame, CardLayout cardLayout, JPanel cardPanel) {
        this.frame = frame;
        this.cardLayout = cardLayout;
        this.cardPanel = cardPanel;
    }

    public JPanel createCustomerPanel() {
        JPanel customerPanel = new JPanel(new BorderLayout());

        JTabbedPane tabbedPane = new JTabbedPane();

        JPanel homePanel = createCustomerHomeTab();
        JPanel feedbackTab = createFeedbackTab();
        JPanel foodMenuPanel = createFoodMenuTab();
        JPanel ordersPanel = createOrdersTab();

        tabbedPane.addTab("Profile", homePanel);
        tabbedPane.addTab("Food Menu", foodMenuPanel);
        tabbedPane.addTab("Orders", ordersPanel);
        tabbedPane.addTab("Feedback", feedbackTab);

        tabbedPane.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                if (tabbedPane.getSelectedIndex() == 2) {
                    reloadOrdersData((DefaultTableModel) ordersTable.getModel());
                }
            }
        });

        customerPanel.add(tabbedPane, BorderLayout.CENTER);

        JButton logoutButton = new JButton("Logout");
        logoutButton.addActionListener(e -> handleLogout());

        customerPanel.add(logoutButton, BorderLayout.SOUTH);
        return customerPanel;
    }

    private void handleLogout() {
        JOptionPane.showMessageDialog(frame, "Logged out!");
        AppState.setUserLoggedIn(false);
        this.cardLayout.show(this.cardPanel, "home");
        frame.setTitle("APU Cafeteria Management System");
    }

    private JPanel createOrdersTab() {
        JPanel ordersPanel = new JPanel(new BorderLayout());
        DefaultTableModel model = new DefaultTableModel(new Object[]{"Order ID", "Item", "Price", "Date", "Quantity", "Total", "Payment Status"}, 0);
        JTable ordersTable = new JTable(model);
        this.ordersTable = ordersTable;
        JScrollPane scrollPane = new JScrollPane(ordersTable);
        ordersPanel.add(scrollPane, BorderLayout.CENTER);
        reloadOrdersData(model);

        /*
         * To centrally align the data in the cell
         */
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        ordersTable.setDefaultRenderer(Object.class, centerRenderer);
        for (int i = 0; i < ordersTable.getColumnCount(); i++) {
            ordersTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        // Add a Pay Now button for items where payment is pending
        TableColumn paymentColumn = ordersTable.getColumnModel().getColumn(6);
        paymentColumn.setCellRenderer(new ButtonRenderer());
        paymentColumn.setCellEditor(new ButtonEditor(new JCheckBox(), ordersTable));

        return ordersPanel;
    }

    private void reloadOrdersData(DefaultTableModel model) {
        model.setRowCount(0);
        List<Order> ordersData = getOrdersData();
        for (Order order : ordersData) {
            Object[] rowData = {order.getOrderId(), order.getItem(), order.getPrice(), order.getDate(), order.getQuantity(), order.getTotal()};
            model.addRow(rowData);
        }
    }


    private List<Order> getOrdersData() {
        List<Order> orders = new ArrayList<>();

        User loggedInUser = AppState.getLoggedInUser();
        if (loggedInUser == null) {
            return orders;
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        try (BufferedReader reader = new BufferedReader(new FileReader("orders.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 7) {
                    int orderId = Integer.parseInt(parts[0]);
                    String username = parts[1];

                    if (username.equals(loggedInUser.getUserName())) {
                        // Only add orders for the logged-in user
                        String item = parts[2];
                        double price = Double.parseDouble(parts[3]);
                        Date date = dateFormat.parse(parts[4]);
                        int quantity = Integer.parseInt(parts[5]);
                        double total = Double.parseDouble(parts[6]);
                        orders.add(new Order(orderId, username, item, price, date, quantity, total));
                    }

                } else {
                    System.err.println("Invalid order data: " + line);
                }
            }
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }

        return orders;
    }

    private JPanel createFoodMenuTab() {
        JPanel foodMenuPanel = new JPanel(new GridLayout(0, 2));
        List<MenuItem> menuItems = getMenuItems();

        for (MenuItem item : menuItems) {
            JPanel menuItemPanel = new JPanel(new BorderLayout());
            menuItemPanel.setBorder(BorderFactory.createEtchedBorder());

            JLabel nameLabel = new JLabel(item.getName());
            JLabel priceLabel = new JLabel("$" + item.getPrice());
            JTextArea descriptionArea = new JTextArea(item.getDescription());
            descriptionArea.setEditable(false);

            JButton orderButton = new JButton("Order");
            orderButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    handleOrder(item);
                }
            });

            JPanel infoPanel = new JPanel(new GridLayout(2, 1));
            infoPanel.add(nameLabel);
            infoPanel.add(priceLabel);

            menuItemPanel.add(infoPanel, BorderLayout.NORTH);
            menuItemPanel.add(descriptionArea, BorderLayout.CENTER);
            menuItemPanel.add(orderButton, BorderLayout.SOUTH);

            foodMenuPanel.add(menuItemPanel);
        }
        
        JScrollPane scrollPane = new JScrollPane(foodMenuPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        
        JPanel panelWithScrollPane = new JPanel(new BorderLayout());
        panelWithScrollPane.add(scrollPane, BorderLayout.CENTER);
        
        return panelWithScrollPane;
    }

    private void handleOrder(MenuItem item) {
        JTextField quantityField = new JTextField(5);
        JPanel quantityPanel = new JPanel();
        quantityPanel.add(new JLabel("Quantity: "));
        quantityPanel.add(quantityField);

        int result = JOptionPane.showConfirmDialog(frame, quantityPanel,
                "Enter Quantity", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            try {
                int quantity = Integer.parseInt(quantityField.getText());
                if (quantity > 0) {
                    saveOrder(item, quantity);
                } else {
                    JOptionPane.showMessageDialog(frame, "Quantity must be greater than 0.");
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(frame, "Please enter a valid quantity.");
            }
        }
    }

    private void saveOrder(MenuItem item, int quantity) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("orders.txt", true))) {
            int orderId = generateOrderId();

            double totalPrice = item.getPrice() * quantity;

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String date = dateFormat.format(new Date());

            String username = AppState.getLoggedInUser().getUserName();

            String orderLine = orderId + "," + username + "," + item.getName() + "," + item.getPrice() + "," +
                    date + "," + quantity + "," + totalPrice;

            writer.write(orderLine);
            writer.newLine();

            JOptionPane.showMessageDialog(frame, "Order placed successfully!");

        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Failed to place order. Please try again later.");
        }
    }

    private int generateOrderId() {
        return (int) (System.currentTimeMillis() % Integer.MAX_VALUE);
    }

    private List<MenuItem> getMenuItems() {
        List<MenuItem> menuItems = new ArrayList<>();
        
        try (BufferedReader reader = new BufferedReader(new FileReader("menus.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 3) {
                    String name = parts[0];
                    double price = Double.parseDouble(parts[1]);
                    String description = parts[2];
                    menuItems.add(new MenuItem(name, price, description));
                } else {
                    System.err.println("Invalid menu item data: " + line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        return menuItems;
    }

    private JPanel createCustomerHomeTab() {
        JPanel homeTab = new JPanel();
        if (AppState.isUserLoggedIn()) {
            User loggedInUser = AppState.getLoggedInUser();
            homeTab.add(new JLabel("Welcome " + loggedInUser.getUserName() + "!"));
        } else {
            homeTab.add(new JLabel("Welcome to Customer Home Tab"));
        }
        return homeTab;


    }

    private JPanel createFeedbackTab() {
        JPanel feedbackTab = new JPanel(new BorderLayout());
    
        JTextArea feedbackTextArea = new JTextArea(5, 20);
        JScrollPane scrollPane = new JScrollPane(feedbackTextArea);
    
        JButton submitButton = new JButton("Submit Feedback");
    
        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String feedback = feedbackTextArea.getText();
                if (!feedback.isEmpty()) {
                    saveFeedback(feedback);
                    JOptionPane.showMessageDialog(frame, "Feedback submitted successfully!");
                    feedbackTextArea.setText("");
                } else {
                    JOptionPane.showMessageDialog(frame, "Please provide your feedback before submitting.");
                }
            }
        });
    
        JPanel inputPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(10, 10, 0, 10); // Adjust top margin
    
        inputPanel.add(new JLabel("Your Feedback:"), gbc);
        gbc.gridy++;
        gbc.weightx = 1.0; // Allow the text area to expand horizontally
        gbc.fill = GridBagConstraints.HORIZONTAL;
        inputPanel.add(scrollPane, gbc);
    
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(submitButton);
    
        feedbackTab.add(inputPanel, BorderLayout.CENTER);
        feedbackTab.add(buttonPanel, BorderLayout.SOUTH);
    
        return feedbackTab;
    }
    


    private void saveFeedback(String feedback) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("feedbacks.txt", true))) {
            writer.write(feedback);
            writer.newLine();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}


class ButtonRenderer extends JButton implements TableCellRenderer {
    public ButtonRenderer() {
        setOpaque(true);
        setFocusable(false);
    }

    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {

        Object orderIdObject = table.getModel().getValueAt(row, 0);
        String orderId = orderIdObject != null ? orderIdObject.toString() : "";

        if (isOrderPaid(orderId)) {
            setBackground(new Color(123,213,0));
            setText("Paid");
            setEnabled(false);
        } else {
            setBackground(new Color(246,176,0));
            setText("Pay Now");
            setEnabled(true);
        }

        return this;
    }
    

    private boolean isOrderPaid(String orderId) {
        try (BufferedReader reader = new BufferedReader(new FileReader("payments.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 4 && parts[0].equals(orderId) && "Paid".equals(parts[2])) {
                    return true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
}



class ButtonEditor extends DefaultCellEditor {
    protected JButton button;
    private String label;
    private boolean isPushed;

    public ButtonEditor(JCheckBox checkBox, JTable table) {
        super(checkBox);
        button = new JButton();
        button.setOpaque(true);
        button.setPreferredSize(new Dimension(70, 20));
        button.addActionListener(e -> fireEditingStopped());
    }


    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        if (isSelected) {
            // button.setForeground(table.getSelectionForeground());
            // button.setForeground(table.getSelectionForeground());
        } else {
            // button.setForeground(table.getForeground());
            // button.setBackground(table.getBackground());
        }

        Object orderIdObject = table.getValueAt(row, 0);
        Object quantityObject = table.getValueAt(row, 4);
        Object amountObject = table.getValueAt(row, 5);

        String orderId = orderIdObject instanceof String ? (String) orderIdObject : String.valueOf(orderIdObject);
        String quantity = quantityObject instanceof String ? (String) quantityObject : String.valueOf(quantityObject);
        String amount = amountObject instanceof String ? (String) amountObject : String.valueOf(amountObject);

        label = orderId + "," + quantity + "," + amount;
        button.setText(label);
        isPushed = true;
        return button;
    }


    public Object getCellEditorValue() {
        if (isPushed && !label.isEmpty()) {
            String[] parts = label.split(",");

            int orderId = Integer.parseInt(parts[0]);
            int quantity = Integer.parseInt(parts[1]);
            double amount = Double.parseDouble(parts[2]);

            String username = getUsernameForOrder(orderId);
            boolean paymentCompleted = updatePaymentStatus(orderId, username, quantity, amount);

            if (paymentCompleted) {
                JOptionPane.showMessageDialog(button, "Payment completed for Order ID: " + label);
            } else {
                JOptionPane.showMessageDialog(button, "Failed to update payment status for Order ID: " + label);
            }
        }

        isPushed = false;
        return label;
    }

    private boolean updatePaymentStatus(int orderId, String username, int quantity, double amount) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("payments.txt", true))) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String paymentLine = orderId + "," + username + ",Paid," + dateFormat.format(new Date()) + "," + quantity + "," + amount;
            writer.write(paymentLine);
            writer.newLine();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private String getUsernameForOrder(int orderId) {
        List<Order> ordersData = getOrdersData();
        for (Order order : ordersData) {
            if (order.getOrderId() == orderId) {
                return order.getUsername();
            }
        }
        return null;
    }

    private List<Order> getOrdersData() {
    List<Order> orders = new ArrayList<>();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        try (BufferedReader reader = new BufferedReader(new FileReader("orders.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 7) {
                    int orderId = Integer.parseInt(parts[0]);
                    String username = parts[1];
                    String item = parts[2];
                    double price = Double.parseDouble(parts[3]);
                    Date date = dateFormat.parse(parts[4]);
                    int quantity = Integer.parseInt(parts[5]);
                    double total = Double.parseDouble(parts[6]);
                    orders.add(new Order(orderId, username, item, price, date, quantity, total));
                } else {
                    System.err.println("Invalid order data: " + line);
                }
            }
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }

        return orders;
    }

    public boolean stopCellEditing() {
        isPushed = false;
        return super.stopCellEditing();
    }

    protected void fireEditingStopped() {
        super.fireEditingStopped();
    }
}

