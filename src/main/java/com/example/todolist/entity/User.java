package com.example.todolist.entity;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 用户实体类
 */
@Data
@Entity
@Table(name = "users")
public class User {// id,username,password,email,createTime,updateTime,

    // Getter和Setter方法
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)// 表示依赖自增的手段来生成主键
    private Long id;

    // 数据校验注解（用于输入验证）
    @NotBlank(message = "用户名不能为空")
    @Size(min = 3, max = 50, message = "用户名长度必须在3-50个字符之间")
    @Column(unique = true, nullable = false, length = 50)// 保持用户名的唯一性,不能为空
    private String username;
    
    @NotBlank(message = "密码不能为空")
    @Size(min = 6, message = "密码长度不能少于6个字符")
    @Column(nullable = false)
    private String password;
    
    @Email(message = "邮箱格式不正确")
    @Column(unique = true, length = 100)
    private String email;
    
    @Column(name = "create_time", nullable = false, updatable = false)// 表示创建时间是不可更新的
    private LocalDateTime createTime;
    
    @Column(name = "update_time")
    private LocalDateTime updateTime;
    
    // 一对多关系：一个用户可以有多个待办事项
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TodoItem> todoItems = new ArrayList<>();
    
    // 一对多关系：一个用户可以有多个专注时间会话
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FocusSession> focusSessions = new ArrayList<>();
    
    // 一对多关系：一个用户可以有多个纪念日
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Anniversary> anniversaries = new ArrayList<>();
    
    @PrePersist
    protected void onCreate() {// 创建前执行
        createTime = LocalDateTime.now();
        updateTime = LocalDateTime.now();
    }
    
    @PreUpdate // 更新时执行
    protected void onUpdate() {
        updateTime = LocalDateTime.now();
    }
    
    // 构造函数
    public User() {
    }
    
    public User(String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                '}';
    }
}

