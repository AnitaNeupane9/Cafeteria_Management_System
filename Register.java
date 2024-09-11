import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Register {
    private JFrame frame;
    private CardLayout cardLayout;
    private JPanel cardPanel;
    private UserManager userManager;

    public Register(JFrame frame, CardLayout cardLayout, JPanel cardPanel, UserManager userManager) {
        this.frame = frame;
        this.cardLayout = cardLayout;
        this.cardPanel = cardPanel;
        this.userManager = userManager;
    }

    public JPanel createRegisterPanel() {
        JPanel registerPanel = new JPanel(new BorderLayout());

        JLabel registerLabel = createLabel("CREATE ACCOUNT", Font.BOLD, 16);
        JTextField usernameField = createTextField(15);
        JPasswordField passwordField = createPasswordField(15);
        JButton registerButton = createButton("Register");
        JMenuBar menuBar = createMenuBar();

        JPanel registerFormPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;

        registerFormPanel.add(registerLabel, gbc);
        gbc.gridy++;
        gbc.insets = new Insets(10, 0, 0, 0);
        registerFormPanel.add(createLabel("Username:", Font.PLAIN, 12), gbc);
        gbc.gridy++;
        gbc.insets = new Insets(5, 0, 0, 0);
        registerFormPanel.add(usernameField, gbc);
        gbc.gridy++;
        gbc.insets = new Insets(10, 0, 0, 0);
        registerFormPanel.add(createLabel("Password:", Font.PLAIN, 12), gbc);
        gbc.gridy++;
        gbc.insets = new Insets(5, 0, 0, 0);
        registerFormPanel.add(passwordField, gbc);
        gbc.gridy++;
        gbc.insets = new Insets(20, 0, 0, 0);
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        registerFormPanel.add(registerButton, gbc);

        registerPanel.add(menuBar, BorderLayout.NORTH);
        registerPanel.add(registerFormPanel, BorderLayout.CENTER);
        registerPanel.add(createBottomPanel(), BorderLayout.SOUTH);

        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performRegistration(usernameField.getText(), new String(passwordField.getPassword()));
            }
        });

        return registerPanel;
    }

    private JLabel createLabel(String text, int style, int fontSize) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", style, fontSize));
        return label;
    }

    private JTextField createTextField(int columns) {
        return new JTextField(columns);
    }

    private JPasswordField createPasswordField(int columns) {
        return new JPasswordField(columns);
    }

    private JButton createButton(String text) {
        return new JButton(text);
    }

    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        JMenu navigationMenu = new JMenu("Navigation");
        JMenuItem homeMenuItem = new JMenuItem("Home");
        JMenuItem loginMenuItem = new JMenuItem("Login");

        navigationMenu.add(homeMenuItem);
        navigationMenu.add(loginMenuItem);
        menuBar.add(navigationMenu);

        homeMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(cardPanel, "home");
                frame.setTitle("APU Cafeteria Management System");
            }
        });

        loginMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(cardPanel, "login");
                frame.setTitle("Login Panel");
            }
        });

        return menuBar;
    }

    private JLabel createLoginHyperlink() {
        JLabel loginLinkLabel = new JLabel("Login here");
        loginLinkLabel.setForeground(Color.BLUE);
        loginLinkLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        loginLinkLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                cardLayout.show(cardPanel, "login");
                frame.setTitle("Login Panel");
            }

            public void mouseEntered(java.awt.event.MouseEvent evt) {
                loginLinkLabel.setText("<html><u>Login here</u></html>");
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                loginLinkLabel.setText("Login here");
            }
        });

        return loginLinkLabel;
    }

    private JPanel createBottomPanel() {
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottomPanel.add(new JLabel("Have already an account?"));
        bottomPanel.add(createLoginHyperlink());
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
        return bottomPanel;
    }

    private void performRegistration(String username, String password) {
        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Username and password are required.");
        } else {
            if (!userManager.doesUserExist(username)) {
                User newUser = new User(username, password, "customer");
                userManager.addUserToFile(newUser);
                JOptionPane.showMessageDialog(frame, "Registration successful!");
                cardLayout.show(cardPanel, "login");
            } else {
                JOptionPane.showMessageDialog(frame, "Username already exists. Registration failed.");
                cardLayout.show(cardPanel, "login");
            }
        }
    }
}

