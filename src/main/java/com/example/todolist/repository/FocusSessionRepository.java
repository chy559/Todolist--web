package com.example.todolist.repository;

import com.example.todolist.entity.FocusSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 专注会话数据访问层
 */
@Repository
public interface FocusSessionRepository extends JpaRepository<FocusSession, Long> {
    
    /**
     * 根据用户ID查找所有专注会话
     */
    List<FocusSession> findByUserId(Long userId);
    
    /**
     * 根据用户ID和状态查找专注会话
     */
    List<FocusSession> findByUserIdAndStatus(Long userId, FocusSession.SessionStatus status);
    
    /**
     * 根据用户ID按创建时间倒序查找
     */
    List<FocusSession> findByUserIdOrderByCreateTimeDesc(Long userId);
    
    /**
     * 根据用户ID按开始时间倒序查找
     */
    List<FocusSession> findByUserIdOrderByStartTimeDesc(Long userId);
    
    /**
     * 查找用户在指定时间范围内的专注会话
     */
    List<FocusSession> findByUserIdAndStartTimeBetween(Long userId, LocalDateTime start, LocalDateTime end);
    
    /**
     * 查找用户已完成的专注会话
     */
    List<FocusSession> findByUserIdAndStatusOrderByStartTimeDesc(Long userId, FocusSession.SessionStatus status);
    
    /**
     * 统计用户的专注会话总数
     */
    long countByUserId(Long userId);
    
    /**
     * 统计用户已完成的专注会话数
     */
    long countByUserIdAndStatus(Long userId, FocusSession.SessionStatus status);
}
