package application.User;

import application.ToDoManager;

public abstract class User {
    protected String username;
    protected String password;
    protected UserType userType;
    protected ToDoManager toDoManager;

    // constructor
    public User(String username, String password, UserType userType) {
        this.username = username;
        this.password = password;
        this.userType = userType;
        this.toDoManager = new ToDoManager(); // Further discuss ...
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

    public ToDoManager getToDoManager() {
        return toDoManager;
    }

    // abstract method just prepare for probably future use
    public abstract void accessFeatures();
}
