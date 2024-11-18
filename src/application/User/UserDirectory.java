package application.User;

import java.util.ArrayList;
import java.util.List;

public class UserDirectory {
    private List<NormalUser> normalUsers;
    private List<VIPUser> vipUsers;

    // Constructor to initialize the lists
    public UserDirectory() {
        this.normalUsers = new ArrayList<>();
        this.vipUsers = new ArrayList<>();
    }

    // Add NormalUser
    public boolean addNormalUser(NormalUser user) {
        if (getNormalUserByUsername(user.getUsername()) != null || getVIPUserByUsername(user.getUsername()) != null) {
            return false; // Username already exists in either list
        } else {
            normalUsers.add(user);
            return true; // User added successfully
        }
    }

    // Add VIPUser
    public boolean addVIPUser(VIPUser user) {
        if (getNormalUserByUsername(user.getUsername()) != null || getVIPUserByUsername(user.getUsername()) != null) {
            return false; // Username already exists in either list
        } else {
            vipUsers.add(user);
            return true; // User added successfully
        }
    }

    // Get NormalUser by username
    public NormalUser getNormalUserByUsername(String username) {
        for (NormalUser user : normalUsers) {
            if (user.getUsername().equals(username)) {
                return user;
            }
        }
        return null; // Not found
    }

    // Get VIPUser by username
    public VIPUser getVIPUserByUsername(String username) {
        for (VIPUser user : vipUsers) {
            if (user.getUsername().equals(username)) {
                return user;
            }
        }
        return null; // Not found
    }

    // Login
    public User login(String username, String password) {
        User user = getNormalUserByUsername(username);
        if (user == null) {
            user = getVIPUserByUsername(username);
        }
        if (user != null && user.getPassword().equals(password)) {
            return user; // Login successful
        }
        return null; // Login failed
    }

    // Get all NormalUsers
    public List<NormalUser> getAllNormalUsers() {
        return new ArrayList<>(normalUsers);
    }

    // Get all VIPUsers
    public List<VIPUser> getAllVIPUsers() {
        return new ArrayList<>(vipUsers);
    }
}


