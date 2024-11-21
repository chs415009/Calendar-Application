package application;
	
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import application.csvhandler.*;


public class Main extends Application {
	@Override
	public void start(Stage primaryStage) {
		try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/User/UserUI/Login.fxml"));
            Parent root = loader.load();

            Scene scene = new Scene(root, 400, 300);
            primaryStage.setTitle("To Do List - Login");
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
//    // Monthly page test main code
//    public void start(Stage primaryStage) throws Exception {
//        FXMLLoader loader = new FXMLLoader(getClass().getResource("monthly/ui/Monthly.fxml"));
//        primaryStage.setScene(new Scene(loader.load()));
//        primaryStage.setTitle("Monthly Calendar");
//        primaryStage.show();
//    }
	
	
	public static void main(String[] args) {
		launch(args);
	}
}
