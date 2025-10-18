package com.example.todolist.controller;

import com.example.todolist.common.ApiResponse;
import com.example.todolist.dto.FocusSessionCreateDTO;
import com.example.todolist.dto.FocusSessionResponseDTO;
import com.example.todolist.dto.FocusSessionUpdateDTO;
import com.example.todolist.entity.FocusSession;
import com.example.todolist.service.FocusSessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 专注会话控制器 - REST API
 */
@RestController
@RequestMapping("/api/focus")
@CrossOrigin(origins = "*")
public class FocusSessionController {
    
    private final FocusSessionService focusSessionService;
    
    @Autowired
    public FocusSessionController(FocusSessionService focusSessionService) {
        this.focusSessionService = focusSessionService;
    }
    
    /**
     * 创建专注会话
     * POST /api/focus
     */
    @PostMapping
    public ResponseEntity<ApiResponse<FocusSessionResponseDTO>> createFocusSession(
            @Valid @RequestBody FocusSessionCreateDTO dto) {
        FocusSessionResponseDTO session = focusSessionService.createFocusSession(dto);
        return ResponseEntity.ok(ApiResponse.success(session));
    }
    
    /**
     * 开始专注会话（启动倒计时）
     * POST /api/focus/{id}/start
     */
    @PostMapping("/{id}/start")
    public ResponseEntity<ApiResponse<FocusSessionResponseDTO>> startFocusSession(
            @PathVariable Long id) {
        FocusSessionResponseDTO session = focusSessionService.startFocusSession(id);
        return ResponseEntity.ok(ApiResponse.success(session));
    }
    
    /**
     * 完成专注会话
     * POST /api/focus/{id}/complete
     */
    @PostMapping("/{id}/complete")
    public ResponseEntity<ApiResponse<FocusSessionResponseDTO>> completeFocusSession(
            @PathVariable Long id) {
        FocusSessionResponseDTO session = focusSessionService.completeFocusSession(id);
        return ResponseEntity.ok(ApiResponse.success(session));
    }
    
    /**
     * 取消专注会话
     * POST /api/focus/{id}/cancel
     */
    @PostMapping("/{id}/cancel")
    public ResponseEntity<ApiResponse<FocusSessionResponseDTO>> cancelFocusSession(
            @PathVariable Long id) {
        FocusSessionResponseDTO session = focusSessionService.cancelFocusSession(id);
        return ResponseEntity.ok(ApiResponse.success(session));
    }
    
    /**
     * 根据ID获取专注会话
     * GET /api/focus/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<FocusSessionResponseDTO>> getFocusSessionById(
            @PathVariable Long id) {
        FocusSessionResponseDTO session = focusSessionService.getFocusSessionById(id);
        return ResponseEntity.ok(ApiResponse.success(session));
    }
    
    /**
     * 获取用户的所有专注会话
     * GET /api/focus/user/{userId}
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<FocusSessionResponseDTO>>> getFocusSessionsByUserId(
            @PathVariable Long userId) {
        List<FocusSessionResponseDTO> sessions = focusSessionService.getFocusSessionsByUserId(userId);
        return ResponseEntity.ok(ApiResponse.success(sessions));
    }
    
    /**
     * 获取用户的专注会话（按状态过滤）
     * GET /api/focus/user/{userId}/status/{status}
     */
    @GetMapping("/user/{userId}/status/{status}")
    public ResponseEntity<ApiResponse<List<FocusSessionResponseDTO>>> getFocusSessionsByStatus(
            @PathVariable Long userId,
            @PathVariable FocusSession.SessionStatus status) {
        List<FocusSessionResponseDTO> sessions = focusSessionService.getFocusSessionsByUserIdAndStatus(userId, status);
        return ResponseEntity.ok(ApiResponse.success(sessions));
    }
    
