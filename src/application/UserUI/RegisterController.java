package application.UserUI;

import application.User.NormalUser;
import application.User.UserDirectory;
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

    private static UserDirectory userDirectory = new UserDirectory(); // UserDirectory instance

    // Getter method to access userDirectory
    public static UserDirectory getUserDirectory() {
        return userDirectory;
    }

    @FXML
    public void handleNormalRegister() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (validateInput(username, password)) {
            // Add NormalUser to normalUsers list
            NormalUser normalUser = new NormalUser(username, password);
            if (userDirectory.addNormalUser(normalUser)) {
                showAlert(Alert.AlertType.INFORMATION, "Registration Successful", "Normal User registered successfully!");
                clearFields();
            } else {
                showAlert(Alert.AlertType.ERROR, "Registration Failed", "Username already exists.");
            }
        }
    }

    @FXML
    public void handleVIPRegister() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (validateInput(username, password)) {
            // Add VIPUser to vipUsers list
            VIPUser vipUser = new VIPUser(username, password);
            if (userDirectory.addVIPUser(vipUser)) {
                showAlert(Alert.AlertType.INFORMATION, "Registration Successful", "VIP User registered successfully!");
                clearFields();
            } else {
                showAlert(Alert.AlertType.ERROR, "Registration Failed", "Username already exists.");
            }
        }
    }

    @FXML
    public void handleBackToLogin() {
        try {
            // Load Login.fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/UserUI/Login.fxml"));
            Parent root = loader.load();

            // Show the Login window
            Stage stage = new Stage();
            stage.setTitle("Login");
            stage.setScene(new Scene(root, 400, 300));
            stage.show();

            // Close the current Register window
            Stage currentStage = (Stage) backButton.getScene().getWindow();
            currentStage.close();
        } catch (Exception e) {
            e.printStackTrace();
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
