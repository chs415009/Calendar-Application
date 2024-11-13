package application;

import java.time.LocalDate;

public class ToDoItem {
	
	private String title; 
	private String description; 
	private LocalDate dueDate; 
	private Priority priority;  	// priority level of the to-do item
	private Tag tag; 				// tag/category of the to-do item
	private boolean isCompleted;	// completion status of the to-do item
	
	// Constructor
	public ToDoItem(String title, String description, LocalDate dueDate, Priority priority, Tag tag) {
        this.title = title;
        this.description = description;
        this.dueDate = dueDate;
        this.priority = priority;
        this.tag = tag;
        this.isCompleted = false; 	// default is not completed
    }
	
	// predefined tags for tasks
	public enum Tag {
        ERRAND, HOME, OFFICE, IMPORTANT, PENDING
    }

    // priority levels for tasks
    public enum Priority {
        HIGH, MEDIUM, LOW
    }
	
	public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public Tag getTag() {
        return tag;
    }

    public void setTag(Tag tag) {
        this.tag = tag;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }
    
    // mark the to-do item as completed (Encapsulation)
    public void markAsCompleted() {
        this.isCompleted = true;
    }
    
    // In case if you want to modify multiple properties of a ToDoItem at once, such as the title, description, due date, etc., 
    // the editTask() method allows us to complete it in a single method call, without needing to invoke multiple set methods separately.
    // Method to edit the to-do item details (Encapsulation)
    public void editTask(String newTitle, String newDescription, LocalDate newDueDate, Priority newPriority, Tag newTag) {
        this.title = newTitle;
        this.description = newDescription;
        this.dueDate = newDueDate;
        this.priority = newPriority;
        this.tag = newTag;
    }
	
	@Override
    public String toString() {
        return "ToDoItem{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", dueDate=" + dueDate +
                ", priority='" + priority + '\'' +
                ", tag='" + tag + '\'' +
                ", isCompleted=" + isCompleted +
                '}';
    }
	
}
