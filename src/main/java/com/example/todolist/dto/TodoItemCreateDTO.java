package com.example.todolist.dto;

import com.example.todolist.entity.TodoItem;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

/**
 * 创建待办事项DTO
 */
@Data
public class TodoItemCreateDTO {
    
    @NotBlank(message = "待办事项标题不能为空")
    @Size(max = 200, message = "标题长度不能超过200个字符")
    private String title;
    
    private String description;
    
    private LocalDateTime dueDate;
    
    private LocalDateTime startTime; // 开始时间
    
    private TodoItem.Priority priority = TodoItem.Priority.MEDIUM;
    
    @NotNull(message = "用户ID不能为空")
    private Long userId;
}
