package application.UserUI;

import application.InboxUI.InboxController;
import application.User.User;
import application.User.UserDirectory;
import application.User.UserType;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class LoginController {

    // 添加 userDirectory 作为成员变量
    private UserDirectory userDirectory;

    // Setter 方法，用于将 userDirectory 设置为 Main 类中的 UserDirectory 实例
    public void setUserDirectory(UserDirectory userDirectory) {
        this.userDirectory = userDirectory;
    }

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button loginButton;

    @FXML
    private Button registerButton;

    @FXML
    public void handleLogin() {
        if (userDirectory == null) {
            System.out.println("userDirectory is null in handleLogin");
            showAlert(Alert.AlertType.ERROR, "Error", "User directory is not initialized.");
            return;
        }

        String username = usernameField.getText();
        String password = passwordField.getText();

        User user = userDirectory.login(username, password);
        if (user != null) {
            // 用户登录成功
            if (user.getUserType().equals(UserType.VIP)) {
                showAlert(Alert.AlertType.INFORMATION, "Login Successful", "Welcome, VIP " + user.getUsername() + "!");
            } else if (user.getUserType().equals(UserType.NORMAL)) {
                showAlert(Alert.AlertType.INFORMATION, "Login Successful", "Welcome, " + user.getUsername() + "!");
            }

            // 加载主页面
            loadMainPage(user);
        } else {
            // 用户登录失败
            showAlert(Alert.AlertType.ERROR, "Login Failed", "Invalid username or password.");
        }
    }


    @FXML
    public void handleRegisterRedirect() {
        try {
            // Load Register.fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/UserUI/Register.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Register");
            stage.setScene(new Scene(root, 400, 300));
            stage.show();

            // Hide the current stage (Login)
            Stage loginStage = (Stage) registerButton.getScene().getWindow();
            loginStage.hide();

            // Show the login stage when the register stage is closed
            stage.setOnCloseRequest(event -> loginStage.show());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadMainPage(User user) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/InboxUI/inbox.fxml"));
            Parent root = loader.load();

            InboxController controller = loader.getController();
            controller.initialize(user); // Ensure user and toDoManager are correctly passed
            controller.setUserDirectory(userDirectory); // Pass userDirectory to InboxController

            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setScene(new Scene(root, 800, 600));
            stage.setTitle("To-Do List - Inbox");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }





    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
