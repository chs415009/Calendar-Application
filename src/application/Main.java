package application;

import application.User.UserDirectory;
import application.UserUI.LoginController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;

public class Main extends Application {
    private static final String FILE_NAME = "src/application/users.json";
    private static UserDirectory userDirectory;

    @Override
    public void start(Stage primaryStage) {
        try {
            UserDirectory userDirectory = UserDirectory.loadUsersFromFile("src/application/users.json");

            // Load Login.fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/UserUI/Login.fxml"));
            Parent root = loader.load();

            // Set the UserDirectory in the LoginController
            LoginController loginController = loader.getController();
            loginController.setUserDirectory(userDirectory);

            primaryStage.setScene(new Scene(root, 400, 300));
            primaryStage.setTitle("Login");
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        // 添加 JVM 关闭时保存数据的钩子
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if (userDirectory != null) {
                userDirectory.saveUsersToFile(FILE_NAME);
            }
        }));

        launch(args);
    }
}


