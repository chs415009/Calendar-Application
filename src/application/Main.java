package application;
	
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import application.csvhandler.*;


public class Main extends Application {
	@Override
	public void start(Stage primaryStage) {
		try {
			BorderPane root = new BorderPane();
			Scene scene = new Scene(root,400,400);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		testCSVHandler();
		launch(args);
	}
	
	
	public static void testCSVHandler() {
		ToDoManager todoManager = new ToDoManager();
		List<ToDoItem> todos = generateSampleTodoItems();
		for (ToDoItem item : todos) {
		    todoManager.addTask(item);
		}
		CSVHandler csvHandler = new CSVHandler();
		try {
		    csvHandler.exportToCSV(todoManager.getAllTasks(), "sample_todos.csv");
		    System.out.println("Successfully exported todos to CSV!");
		    
		    // Test CSV import
		    List<ToDoItem> importedTodos = csvHandler.importFromCSV("sample_todos.csv");
		    System.out.println("Successfully imported " + importedTodos.size() + " todos from CSV!");
		} catch (IOException e) {
		    e.printStackTrace();
		}
	}
	
	
	public static List<ToDoItem> generateSampleTodoItems() {
	        List<ToDoItem> sampleTodos = new ArrayList<>();

	        // Add office tasks
	        sampleTodos.add(new ToDoItem(
	            "Quarterly Report",
	            "Complete Q4 financial report",
	            LocalDate.now().plusDays(7),
	            ToDoItem.Priority.HIGH,
	            ToDoItem.Tag.OFFICE
	        ));

	        sampleTodos.add(new ToDoItem(
	            "Team Meeting",
	            "Weekly team sync-up",
	            LocalDate.now().plusDays(1),
	            ToDoItem.Priority.MEDIUM,
	            ToDoItem.Tag.OFFICE
	        ));

	        // Add home tasks
	        sampleTodos.add(new ToDoItem(
	            "Grocery Shopping",
	            "Buy groceries for the week",
	            LocalDate.now(),
	            ToDoItem.Priority.MEDIUM,
	            ToDoItem.Tag.HOME
	        ));

	        sampleTodos.add(new ToDoItem(
	            "Home Maintenance",
	            "Fix leaking kitchen faucet",
	            LocalDate.now().plusDays(3),
	            ToDoItem.Priority.LOW,
	            ToDoItem.Tag.HOME
	        ));

	        // Add errands
	        sampleTodos.add(new ToDoItem(
	            "Car Service",
	            "Take car for regular maintenance",
	            LocalDate.now().plusDays(5),
	            ToDoItem.Priority.HIGH,
	            ToDoItem.Tag.ERRAND
	        ));

	        // Add important tasks
	        sampleTodos.add(new ToDoItem(
	            "Dentist Appointment",
	            "Annual dental checkup",
	            LocalDate.now().plusDays(14),
	            ToDoItem.Priority.HIGH,
	            ToDoItem.Tag.IMPORTANT
	        ));

	        // Add some completed tasks
	        ToDoItem completedTask = new ToDoItem(
	            "Pay Bills",
	            "Pay utility bills",
	            LocalDate.now().minusDays(1),
	            ToDoItem.Priority.HIGH,
	            ToDoItem.Tag.IMPORTANT
	        );
	        completedTask.markAsCompleted();
	        sampleTodos.add(completedTask);

	        // Add pending tasks
	        sampleTodos.add(new ToDoItem(
	            "Birthday Gift",
	            "Buy birthday gift for mom",
	            LocalDate.now().plusDays(10),
	            ToDoItem.Priority.MEDIUM,
	            ToDoItem.Tag.PENDING
	        ));

	        return sampleTodos;
	}
}
