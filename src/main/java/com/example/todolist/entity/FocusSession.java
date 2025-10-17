package com.example.todolist.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * 专注时间会话实体类
 */
@Setter
@Getter
@Entity
@Table(name = "focus_sessions")
public class FocusSession {

    // Getter和Setter方法
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotNull(message = "专注时长不能为空")
    @Min(value = 1, message = "专注时长至少为1分钟")
    @Column(nullable = false)
    private Integer duration; // 专注时长（分钟）
    
    @Column(name = "start_time")
    private LocalDateTime startTime; // 开始时间
    
    @Column(name = "end_time")
    private LocalDateTime endTime; // 结束时间
    
    @Column(name = "actual_duration")
    private Integer actualDuration; // 实际专注时长（分钟），用于记录提前结束的情况
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private SessionStatus status = SessionStatus.PENDING; // 会话状态
    
    @Column(name = "create_time", nullable = false, updatable = false)
    private LocalDateTime createTime;
    
    @Column(name = "update_time")
    private LocalDateTime updateTime;
    
    @Column(length = 500)
    private String notes; // 备注信息
    
    // 多对一关系：多个专注会话属于一个用户
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    // 会话状态枚举
    public enum SessionStatus {
        PENDING,    // 待开始
        IN_PROGRESS, // 进行中
        COMPLETED,   // 已完成
        CANCELLED    // 已取消
    }
    
    @PrePersist
    protected void onCreate() {
        createTime = LocalDateTime.now();
        updateTime = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updateTime = LocalDateTime.now();
    }
    
    // 构造函数
    public FocusSession() {
    }
    
    public FocusSession(Integer duration, User user) {
        this.duration = duration;
        this.user = user;
    }
    
    // 开始专注会话
    public void start() {
        this.status = SessionStatus.IN_PROGRESS;
        this.startTime = LocalDateTime.now();
        this.endTime = this.startTime.plusMinutes(this.duration);
    }
    
    // 完成专注会话
    public void complete() {
        this.status = SessionStatus.COMPLETED;
        if (this.startTime != null) {
            this.actualDuration = (int) java.time.Duration.between(this.startTime, LocalDateTime.now()).toMinutes();
        }
    }
    
    // 取消专注会话
    public void cancel() {
        this.status = SessionStatus.CANCELLED;
        if (this.startTime != null) {
            this.actualDuration = (int) java.time.Duration.between(this.startTime, LocalDateTime.now()).toMinutes();
        }
    }

    @Override
    public String toString() {
        return "FocusSession{" +
                "id=" + id +
                ", duration=" + duration +
                ", status=" + status +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", actualDuration=" + actualDuration +
                '}';
    }
}

