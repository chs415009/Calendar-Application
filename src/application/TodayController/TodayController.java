package application.TodayController;

import application.ToDoItem;
import application.ToDoManager;
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

    private ObservableList<String> navigationItems;
    private ToDoManager toDoManager;

    @FXML
    private void initialize() {
    	// Set the locale to English
        Locale.setDefault(Locale.ENGLISH);
    	
        // Initialize ToDoManager
        toDoManager = new ToDoManager();

        // Initialize navigation items
        navigationItems = FXCollections.observableArrayList("Inbox", "Today", "Upcoming", "Important", "Trash");
        navigationList.setItems(navigationItems);

        // Set a cell factory for bold navigation items
        navigationList.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item);
                    setStyle("-fx-font-weight: bold;"); // Make the text bold
                }
            }
        });
        
        // Navigation list listener
        navigationList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            updateTasks(newValue);
        });

        // Initialize task list view
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
                    Label tagLabel = new Label(item.getTag().toString());
                    tagLabel.setStyle("-fx-background-color: lightgray; -fx-padding: 2 4; -fx-border-radius: 4; -fx-background-radius: 4;");

                    HBox cellBox = new HBox(10, checkBox, titleLabel, tagLabel);
                    setGraphic(cellBox);
                }
            }
        });

        // Task selection listener to display task details
        taskListView.getSelectionModel().selectedItemProperty().addListener((observable, oldTask, newTask) -> {
            if (newTask != null) {
                showTaskDetails(newTask);
            } else {
                taskDetails.setText("Select a task to view its details");
            }
        });

        updateTasks("Inbox");
    }

    private void updateTasks(String category) {
        ObservableList<ToDoItem> filteredTasks = FXCollections.observableArrayList();
        switch (category) {
            case "Inbox" -> filteredTasks.addAll(toDoManager.getAllTasks());
            case "Today" -> filteredTasks.addAll(toDoManager.getTasksForDay(LocalDate.now()));
            case "Important" -> filteredTasks.addAll(toDoManager.filterTasksByTag(ToDoItem.Tag.IMPORTANT));
            case "Trash" -> filteredTasks.addAll(toDoManager.getCompletedTasks());
        }
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
    private void handleAddTask() {
        showAddTaskDialog(null);
    }

    @FXML
    private void handleDeleteTask() {
        ToDoItem selectedTask = taskListView.getSelectionModel().getSelectedItem();
        if (selectedTask != null) {
            toDoManager.deleteTask(selectedTask);
            updateTasks(navigationList.getSelectionModel().getSelectedItem());
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
