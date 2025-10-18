package com.example.todolist.dto;

import com.example.todolist.entity.Anniversary;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 纪念日响应DTO
 */
@Data
public class AnniversaryResponseDTO {
    
    private Long id;
    
    private String title;
    
    private String description;
    
    private LocalDate targetDate;
    
    private Boolean isRecurring;
    
    private Anniversary.AnniversaryType type;
    
    private LocalDateTime createTime;
    
    private LocalDateTime updateTime;
    
    private Integer remindDaysBefore;
    
    private Long userId;
    
    private String username;
    
    // 距离纪念日的天数（倒计时）
    private Long daysUntil;
    
    // 是否已过期
    private Boolean isExpired;
    
    // 是否是今天
    private Boolean isToday;
}
