package application.User;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import application.LocalDateAdapter;
import application.ToDoManager;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
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

    public boolean addNormalUser(NormalUser user) {
        if (getNormalUserByUsername(user.getUsername()) != null || getVIPUserByUsername(user.getUsername()) != null) {
            return false; 
        } else {
            normalUsers.add(user);
            return true; 
        }
    }

    public boolean addVIPUser(VIPUser user) {
        if (getNormalUserByUsername(user.getUsername()) != null || getVIPUserByUsername(user.getUsername()) != null) {
            return false; 
        } else {
            vipUsers.add(user);
            return true; 
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


    public static UserDirectory loadUsersFromFile(String fileName) {
        try (FileReader reader = new FileReader(fileName)) {
            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
                    .create();
            UserDirectory userDirectory = gson.fromJson(reader, UserDirectory.class);

            if (userDirectory != null) {
                for (NormalUser user : userDirectory.getNormalUsers()) {
                    user.setToDoManager(new ToDoManager(user));
                }
                for (VIPUser user : userDirectory.getVipUsers()) {
                    user.setToDoManager(new ToDoManager(user));
                }
            }

            return userDirectory;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new UserDirectory();
    }


    public void saveUsersToFile(String fileName) {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
                .setPrettyPrinting()
                .create();

        try (FileWriter writer = new FileWriter(fileName)) {
            gson.toJson(this, writer);
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    
    public List<NormalUser> getNormalUsers() {
        return normalUsers;
    }

    public List<VIPUser> getVipUsers() {
        return vipUsers;
    }
    

}
