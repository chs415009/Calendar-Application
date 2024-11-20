package application;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import application.ToDoItem.Priority;
import application.ToDoItem.Tag;
import application.User.User;

public class VIPToDoManager extends ToDoManager {

	private List<ToDoItem> toDoList;	
	public VIPToDoManager(User user) {
		super(user);
	}
	
	public void createPeriodicToDo(String title, String description, String Date1, String Date2, Priority priority, Tag tag, String frequency) {
		
		// Strings convert to LocalDate
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd"); //String format: "yyyy-MM-dd"        
        LocalDate startDate = LocalDate.parse(Date1, formatter);
        LocalDate endDate = LocalDate.parse(Date2, formatter);
		
		LocalDate curDate = startDate;
		
		// Create toTo periodically
		while (!curDate.isAfter(endDate)) {
			ToDoItem toDoItem = new ToDoItem(title, description, curDate, priority, tag);
			toDoList.add(toDoItem);  
			
			if (frequency.equalsIgnoreCase("weekly")) {
				curDate = curDate.plus(1, ChronoUnit.WEEKS); 
			} 
			else if (frequency.equalsIgnoreCase("monthly")) {
				curDate = curDate.plus(1, ChronoUnit.MONTHS);
			} 
			else if (frequency.equalsIgnoreCase("daily")) {
				curDate = curDate.plus(1, ChronoUnit.DAYS);
			} 
			else {
				throw new IllegalArgumentException("Invalid frequency: " + frequency);	
			}
		}
	}
	
	public void deleteSameNameTodo(String name) {
        
		Iterator<ToDoItem> iterator = toDoList.iterator();
        // Go through list to find same name todo
		while (iterator.hasNext()) {
            ToDoItem toDoItem = iterator.next();
            if (toDoItem.getTitle().equals(name)) {
                iterator.remove(); 
            }
        }
    }
	
	public void updateSameNameTodo(ToDoItem updatedTodo) {
	    
		Iterator<ToDoItem> iterator = toDoList.iterator();
		// Go through list to find same name todo
	    while (iterator.hasNext()) {
	        ToDoItem toDoItem = iterator.next();
	        //when found, call editTask method from ToDoItem class
	        if (toDoItem.getTitle().equals(updatedTodo.getTitle())) {
	            toDoItem.editTask(updatedTodo.getTitle(), updatedTodo.getDescription(), updatedTodo.getDueDate(), updatedTodo.getPriority(), updatedTodo.getTag());
	            toDoItem.setCompleted(updatedTodo.isCompleted());
	        }
	    }
	}
	
	

}
