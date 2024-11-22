package application;

import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class CSVHandler {
    private static final String DELIMITER = ",";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;

    // Export todos to CSV file
    public void exportToCSV(List<ToDoItem> toDoList, String filePath) throws IOException {
        try (FileWriter writer = new FileWriter(filePath);
             BufferedWriter bufferedWriter = new BufferedWriter(writer)) {
            
            // Write header
            bufferedWriter.write("Title,Description,DueDate,Priority,Tag,IsCompleted");
            bufferedWriter.newLine();
            
            // Write each todo item
            for (ToDoItem item : toDoList) {
                String line = String.join(DELIMITER,
                    escapeSpecialCharacters(item.getTitle()),
                    escapeSpecialCharacters(item.getDescription()),
                    item.getDueDate().format(DATE_FORMATTER),
                    item.getPriority().toString(),
                    item.getTag().toString(),
                    String.valueOf(item.isCompleted())
                );
                bufferedWriter.write(line);
                bufferedWriter.newLine();
            }
        }
    }

    // Import todos from CSV file
    public List<ToDoItem> importFromCSV(String filePath) throws IOException {
        List<ToDoItem> importedList = new ArrayList<>();
        
        try (FileReader reader = new FileReader(filePath);
             BufferedReader bufferedReader = new BufferedReader(reader)) {
            
            // Skip header line
            String line = bufferedReader.readLine();
            
            // Read and parse each line
            while ((line = bufferedReader.readLine()) != null) {
                String[] values = line.split(DELIMITER);
                if (values.length == 6) { // Ensure we have all required fields
                    ToDoItem item = new ToDoItem(
                        unescapeSpecialCharacters(values[0]), // Title
                        unescapeSpecialCharacters(values[1]), // Description
                        LocalDate.parse(values[2], DATE_FORMATTER), // DueDate
                        ToDoItem.Priority.valueOf(values[3]), // Priority
                        ToDoItem.Tag.valueOf(values[4]) // Tag
                    );
                    
                    // Set completion status
                    if (Boolean.parseBoolean(values[5])) {
                        item.markAsCompleted();
                    }
                    
                    importedList.add(item);
                }
            }
        }
        return importedList;
    }

    // Helper method to escape special characters in CSV
    private String escapeSpecialCharacters(String data) {
        if (data == null) {
            return "";
        }
        String escapedData = data.replaceAll("\"", "\"\"");
        if (escapedData.contains(",") || escapedData.contains("\"") || escapedData.contains("\n")) {
            escapedData = "\"" + escapedData + "\"";
        }
        return escapedData;
    }

    // Helper method to unescape special characters from CSV
    private String unescapeSpecialCharacters(String data) {
        if (data == null || data.isEmpty()) {
            return "";
        }
        // Remove surrounding quotes if present
        if (data.startsWith("\"") && data.endsWith("\"")) {
            data = data.substring(1, data.length() - 1);
        }
        // Replace escaped quotes with single quotes
        return data.replaceAll("\"\"", "\"");
    }
}