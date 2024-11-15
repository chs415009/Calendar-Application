package application;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ToDoManager {
	
	private List<ToDoItem> toDoList;
	
	// Constructor (initialize to-do list)
    public ToDoManager() {
        this.toDoList = new ArrayList<>();
    }
    
    public void addTask(ToDoItem task) {
        toDoList.add(task);
    }
    
    public void deleteTask(ToDoItem task) {
        toDoList.remove(task);
    }
    
    public void editTask(ToDoItem task, String newTitle, String newDescription, LocalDate newDueDate,
            			 ToDoItem.Priority newPriority, ToDoItem.Tag newTag) {
		task.editTask(newTitle, newDescription, newDueDate, newPriority, newTag);
    }
    
    // choose a specific tag
    public List<ToDoItem> filterTasksByTag(ToDoItem.Tag tag) {
        List<ToDoItem> filteredTasks = new ArrayList<>();
        for (int i = 0; i < toDoList.size(); i++) { 
            ToDoItem task = toDoList.get(i);
            if (task.getTag() == tag) {
                filteredTasks.add(task);
            }
        }
        return filteredTasks;
    }
    
    // choose a priority level
    public List<ToDoItem> filterTasksByPriority(ToDoItem.Priority priority) {
        List<ToDoItem> filteredTasks = new ArrayList<>();
        for (int i = 0; i < toDoList.size(); i++) { 
            ToDoItem task = toDoList.get(i);
            if (task.getPriority() == priority) {
                filteredTasks.add(task);
            }
        }
        return filteredTasks;
    }
    
    // choose for a date
    public List<ToDoItem> getTasksForDay(LocalDate date) {
        List<ToDoItem> tasksForDay = new ArrayList<>();
        for (int i = 0; i < toDoList.size(); i++) { 
            ToDoItem task = toDoList.get(i);
            if (task.getDueDate().equals(date)) {
                tasksForDay.add(task);
            }
        }
        return tasksForDay;
    }
    
    public List<ToDoItem> getCompletedTasks() {
        List<ToDoItem> completedTasks = new ArrayList<>();
        for (int i = 0; i < toDoList.size(); i++) { 
            ToDoItem task = toDoList.get(i);
            if (task.isCompleted()) {
                completedTasks.add(task);
            }
        }
        return completedTasks;
    }

    public List<ToDoItem> getPendingTasks() {
        List<ToDoItem> pendingTasks = new ArrayList<>();
        for (int i = 0; i < toDoList.size(); i++) { 
            ToDoItem task = toDoList.get(i);
            if (!task.isCompleted()) {
                pendingTasks.add(task);
            }
        }
        return pendingTasks;
    }
    
    // for returns a list of all to-do items, view all ToDoItem objects
    public List<ToDoItem> getAllTasks() {
        return new ArrayList<>(toDoList); 
    }
    
    // go through ToDoManager without direct access to ToDoItem, enhancing encapsulation.
    public void markTaskAsCompleted(ToDoItem task) {
        task.markAsCompleted();
    }
}