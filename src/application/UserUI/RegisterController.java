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
