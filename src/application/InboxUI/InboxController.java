package application.InboxUI;

import application.ToDoItem;
import application.ToDoManager;
import application.MonthlyUI.MonthlyController;
import application.TodayUI.TodayController;
import application.User.User;
import application.WeeklyUI.WeeklyController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Optional;

public class InboxController {

    @FXML
    private ListView<ToDoItem> taskListView;

    @FXML
    private ListView<String> navigationList;

    @FXML
    private Label taskDetails; // To display task details

    @FXML
    private HBox filterSection; // Reference to the filter section (filter buttons)
    
    @FXML
    private Label tasksTitle; // Reference to the "Tasks" label title
    
    private ObservableList<String> navigationItems;
    private ToDoManager toDoManager;
    private User currentUser;
    

    public void initialize(User user) {
    	this.currentUser = user;
        this.toDoManager = user.getToDoManager(); // Use the logged-in user's ToDoManager
        Locale.setDefault(Locale.ENGLISH);

        // Show "Tasks" title and filter section by default
        tasksTitle.setVisible(true);
        tasksTitle.setManaged(true);
        filterSection.setVisible(true);
        filterSection.setManaged(true);
        
        // Populate the navigation items
        navigationItems = FXCollections.observableArrayList("Inbox", "Today", "Weekly", "Monthly");
        navigationList.setItems(navigationItems);
        
        // Select "Inbox" by default
        navigationList.getSelectionModel().select("Inbox");
        navigationList.getSelectionModel().selectedItemProperty().addListener(
        	    (observable, oldValue, newValue) -> {
        	        if (newValue != null) {
        	            switch (newValue) {
        	                case "Today":
        	                	loadTodayView(newValue);
        	                    break;
        	                case "Weekly":
        	                    loadWeeklyView();
        	                    break;
        	                case "Monthly":
        	                    loadMonthlyView();
        	                    break;
        	                default:
        	                    // Do nothing for "Inbox" since this is the current view.
        	                    break;
        	            }
        	        }
        	    }
        	);

        // Configure the task list cells
        taskListView.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(ToDoItem item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    CheckBox checkBox = new CheckBox();
                    checkBox.setSelected(item.isCompleted());
                    checkBox.setOnAction(e -> item.setCompleted(checkBox.isSelected()));

                    Label titleLabel = new Label(item.getTitle());
                    Label tagLabel = new Label(item.getTag() != null ? item.getTag().toString() : "No Tag");
                    tagLabel.setStyle("-fx-background-color: lightgray; -fx-padding: 2 4; -fx-border-radius: 4; -fx-background-radius: 4;");

                    HBox cellBox = new HBox(10, checkBox, titleLabel, tagLabel);
                    setGraphic(cellBox);
                }
            }
        });
        
        navigationList.setCellFactory(lv -> new ListCell<String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item);
                    if (getListView().getSelectionModel().getSelectedItem() != null &&
                        getListView().getSelectionModel().getSelectedItem().equals(item)) {
                        setStyle("-fx-background-color: #0096c9; -fx-text-fill: white;");
                    } else {
                        setStyle("");
                    }
                }
            }
        });
        


        // Listen to task selection changes
        taskListView.getSelectionModel().selectedItemProperty().addListener((observable, oldTask, newTask) -> {
            if (newTask != null) {
                showTaskDetails(newTask);
            }
        });

        // Initialize tasks to "Inbox"
        navigationList.getSelectionModel().select("Inbox");
        updateTasks("Inbox"); // Load tasks for the "Inbox" category

    }

    private void updateTasks(String category) {
        ObservableList<ToDoItem> filteredTasks = FXCollections.observableArrayList();

        if ("Inbox".equals(category)) {
            tasksTitle.setVisible(true);
            tasksTitle.setManaged(true);
            filterSection.setVisible(true);
            filterSection.setManaged(true);

            // 加載所有任務
            filteredTasks.addAll(toDoManager.getAllTasks());
        }

        // Update the task list view
        taskListView.setItems(filteredTasks);
    }


    private void showTaskDetails(ToDoItem task) {
        // Display task details
        taskDetails.setText(
                "Title: " + task.getTitle() + "\n" +
                "Description: " + task.getDescription() + "\n" +
                "Due Date: " + (task.getDueDate() != null ? task.getDueDate() : "No date set") + "\n" +
                "Priority: " + task.getPriority() + "\n" +
                "Tag: " + task.getTag()
        );
    }
    
    @FXML
    private void handleFilterAll() {
        taskListView.setItems(FXCollections.observableArrayList(toDoManager.getAllTasks()));
    }

    @FXML
    private void handleFilterErrand() {
        taskListView.setItems(FXCollections.observableArrayList(toDoManager.filterTasksByTag(ToDoItem.Tag.ERRAND)));
    }

    @FXML
    private void handleFilterHome() {
        taskListView.setItems(FXCollections.observableArrayList(toDoManager.filterTasksByTag(ToDoItem.Tag.HOME)));
    }

    @FXML
    private void handleFilterOffice() {
        taskListView.setItems(FXCollections.observableArrayList(toDoManager.filterTasksByTag(ToDoItem.Tag.OFFICE)));
    }

    @FXML
    private void handleFilterImportant() {
        taskListView.setItems(FXCollections.observableArrayList(toDoManager.filterTasksByTag(ToDoItem.Tag.IMPORTANT)));
    }
    
    @FXML
    private void handleFilterPending() {
        taskListView.setItems(FXCollections.observableArrayList(toDoManager.filterTasksByTag(ToDoItem.Tag.PENDING)));
    }

    @FXML
    private void handleAddTask() {
        showAddTaskDialog(null); // Show the dialog to add a task

        // Refresh the task list to display the new task immediately
        String selectedCategory = navigationList.getSelectionModel().getSelectedItem();
        updateTasks(selectedCategory); // Update the tasks based on the current category
    }
    
    @FXML
    private void handleEditTask() {
        ToDoItem selectedTask = taskListView.getSelectionModel().getSelectedItem();
        if (selectedTask != null) {
            // Open the dialog with the selected task's details pre-filled
            showAddTaskDialog(selectedTask);
        } else {
            // Show an alert if no task is selected
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("No Task Selected");
            alert.setHeaderText(null);
            alert.setContentText("Please select a task to edit.");
            alert.showAndWait();
        }
    }

    @FXML
    private void handleDeleteTask() {
        ToDoItem selectedTask = taskListView.getSelectionModel().getSelectedItem();
        if (selectedTask != null) {
            toDoManager.deleteTask(selectedTask);
            updateTasks(navigationList.getSelectionModel().getSelectedItem());

            // Clear the task details since the task was deleted
            taskDetails.setText("Select a task to view its details");
        }
    }
    
    @FXML
    private void handleLogout() {
        //Feiyu:.....
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
        dueDatePicker.setEditable(false); // Disable manual typing
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
                // Validate fields
                if (titleField.getText().isEmpty() ||
                    descriptionField.getText().isEmpty() ||
                    dueDatePicker.getValue() == null ||
                    priorityComboBox.getValue() == null ||
                    tagComboBox.getValue() == null) {

                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle(null); // No title
                    alert.setHeaderText(null); // No header
                    alert.setContentText("Please fill in all fields.");
                    alert.showAndWait();

                    // Reopen dialog if validation fails
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
            updateTasks(navigationList.getSelectionModel().getSelectedItem());
        });
    }
    
    public void selectNavigationItem(String item) {
        navigationList.getSelectionModel().select(item);
        updateTasks(item);
    }
    
    private void loadWeeklyView() {
        try {
            // Get current window dimensions
            Stage currentStage = (Stage) navigationList.getScene().getWindow();
            double width = currentStage.getWidth();
            double height = currentStage.getHeight();
            
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/WeeklyUI/weekly.fxml"));
            Parent root = loader.load();

            WeeklyController controller = loader.getController();
            controller.initialize(currentUser);

            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/application/application.css").toExternalForm());

            // Apply the size before showing
            currentStage.setScene(scene);
            currentStage.setTitle("To-Do List - Weekly View");
            currentStage.setWidth(width);
            currentStage.setHeight(height);
        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Navigation Error");
            alert.setHeaderText(null);
            alert.setContentText("Could not load the weekly view. Error: " + e.getMessage());
            alert.showAndWait();
        }
    }
    
    private void loadMonthlyView() {
    	try {
            // Get current window dimensions
            Stage currentStage = (Stage) navigationList.getScene().getWindow();
            double width = currentStage.getWidth();
            double height = currentStage.getHeight();
            
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/MonthlyUI/Monthly.fxml"));
            Parent root = loader.load();

            MonthlyController controller = loader.getController();
            controller.initialize(currentUser);

            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/application/application.css").toExternalForm());

            // Apply the size before showing
            currentStage.setScene(scene);
            currentStage.setTitle("To-Do List - Monthly View");
            currentStage.setWidth(width);
            currentStage.setHeight(height);
        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Navigation Error");
            alert.setHeaderText(null);
            alert.setContentText("Could not load the Monthly view. Error: " + e.getMessage());
            alert.showAndWait();
        }
    }
    
    private void loadTodayView(String selectedItem) {
        try {
            // Get current window dimensions
            Stage currentStage = (Stage) navigationList.getScene().getWindow();
            double width = currentStage.getWidth();
            double height = currentStage.getHeight();
            
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/TodayUI/today.fxml"));
            Parent root = loader.load();

            TodayController controller = loader.getController();
            controller.initialize(currentUser);
            controller.selectNavigationItem(selectedItem);

            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/application/application.css").toExternalForm());
            
            // Apply the size before showing
            currentStage.setScene(scene);
            currentStage.setTitle("To-Do List - Today");
            currentStage.setWidth(width);
            currentStage.setHeight(height);
        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Navigation Error");
            alert.setHeaderText(null);
            alert.setContentText("Could not load the today view. Error: " + e.getMessage());
            alert.showAndWait();
        }
    }

}