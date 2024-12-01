package application.WeeklyUI;

import application.ToDoItem;
import application.ToDoManager;
import application.MonthlyUI.MonthlyController;
import application.TodayUI.TodayController;
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
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import application.User.User;
import application.User.UserType;
import javafx.scene.layout.Priority;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.RowConstraints;

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
        navigationItems = FXCollections.observableArrayList("Inbox", "Today", "Weekly", "Monthly");

        navigationList.setItems(navigationItems);

        // Set cell factory for styling
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

        // Select "Weekly" by default
        navigationList.getSelectionModel().select("Weekly");

        navigationList.getSelectionModel().selectedItemProperty().addListener(
        	    (observable, oldValue, newValue) -> {
        	        if (newValue != null) {
        	            switch (newValue) {
        	                case "Inbox":
        	                    loadInboxView();
        	                    break;
        	                case "Today":
        	                    loadTodayView(newValue);
        	                    break;
        	                case "Monthly":
        	                    loadMonthlyView();
        	                    break;
        	                default:
        	                    // Do nothing for "Weekly" since this is the current view.
        	                    break;
        	            }
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
            // Set minimum height but allow expansion
            dayList.setMinHeight(100);
            
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
            
            // Add the list to the grid
            weeklyGrid.add(dayList, i, 1);
            
            // Make the list take up available vertical space
            GridPane.setVgrow(dayList, Priority.ALWAYS);
            // Make columns evenly sized
            GridPane.setHgrow(dayList, Priority.ALWAYS);
            
            // Add column constraints to make columns equal width
            ColumnConstraints column = new ColumnConstraints();
            column.setPercentWidth(100.0 / 7.0); // Divide space equally
            column.setHgrow(Priority.ALWAYS);
            weeklyGrid.getColumnConstraints().add(column);
        }
        
        // Add row constraints
        RowConstraints headerRow = new RowConstraints();
        headerRow.setMinHeight(30); // Set minimum height for header
        headerRow.setPrefHeight(30);
        
        RowConstraints contentRow = new RowConstraints();
        contentRow.setVgrow(Priority.ALWAYS); // Make content row take available space
        contentRow.setMinHeight(100); // Set minimum height
        
        weeklyGrid.getRowConstraints().addAll(headerRow, contentRow);
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
    
    public void loadMonthlyView() {
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
    
    @FXML
    private void handleAddTask() {
        if(currentUser.getUserType() == UserType.VIP) {
        	VIPAddTaskDialog(null);
        	return;
        }
    	   	
    	showAddTaskDialog(null); // Show the dialog to add a task
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
        
        if (selectedTask == null) {
        	// Show an alert if no task is selected
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("No Task Selected");
            alert.setHeaderText(null);
            alert.setContentText("Please select a task to delete.");
            alert.showAndWait();
            return;
        }
        
        if(currentUser.getUserType() == UserType.VIP) {
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
        	    while(iterator.hasNext()) {
        	    	ToDoItem todo = iterator.next();
        	    	if(todo.getTitle().equals(selectedTask.getTitle()) && todo.getTag().equals(selectedTask.getTag())) {
        	    		iterator.remove();
        	    	}
        	    }
        	}
        	else {
        		toDoManager.deleteTask(selectedTask);
        	}
        }
         
        populateWeeklyTasks();
        taskDetails.setText("Select a task to view its details");
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
        });
        populateWeeklyTasks();
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
        frequencyComboBox.setValue(""); // 預設為空白
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
        populateWeeklyTasks();
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
    
    @FXML
    private void handleEditTask() {
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
