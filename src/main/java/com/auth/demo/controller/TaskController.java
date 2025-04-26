package com.auth.demo.controller;

import com.auth.demo.entity.Task;
import com.auth.demo.entity.User;
import com.auth.demo.model.TaskModel; // Use TaskModel from the model package
import com.auth.demo.repository.UserRepository;
import com.auth.demo.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller for handling task-related operations.
 * Provides endpoints for creating, updating, deleting, and fetching tasks.
 */
@RestController
@RequestMapping("/tasks")
public class TaskController {

    @Autowired
    private TaskService taskService;

    @Autowired
    private UserRepository userRepository;

    /**
     * Endpoint to create a new task.
     *
     * @param taskModel Task details from the request body.
     * @param authentication Authenticated user's details.
     * @return The created task.
     */
    @PostMapping
    public ResponseEntity<TaskModel> createTask(@RequestBody TaskModel taskModel, Authentication authentication) {
        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Task task = convertToEntity(taskModel);
        task.setUser(user);
        Task savedTask = taskService.saveTask(task);
        return ResponseEntity.ok(convertToModel(savedTask));
    }

    @GetMapping
    public ResponseEntity<List<TaskModel>> getTasks(Authentication authentication) {
        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Task> tasks;
        if ("ADMIN".equals(user.getRole())) {
            tasks = taskService.getAllTasks();
        } else {
            tasks = taskService.getTasksByUserId(user.getId());
        }

        List<TaskModel> taskModels = tasks.stream().map(this::convertToModel).collect(Collectors.toList());
        return ResponseEntity.ok(taskModels);
    }

    @PutMapping("/{taskId}")
    public ResponseEntity<TaskModel> updateTask(
            @PathVariable Long taskId,
            @RequestBody TaskModel updatedTaskModel,
            Authentication authentication) {
        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Task updatedTask = convertToEntity(updatedTaskModel);
        Task task = taskService.updateTask(taskId, updatedTask, user.getId());
        return ResponseEntity.ok(convertToModel(task));
    }

    @DeleteMapping("/{taskId}")
    public ResponseEntity<String> deleteTask(
            @PathVariable Long taskId,
            Authentication authentication) {
        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        taskService.deleteTask(taskId, user.getId());
        return ResponseEntity.ok("Task deleted successfully.");
    }

    private TaskModel convertToModel(Task task) {
        TaskModel model = new TaskModel();
        model.setId(task.getId());
        model.setTitle(task.getTitle());
        model.setDescription(task.getDescription());
        model.setPriority(task.getPriority());
        model.setDeadline(task.getDeadline());
        model.setStatus(task.getStatus());
        model.setAssignedTo(task.getUser().getEmail());
        return model;
    }

    private Task convertToEntity(TaskModel model) {
        Task task = new Task();
        task.setId(model.getId());
        task.setTitle(model.getTitle());
        task.setDescription(model.getDescription());
        task.setPriority(model.getPriority());
        task.setDeadline(model.getDeadline());
        task.setStatus(model.getStatus());
        return task;
    }
}