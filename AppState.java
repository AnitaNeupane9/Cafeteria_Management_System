public class AppState {
    private static boolean userLoggedIn = false;
    private static User loggedInUser=null;

    public static boolean isUserLoggedIn() {
        return userLoggedIn;
    }

    public static void setUserLoggedIn(boolean loggedIn) {
        userLoggedIn = loggedIn;
    }

    public static User getLoggedInUser() {
        return loggedInUser;
    }

    public static void setLoggedInUser(User user) {
        loggedInUser = user;
    }

    public static void logout() {
        userLoggedIn = false;
        loggedInUser = null;
    }
    
}
