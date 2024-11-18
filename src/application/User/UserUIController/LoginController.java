package application.User.UserUIController;

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
        String username = usernameField.getText();
        String password = passwordField.getText();

        // Access userDirectory via the getter
        UserDirectory userDirectory = RegisterController.getUserDirectory();

        User user = userDirectory.login(username, password);
        if (user != null) {
            // 用戶登入成功
            if (user.getUserType().equals(UserType.VIP)) {
                showAlert(Alert.AlertType.INFORMATION, "Login Successful", "Welcome, VIP " + user.getUsername() + "!");
            } else if (user.getUserType().equals(UserType.NORMAL)) {
                showAlert(Alert.AlertType.INFORMATION, "Login Successful", "Welcome, " + user.getUsername() + "!");
            }

            // 加載主頁面
            loadMainPage();
        } else {
            // 用戶登入失敗
            showAlert(Alert.AlertType.ERROR, "Login Failed", "Invalid username or password.");
        }
    }

    @FXML
    public void handleRegisterRedirect() {
        try {
            // Load Register.fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/User/UserUI/Register.fxml"));
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

    private void loadMainPage() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/today.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setScene(new Scene(root, 800, 600));
            stage.setTitle("To-Do List - Today");
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
