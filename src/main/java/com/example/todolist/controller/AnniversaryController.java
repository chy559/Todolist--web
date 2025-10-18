package com.example.todolist.controller;

import com.example.todolist.common.ApiResponse;
import com.example.todolist.dto.AnniversaryCreateDTO;
import com.example.todolist.dto.AnniversaryResponseDTO;
import com.example.todolist.dto.AnniversaryUpdateDTO;
import com.example.todolist.entity.Anniversary;
import com.example.todolist.service.AnniversaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 纪念日控制器 - REST API
 */
@RestController
@RequestMapping("/api/anniversaries")
@CrossOrigin(origins = "*")
public class AnniversaryController {
    
    private final AnniversaryService anniversaryService;
    
    @Autowired
    public AnniversaryController(AnniversaryService anniversaryService) {
        this.anniversaryService = anniversaryService;
    }
    
    /**
     * 创建纪念日
     * POST /api/anniversaries
     */
    @PostMapping
    public ResponseEntity<ApiResponse<AnniversaryResponseDTO>> createAnniversary(
            @Valid @RequestBody AnniversaryCreateDTO dto) {
        AnniversaryResponseDTO anniversary = anniversaryService.createAnniversary(dto);
        return ResponseEntity.ok(ApiResponse.success(anniversary));
    }
    
    /**
     * 根据ID获取纪念日
     * GET /api/anniversaries/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<AnniversaryResponseDTO>> getAnniversaryById(
            @PathVariable Long id) {
        AnniversaryResponseDTO anniversary = anniversaryService.getAnniversaryById(id);
        return ResponseEntity.ok(ApiResponse.success(anniversary));
    }
    
    /**
     * 获取用户的所有纪念日
     * GET /api/anniversaries/user/{userId}
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<AnniversaryResponseDTO>>> getAnniversariesByUserId(
            @PathVariable Long userId) {
        List<AnniversaryResponseDTO> anniversaries = anniversaryService.getAnniversariesByUserId(userId);
        return ResponseEntity.ok(ApiResponse.success(anniversaries));
    }
    
    /**
     * 获取纪念日（按类型过滤）
     * GET /api/anniversaries/user/{userId}/type/{type}
     */
    @GetMapping("/user/{userId}/type/{type}")
    public ResponseEntity<ApiResponse<List<AnniversaryResponseDTO>>> getAnniversariesByType(
            @PathVariable Long userId,
            @PathVariable Anniversary.AnniversaryType type) {
        List<AnniversaryResponseDTO> anniversaries = anniversaryService.getAnniversariesByUserIdAndType(userId, type);
        return ResponseEntity.ok(ApiResponse.success(anniversaries));
    }
    
    /**
     * 获取纪念日（按是否循环过滤）
     * GET /api/anniversaries/user/{userId}/recurring/{isRecurring}
     */
    @GetMapping("/user/{userId}/recurring/{isRecurring}")
    public ResponseEntity<ApiResponse<List<AnniversaryResponseDTO>>> getAnniversariesByRecurring(
            @PathVariable Long userId,
            @PathVariable Boolean isRecurring) {
        List<AnniversaryResponseDTO> anniversaries = anniversaryService.getAnniversariesByUserIdAndIsRecurring(userId, isRecurring);
        return ResponseEntity.ok(ApiResponse.success(anniversaries));
    }
    
    /**
     * 获取纪念日（按目标日期升序）
     * GET /api/anniversaries/user/{userId}/sort/targetdate
     */
    @GetMapping("/user/{userId}/sort/targetdate")
    public ResponseEntity<ApiResponse<List<AnniversaryResponseDTO>>> getAnniversariesSortedByTargetDate(
            @PathVariable Long userId) {
        List<AnniversaryResponseDTO> anniversaries = anniversaryService.getAnniversariesByUserIdOrderByTargetDate(userId);
        return ResponseEntity.ok(ApiResponse.success(anniversaries));
    }
    
    /**
     * 获取纪念日（按创建时间倒序）
     * GET /api/anniversaries/user/{userId}/sort/createtime
     */
    @GetMapping("/user/{userId}/sort/createtime")
    public ResponseEntity<ApiResponse<List<AnniversaryResponseDTO>>> getAnniversariesSortedByCreateTime(
            @PathVariable Long userId) {
        List<AnniversaryResponseDTO> anniversaries = anniversaryService.getAnniversariesByUserIdOrderByCreateTime(userId);
        return ResponseEntity.ok(ApiResponse.success(anniversaries));
    }
    
