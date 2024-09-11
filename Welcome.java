import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

class Welcome {

    private JFrame frame;
    private CardLayout cardLayout;
    private JPanel cardPanel;

    public Welcome(JFrame frame, CardLayout cardLayout, JPanel cardPanel) {
        this.frame = frame;
        this.cardLayout = cardLayout;
        this.cardPanel = cardPanel;
    }

    public JPanel createWelcomePanel() {
        JPanel homePanel = new JPanel(new BorderLayout());

        JLabel welcomeLabel = createLabel("Welcome to APU Cafeteria!", Font.BOLD, 16);
        welcomeLabel.setHorizontalAlignment(JLabel.CENTER);

        JLabel title = createLabel("APU MANAGEMENT SYSTEM", Font.BOLD, 20);
        title.setHorizontalAlignment(JLabel.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton loginButton = createButton("Login");
        JButton registerButton = createButton("Register");

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(cardPanel, "login");
                frame.setTitle("Login");
            }
        });

        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(cardPanel, "register");
                frame.setTitle("New User Registration");
            }
        });

        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        buttonPanel.add(loginButton);
        buttonPanel.add(registerButton);

        homePanel.add(welcomeLabel, BorderLayout.NORTH);
        homePanel.add(title, BorderLayout.CENTER);
        homePanel.add(buttonPanel, BorderLayout.SOUTH);

        return homePanel;
    }

    private JLabel createLabel(String text, int style, int fontSize) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", style, fontSize));
        return label;
    }

    private JButton createButton(String text) {
        JButton button = new JButton(text);
        return button;
    }
}