import java.time.LocalDateTime;

public record Task(
        int id,
        String description,
        Priority priority,
        boolean completed,
        LocalDateTime createdAt,
        LocalDateTime completedAt,
        String[] tags
) {

    public static Task create(int id, String description, Priority priority, String[] tags) {
        return new Task(id, description, priority, false, LocalDateTime.now(), null, tags);
    }

    public Task markCompleted() {
        return new Task(id, description, priority, true, createdAt, LocalDateTime.now(), tags);
    }

    public boolean matchesFilter(String filter) {
        if (filter == null) return true;

        return switch (filter.toLowerCase()) {
            case "completed" -> completed;
            case "pending" -> !completed;
            default -> true;
        };
    }
}

enum Priority {
    HIGH,
    MEDIUM,
    LOW
}