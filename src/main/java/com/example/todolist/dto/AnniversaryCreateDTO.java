package com.example.todolist.dto;

import com.example.todolist.entity.Anniversary;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;

/**
 * 创建纪念日DTO
 */
@Data
public class AnniversaryCreateDTO {
    
    @NotBlank(message = "纪念日标题不能为空")
    @Size(max = 200, message = "标题长度不能超过200个字符")
    private String title;
    
    private String description;
    
    @NotNull(message = "目标日期不能为空")
    private LocalDate targetDate;
    
    private Boolean isRecurring = false; // 是否每年循环
    
    private Anniversary.AnniversaryType type = Anniversary.AnniversaryType.OTHER;
    
    private Integer remindDaysBefore; // 提前提醒天数
    
    @NotNull(message = "用户ID不能为空")
    private Long userId;
}
