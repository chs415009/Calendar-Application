package application.UserUI;

import application.User.NormalUser;
import application.User.UserDirectory;
import application.User.UserDirectoryHolder;
import application.User.VIPUser;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class RegisterController {

    private UserDirectory userDirectory;

    public void setUserDirectory(UserDirectory userDirectory) {
        this.userDirectory = userDirectory;
    }

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button normalRegisterButton;

    @FXML
    private Button vipRegisterButton;

    @FXML
    private Button backButton;

    
    @FXML
    public void handleNormalRegister() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (validateInput(username, password)) {
            NormalUser normalUser = new NormalUser(username, password);
            if (userDirectory.addNormalUser(normalUser)) {
                showAlert(Alert.AlertType.INFORMATION, "Registration Successful", "Normal User registered successfully!");
                clearFields();
            } else {
                showAlert(Alert.AlertType.ERROR, "Registration Failed", "Username already exists.");
            }
        }
        UserDirectoryHolder.setUserDirectory(userDirectory);
    }

    @FXML
    public void handleVIPRegister() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (validateInput(username, password)) {
            VIPUser vipUser = new VIPUser(username, password);
            if (userDirectory.addVIPUser(vipUser)) {
                showAlert(Alert.AlertType.INFORMATION, "Registration Successful", "VIP User registered successfully!");
                clearFields();
            } else {
                showAlert(Alert.AlertType.ERROR, "Registration Failed", "Username already exists.");
            }
        }
    }
    
    public void handleBackToLogin() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/UserUI/Login.fxml"));
            Parent root = loader.load();

            // 获取 LoginController
            LoginController loginController = loader.getController();

            // 通过 UserDirectoryHolder 获取全局的 userDirectory 实例
            loginController.setUserDirectory(UserDirectoryHolder.getUserDirectory());

            // 获取当前窗口并加载登录页面
            Stage currentStage = (Stage) backButton.getScene().getWindow();
            Scene scene = new Scene(root, 400, 300);
            scene.getStylesheets().add(getClass().getResource("/application/application.css").toExternalForm());

            currentStage.setScene(scene);
            currentStage.setTitle("Login");
            currentStage.centerOnScreen();
            currentStage.show();

        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Navigation Error");
            alert.setHeaderText(null);
            alert.setContentText("Could not load the login view. Error: " + e.getMessage());
            alert.showAndWait();
        }
    }



    private boolean validateInput(String username, String password) {
        if (username == null || username.isEmpty() || password == null || password.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Invalid Input", "Username and password cannot be empty.");
            return false;
        }
        return true;
    }

    private void clearFields() {
        usernameField.clear();
        passwordField.clear();
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
