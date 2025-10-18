package com.example.todolist.service;

import com.example.todolist.dto.FocusSessionCreateDTO;
import com.example.todolist.dto.FocusSessionResponseDTO;
import com.example.todolist.dto.FocusSessionUpdateDTO;
import com.example.todolist.entity.FocusSession;
import com.example.todolist.entity.User;
import com.example.todolist.exception.BusinessException;
import com.example.todolist.exception.ResourceNotFoundException;
import com.example.todolist.repository.FocusSessionRepository;
import com.example.todolist.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 专注会话业务逻辑层
 */
@Service
@Transactional
public class FocusSessionService {
    
    private final FocusSessionRepository focusSessionRepository;
    private final UserRepository userRepository;
    
    @Autowired
    public FocusSessionService(FocusSessionRepository focusSessionRepository, UserRepository userRepository) {
        this.focusSessionRepository = focusSessionRepository;
        this.userRepository = userRepository;
    }
    
    /**
     * 创建专注会话
     */
    public FocusSessionResponseDTO createFocusSession(FocusSessionCreateDTO dto) {
        // 验证用户是否存在
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("用户", "id", dto.getUserId()));
        
        // 检查用户是否有正在进行的专注会话
        List<FocusSession> inProgressSessions = focusSessionRepository
                .findByUserIdAndStatus(dto.getUserId(), FocusSession.SessionStatus.IN_PROGRESS);
        
        if (!inProgressSessions.isEmpty()) {
            throw new BusinessException("您有正在进行的专注会话，无法创建新会话");
        }
        
        // 创建专注会话实体
        FocusSession focusSession = new FocusSession();
        focusSession.setDuration(dto.getDuration());
        focusSession.setNotes(dto.getNotes());
        focusSession.setUser(user);
        focusSession.setStatus(FocusSession.SessionStatus.PENDING);
        
        // 保存专注会话
        FocusSession savedSession = focusSessionRepository.save(focusSession);
        
