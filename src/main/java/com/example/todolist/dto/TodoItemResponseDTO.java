package com.example.todolist.dto;

import com.example.todolist.entity.TodoItem;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

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
    
    // 计算字段：是否已完成（前端使用 isCompleted）
    public Boolean getIsCompleted() {
        return this.completed;
    }
    
    // 计算字段：是否已过期
    public Boolean getIsOverdue() {
        if (dueDate == null || completed) {
            return false;
        }
        return LocalDateTime.now().isAfter(dueDate);
    }
    
    // 计算字段：距离截止日期的天数
    public Long getDaysUntilDue() {
        if (dueDate == null) {
            return null;
        }
        LocalDateTime now = LocalDateTime.now();
        long days = ChronoUnit.DAYS.between(now.toLocalDate(), dueDate.toLocalDate());
        return days;
    }
}
