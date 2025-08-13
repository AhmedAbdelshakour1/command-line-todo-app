import java.util.List;
import java.util.ArrayList;


public class TodoService {
    private final TaskStorage storage;
    private final List<Task> tasks;

    public TodoService() {
        this.storage = new TaskStorage();
        this.tasks = new ArrayList<>(storage.loadTasks());
    }

    public Task addTask(String description, Priority priority, String[] tags) {

        Task task = Task.create(calculateNextId(), description, priority, tags);
        tasks.add(task);
        saveToStorage();
        return task;
    }


    public List<Task> getTasks(String filter) {
        return tasks.stream()
                .filter(task -> task.matchesFilter(filter))
                .toList();
    }


    public boolean markTaskCompleted(int id) {
        for (int i = 0; i < tasks.size(); i++) {
            Task task = tasks.get(i);
            if (task.id() == id) {
                if (task.completed()) {
                    return false;
                }
                tasks.set(i, task.markCompleted());
                saveToStorage();
                return true;
            }
        }
        return false;
    }


    public boolean deleteTask(int id) {
        boolean removed = tasks.removeIf(task -> task.id() == id);
        if (removed) {
            saveToStorage();
        }
        return removed;
    }



    private int calculateNextId() {
        return tasks.stream()
                .mapToInt(Task::id)
                .max()
                .orElse(0) + 1;
    }

    private void saveToStorage() {
        try {
            storage.saveTasks(tasks);
        } catch (Exception e) {
            System.err.println("Warning: Failed to save tasks to storage: " + e.getMessage());
        }
    }

}