        return convertToDTO(savedSession);
    }
    
    /**
     * 开始专注会话（启动倒计时）
     */
    public FocusSessionResponseDTO startFocusSession(Long id) {
        FocusSession focusSession = focusSessionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("专注会话", "id", id));
        
        // 检查状态
        if (focusSession.getStatus() != FocusSession.SessionStatus.PENDING) {
            throw new BusinessException("只有待开始的专注会话可以启动");
        }
        
        // 检查用户是否有其他正在进行的会话
        List<FocusSession> inProgressSessions = focusSessionRepository
                .findByUserIdAndStatus(focusSession.getUser().getId(), FocusSession.SessionStatus.IN_PROGRESS);
        
        if (!inProgressSessions.isEmpty()) {
            throw new BusinessException("您有正在进行的专注会话，请先完成或取消");
        }
        
        // 启动会话
        focusSession.start();
        
        FocusSession updatedSession = focusSessionRepository.save(focusSession);
        return convertToDTO(updatedSession);
    }
    
    /**
     * 完成专注会话
     */
    public FocusSessionResponseDTO completeFocusSession(Long id) {
        FocusSession focusSession = focusSessionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("专注会话", "id", id));
        
        // 检查状态
        if (focusSession.getStatus() != FocusSession.SessionStatus.IN_PROGRESS) {
            throw new BusinessException("只有进行中的专注会话可以完成");
        }
        
        // 完成会话
        focusSession.complete();
        
        FocusSession updatedSession = focusSessionRepository.save(focusSession);
        return convertToDTO(updatedSession);
    }
    
    /**
     * 取消专注会话
     */
    public FocusSessionResponseDTO cancelFocusSession(Long id) {
        FocusSession focusSession = focusSessionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("专注会话", "id", id));
        
        // 检查状态
        if (focusSession.getStatus() == FocusSession.SessionStatus.COMPLETED ||
            focusSession.getStatus() == FocusSession.SessionStatus.CANCELLED) {
            throw new BusinessException("已完成或已取消的专注会话无法再次操作");
        }
        
        // 取消会话
        focusSession.cancel();
        
        FocusSession updatedSession = focusSessionRepository.save(focusSession);
        return convertToDTO(updatedSession);
    }
    
    /**
     * 根据ID获取专注会话
     */
    public FocusSessionResponseDTO getFocusSessionById(Long id) {
        FocusSession focusSession = focusSessionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("专注会话", "id", id));
        return convertToDTO(focusSession);
    }
    
    /**
     * 获取用户的所有专注会话
     */
    public List<FocusSessionResponseDTO> getFocusSessionsByUserId(Long userId) {
        // 验证用户是否存在
        userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("用户", "id", userId));
        
        return focusSessionRepository.findByUserId(userId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * 获取用户的专注会话（按状态过滤）
     */
    public List<FocusSessionResponseDTO> getFocusSessionsByUserIdAndStatus(Long userId, FocusSession.SessionStatus status) {
        userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("用户", "id", userId));
        
        return focusSessionRepository.findByUserIdAndStatus(userId, status).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * 获取用户正在进行的专注会话（用于倒计时）
     */
    public FocusSessionResponseDTO getInProgressSession(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("用户", "id", userId));
        
        List<FocusSession> inProgressSessions = focusSessionRepository
                .findByUserIdAndStatus(userId, FocusSession.SessionStatus.IN_PROGRESS);
        
        if (inProgressSessions.isEmpty()) {
            throw new ResourceNotFoundException("专注会话", "status", "IN_PROGRESS");
        }
        
        return convertToDTO(inProgressSessions.get(0));
    }
    
    /**
     * 获取用户的专注会话（按创建时间倒序）
     */
    public List<FocusSessionResponseDTO> getFocusSessionsByUserIdOrderByCreateTime(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("用户", "id", userId));
        
        return focusSessionRepository.findByUserIdOrderByCreateTimeDesc(userId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * 获取用户的专注会话（按开始时间倒序）
     */
    public List<FocusSessionResponseDTO> getFocusSessionsByUserIdOrderByStartTime(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("用户", "id", userId));
        
        return focusSessionRepository.findByUserIdOrderByStartTimeDesc(userId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * 获取用户在指定时间范围内的专注会话
     */
    public List<FocusSessionResponseDTO> getFocusSessionsByDateRange(Long userId, LocalDateTime start, LocalDateTime end) {
        userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("用户", "id", userId));
        
        return focusSessionRepository.findByUserIdAndStartTimeBetween(userId, start, end).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * 获取用户今天的专注会话
     */
    public List<FocusSessionResponseDTO> getTodayFocusSessions(Long userId) {
        LocalDateTime startOfDay = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
        LocalDateTime endOfDay = LocalDateTime.now().withHour(23).withMinute(59).withSecond(59);
        
        return getFocusSessionsByDateRange(userId, startOfDay, endOfDay);
    }
    
    /**
     * 统计用户的专注会话总数
     */
    public long countFocusSessions(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("用户", "id", userId));
        
        return focusSessionRepository.countByUserId(userId);
    }
    
    /**
     * 统计用户已完成的专注会话数
     */
    public long countCompletedFocusSessions(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("用户", "id", userId));
        
        return focusSessionRepository.countByUserIdAndStatus(userId, FocusSession.SessionStatus.COMPLETED);
    }
    
    /**
     * 计算用户的总专注时长（分钟）
     */
    public long calculateTotalFocusTime(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("用户", "id", userId));
        
        List<FocusSession> completedSessions = focusSessionRepository
                .findByUserIdAndStatusOrderByStartTimeDesc(userId, FocusSession.SessionStatus.COMPLETED);
        
        return completedSessions.stream()
                .mapToLong(session -> session.getActualDuration() != null ? session.getActualDuration() : 0)
                .sum();
    }
    
    /**
     * 更新专注会话
     */
    public FocusSessionResponseDTO updateFocusSession(Long id, FocusSessionUpdateDTO dto) {
        FocusSession focusSession = focusSessionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("专注会话", "id", id));
        
        // 只有待开始的会话可以更新时长
        if (focusSession.getStatus() != FocusSession.SessionStatus.PENDING) {
            throw new BusinessException("只有待开始的专注会话可以修改");
        }
        
        // 更新时长
        if (dto.getDuration() != null) {
            focusSession.setDuration(dto.getDuration());
        }
        
        // 更新备注
        if (dto.getNotes() != null) {
            focusSession.setNotes(dto.getNotes());
        }
        
        FocusSession updatedSession = focusSessionRepository.save(focusSession);
        return convertToDTO(updatedSession);
    }
    
    /**
     * 删除专注会话
     */
    public void deleteFocusSession(Long id) {
        FocusSession focusSession = focusSessionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("专注会话", "id", id));
        
        // 不允许删除进行中的会话
        if (focusSession.getStatus() == FocusSession.SessionStatus.IN_PROGRESS) {
            throw new BusinessException("无法删除进行中的专注会话，请先完成或取消");
        }
        
        focusSessionRepository.delete(focusSession);
    }
    
    /**
     * 将FocusSession实体转换为FocusSessionResponseDTO
     */
    private FocusSessionResponseDTO convertToDTO(FocusSession focusSession) {
        FocusSessionResponseDTO dto = new FocusSessionResponseDTO();
        dto.setId(focusSession.getId());
        dto.setDuration(focusSession.getDuration());
        dto.setStartTime(focusSession.getStartTime());
        dto.setEndTime(focusSession.getEndTime());
        dto.setActualDuration(focusSession.getActualDuration());
        dto.setStatus(focusSession.getStatus());
        dto.setCreateTime(focusSession.getCreateTime());
        dto.setUpdateTime(focusSession.getUpdateTime());
        dto.setNotes(focusSession.getNotes());
        dto.setUserId(focusSession.getUser().getId());
        dto.setUsername(focusSession.getUser().getUsername());
        
        // 计算剩余时间（用于倒计时）
        if (focusSession.getStatus() == FocusSession.SessionStatus.IN_PROGRESS && 
            focusSession.getEndTime() != null) {
            LocalDateTime now = LocalDateTime.now();
            if (now.isBefore(focusSession.getEndTime())) {
                long remaining = Duration.between(now, focusSession.getEndTime()).toMinutes();
                dto.setRemainingMinutes(remaining);
            } else {
                dto.setRemainingMinutes(0L);
            }
        }
        
        return dto;
    }
}
