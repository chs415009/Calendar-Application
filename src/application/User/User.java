package application.User;

import java.util.ArrayList;
import java.util.List;

import application.ToDoItem;

public abstract class User {
    protected String username;
    protected String password;
    protected UserType userType;
    private List<ToDoItem> toDoList;

    // constructor
    public User(String username, String password, UserType userType) {
        this.username = username;
        this.password = password;
        this.userType = userType;
        this.toDoList = new ArrayList<>(); // Further discuss ...
    }

    // Getter method
    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public UserType getUserType() {
        return userType;
    }

    public List<ToDoItem> getToDoList() {
        return toDoList;
    }

    // abstract method just prepare for probably future use
    public abstract void accessFeatures();
}
