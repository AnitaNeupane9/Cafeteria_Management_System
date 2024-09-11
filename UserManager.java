import java.io.*;
import java.util.HashMap;
import java.util.Map;

class UserManager {
    private static final String USER_FILE_PATH = "users.txt";
    private Map<String, User> users;
    private User currentUser;

    public UserManager() {

        if(!userFileExists()) {
            createEmptyUserFile();
        }

        users = readUsersFromFile();

        if(userFileExists() && !doesUserExist("admin")) {
            User defaultAdmin = new User("admin", "adminpassword", "admin");
            addUserToFile(defaultAdmin);
            users.put("admin", defaultAdmin);
        }
    }

    private void createEmptyUserFile() {
        try {
            File file = new File(USER_FILE_PATH);
            if (file.createNewFile()) {
                System.out.println("User file created: " + USER_FILE_PATH);
            } else {
                System.out.println("User file already exists.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void reloadUsers() {
        if (!userFileExists()) {
            createEmptyUserFile();
        }

        users = readUsersFromFile();
    }

    private boolean userFileExists() {
        File file = new File(USER_FILE_PATH);
        return file.exists();
    }

    public User authenticateUser(String username, String password) {
        currentUser = users.get(username + password);
        return currentUser;
    }

    public boolean doesUserExist(String username) {
        for (User user : users.values()) {
            if (user.getUserName().equals(username)) {
                return true;
            }
        }
        return false;
    }

    public boolean isManager(User user) {
        return currentUser != null && "manager".equalsIgnoreCase(user.getRole());
    }

    public boolean isLoggedIn() {
        return currentUser != null;
    }

    public void logout() {
        currentUser = null;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    private Map<String, User> readUsersFromFile() {
        Map<String, User> userMap = new HashMap<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(USER_FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] userData = line.split(",");
                if (userData.length == 3) {
                    User user = new User(userData[0], userData[1], userData[2]);
                    userMap.put(user.getUserName() + user.getPassword(), user);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return userMap;
    }

    public void addUserToFile(User user) {
        try (FileWriter writer = new FileWriter(USER_FILE_PATH, true)) {
            String userLine = user.getUserName() + "," + user.getPassword() + "," + user.getRole() + "\n";
            writer.write(userLine);
        } catch (IOException e) {
            e.printStackTrace();
        }

        reloadUsers(); // Reload users after adding a new user
    }
}