    /**
     * 获取用户正在进行的专注会话（用于倒计时）
     * GET /api/focus/user/{userId}/inprogress
     */
    @GetMapping("/user/{userId}/inprogress")
    public ResponseEntity<ApiResponse<FocusSessionResponseDTO>> getInProgressSession(
            @PathVariable Long userId) {
        FocusSessionResponseDTO session = focusSessionService.getInProgressSession(userId);
        return ResponseEntity.ok(ApiResponse.success(session));
    }
    
    /**
     * 获取用户的专注会话（按创建时间倒序）
     * GET /api/focus/user/{userId}/sort/createtime
     */
    @GetMapping("/user/{userId}/sort/createtime")
    public ResponseEntity<ApiResponse<List<FocusSessionResponseDTO>>> getFocusSessionsSortedByCreateTime(
            @PathVariable Long userId) {
        List<FocusSessionResponseDTO> sessions = focusSessionService.getFocusSessionsByUserIdOrderByCreateTime(userId);
        return ResponseEntity.ok(ApiResponse.success(sessions));
    }
    
    /**
     * 获取用户的专注会话（按开始时间倒序）
     * GET /api/focus/user/{userId}/sort/starttime
     */
    @GetMapping("/user/{userId}/sort/starttime")
    public ResponseEntity<ApiResponse<List<FocusSessionResponseDTO>>> getFocusSessionsSortedByStartTime(
            @PathVariable Long userId) {
        List<FocusSessionResponseDTO> sessions = focusSessionService.getFocusSessionsByUserIdOrderByStartTime(userId);
        return ResponseEntity.ok(ApiResponse.success(sessions));
    }
    
    /**
     * 获取用户在指定时间范围内的专注会话
     * GET /api/focus/user/{userId}/range?start=2024-01-01T00:00:00&end=2024-01-31T23:59:59
     */
    @GetMapping("/user/{userId}/range")
    public ResponseEntity<ApiResponse<List<FocusSessionResponseDTO>>> getFocusSessionsByDateRange(
            @PathVariable Long userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        List<FocusSessionResponseDTO> sessions = focusSessionService.getFocusSessionsByDateRange(userId, start, end);
        return ResponseEntity.ok(ApiResponse.success(sessions));
    }
    
    /**
     * 获取用户今天的专注会话
     * GET /api/focus/user/{userId}/today
     */
    @GetMapping("/user/{userId}/today")
    public ResponseEntity<ApiResponse<List<FocusSessionResponseDTO>>> getTodayFocusSessions(
            @PathVariable Long userId) {
        List<FocusSessionResponseDTO> sessions = focusSessionService.getTodayFocusSessions(userId);
        return ResponseEntity.ok(ApiResponse.success(sessions));
    }
    
    /**
     * 获取用户的专注统计信息
     * GET /api/focus/user/{userId}/statistics
     */
    @GetMapping("/user/{userId}/statistics")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getFocusStatistics(
            @PathVariable Long userId) {
        Map<String, Object> statistics = new HashMap<>();
        statistics.put("totalSessions", focusSessionService.countFocusSessions(userId));
        statistics.put("completedSessions", focusSessionService.countCompletedFocusSessions(userId));
        statistics.put("totalFocusMinutes", focusSessionService.calculateTotalFocusTime(userId));
        
        return ResponseEntity.ok(ApiResponse.success(statistics));
    }
    
    /**
     * 更新专注会话
     * PUT /api/focus/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<FocusSessionResponseDTO>> updateFocusSession(
            @PathVariable Long id,
            @Valid @RequestBody FocusSessionUpdateDTO dto) {
        FocusSessionResponseDTO session = focusSessionService.updateFocusSession(id, dto);
        return ResponseEntity.ok(ApiResponse.success(session));
    }
    
    /**
     * 删除专注会话
     * DELETE /api/focus/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteFocusSession(@PathVariable Long id) {
        focusSessionService.deleteFocusSession(id);
        return ResponseEntity.ok(ApiResponse.success("专注会话删除成功"));
    }
}
