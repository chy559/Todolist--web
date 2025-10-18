package com.example.todolist.service;

import com.example.todolist.dto.AnniversaryCreateDTO;
import com.example.todolist.dto.AnniversaryResponseDTO;
import com.example.todolist.dto.AnniversaryUpdateDTO;
import com.example.todolist.entity.Anniversary;
import com.example.todolist.entity.User;
import com.example.todolist.exception.ResourceNotFoundException;
import com.example.todolist.repository.AnniversaryRepository;
import com.example.todolist.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 纪念日业务逻辑层
 */
@Service
@Transactional
public class AnniversaryService {
    
    private final AnniversaryRepository anniversaryRepository;
    private final UserRepository userRepository;
    
    @Autowired
    public AnniversaryService(AnniversaryRepository anniversaryRepository, UserRepository userRepository) {
        this.anniversaryRepository = anniversaryRepository;
        this.userRepository = userRepository;
    }
    
    /**
     * 创建纪念日
     */
    public AnniversaryResponseDTO createAnniversary(AnniversaryCreateDTO dto) {
        // 验证用户是否存在
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("用户", "id", dto.getUserId()));
        
        // 创建纪念日实体
        Anniversary anniversary = new Anniversary();
        anniversary.setTitle(dto.getTitle());
        anniversary.setDescription(dto.getDescription());
        anniversary.setTargetDate(dto.getTargetDate());
        anniversary.setIsRecurring(dto.getIsRecurring());
        anniversary.setType(dto.getType());
        anniversary.setRemindDaysBefore(dto.getRemindDaysBefore());
        anniversary.setUser(user);
        
        // 保存纪念日
        Anniversary savedAnniversary = anniversaryRepository.save(anniversary);
        
