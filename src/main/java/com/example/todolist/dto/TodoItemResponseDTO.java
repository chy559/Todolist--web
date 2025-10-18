package com.example.todolist.dto;

import com.example.todolist.entity.TodoItem;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 待办事项响应DTO
 */
@Data
public class TodoItemResponseDTO {
    
    private Long id;
    
    private String title;
    
    private String description;
    
    private Boolean completed;
    
    private LocalDateTime createTime;
    
    private LocalDateTime updateTime;
    
    private LocalDateTime dueDate;
    
    private LocalDateTime startTime; // 开始时间
    
    private LocalDateTime completedTime;
    
    private TodoItem.Priority priority;
    
    private Long userId;
    
    private String username; // 用户名，方便前端显示
}
