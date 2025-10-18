package com.example.todolist.dto;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * 创建专注会话DTO
 */
@Data
public class FocusSessionCreateDTO {
    
    @NotNull(message = "专注时长不能为空")
    @Min(value = 1, message = "专注时长至少为1分钟")
    private Integer duration; // 专注时长（分钟）
    
    private String notes; // 备注信息
    
    @NotNull(message = "用户ID不能为空")
    private Long userId;
}
