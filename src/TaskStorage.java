import java.io.*;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;


public class TaskStorage {
    private static final Path FILE_PATH = Paths.get("tasks.txt");
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    private static final String DELIMITER = "|";
    private static final String NULL_VALUE = "NULL";


    public List<Task> loadTasks() {

        if (!Files.exists(FILE_PATH)) {
            return new ArrayList<>();
        }

        try {
            List<String> lines = Files.readAllLines(FILE_PATH);
            return parseTasksFromLines(lines);
        } catch (IOException e) {
            System.err.println("Warning: Could not load tasks from tasks.txt" + ": " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public void saveTasks(List<Task> tasks) throws IOException {
        List<String> lines = generateLinesFromTasks(tasks);
        Files.write(FILE_PATH, lines);
    }


    private List<Task> parseTasksFromLines(List<String> lines) {
        List<Task> tasks = new ArrayList<>();

        for (String line : lines) {
            line = line.trim();
            if (line.isEmpty() || line.startsWith("#")) {
                continue;
            }
            Task task = parseTaskFromLine(line);
            if (task != null) {
                tasks.add(task);
            }
        }

        return tasks;
    }

    private Task parseTaskFromLine(String line) {
        try {
            String[] parts = line.split("\\|", -1);

            if (parts.length < 6) {
                System.err.println("Warning: Invalid task line format: " + line);
                return null;
            }

            int id = Integer.parseInt(parts[0]);
            String description = parts[1];
            Priority priority = Priority.valueOf(parts[2]);
            boolean completed = Boolean.parseBoolean(parts[3]);
            LocalDateTime createdAt = LocalDateTime.parse(parts[4], DATE_FORMATTER);

            LocalDateTime completedAt = null;
            if (!parts[5].equals(NULL_VALUE)) {
                completedAt = LocalDateTime.parse(parts[5], DATE_FORMATTER);
            }

            String[] tags = new String[0];
            if (parts.length > 6 && !parts[6].isEmpty()) {
                tags = parts[6].split(",");
                for (int i = 0; i < tags.length; i++) {
                    tags[i] = tags[i].trim();
                }
            }

            return new Task(id, description, priority, completed, createdAt, completedAt, tags);

        } catch (Exception e) {
            System.err.println("Warning: Error parsing task line '" + line + "': " + e.getMessage());
            return null;
        }
    }


    private List<String> generateLinesFromTasks(List<Task> tasks) {
        List<String> lines = new ArrayList<>();

        lines.add("# To-Do App Tasks File");
        lines.add("# Format: id|description|priority|completed|createdAt|completedAt|tags");
        lines.add("");

        for (Task task : tasks) {
            StringBuilder line = new StringBuilder();

            line.append(task.id()).append(DELIMITER);
            line.append(escapeText(task.description())).append(DELIMITER);
            line.append(task.priority().name()).append(DELIMITER);
            line.append(task.completed()).append(DELIMITER);
            line.append(task.createdAt().format(DATE_FORMATTER)).append(DELIMITER);

            if (task.completedAt() != null) {
                line.append(task.completedAt().format(DATE_FORMATTER));
            } else {
                line.append(NULL_VALUE);
            }
            line.append(DELIMITER);

            if (task.tags().length > 0) {
                line.append(String.join(",", task.tags()));
            }

            lines.add(line.toString());
        }

        return lines;
    }


    private String escapeText(String text) {
        if (text == null) return "";
        return text.replace(DELIMITER, "\\|")
                .replace("\n", "\\n")
                .replace("\r", "\\r");
    }
}