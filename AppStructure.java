import javax.swing.*;
import java.awt.*;

public class AppStructure {
    private JFrame frame;
    private JPanel cardPanel;
    private CardLayout cardLayout;
    private UserManager userManager;

    public AppStructure() {
        frame = new JFrame("APU Cafeteria Management System");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1000, 650);

        frame.setMinimumSize(new Dimension(600, 400));

        frame.setLayout(new BorderLayout());
        frame.setLocationRelativeTo(null);

        cardPanel = new JPanel();
        cardLayout = new CardLayout();
        cardPanel.setLayout(cardLayout);
        userManager = new UserManager();


        Welcome home = new Welcome(
            frame,
            cardLayout,
            cardPanel
        );
        JPanel homePanel = home.createWelcomePanel();
        cardPanel.add(homePanel, "home");

        
        Admin admin = new Admin(
            frame,
            cardLayout,
            cardPanel
        );
        JPanel adminPanel = admin.createAdminPanel();
        cardPanel.add(adminPanel, "admin");


        Login login = new Login(
            frame,
            cardLayout,
            cardPanel,
            userManager
        );
        JPanel loginPanel = login.createLoginPanel();
        cardPanel.add(loginPanel, "login");


        Register register = new Register(
            frame,
            cardLayout,
            cardPanel,
            userManager
        );
        JPanel registerPanel = register.createRegisterPanel();
        cardPanel.add(registerPanel, "register");


        cardLayout.show(cardPanel, "home");
        frame.add(cardPanel);
        frame.setVisible(true);
    }
}
