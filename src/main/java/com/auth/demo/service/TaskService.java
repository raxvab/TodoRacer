package com.auth.demo.service;

import com.auth.demo.entity.Task;
import com.auth.demo.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service class for handling task-related business logic.
 * Provides methods for creating, updating, deleting, and retrieving tasks.
 */
@Service
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;

    /**
     * Saves a new task to the database after validation.
     *
     * @param task The task to be saved.
     * @return The saved task.
     */
    public Task saveTask(Task task) {
        validateTask(task); // Validate task before saving
        return taskRepository.save(task);
    }

    /**
     * Retrieves tasks assigned to a specific user by their user ID.
     *
     * @param userId The ID of the user.
     * @return A list of tasks assigned to the user.
     */
    public List<Task> getTasksByUserId(Long userId) {
        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException("Invalid user ID");
        }
        return taskRepository.findByUserId(userId);
    }

    /**
     * Updates an existing task in the database.
     *
     * @param taskId      The ID of the task to be updated.
     * @param updatedTask The updated task details.
     * @param userId      The ID of the user making the update request.
     * @return The updated task.
     */
    public Task updateTask(Long taskId, Task updatedTask, Long userId) {
        if (taskId == null || taskId <= 0) {
            throw new IllegalArgumentException("Invalid task ID");
        }
        validateTask(updatedTask); // Validate task before updating
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found with ID: " + taskId));
        // Ensure the user is authorized to update the task
        if (!task.getUser().getId().equals(userId)) {
            throw new SecurityException("You are not authorized to update this task");
        }
        // Update task fields
        task.setTitle(updatedTask.getTitle());
        task.setDescription(updatedTask.getDescription());
        task.setPriority(updatedTask.getPriority());
        task.setDeadline(updatedTask.getDeadline());
        task.setStatus(updatedTask.getStatus());
        return taskRepository.save(task);
    }

    /**
     * Deletes a task from the database.
     *
     * @param taskId The ID of the task to be deleted.
     * @param userId The ID of the user making the delete request.
     */
    public void deleteTask(Long taskId, Long userId) {
        if (taskId == null || taskId <= 0) {
            throw new IllegalArgumentException("Invalid task ID");
        }
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found with ID: " + taskId));
        // Ensure the user is authorized to delete the task
        if (!task.getUser().getId().equals(userId)) {
            throw new SecurityException("You are not authorized to delete this task");
        }
        taskRepository.deleteById(taskId);
    }

    /**
     * Retrieves all tasks from the database.
     * This method is typically used by admin users.
     *
     * @return A list of all tasks.
     */
    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    /**
     * Validates the task object to ensure it meets the required criteria.
     *
     * @param task The task to be validated.
     */
    private void validateTask(Task task) {
        if (task == null) {
            throw new IllegalArgumentException("Task cannot be null");
        }
        if (task.getTitle() == null || task.getTitle().isEmpty()) {
            throw new IllegalArgumentException("Task title cannot be null or empty");
        }
        if (task.getPriority() != null && !List.of("LOW", "MEDIUM", "HIGH").contains(task.getPriority().toUpperCase())) {
            throw new IllegalArgumentException("Invalid priority value. Allowed values are: LOW, MEDIUM, HIGH");
        }
        if (task.getDeadline() != null && task.getDeadline().before(new java.util.Date())) {
            throw new IllegalArgumentException("Deadline cannot be in the past");
        }
    }
}