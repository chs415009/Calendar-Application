package application.TodayUI;

import application.ToDoItem;
import application.ToDoManager;
import application.MonthlyUI.MonthlyController;
import application.User.User;
import application.User.UserType;
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
import java.util.Iterator;
import java.util.Locale;
import java.util.Optional;

import application.User.UserDirectory;
import application.User.UserDirectoryHolder;
import application.UserUI.LoginController;

public class TodayController {

    @FXML
    private ListView<ToDoItem> taskListView;

    @FXML
    private ListView<String> navigationList;

    @FXML
    private Label taskDetails; // To display task details

    @FXML
    private HBox filterSection; // Reference to the filter section (filter buttons)
    
    @FXML
    private Label dateLabel; // Add reference to the new date label
    
    private ObservableList<String> navigationItems;
    private ToDoManager toDoManager;
    private User currentUser;
    private UserDirectory userDirectory;
    
    

    public void initialize(User user) {
    	this.currentUser = user;
        this.toDoManager = user.getToDoManager(); // Use the logged-in user's ToDoManager
        Locale.setDefault(Locale.ENGLISH);

        // Set today's date in dateLabel
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy");
        dateLabel.setText(today.format(formatter));

        // Default view: show "Tasks" and hide the date
        dateLabel.setVisible(false);
        dateLabel.setManaged(false);
        
        // Populate the navigation items
        navigationItems = FXCollections.observableArrayList("Inbox", "Today", "Weekly", "Monthly");
        navigationList.setItems(navigationItems);
        
        
        // Select "Today" by default
        navigationList.getSelectionModel().select("Today");
        navigationList.getSelectionModel().selectedItemProperty().addListener(
        	    (observable, oldValue, newValue) -> {
        	        if (newValue != null) {
        	            switch (newValue) {
        	                case "Inbox":
        	                    loadInboxView();
        	                    break;
        	                case "Weekly":
        	                    loadWeeklyView();
        	                    break;
        	                case "Monthly":
        	                    loadMonthlyView();
        	                    break;
        	                default:
        	                    // Do nothing for "Today" since this is the current view.
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

    }

    private void updateTasks(String category) {
        ObservableList<ToDoItem> filteredTasks = FXCollections.observableArrayList();

        if ("Today".equals(category)) {
            dateLabel.setVisible(true);
            dateLabel.setManaged(true);
            LocalDate today = LocalDate.now();
            filteredTasks.addAll(toDoManager.getTasksForDay(today));
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
        if(currentUser.getUserType() == UserType.VIP) {
        	VIPAddTaskDialog(null);
        	return;
        }   	   	
    	showAddTaskDialog(null); // Show the dialog to add a task
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
        
        if (selectedTask == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("No Task Selected");
            alert.setHeaderText(null);
            alert.setContentText("Please select a task to delete.");
            alert.showAndWait();
            return;
        }

        if (currentUser.getUserType() == UserType.VIP) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Delete Task");
            alert.setHeaderText(null);
            alert.setContentText("Do you want to delete all tasks with the same name?");

            ButtonType buttonYes = new ButtonType("Yes", ButtonBar.ButtonData.YES);
            ButtonType buttonNo = new ButtonType("No", ButtonBar.ButtonData.NO);
            alert.getButtonTypes().setAll(buttonYes, buttonNo);

            Optional<ButtonType> result = alert.showAndWait();

            if (result.isPresent() && result.get() == buttonYes) {              
                Iterator<ToDoItem> iterator = currentUser.getToDoList().iterator();
                while (iterator.hasNext()) {
                    ToDoItem todo = iterator.next();
                    if (todo.getTitle().equals(selectedTask.getTitle()) && todo.getTag().equals(selectedTask.getTag())) {
                        iterator.remove();
                        toDoManager.deleteTask(todo); 
                    }
                }
            } else {
                currentUser.getToDoList().remove(selectedTask);
                toDoManager.deleteTask(selectedTask); 
            }
        } else {
            currentUser.getToDoList().remove(selectedTask);
            toDoManager.deleteTask(selectedTask); 
        }
        updateTasks(navigationList.getSelectionModel().getSelectedItem());
        taskDetails.setText("Select a task to view its details");
    }

    
    @FXML
    private void handleLogout() {
        if (userDirectory != null) {
            userDirectory.saveUsersToFile("src/application/users.json");
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/UserUI/Login.fxml"));
            Parent root = loader.load();
            
            LoginController loginController = loader.getController();
            loginController.setUserDirectory(UserDirectoryHolder.getUserDirectory());

            Stage currentStage = (Stage) taskListView.getScene().getWindow();
            Scene scene = new Scene(root, 400, 300);
            scene.getStylesheets().add(getClass().getResource("/application/application.css").toExternalForm());

            currentStage.setScene(scene);
            currentStage.setTitle("Login");
            currentStage.setWidth(400);
            currentStage.setHeight(300);
            currentStage.centerOnScreen();

        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Logout Error");
            alert.setHeaderText(null);
            alert.setContentText("Could not load the login view. Error: " + e.getMessage());
            alert.showAndWait();
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
        dueDatePicker.setEditable(false);
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
                if (titleField.getText().isEmpty() || dueDatePicker.getValue() == null) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setContentText("Title and Due Date are required.");
                    alert.showAndWait();
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
        });
        updateTasks(navigationList.getSelectionModel().getSelectedItem());
    }
    
    private void VIPAddTaskDialog(ToDoItem task) {
        Dialog<ToDoItem> dialog = new Dialog<>();
        dialog.setTitle("Add Task");

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
        Label infoLabel = new Label( "If only creating one task, ignore below.");
        infoLabel.setStyle("-fx-font-weight: bold;");
        ComboBox<String> frequencyComboBox = new ComboBox<>(FXCollections.observableArrayList("Daily", "Weekly", "Monthly"));
        frequencyComboBox.setPromptText("Frequency");
        frequencyComboBox.setValue(""); 
        TextField quantityField = new TextField();
        quantityField.setPromptText("Quantity");
        quantityField.setTextFormatter(new TextFormatter<>(change -> 
            (change.getControlNewText().matches("\\d*")) ? change : null)); 

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
        grid.add(new Label(), 0, 5); 
        grid.add(infoLabel, 0, 6, 2, 1); 
        grid.add(new Label("Frequency:"), 0, 7);
        grid.add(frequencyComboBox, 1, 7);
        grid.add(new Label("Quantity:"), 0, 8);
        grid.add(quantityField, 1, 8);

        dialog.getDialogPane().setContent(grid);

        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                
                if (titleField.getText().isEmpty() ||
                    descriptionField.getText().isEmpty() ||
                    dueDatePicker.getValue() == null ||
                    priorityComboBox.getValue() == null ||
                    tagComboBox.getValue() == null) {

                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Invalid Input");
                    alert.setHeaderText(null);
                    alert.setContentText("Please fill in all required fields.");
                    alert.showAndWait();

                    VIPAddTaskDialog(task); // Reopen dialog
                    return null;
                }

                
                if ((frequencyComboBox.getValue().isEmpty() && !quantityField.getText().isEmpty()) || 
                    (!frequencyComboBox.getValue().isEmpty() && quantityField.getText().isEmpty())) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Invalid Input");
                    alert.setHeaderText(null);
                    alert.setContentText("If you select a frequency, you must also specify a quantity.");
                    alert.showAndWait();
                    
                    VIPAddTaskDialog(task); // Reopen dialog
                    return null;
                }

                if (!frequencyComboBox.getValue().isEmpty() && !quantityField.getText().isEmpty()) {
                    int quantity = Integer.parseInt(quantityField.getText());
                    String frequency = frequencyComboBox.getValue();
                    LocalDate startDate = dueDatePicker.getValue();

                    for (int i = 0; i < quantity; i++) {
                        LocalDate newDueDate = calculateNextDueDate(startDate, frequency, i);
                        ToDoItem newTask = new ToDoItem(
                            titleField.getText(),
                            descriptionField.getText(),
                            newDueDate,
                            priorityComboBox.getValue(),
                            tagComboBox.getValue()
                        );
                        toDoManager.addTask(newTask); 
                    }
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
            }
        });
        updateTasks(navigationList.getSelectionModel().getSelectedItem());
    }
    
