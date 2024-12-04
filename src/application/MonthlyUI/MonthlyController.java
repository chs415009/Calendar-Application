package application.MonthlyUI;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.scene.Node;

import java.io.IOException;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import application.ToDoItem;
import application.ToDoManager;
import application.TodayUI.TodayController;
import application.User.User;
import application.User.UserDirectory;
import application.User.UserDirectoryHolder;
import application.UserUI.LoginController;
import application.User.UserType;
import application.WeeklyUI.WeeklyController;

public class MonthlyController {
    @FXML
    private Label monthLabel;
    @FXML
    private GridPane calendarGrid;
    @FXML
    private HBox headerBox;
    @FXML
    private Label taskDetails;
    @FXML
    private ListView<String> navigationList;
    
    private ToDoManager toDoManager;
    private User currentUser;
    private YearMonth currentYearMonth;
    private ObservableList<String> navigationItems;
    private UserDirectory userDirectory;
    

    public void initialize(User user) {
    	this.currentUser = user;
        this.toDoManager = user.getToDoManager();
        
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

        // Select "Monthly" by default
        navigationList.getSelectionModel().select("Monthly");

        navigationList.getSelectionModel().selectedItemProperty().addListener(
        	    (observable, oldValue, newValue) -> {
        	        if (newValue != null) {
        	            switch (newValue) {
        	                case "Weekly":
        	                    loadWeeklyView();
        	                    break;
        	                case "Inbox":
        	                    loadInboxView();
        	                    break;
        	                case "Today":
        	                    loadTodayView(newValue);
        	                    break;
        	                default:
        	                    // Do nothing for "Monthly" since this is the current view.
        	                    break;
        	            }
        	        }
        	    }
        	);

    	
    	currentYearMonth = YearMonth.now();
        updateCalendar();
        populateMonthlyTasks();
    }

//    @FXML
//    public void setStage(Stage stage) {
//        stage.setMaximized(true); // 設定視窗最大化
//    }

    @FXML
    private void handlePreviousMonth() {
        currentYearMonth = currentYearMonth.minusMonths(1);
        updateCalendar();
        populateMonthlyTasks();
    }

    @FXML
    private void handleNextMonth() {
        currentYearMonth = currentYearMonth.plusMonths(1);
        updateCalendar();
        populateMonthlyTasks();
    }

    private void updateCalendar() {
    	calendarGrid.setGridLinesVisible(true);
        calendarGrid.getChildren().clear();

        // 月份標題
        monthLabel.setText(currentYearMonth.getMonth().toString() + " " + currentYearMonth.getYear());
        monthLabel.setFont(Font.font(30)); // 調整字體大小

        // 添加星期幾標籤
        String[] daysOfWeek = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
        for (int i = 0; i < 7; i++) {
            Label dayOfWeekLabel = new Label(daysOfWeek[i]);
            dayOfWeekLabel.setStyle("-fx-font-size: 20; -fx-alignment: center;");
            calendarGrid.add(dayOfWeekLabel, i, 0);
        }

        // 計算每月的天數與第一天是星期幾
        LocalDate firstDayOfMonth = currentYearMonth.atDay(1);
        int dayOfWeek = firstDayOfMonth.getDayOfWeek().getValue() % 7;
        int daysInMonth = currentYearMonth.lengthOfMonth();

        int row = 1, column = dayOfWeek;

        // 填充日期格子
        for (int day = 1; day <= daysInMonth; day++) {
            // 使用 VBox 來顯示日期數字和 ListView
            VBox dayBox = new VBox();

            // 顯示當日數字
            Label dayLabel = new Label(String.valueOf(day));
            dayLabel.setStyle("-fx-font-size: 14; -fx-alignment: top-left; -fx-padding: 5;");
            dayBox.getChildren().add(dayLabel);

            // 創建 ListView 來顯示任務
            ListView<ToDoItem> dayList = new ListView<>();
            dayList.setPrefSize(80, 80);
            
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
                            showTaskDetails(item);
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
                (observable, oldValue, newValue) -> showTaskDetails(newValue)
            );

            dayBox.getChildren().add(dayList);  // Add the ListView under the day label

            // Add VBox to the calendar grid
            calendarGrid.add(dayBox, column, row);

            // 更新列與行
            column++;
            if (column > 6) {
                column = 0;
                row++;
            }
            
        }
        populateMonthlyTasks();
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

        ToDoItem selectedTask = null;       
        LocalDate firstDayOfMonth = currentYearMonth.atDay(1);
        LocalDate lastDayOfMonth = currentYearMonth.atEndOfMonth();
        
