package com.example.todolist.dto;

import com.example.todolist.entity.Anniversary;
import lombok.Data;

import javax.validation.constraints.Size;
import java.time.LocalDate;

/**
 * 更新纪念日DTO
 */
@Data
public class AnniversaryUpdateDTO {
    
    @Size(max = 200, message = "标题长度不能超过200个字符")
    private String title;
    
    private String description;
    
    private LocalDate targetDate;
    
    private Boolean isRecurring;
    
    private Anniversary.AnniversaryType type;
    
    private Integer remindDaysBefore;
}
