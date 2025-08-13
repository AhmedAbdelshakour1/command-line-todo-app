import java.util.Scanner;

/**
 * Supports commands:
 * - add "task description" [--priority high|medium|low] [--tags tag1,tag2]
 * - list [--filter completed|pending]
 * - done <id>
 * - delete <id>
 * - help
 * - exit
 */
public class TodoApp {
    private final TodoService todoService;
    private final Scanner scanner;
    private boolean running = true;

    public TodoApp() {
        this.todoService = new TodoService();
        this.scanner = new Scanner(System.in);
    }

    public static void main(String[] args) {
        System.out.println("=== Command-Line To-Do App ===");
        System.out.println("Type 'help' for available commands or 'exit' to quit.");
        System.out.println();

        TodoApp app = new TodoApp();
        app.run();
    }

    public void run() {
        while (running) {
            System.out.print("todo> ");
            String input = scanner.nextLine().trim();

            if (input.isEmpty()) {
                continue;
            }

            try {
                processCommand(input);
            } catch (Exception e) {
                System.err.println("Error: " + e.getMessage());
            }
        }

        scanner.close();
    }

    private void processCommand(String input) {
        String[] parts = input.split("\\s+", 2);
        if( parts.length == 0) {
            System.out.println("No command entered. Type 'help' for available commands.");
            return;
        }
        String command = parts[0].toLowerCase();
        if(parts.length == 1) {
            if(!command.equals("help") && !command.equals("exit") && !command.equals("list")) {
                System.out.println("No arguments provided for command: " + command + ". Type 'help' for available commands.");
                return;
            }
        }

        switch (command) {
            case "add" -> handleAdd(parts);
            case "list" -> handleList(parts);
            case "done" -> handleDone(parts);
            case "delete" -> handleDelete(parts);
            case "help" -> showHelp();
            case "exit", "quit" -> {
                System.out.println("Goodbye!");
                running = false;
            }
            default -> System.out.println("Unknown command: " + command + ". Type 'help' for available commands.");
        }
    }


    private void handleAdd(String[] parts) {
        if (parts.length < 2) {
            System.out.println("Usage: add \"task description\" [--priority high|medium|low] [--tags tag1,tag2]");
            return;
        }

        String description = parts[1].replaceAll("^\"|\"$", "").trim();
        if(description.isEmpty()) {
            System.out.println("Task description cannot be empty.");
            return;
        }
        Priority priority = Priority.MEDIUM;
        String[] tags = new String[0];

        for (int i = 2; i < parts.length; i++) {
            if (parts[i].equals("--priority")) {
                if(i + 1 < parts.length) {
                    try {
                        priority = Priority.valueOf(parts[i + 1].toUpperCase());
                        i++;
                    } catch (IllegalArgumentException e) {
                        System.out.println("Invalid priority. Use: high, medium, or low");
                        return;
                    }
                }else {
                    System.out.println("No priority specified. Use: --priority high|medium|low");
                    return;
                }
            } else if (parts[i].equals("--tags")) {
                if(i + 1 < parts.length) {
                    tags = parts[i + 1].split(",");
                    for (int j = 0; j < tags.length; j++) {
                        tags[j] = tags[j].trim();
                    }
                    break;
                }else {
                    System.out.println("No tags specified. Use: --tags tag1,tag2");
                    return;
                }
            }
        }

        Task task = todoService.addTask(description, priority, tags);
        System.out.println("Added task #" + task.id() + ": " + task.description());
    }

    private void handleList(String[] parts) {
        String filter = null;

        for (int i = 1; i < parts.length; i++) {
            if (parts[i].equals("--filter") && i + 1 < parts.length) {
                filter = parts[i + 1];
                i++;
            }
        }

        var tasks = todoService.getTasks(filter);

        if (tasks.isEmpty()) {
            System.out.println("No tasks found.");
            return;
        }

        System.out.println("\nTasks:");
        System.out.println("─────────────────────────────────────────────");

        for (Task task : tasks) {
            String status = task.completed() ? "✓" : "○";
            String priorityIcon = getPriorityIcon(task.priority());
            String tagsStr = task.tags().length > 0 ? " [" + String.join(", ", task.tags()) + "]" : "";

            System.out.printf("%s #%d %s %s%s%n",
                    status, task.id(), priorityIcon, task.description(), tagsStr);
            System.out.printf("    Created: %s%n",
                    task.createdAt().toString().replace('T', ' ').substring(0, 19));

            if (task.completed() && task.completedAt() != null) {
                System.out.printf("    Completed: %s%n",
                        task.completedAt().toString().replace('T', ' ').substring(0, 19));
            }
            System.out.println();
        }
    }

    private void handleDone(String[] parts) {
        if (parts.length < 2) {
            System.out.println("Usage: done <task_id>");
            return;
        }

        try {
            int id = Integer.parseInt(parts[1]);
            if (todoService.markTaskCompleted(id)) {
                System.out.println("Task #" + id + " marked as completed!");
            } else {
                System.out.println("Task #" + id + " not found or already completed.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid task ID. Please provide a number.");
        }
    }

    private void handleDelete(String[] parts) {
        if (parts.length < 2) {
            System.out.println("Usage: delete <task_id>");
            return;
        }

        try {
            int id = Integer.parseInt(parts[1]);
            if (todoService.deleteTask(id)) {
                System.out.println("Task #" + id + " deleted!");
            } else {
                System.out.println("Task #" + id + " not found.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid task ID. Please provide a number.");
        }
    }

    private String getPriorityIcon(Priority priority) {
        return switch (priority) {
            case HIGH -> "🔴";
            case MEDIUM -> "🟡";
            case LOW -> "🟢";
        };
    }

    private void showHelp() {
        System.out.println("""
            
            Available Commands:
            ──────────────────────────────────────────────────────────
            
            add "description" [options]    Add a new task
              --priority high|medium|low   Set task priority (default: medium)
              --tags tag1,tag2            Add tags to task
              
            list [options]                 List all tasks
              --filter completed|pending   Filter tasks by status
              
            done <id>                      Mark task as completed
            delete <id>                    Delete a task
            help                          Show this help message
            exit                          Quit the application
            
            Examples:
            ──────────────────────────────────────────────────────────
            add "Buy milk" --priority high --tags shopping,food
            list --filter pending
            done 3
            delete 5
            """
        );
    }
}