    /**
     * 获取指定日期范围内的纪念日
     * GET /api/anniversaries/user/{userId}/range?start=2024-01-01&end=2024-12-31
     */
    @GetMapping("/user/{userId}/range")
    public ResponseEntity<ApiResponse<List<AnniversaryResponseDTO>>> getAnniversariesByDateRange(
            @PathVariable Long userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {
        List<AnniversaryResponseDTO> anniversaries = anniversaryService.getAnniversariesByDateRange(userId, start, end);
        return ResponseEntity.ok(ApiResponse.success(anniversaries));
    }
    
    /**
     * 获取即将到来的纪念日（未来N天内）
     * GET /api/anniversaries/user/{userId}/upcoming?days=30
     */
    @GetMapping("/user/{userId}/upcoming")
    public ResponseEntity<ApiResponse<List<AnniversaryResponseDTO>>> getUpcomingAnniversaries(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "30") int days) {
        List<AnniversaryResponseDTO> anniversaries = anniversaryService.getUpcomingAnniversaries(userId, days);
        return ResponseEntity.ok(ApiResponse.success(anniversaries));
    }
    
    /**
     * 获取今天的纪念日
     * GET /api/anniversaries/user/{userId}/today
     */
    @GetMapping("/user/{userId}/today")
    public ResponseEntity<ApiResponse<List<AnniversaryResponseDTO>>> getTodayAnniversaries(
            @PathVariable Long userId) {
        List<AnniversaryResponseDTO> anniversaries = anniversaryService.getTodayAnniversaries(userId);
        return ResponseEntity.ok(ApiResponse.success(anniversaries));
    }
    
    /**
     * 获取本月的纪念日
     * GET /api/anniversaries/user/{userId}/thismonth
     */
    @GetMapping("/user/{userId}/thismonth")
    public ResponseEntity<ApiResponse<List<AnniversaryResponseDTO>>> getThisMonthAnniversaries(
            @PathVariable Long userId) {
        List<AnniversaryResponseDTO> anniversaries = anniversaryService.getThisMonthAnniversaries(userId);
        return ResponseEntity.ok(ApiResponse.success(anniversaries));
    }
    
    /**
     * 获取需要提醒的纪念日
     * GET /api/anniversaries/user/{userId}/remind
     */
    @GetMapping("/user/{userId}/remind")
    public ResponseEntity<ApiResponse<List<AnniversaryResponseDTO>>> getAnniversariesToRemind(
            @PathVariable Long userId) {
        List<AnniversaryResponseDTO> anniversaries = anniversaryService.getAnniversariesToRemind(userId);
        return ResponseEntity.ok(ApiResponse.success(anniversaries));
    }
    
    /**
     * 获取纪念日统计信息
     * GET /api/anniversaries/user/{userId}/statistics
     */
    @GetMapping("/user/{userId}/statistics")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getAnniversaryStatistics(
            @PathVariable Long userId) {
        Map<String, Object> statistics = new HashMap<>();
        statistics.put("totalAnniversaries", anniversaryService.countAnniversaries(userId));
        statistics.put("birthdayCount", anniversaryService.countAnniversariesByType(userId, Anniversary.AnniversaryType.BIRTHDAY));
        statistics.put("weddingCount", anniversaryService.countAnniversariesByType(userId, Anniversary.AnniversaryType.WEDDING));
        statistics.put("relationshipCount", anniversaryService.countAnniversariesByType(userId, Anniversary.AnniversaryType.RELATIONSHIP));
        statistics.put("workCount", anniversaryService.countAnniversariesByType(userId, Anniversary.AnniversaryType.WORK));
        statistics.put("holidayCount", anniversaryService.countAnniversariesByType(userId, Anniversary.AnniversaryType.HOLIDAY));
        statistics.put("otherCount", anniversaryService.countAnniversariesByType(userId, Anniversary.AnniversaryType.OTHER));
        
        return ResponseEntity.ok(ApiResponse.success(statistics));
    }
    
    /**
     * 更新纪念日
     * PUT /api/anniversaries/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<AnniversaryResponseDTO>> updateAnniversary(
            @PathVariable Long id,
            @Valid @RequestBody AnniversaryUpdateDTO dto) {
        AnniversaryResponseDTO anniversary = anniversaryService.updateAnniversary(id, dto);
        return ResponseEntity.ok(ApiResponse.success(anniversary));
    }
    
    /**
     * 删除纪念日
     * DELETE /api/anniversaries/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteAnniversary(@PathVariable Long id) {
        anniversaryService.deleteAnniversary(id);
        return ResponseEntity.ok(ApiResponse.success("纪念日删除成功"));
    }
}
