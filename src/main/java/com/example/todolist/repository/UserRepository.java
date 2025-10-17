package com.example.todolist.repository;

import com.example.todolist.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 用户数据访问层
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {// <实体类名,主键类型> -> 对实体的持久化操作
    // 不需要自己手动实现，Spring Data JPA 会对继承自 JpaRepository 的接口中定义的方法名进行解析，
    // 根据方法名中的关键字，自动生成对应的 SQL 查询语句
    
    /**
     * 根据用户名查找用户
     */
    Optional<User> findByUsername(String username);// optional的用法类似于C++
    
    /**
     * 根据邮箱查找用户
     */
    Optional<User> findByEmail(String email);
    
    /**
     * 检查用户名是否已存在
     */
    boolean existsByUsername(String username);
    
    /**
     * 检查邮箱是否已存在
     */
    boolean existsByEmail(String email);
}

