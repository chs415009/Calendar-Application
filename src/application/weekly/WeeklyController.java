package application.weekly;

import application.ToDoItem;
import application.ToDoManager;
import application.TodayController.TodayController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.WeekFields;
import java.util.Locale;
import java.util.List;
import application.User.User;

public class WeeklyController {

    @FXML
    private ListView<String> navigationList;
    @FXML
    private GridPane weeklyGrid;
    @FXML
    private Label weekLabel;
    @FXML
    private Label taskDetails;
    @FXML
    private Button previousWeekButton;
    @FXML
    private Button nextWeekButton;

    private LocalDate currentWeekStart;
    private ToDoManager toDoManager;
    private User currentUser;
    private ObservableList<String> navigationItems;
    
    public void initialize(User user) {
        this.toDoManager = user.getToDoManager();
        currentWeekStart = LocalDate.now().with(WeekFields.of(Locale.getDefault()).dayOfWeek(), 1);
        
        // Set up navigation list
        navigationItems = FXCollections.observableArrayList(
            "Inbox", "Today", "Weekly", "Upcoming", "Important", "Trash"
        );
        navigationList.setItems(navigationItems);

        // Add navigation listener
        navigationList.getSelectionModel().selectedItemProperty().addListener(
            (observable, oldValue, newValue) -> {
                if ("Today".equals(newValue)) {
                    loadTodayView();
                }
                // Handle other navigation options if needed
            }
        );
        
        // Set up week navigation
        previousWeekButton.setOnAction(e -> navigateWeek(-1));
        nextWeekButton.setOnAction(e -> navigateWeek(1));
        
        // Initialize the grid
        setupWeeklyGrid();
        updateWeekLabel();
        populateWeeklyTasks();
    }
    
    private void setupWeeklyGrid() {
        // Clear existing content
        weeklyGrid.getChildren().clear();
        
        // Add day headers
        String[] days = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
        for (int i = 0; i < days.length; i++) {
            Label dayLabel = new Label(days[i]);
            dayLabel.setStyle("-fx-font-weight: bold;");
            weeklyGrid.add(dayLabel, i, 0);
        }
        
        // Add content areas for each day
        for (int i = 0; i < 7; i++) {
            ListView<ToDoItem> dayList = new ListView<>();
            dayList.setPrefHeight(300);
            
            // Set cell factory to display tasks
            dayList.setCellFactory(param -> new ListCell<>() {
                @Override
                protected void updateItem(ToDoItem item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                        setGraphic(null);
                    } else {
                        CheckBox checkBox = new CheckBox(item.getTitle());
                        checkBox.setSelected(item.isCompleted());
                        checkBox.setOnAction(e -> {
                            item.setCompleted(checkBox.isSelected());
                            updateTaskDetails(item);
                        });
                        setGraphic(checkBox);
                    }
                }
            });
            
            // Add selection listener for task details
            dayList.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> updateTaskDetails(newValue)
            );
            
            weeklyGrid.add(dayList, i, 1);
        }
    }
    
    private void populateWeeklyTasks() {
        for (int i = 0; i < 7; i++) {
            LocalDate date = currentWeekStart.plusDays(i);
            List<ToDoItem> dayTasks = toDoManager.getTasksForDay(date);
            
            @SuppressWarnings("unchecked")
            ListView<ToDoItem> dayList = (ListView<ToDoItem>) weeklyGrid.getChildren().get(i + 7);
            dayList.setItems(FXCollections.observableArrayList(dayTasks));
        }
    }
    
    private void navigateWeek(int weeks) {
        currentWeekStart = currentWeekStart.plusWeeks(weeks);
        updateWeekLabel();
        populateWeeklyTasks();
    }
    
    private void updateWeekLabel() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy");
        String weekRange = formatter.format(currentWeekStart) + " - " + 
                          formatter.format(currentWeekStart.plusDays(6));
        weekLabel.setText(weekRange);
    }
    
    private void updateTaskDetails(ToDoItem task) {
        if (task != null) {
            taskDetails.setText(
                "Title: " + task.getTitle() + "\n" +
                "Description: " + task.getDescription() + "\n" +
                "Due Date: " + task.getDueDate() + "\n" +
                "Priority: " + task.getPriority() + "\n" +
                "Tag: " + task.getTag() + "\n" +
                "Status: " + (task.isCompleted() ? "Completed" : "Pending")
            );
        } else {
            taskDetails.setText("Select a task to view its details");
        }
    }
    
    private void loadTodayView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/today.fxml"));
            Parent root = loader.load();

            TodayController controller = loader.getController();
            controller.initialize(currentUser);

            Stage stage = (Stage) navigationList.getScene().getWindow();
            Scene scene = new Scene(root, 800, 600);
            scene.getStylesheets().add(getClass().getResource("/application/application.css").toExternalForm());
            
            stage.setScene(scene);
            stage.setTitle("To-Do List - Today");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Navigation Error");
            alert.setHeaderText(null);
            alert.setContentText("Could not load the today view. Error: " + e.getMessage());
            alert.showAndWait();
        }
    }
    
    @FXML
    private void handleAddTask() {
        // Reuse the existing task dialog from TodayController
        // You can either create a new instance of the dialog or
        // extract the dialog creation to a utility class
    }
    
    @FXML
    private void handleDeleteTask() {
        // Find the selected task from any of the day lists
        ToDoItem selectedTask = null;
        for (int i = 0; i < 7; i++) {
            @SuppressWarnings("unchecked")
            ListView<ToDoItem> dayList = (ListView<ToDoItem>) weeklyGrid.getChildren().get(i + 7);
            if (dayList.getSelectionModel().getSelectedItem() != null) {
                selectedTask = dayList.getSelectionModel().getSelectedItem();
                break;
            }
        }
        
        if (selectedTask != null) {
            toDoManager.deleteTask(selectedTask);
            populateWeeklyTasks();
            taskDetails.setText("Select a task to view its details");
        }
    }
}