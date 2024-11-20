package application.TodayController;

import application.ToDoItem;
import application.ToDoManager;
import application.User.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;

import java.time.LocalDate;
import java.util.Locale;
import java.util.Optional;

public class TodayController {

    @FXML
    private ListView<ToDoItem> taskListView;

    @FXML
    private ListView<String> navigationList;

    @FXML
    private Label taskDetails; // To display task details

    @FXML
    private HBox filterSection; // Reference to the filter section (filter buttons)
    
    private ObservableList<String> navigationItems;
    private ToDoManager toDoManager;

    public void initialize(User user) {
        this.toDoManager = user.getToDoManager(); // Use the logged-in user's ToDoManager
        Locale.setDefault(Locale.ENGLISH);

        // Populate the navigation items
        navigationItems = FXCollections.observableArrayList("Inbox", "Today", "Upcoming", "Important", "Trash");
        navigationList.setItems(navigationItems);

        // Listen to changes in navigation selection
        navigationList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> updateTasks(newValue));

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

        // Listen to task selection changes
        taskListView.getSelectionModel().selectedItemProperty().addListener((observable, oldTask, newTask) -> {
            if (newTask != null) {
                showTaskDetails(newTask);
            }
        });

        // Initialize tasks to "Inbox"
        updateTasks("Inbox");
    }

    private void updateTasks(String category) {
        ObservableList<ToDoItem> filteredTasks = FXCollections.observableArrayList();

        // Control the visibility of the filter section
        if ("Inbox".equals(category) || "Today".equals(category)) {
            filterSection.setVisible(true);
            filterSection.setManaged(true); // Ensures space is allocated when visible
        } else {
            filterSection.setVisible(false);
            filterSection.setManaged(false); // Ensures no space is allocated when hidden
        }

        // Update the task list based on the selected category
        switch (category) {
            case "Inbox" -> filteredTasks.addAll(toDoManager.getAllTasks());
            case "Today" -> filteredTasks.addAll(toDoManager.getTasksForDay(LocalDate.now()));
            case "Upcoming", "Important" , "Trash" -> {
                // Display a blank area for "Upcoming" and "Important"
                taskListView.setItems(FXCollections.observableArrayList()); // No tasks
                taskDetails.setText(""); // Clear task details
            }
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
        showAddTaskDialog(null);
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
}
