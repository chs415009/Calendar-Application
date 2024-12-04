package application.User;

public class UserDirectoryHolder {
    private static UserDirectory userDirectory;

    public static UserDirectory getUserDirectory() {
        return userDirectory;
    }

    public static void setUserDirectory(UserDirectory directory) {
        userDirectory = directory;
    }
}
