package com.example.todolist.dto;

import com.example.todolist.entity.TodoItem;
import lombok.Data;

import javax.validation.constraints.Size;
import java.time.LocalDateTime;

/**
 * 更新待办事项DTO
 */
@Data
public class TodoItemUpdateDTO {
    
    @Size(max = 200, message = "标题长度不能超过200个字符")
    private String title;
    
    private String description;
    
    private Boolean completed;
    
    private LocalDateTime dueDate;
    
    private LocalDateTime startTime; // 开始时间
    
    private TodoItem.Priority priority;
}
