package com.example.todolist.dto;

import lombok.Data;

import javax.validation.constraints.Min;

/**
 * 更新专注会话DTO
 */
@Data
public class FocusSessionUpdateDTO {
    
    @Min(value = 1, message = "专注时长至少为1分钟")
    private Integer duration; // 专注时长（分钟）
    
    private String notes; // 备注信息
}
