import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class CustomerHomeTab {

    private JPanel homeTab;
    private JTextArea feedbackTextArea;

    public CustomerHomeTab() {
        homeTab = createCustomerHomeTab();
    }

    private JPanel createCustomerHomeTab() {
        JPanel homeTabPanel = new JPanel(new BorderLayout());

        // Add welcome message
        JLabel welcomeLabel;
        if (AppState.isUserLoggedIn()) {
            User loggedInUser = AppState.getLoggedInUser();
            welcomeLabel = new JLabel("Welcome " + loggedInUser.getUserName() + "!");
        } else {
            welcomeLabel = new JLabel("Welcome to Customer Home Tab");
        }
        homeTabPanel.add(welcomeLabel, BorderLayout.NORTH);

        // Add feedback components
        JPanel feedbackPanel = new JPanel(new BorderLayout());
        JLabel feedbackLabel = new JLabel("Your Feedback:");
        feedbackTextArea = new JTextArea(5, 20);
        JScrollPane feedbackScrollPane = new JScrollPane(feedbackTextArea);
        JButton submitButton = new JButton("Submit Feedback");

        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveFeedback();
                clearFeedbackTextArea();
                JOptionPane.showMessageDialog(homeTab, "Thank you for your feedback!");
            }
        });

        feedbackPanel.add(feedbackLabel, BorderLayout.NORTH);
        feedbackPanel.add(feedbackScrollPane, BorderLayout.CENTER);
        feedbackPanel.add(submitButton, BorderLayout.SOUTH);

        homeTabPanel.add(feedbackPanel, BorderLayout.CENTER);

        return homeTabPanel;
    }

    private void saveFeedback() {
        String feedbackText = feedbackTextArea.getText();
        if (!feedbackText.isEmpty()) {
            String userName = AppState.getLoggedInUser().getUserName();
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            String feedbackEntry = userName + " (" + timestamp + "): " + feedbackText;

            try (BufferedWriter writer = new BufferedWriter(new FileWriter("feedbacks.txt", true))) {
                writer.write(feedbackEntry);
                writer.newLine();
            } catch (IOException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(homeTab, "Failed to save feedback. Please try again.");
            }
        }
    }

    private void clearFeedbackTextArea() {
        feedbackTextArea.setText("");
    }

    public JPanel getHomeTab() {
        return homeTab;
    }
}
