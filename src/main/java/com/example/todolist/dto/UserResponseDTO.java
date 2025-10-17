package com.example.todolist.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 用户响应DTO（不包含敏感信息如密码）
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDTO {
    
    private Long id;
    
    private String username;
    
    private String email;
    
    private LocalDateTime createTime;
    
    private LocalDateTime updateTime;
}

