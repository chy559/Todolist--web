package com.example.todolist.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

/**
 * 待办事项实体类
 */
@Setter
@Getter
@Entity
@Table(name = "todo_items")
public class TodoItem {

    // Getter和Setter方法
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "待办事项标题不能为空")
    @Size(max = 200, message = "标题长度不能超过200个字符")
    @Column(nullable = false, length = 200)
    private String title;
    
    @Column(columnDefinition = "TEXT")// 超长文本
    private String description;
    
    @Column(nullable = false)
    private Boolean completed = false;
    
    @Column(name = "create_time", nullable = false, updatable = false)
    private LocalDateTime createTime;
    
    @Column(name = "update_time")
    private LocalDateTime updateTime;
    
    @Column(name = "start_time")
    private LocalDateTime startTime; // 开始时间
    
    @Column(name = "due_date")
    private LocalDateTime dueDate;
    
    @Column(name = "completed_time")
    private LocalDateTime completedTime;
    
    // 优先级：LOW(低), MEDIUM(中), HIGH(高)
    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private Priority priority = Priority.MEDIUM;
    
    // 多对一关系：多个待办事项属于一个用户
    @ManyToOne(fetch = FetchType.LAZY)// 延迟加载（懒加载)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @PrePersist
    protected void onCreate() {
        createTime = LocalDateTime.now();
        updateTime = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updateTime = LocalDateTime.now();
        // 如果任务被标记为完成，记录完成时间
        if (completed && completedTime == null) {
            completedTime = LocalDateTime.now();
        }
        // 如果任务被取消完成状态，清除完成时间
        if (!completed && completedTime != null) {
            completedTime = null;
        }
    }
    
    // 优先级枚举
    public enum Priority {
        LOW,    // 低优先级
        MEDIUM, // 中优先级
        HIGH    // 高优先级
    }
    
    // 构造函数
    public TodoItem() {
    }
    
    public TodoItem(String title, String description, User user) {
        this.title = title;
        this.description = description;
        this.user = user;
    }

    @Override
    public String toString() {
        return "TodoItem{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", completed=" + completed +
                ", priority=" + priority +
                ", dueDate=" + dueDate +
                ", createTime=" + createTime +
                '}';
    }
}