        return convertToDTO(savedAnniversary);
    }
    
    /**
     * 根据ID获取纪念日
     */
    public AnniversaryResponseDTO getAnniversaryById(Long id) {
        Anniversary anniversary = anniversaryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("纪念日", "id", id));
        return convertToDTO(anniversary);
    }
    
    /**
     * 获取用户的所有纪念日
     */
    public List<AnniversaryResponseDTO> getAnniversariesByUserId(Long userId) {
        // 验证用户是否存在
        userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("用户", "id", userId));
        
        return anniversaryRepository.findByUserId(userId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * 获取用户的纪念日（按类型过滤）
     */
    public List<AnniversaryResponseDTO> getAnniversariesByUserIdAndType(Long userId, Anniversary.AnniversaryType type) {
        userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("用户", "id", userId));
        
        return anniversaryRepository.findByUserIdAndType(userId, type).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * 获取用户的纪念日（按是否循环过滤）
     */
    public List<AnniversaryResponseDTO> getAnniversariesByUserIdAndIsRecurring(Long userId, Boolean isRecurring) {
        userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("用户", "id", userId));
        
        return anniversaryRepository.findByUserIdAndIsRecurring(userId, isRecurring).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * 获取用户的纪念日（按目标日期升序）
     */
    public List<AnniversaryResponseDTO> getAnniversariesByUserIdOrderByTargetDate(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("用户", "id", userId));
        
        return anniversaryRepository.findByUserIdOrderByTargetDateAsc(userId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * 获取用户的纪念日（按创建时间倒序）
     */
    public List<AnniversaryResponseDTO> getAnniversariesByUserIdOrderByCreateTime(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("用户", "id", userId));
        
        return anniversaryRepository.findByUserIdOrderByCreateTimeDesc(userId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * 获取用户在指定日期范围内的纪念日
     */
    public List<AnniversaryResponseDTO> getAnniversariesByDateRange(Long userId, LocalDate start, LocalDate end) {
        userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("用户", "id", userId));
        
        return anniversaryRepository.findByUserIdAndTargetDateBetween(userId, start, end).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * 获取用户即将到来的纪念日（未来N天内）
     */
    public List<AnniversaryResponseDTO> getUpcomingAnniversaries(Long userId, int days) {
        userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("用户", "id", userId));
        
        List<Anniversary> allAnniversaries = anniversaryRepository.findByUserId(userId);
        
        return allAnniversaries.stream()
                .filter(anniversary -> {
                    long daysUntil = anniversary.getDaysUntil();
                    return daysUntil >= 0 && daysUntil <= days;
                })
                .map(this::convertToDTO)
                .sorted((a, b) -> Long.compare(a.getDaysUntil(), b.getDaysUntil()))
                .collect(Collectors.toList());
    }
    
    /**
     * 获取用户今天的纪念日
     */
    public List<AnniversaryResponseDTO> getTodayAnniversaries(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("用户", "id", userId));
        
        List<Anniversary> allAnniversaries = anniversaryRepository.findByUserId(userId);
        
        return allAnniversaries.stream()
                .filter(Anniversary::isToday)
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * 获取用户本月的纪念日
     */
    public List<AnniversaryResponseDTO> getThisMonthAnniversaries(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("用户", "id", userId));
        
        LocalDate today = LocalDate.now();
        LocalDate startOfMonth = today.withDayOfMonth(1);
        LocalDate endOfMonth = today.withDayOfMonth(today.lengthOfMonth());
        
        return getAnniversariesByDateRange(userId, startOfMonth, endOfMonth);
    }
    
    /**
     * 获取需要提醒的纪念日
     */
    public List<AnniversaryResponseDTO> getAnniversariesToRemind(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("用户", "id", userId));
        
        List<Anniversary> allAnniversaries = anniversaryRepository.findByUserId(userId);
        
        return allAnniversaries.stream()
                .filter(anniversary -> {
                    if (anniversary.getRemindDaysBefore() == null) {
                        return false;
                    }
                    long daysUntil = anniversary.getDaysUntil();
                    return daysUntil >= 0 && daysUntil <= anniversary.getRemindDaysBefore();
                })
                .map(this::convertToDTO)
                .sorted((a, b) -> Long.compare(a.getDaysUntil(), b.getDaysUntil()))
                .collect(Collectors.toList());
    }
    
    /**
     * 统计用户的纪念日总数
     */
    public long countAnniversaries(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("用户", "id", userId));
        
        return anniversaryRepository.countByUserId(userId);
    }
    
    /**
     * 统计用户指定类型的纪念日数量
     */
    public long countAnniversariesByType(Long userId, Anniversary.AnniversaryType type) {
        userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("用户", "id", userId));
        
        return anniversaryRepository.countByUserIdAndType(userId, type);
    }
    
    /**
     * 更新纪念日
     */
    public AnniversaryResponseDTO updateAnniversary(Long id, AnniversaryUpdateDTO dto) {
        Anniversary anniversary = anniversaryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("纪念日", "id", id));
        
        // 更新标题
        if (dto.getTitle() != null && !dto.getTitle().isEmpty()) {
            anniversary.setTitle(dto.getTitle());
        }
        
        // 更新描述
        if (dto.getDescription() != null) {
            anniversary.setDescription(dto.getDescription());
        }
        
        // 更新目标日期
        if (dto.getTargetDate() != null) {
            anniversary.setTargetDate(dto.getTargetDate());
        }
        
        // 更新是否循环
        if (dto.getIsRecurring() != null) {
            anniversary.setIsRecurring(dto.getIsRecurring());
        }
        
        // 更新类型
        if (dto.getType() != null) {
            anniversary.setType(dto.getType());
        }
        
        // 更新提醒天数
        if (dto.getRemindDaysBefore() != null) {
            anniversary.setRemindDaysBefore(dto.getRemindDaysBefore());
        }
        
        Anniversary updatedAnniversary = anniversaryRepository.save(anniversary);
        return convertToDTO(updatedAnniversary);
    }
    
    /**
     * 删除纪念日
     */
    public void deleteAnniversary(Long id) {
        Anniversary anniversary = anniversaryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("纪念日", "id", id));
        anniversaryRepository.delete(anniversary);
    }
    
    /**
     * 将Anniversary实体转换为AnniversaryResponseDTO
     */
    private AnniversaryResponseDTO convertToDTO(Anniversary anniversary) {
        AnniversaryResponseDTO dto = new AnniversaryResponseDTO();
        dto.setId(anniversary.getId());
        dto.setTitle(anniversary.getTitle());
        dto.setDescription(anniversary.getDescription());
        dto.setTargetDate(anniversary.getTargetDate());
        dto.setIsRecurring(anniversary.getIsRecurring());
        dto.setType(anniversary.getType());
        dto.setCreateTime(anniversary.getCreateTime());
        dto.setUpdateTime(anniversary.getUpdateTime());
        dto.setRemindDaysBefore(anniversary.getRemindDaysBefore());
        dto.setUserId(anniversary.getUser().getId());
        dto.setUsername(anniversary.getUser().getUsername());
        
        // 计算倒计时相关信息
        dto.setDaysUntil(anniversary.getDaysUntil());
        dto.setIsExpired(anniversary.isExpired());
        dto.setIsToday(anniversary.isToday());
        
        return dto;
    }
}
