package com.example.todolist.repository;

import com.example.todolist.entity.Anniversary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * 纪念日数据访问层
 */
@Repository
public interface AnniversaryRepository extends JpaRepository<Anniversary, Long> {
    
    /**
     * 根据用户ID查找所有纪念日
     */
    List<Anniversary> findByUserId(Long userId);
    
    /**
     * 根据用户ID和类型查找纪念日
     */
    List<Anniversary> findByUserIdAndType(Long userId, Anniversary.AnniversaryType type);
    
    /**
     * 根据用户ID和是否循环查找纪念日
     */
    List<Anniversary> findByUserIdAndIsRecurring(Long userId, Boolean isRecurring);
    
    /**
     * 根据用户ID按目标日期升序查找
     */
    List<Anniversary> findByUserIdOrderByTargetDateAsc(Long userId);
    
    /**
     * 根据用户ID按创建时间倒序查找
     */
    List<Anniversary> findByUserIdOrderByCreateTimeDesc(Long userId);
    
    /**
     * 查找用户在指定日期范围内的纪念日
     */
    List<Anniversary> findByUserIdAndTargetDateBetween(Long userId, LocalDate start, LocalDate end);
    
    /**
     * 查找用户即将到来的纪念日（目标日期在指定日期之前）
     */
    List<Anniversary> findByUserIdAndTargetDateBeforeOrderByTargetDateAsc(Long userId, LocalDate date);
    
    /**
     * 查找用户今天的纪念日（循环纪念日按月日匹配）
     */
    List<Anniversary> findByUserIdAndTargetDate(Long userId, LocalDate date);
    
    /**
     * 统计用户的纪念日总数
     */
    long countByUserId(Long userId);
    
    /**
     * 统计用户指定类型的纪念日数量
     */
    long countByUserIdAndType(Long userId, Anniversary.AnniversaryType type);
}
