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
import javafx.scene.layout.HBox;
import javafx.geometry.Insets;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.WeekFields;
import java.util.Locale;
import java.util.List;
import java.util.Optional;
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
        this.currentUser = user;
        this.toDoManager = user.getToDoManager();
        currentWeekStart = LocalDate.now().with(WeekFields.of(Locale.getDefault()).dayOfWeek(), 1);
        
        // Set up navigation list
        navigationItems = FXCollections.observableArrayList(
            "Inbox", "Today", "Weekly", "Upcoming", "Important", "Trash"
        );
        navigationList.setItems(navigationItems);

        // Set up cell factory for styling
        navigationList.setCellFactory(lv -> new ListCell<String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item);
                    if (item.equals("Weekly")) {
                        setStyle("-fx-background-color: #0096c9; -fx-text-fill: white;");
                    } else {
                        setStyle("");
                    }
                }
            }
        });

        // Select "Weekly" by default
        navigationList.getSelectionModel().select("Weekly");

        // Add navigation listener with expanded handling
        navigationList.getSelectionModel().selectedItemProperty().addListener(
            (observable, oldValue, newValue) -> {
                if ("Today".equals(newValue) || "Inbox".equals(newValue) || 
                    "Important".equals(newValue) || "Trash".equals(newValue)) {
                    loadTodayView(newValue);
                }
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
                        CheckBox checkBox = new CheckBox();
                        checkBox.setSelected(item.isCompleted());
                        checkBox.setOnAction(e -> {
                            item.setCompleted(checkBox.isSelected());
                            updateTaskDetails(item);
                        });

                        Label titleLabel = new Label(item.getTitle());
                        Label tagLabel = new Label(item.getTag() != null ? item.getTag().toString() : "No Tag");
                        tagLabel.setStyle("-fx-background-color: lightgray; -fx-padding: 2 4; -fx-border-radius: 4; -fx-background-radius: 4;");

                        HBox cellBox = new HBox(10, checkBox, titleLabel, tagLabel);
                        setGraphic(cellBox);
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
    
    private void loadTodayView(String selectedItem) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/today.fxml"));
            Parent root = loader.load();

            TodayController controller = loader.getController();
            controller.initialize(currentUser);
            controller.selectNavigationItem(selectedItem);

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
        showAddTaskDialog(null);
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

    private void showAddTaskDialog(ToDoItem task) {
        Dialog<ToDoItem> dialog = new Dialog<>();
        dialog.setTitle(task == null ? "Add Task" : "Edit Task");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField titleField = new TextField();
        titleField.setPromptText("Title");
        TextField descriptionField = new TextField();
        descriptionField.setPromptText("Description");
        DatePicker dueDatePicker = new DatePicker();
        ComboBox<ToDoItem.Priority> priorityComboBox = new ComboBox<>(FXCollections.observableArrayList(ToDoItem.Priority.values()));
        ComboBox<ToDoItem.Tag> tagComboBox = new ComboBox<>(FXCollections.observableArrayList(ToDoItem.Tag.values()));

        if (task != null) {
            titleField.setText(task.getTitle());
            descriptionField.setText(task.getDescription());
            dueDatePicker.setValue(task.getDueDate());
            priorityComboBox.setValue(task.getPriority());
            tagComboBox.setValue(task.getTag());
        }

        grid.add(new Label("Title:"), 0, 0);
        grid.add(titleField, 1, 0);
        grid.add(new Label("Description:"), 0, 1);
        grid.add(descriptionField, 1, 1);
        grid.add(new Label("Due Date:"), 0, 2);
        grid.add(dueDatePicker, 1, 2);
        grid.add(new Label("Priority:"), 0, 3);
        grid.add(priorityComboBox, 1, 3);
        grid.add(new Label("Tag:"), 0, 4);
        grid.add(tagComboBox, 1, 4);

        dialog.getDialogPane().setContent(grid);

        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                // Validate required fields
                if (titleField.getText().isEmpty() || 
                    descriptionField.getText().isEmpty() || 
                    dueDatePicker.getValue() == null || 
                    priorityComboBox.getValue() == null || 
                    tagComboBox.getValue() == null) {
                    
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle(null);
                    alert.setHeaderText(null);
                    alert.setContentText("Please fill in all fields.");
                    alert.showAndWait();

                    showAddTaskDialog(task);
                    return null;
                }

                return new ToDoItem(
                    titleField.getText(),
                    descriptionField.getText(),
                    dueDatePicker.getValue(),
                    priorityComboBox.getValue(),
                    tagComboBox.getValue()
                );
            }
            return null;
        });

        Optional<ToDoItem> result = dialog.showAndWait();
        result.ifPresent(newTask -> {
            if (task == null) {
                toDoManager.addTask(newTask);
            } else {
                task.editTask(
                    newTask.getTitle(),
                    newTask.getDescription(),
                    newTask.getDueDate(),
                    newTask.getPriority(),
                    newTask.getTag()
                );
            }
            populateWeeklyTasks();
        });
    }
}