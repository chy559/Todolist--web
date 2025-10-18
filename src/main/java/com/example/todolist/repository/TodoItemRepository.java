package com.example.todolist.repository;

import com.example.todolist.entity.TodoItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 待办事项数据访问层
 */
@Repository
public interface TodoItemRepository extends JpaRepository<TodoItem, Long> {
    
    /**
     * 根据用户ID查找所有待办事项
     */
    List<TodoItem> findByUserId(Long userId);
    
    /**
     * 根据用户ID和完成状态查找待办事项
     */
    List<TodoItem> findByUserIdAndCompleted(Long userId, Boolean completed);
    
    /**
     * 根据用户ID和优先级查找待办事项
     */
    List<TodoItem> findByUserIdAndPriority(Long userId, TodoItem.Priority priority);
    
    /**
     * 查找用户的已过期未完成待办事项
     */
    List<TodoItem> findByUserIdAndCompletedFalseAndDueDateBefore(Long userId, LocalDateTime dateTime);
    
    /**
     * 查找用户的即将到期待办事项（未完成且截止日期在指定时间之前）
     */
    List<TodoItem> findByUserIdAndCompletedFalseAndDueDateBetween(Long userId, LocalDateTime start, LocalDateTime end);
    
    /**
     * 根据用户ID按创建时间倒序查找
     */
    List<TodoItem> findByUserIdOrderByCreateTimeDesc(Long userId);
    
    /**
     * 根据用户ID按截止日期升序查找
     */
    List<TodoItem> findByUserIdOrderByDueDateAsc(Long userId);
    
    /**
     * 根据用户ID按优先级降序查找
     */
    List<TodoItem> findByUserIdOrderByPriorityDesc(Long userId);
}