    //Used for VIPAddTaskDialog
    private LocalDate calculateNextDueDate(LocalDate startDate, String frequency, int index) {
        switch (frequency.toLowerCase()) {
            case "daily":
                return startDate.plusDays(index);
            case "weekly":
                return startDate.plusWeeks(index);
            case "monthly":
                return startDate.plusMonths(index);
            default:
                throw new IllegalArgumentException("Invalid frequency: " + frequency);
        }
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
    
    private void loadInboxView() {
        try {
            // Get current window dimensions
            Stage currentStage = (Stage) navigationList.getScene().getWindow();
            double width = currentStage.getWidth();
            double height = currentStage.getHeight();

            // Load the Inbox FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/InboxUI/inbox.fxml"));
            Parent root = loader.load();

            // Get the controller and initialize it
            application.InboxUI.InboxController controller = loader.getController();
            controller.initialize(currentUser);

            // Set the new scene
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/application/application.css").toExternalForm());
            currentStage.setScene(scene);
            currentStage.setTitle("To-Do List - Inbox");
            currentStage.setWidth(width);
            currentStage.setHeight(height);
        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Navigation Error");
            alert.setHeaderText(null);
            alert.setContentText("Could not load the Inbox view. Error: " + e.getMessage());
            alert.showAndWait();
        }
    }
    
}
