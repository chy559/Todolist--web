package com.example.todolist.entity;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * 纪念日实体类
 */
@Entity
@Table(name = "anniversaries")
public class Anniversary {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "纪念日标题不能为空")
    @Size(max = 200, message = "标题长度不能超过200个字符")
    @Column(nullable = false, length = 200)
    private String title;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @NotNull(message = "目标日期不能为空")
    @Column(name = "target_date", nullable = false)
    private LocalDate targetDate; // 纪念日目标日期
    
    @Column(name = "is_recurring")
    private Boolean isRecurring = false; // 是否每年循环
    
    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private AnniversaryType type = AnniversaryType.OTHER; // 纪念日类型
    
    @Column(name = "create_time", nullable = false, updatable = false)
    private LocalDateTime createTime;
    
    @Column(name = "update_time")
    private LocalDateTime updateTime;
    
    @Column(name = "remind_days_before")
    private Integer remindDaysBefore; // 提前提醒天数
    
    // 多对一关系：多个纪念日属于一个用户
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    // 纪念日类型枚举
    public enum AnniversaryType {
        BIRTHDAY,      // 生日
        WEDDING,       // 结婚纪念日
        RELATIONSHIP,  // 恋爱纪念日
        WORK,          // 工作纪念日
        HOLIDAY,       // 节日
        OTHER          // 其他
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
    public Anniversary() {
    }
    
    public Anniversary(String title, LocalDate targetDate, User user) {
        this.title = title;
        this.targetDate = targetDate;
        this.user = user;
    }
    
    // 计算距离纪念日的天数（倒计时）
    public long getDaysUntil() {
        LocalDate today = LocalDate.now();
        LocalDate nextOccurrence = targetDate;
        
        // 如果是循环纪念日且今年的日期已过，计算明年的
        if (isRecurring && targetDate.isBefore(today)) {
            nextOccurrence = targetDate.withYear(today.getYear());
            if (nextOccurrence.isBefore(today)) {
                nextOccurrence = nextOccurrence.plusYears(1);
            }
        }
        
        return ChronoUnit.DAYS.between(today, nextOccurrence);
    }
    
    // 检查是否已过期（非循环纪念日）
    public boolean isExpired() {
        if (isRecurring) {
            return false; // 循环纪念日不会过期
        }
        return targetDate.isBefore(LocalDate.now());
    }
    
    // 检查是否是今天
    public boolean isToday() {
        LocalDate today = LocalDate.now();
        if (isRecurring) {
            return targetDate.getMonth() == today.getMonth() && 
                   targetDate.getDayOfMonth() == today.getDayOfMonth();
        }
        return targetDate.equals(today);
    }
    
    // Getter和Setter方法
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public LocalDate getTargetDate() {
        return targetDate;
    }
    
    public void setTargetDate(LocalDate targetDate) {
        this.targetDate = targetDate;
    }
    
    public Boolean getIsRecurring() {
        return isRecurring;
    }
    
    public void setIsRecurring(Boolean isRecurring) {
        this.isRecurring = isRecurring;
    }
    
    public AnniversaryType getType() {
        return type;
    }
    
    public void setType(AnniversaryType type) {
        this.type = type;
    }
    
    public LocalDateTime getCreateTime() {
        return createTime;
    }
    
    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }
    
    public LocalDateTime getUpdateTime() {
        return updateTime;
    }
    
    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }
    
    public Integer getRemindDaysBefore() {
        return remindDaysBefore;
    }
    
    public void setRemindDaysBefore(Integer remindDaysBefore) {
        this.remindDaysBefore = remindDaysBefore;
    }
    
    public User getUser() {
        return user;
    }
    
    public void setUser(User user) {
        this.user = user;
    }
    
    @Override
    public String toString() {
        return "Anniversary{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", targetDate=" + targetDate +
                ", isRecurring=" + isRecurring +
                ", type=" + type +
                ", daysUntil=" + getDaysUntil() +
                '}';
    }
}

