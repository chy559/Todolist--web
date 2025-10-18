package com.example.todolist.dto;

import com.example.todolist.entity.FocusSession;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 专注会话响应DTO
 */
@Data
public class FocusSessionResponseDTO {
    
    private Long id;
    
    private Integer duration; // 专注时长（分钟）
    
    private LocalDateTime startTime; // 开始时间
    
    private LocalDateTime endTime; // 结束时间
    
    private Integer actualDuration; // 实际专注时长（分钟）
    
    private FocusSession.SessionStatus status; // 会话状态
    
    private LocalDateTime createTime;
    
    private LocalDateTime updateTime;
    
    private String notes; // 备注信息
    
    private Long userId;
    
    private String username; // 用户名，方便前端显示
    
    // 计算剩余时间（分钟）- 用于倒计时
    private Long remainingMinutes;
}