        int daysInMonth = lastDayOfMonth.getDayOfMonth();
        int dayOfWeek = firstDayOfMonth.getDayOfWeek().getValue() % 7;
        int row = 1, column = dayOfWeek;
        
        for (int day = 1; day <= daysInMonth; day++) {
            VBox dayBox = (VBox) getNodeFromGrid(calendarGrid, column, row);  
            if (dayBox != null) {
                @SuppressWarnings("unchecked")
                ListView<ToDoItem> dayList = (ListView<ToDoItem>) dayBox.getChildren().get(1);  
                
                if (dayList.getSelectionModel().getSelectedItem() != null) {
                    selectedTask = dayList.getSelectionModel().getSelectedItem();
                    break;
                }
            }

            column++;
            if (column > 6) {
                column = 0;
                row++;
            }
        }
        
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
        populateMonthlyTasks();
        taskDetails.setText("Select a task to view its details");
    }


    
    @FXML
    private void handleLogout() {
        UserDirectory userDirectory = UserDirectoryHolder.getUserDirectory();
        if (userDirectory != null) {
            userDirectory.saveUsersToFile("src/application/users.json");
        }

        try {
            // Load Login view
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/UserUI/Login.fxml"));
            Parent root = loader.load();

            LoginController loginController = loader.getController();
            loginController.setUserDirectory(userDirectory); // 传递 userDirectory

            Stage currentStage = (Stage) navigationList.getScene().getWindow();
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
            populateMonthlyTasks();
        });
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
        populateMonthlyTasks();
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
        
        LocalDate firstDayOfMonth = currentYearMonth.atDay(1);
        LocalDate lastDayOfMonth = currentYearMonth.atEndOfMonth();

        // 計算當月的總天數
        int daysInMonth = lastDayOfMonth.getDayOfMonth();

        // 計算當月的第一天是星期幾
        int dayOfWeek = firstDayOfMonth.getDayOfWeek().getValue() % 7;

        int row = 1, column = dayOfWeek;
        
        for (int day = 1; day <= daysInMonth; day++) {
            LocalDate date = firstDayOfMonth.withDayOfMonth(day);  // 計算當前的日期
            
            // 找到對應日期的 ListView 元素
            VBox dayBox = (VBox) getNodeFromGrid(calendarGrid, column, row);  // 獲取當前格子的 VBox
            if (dayBox != null) {
                @SuppressWarnings("unchecked")
				ListView<ToDoItem> dayList = (ListView<ToDoItem>) dayBox.getChildren().get(1);  // 假設 ListView 是在 VBox 中的第二個元素
                
                if (dayList.getSelectionModel().getSelectedItem() != null) {
                    selectedTask = dayList.getSelectionModel().getSelectedItem();
                    break;
                }
            }

            // 更新列與行
            column++;
            if (column > 6) {
                column = 0;
                row++;
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
    
    private void showTaskDetails(ToDoItem task) {
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
    
    private void populateMonthlyTasks() {
        // 獲取當月的第一天和最後一天
        LocalDate firstDayOfMonth = currentYearMonth.atDay(1);
        LocalDate lastDayOfMonth = currentYearMonth.atEndOfMonth();

        // 計算當月的總天數
        int daysInMonth = lastDayOfMonth.getDayOfMonth();

        // 計算當月的第一天是星期幾
        int dayOfWeek = firstDayOfMonth.getDayOfWeek().getValue() % 7;

        int row = 1, column = dayOfWeek;
        
        for (int day = 1; day <= daysInMonth; day++) {
            LocalDate date = firstDayOfMonth.withDayOfMonth(day);  // 計算當前的日期

            // 獲取當天的任務列表
            List<ToDoItem> dayTasks = toDoManager.getTasksForDay(date);
            
            // 找到對應日期的 ListView 元素
            VBox dayBox = (VBox) getNodeFromGrid(calendarGrid, column, row);  // 獲取當前格子的 VBox
            if (dayBox != null) {
                @SuppressWarnings("unchecked")
				ListView<ToDoItem> dayList = (ListView<ToDoItem>) dayBox.getChildren().get(1);  // 假設 ListView 是在 VBox 中的第二個元素
                
                dayList.setItems(FXCollections.observableArrayList(dayTasks));
            }

            // 更新列與行
            column++;
            if (column > 6) {
                column = 0;
                row++;
            }
        }
    }
    
    private Node getNodeFromGrid(GridPane grid, int column, int row) {
        for (Node node : grid.getChildren()) {
            if (GridPane.getColumnIndex(node) == column && GridPane.getRowIndex(node) == row) {
                return node;
            }
        }
        return null;
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
