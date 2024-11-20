package application.User;

import java.util.ArrayList;
import java.util.List;

import application.ToDoItem;
import application.ToDoManager;

public abstract class User {
    protected String username;
    protected String password;
    protected UserType userType;
    private List<ToDoItem> toDoList;
    private ToDoManager toDoManager; // ToDoManager for each user

    // constructor
    public User(String username, String password, UserType userType) {
    	this.username = username;
        this.password = password;
        this.userType = userType;
        this.toDoList = new ArrayList<>(); // Initialize task list
        this.toDoManager = new ToDoManager(this); // Pass this user to the manager
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
    
    public ToDoManager getToDoManager() {
        return toDoManager; // Return the user's ToDoManager
    }

    // abstract method just prepare for probably future use
    public abstract void accessFeatures();
    
    
}
