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
            // 加载用户数据
            userDirectory = UserDirectory.loadUsersFromFile(FILE_NAME);

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/UserUI/Login.fxml"));
            Parent root = loader.load();

            // 设置 UserDirectory 给控制器
            LoginController controller = loader.getController();
            controller.setUserDirectory(userDirectory);

            Scene scene = new Scene(root, 400, 300);
            primaryStage.setTitle("To Do List - Login");
            primaryStage.setScene(scene);
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


