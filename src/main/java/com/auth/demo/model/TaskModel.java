package com.auth.demo.model;

import lombok.*;

import java.util.Date;

/**
 * Data Transfer Object (DTO) for Task entity.
 * Used for API requests and responses.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class TaskModel {

    private Long id;
    private String title;
    private String description;
    private String priority;
    private Date deadline;
    private String status;
    private String assignedTo; // Email of the user
}