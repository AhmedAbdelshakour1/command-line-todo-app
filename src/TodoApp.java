import java.util.Scanner;

/**
 * Supports commands:
 * - add "task description" [--priority high|medium|low] [--tags tag1,tag2]
 * - list [--filter completed|pending] [--sort priority|date|name]
 * - done <id>
 * - delete <id>
 * - help
 * - exit
 */
public class TodoApp {
    private final Scanner scanner;
    private boolean running = true;

    public TodoApp() {
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
            case "exit", "quit" -> {
                System.out.println("Goodbye!");
                running = false;
            }
            default -> System.out.println("Unknown command: " + command + ". Type 'help' for available commands.");
        }
    }








    private String getPriorityIcon(Priority priority) {
        return switch (priority) {
            case HIGH -> "🔴";
            case MEDIUM -> "🟡";
            case LOW -> "🟢";
        };
    }

}