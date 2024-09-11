import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Login{
    private JFrame frame;
    private CardLayout cardLayout;
    private JPanel cardPanel;
    private UserManager userManager;

    public Login(JFrame frame, CardLayout cardLayout, JPanel cardPanel, UserManager userManager) {
        this.frame = frame;
        this.cardLayout = cardLayout;
        this.cardPanel = cardPanel;
        this.userManager = userManager;
    }

    public JPanel createLoginPanel() {
        JPanel loginPanel = new JPanel(new BorderLayout());

        JLabel loginLabel = new JLabel("Login to your account");
        loginLabel.setFont(new Font("Arial", Font.BOLD, 16));

        JTextField usernameField = new JTextField(15);
        JPasswordField passwordField = new JPasswordField(15);
        JButton loginButton = new JButton("Login");
        JButton registerButton = new JButton("Don't have an account? Register here");

        JPanel loginFormPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;

        loginFormPanel.add(loginLabel, gbc);
        gbc.insets = new Insets(30, 0, 0, 0); // Add some vertical space
        gbc.gridy++;

        loginFormPanel.add(new JLabel("Username:"), gbc);
        gbc.gridy++;
        gbc.insets = new Insets(5, 0, 0, 0); // Add some vertical space
        loginFormPanel.add(usernameField, gbc);
        gbc.gridy++;
        gbc.insets = new Insets(10, 0, 0, 0); // Add more vertical space
        loginFormPanel.add(new JLabel("Password:"), gbc);
        gbc.gridy++;
        gbc.insets = new Insets(5, 0, 0, 0); // Add some vertical space
        loginFormPanel.add(passwordField, gbc);
        gbc.gridy++;
        gbc.insets = new Insets(20, 0, 0, 0); // Add more vertical space
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        loginFormPanel.add(loginButton, gbc);

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());

                if (username.isEmpty() || password.isEmpty()) {
                    JOptionPane.showMessageDialog(frame, "Username and password are required.");
                } else {
                    User authenticatedUser = userManager.authenticateUser(username, password);
                    if (authenticatedUser != null) {
                        
                        AppState.setUserLoggedIn(true);
                        AppState.setLoggedInUser(authenticatedUser);

                        if ("admin".equalsIgnoreCase(authenticatedUser.getRole())) {
                            showAdminPanel();
                        } else {
                            showCustomerPanel();
                        }

                    } else {
                        JOptionPane.showMessageDialog(frame, "Invalid username or password. Please try again.");
                    }
                }
            }
        });

        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Handle registration button click, e.g., show registration panel
                // Assuming you have a method to show registration panel
                showRegistrationPanel();
            }
        });

        loginPanel.add(loginFormPanel, BorderLayout.CENTER);

        JLabel registerLabel = createRegisterHyperlink();
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottomPanel.add(new JLabel("Don't have an account? "));
        bottomPanel.add(registerLabel);
        loginPanel.add(bottomPanel, BorderLayout.SOUTH);

        return loginPanel;
    }

    private JLabel createRegisterHyperlink() {
        JLabel registerLinkLabel = new JLabel("Register here");
        registerLinkLabel.setForeground(Color.BLUE);
        registerLinkLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        registerLinkLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                cardLayout.show(cardPanel, "register");
                frame.setTitle("Registration Panel");
            }

            public void mouseEntered(java.awt.event.MouseEvent evt) {
                registerLinkLabel.setText("<html><u>Register here</u></html>");
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                registerLinkLabel.setText("Register here");
            }
        });

        return registerLinkLabel;
    }

    private void showRegistrationPanel() {
        cardLayout.show(cardPanel, "login");
        frame.setTitle("Login Panel");
    }

    private void showAdminPanel() {
        cardLayout.show(cardPanel, "admin");
        frame.setTitle("Admin Panel");
    }


    private void showCustomerPanel() {
        CustomerGui customerGui = new CustomerGui(frame,
            cardLayout,
            cardPanel
        );

        JPanel customerPanel = customerGui.createCustomerPanel();
        cardPanel.add(customerPanel, "customer");
        cardLayout.show(cardPanel, "customer");
        frame.setTitle("Customer Panel");
    }
}

