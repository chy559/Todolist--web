package com.example.todolist.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.Size;

/**
 * 用户更新请求DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateDTO {
    
    @Email(message = "邮箱格式不正确")
    private String email;
    
    @Size(min = 6, message = "密码长度不能少于6个字符")
    private String password;
}

