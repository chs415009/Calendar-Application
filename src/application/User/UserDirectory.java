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
        // 检查普通用户和 VIP 用户列表中是否存在相同的用户名
        if (getNormalUserByUsername(user.getUsername()) != null || getVIPUserByUsername(user.getUsername()) != null) {
            return false; // 用户名已存在
        } else {
            normalUsers.add(user);
            return true; // 成功添加用户
        }
    }

    public boolean addVIPUser(VIPUser user) {
        // 检查普通用户和 VIP 用户列表中是否存在相同的用户名
        if (getNormalUserByUsername(user.getUsername()) != null || getVIPUserByUsername(user.getUsername()) != null) {
            return false; // 用户名已存在
        } else {
            vipUsers.add(user);
            return true; // 成功添加用户
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

            // 确保每个用户的 ToDoManager 被正确初始化
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
        return new UserDirectory(); // Return an empty directory if loading fails
    }


    public void saveUsersToFile(String fileName) {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
                .setPrettyPrinting() // 添加这个来格式化输出
                .create();

        try {
            // Step 1: 读取现有的 JSON 文件内容
            FileReader reader = new FileReader(fileName);
            JsonElement existingData = JsonParser.parseReader(reader);
            reader.close();

            // Step 2: 将读取的内容解析为 UserDirectory 对象
            UserDirectory existingUserDirectory = null;
            if (existingData.isJsonObject()) {
                existingUserDirectory = gson.fromJson(existingData, UserDirectory.class);
            }

            // Step 3: 合并现有用户与新用户
            if (existingUserDirectory != null) {
                // 合并普通用户，避免重复添加相同的用户
                for (NormalUser user : existingUserDirectory.getNormalUsers()) {
                    if (getNormalUserByUsername(user.getUsername()) == null &&
                            getVIPUserByUsername(user.getUsername()) == null) {
                        this.normalUsers.add(user);
                    }
                }

                // 合并 VIP 用户，避免重复添加相同的用户
                for (VIPUser user : existingUserDirectory.getVipUsers()) {
                    if (getNormalUserByUsername(user.getUsername()) == null &&
                            getVIPUserByUsername(user.getUsername()) == null) {
                        this.vipUsers.add(user);
                    }
                }
            }

            // Step 4: 写入合并后的数据到文件中
            FileWriter writer = new FileWriter(fileName);
            gson.toJson(this, writer); // 使用 Pretty Printing 格式化输出
            writer.flush();
            writer.close();

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
