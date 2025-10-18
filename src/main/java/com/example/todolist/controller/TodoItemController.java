package com.example.todolist.controller;

import com.example.todolist.common.ApiResponse;
import com.example.todolist.dto.TodoItemCreateDTO;
import com.example.todolist.dto.TodoItemResponseDTO;
import com.example.todolist.dto.TodoItemUpdateDTO;
import com.example.todolist.entity.TodoItem;
import com.example.todolist.service.TodoItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 待办事项控制器 - REST API
 */
@RestController
@RequestMapping("/api/todoitems")
@CrossOrigin(origins = "*")
public class TodoItemController {
    
    private final TodoItemService todoItemService;
    
    @Autowired
    public TodoItemController(TodoItemService todoItemService) {
        this.todoItemService = todoItemService;
    }
    
    /**
     * 创建待办事项
     * POST /api/todoitems
     */
    @PostMapping
    public ResponseEntity<ApiResponse<TodoItemResponseDTO>> createTodoItem(
            @Valid @RequestBody TodoItemCreateDTO dto) {
        TodoItemResponseDTO todoItem = todoItemService.createTodoItem(dto);
        return ResponseEntity.ok(ApiResponse.success(todoItem));
    }
    
    /**
     * 根据ID获取待办事项
     * GET /api/todoitems/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TodoItemResponseDTO>> getTodoItemById(
            @PathVariable Long id) {
        TodoItemResponseDTO todoItem = todoItemService.getTodoItemById(id);
        return ResponseEntity.ok(ApiResponse.success(todoItem));
    }
    
    /**
     * 获取用户的所有待办事项
     * GET /api/todoitems/user/{userId}
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<TodoItemResponseDTO>>> getTodoItemsByUserId(
            @PathVariable Long userId) {
        List<TodoItemResponseDTO> todoItems = todoItemService.getTodoItemsByUserId(userId);
        return ResponseEntity.ok(ApiResponse.success(todoItems));
    }
    
    /**
     * 获取用户的待办事项（按完成状态过滤）
     * GET /api/todoitems/user/{userId}/completed/{completed}
     */
    @GetMapping("/user/{userId}/completed/{completed}")
    public ResponseEntity<ApiResponse<List<TodoItemResponseDTO>>> getTodoItemsByCompleted(
            @PathVariable Long userId,
            @PathVariable Boolean completed) {
        List<TodoItemResponseDTO> todoItems = todoItemService.getTodoItemsByUserIdAndCompleted(userId, completed);
        return ResponseEntity.ok(ApiResponse.success(todoItems));
    }
    
    /**
     * 获取用户的待办事项（按优先级过滤）
     * GET /api/todoitems/user/{userId}/priority/{priority}
     */
    @GetMapping("/user/{userId}/priority/{priority}")
    public ResponseEntity<ApiResponse<List<TodoItemResponseDTO>>> getTodoItemsByPriority(
            @PathVariable Long userId,
            @PathVariable TodoItem.Priority priority) {
        List<TodoItemResponseDTO> todoItems = todoItemService.getTodoItemsByUserIdAndPriority(userId, priority);
        return ResponseEntity.ok(ApiResponse.success(todoItems));
    }
    
    /**
     * 获取用户的已过期未完成待办事项
     * GET /api/todoitems/user/{userId}/overdue
     */
    @GetMapping("/user/{userId}/overdue")
    public ResponseEntity<ApiResponse<List<TodoItemResponseDTO>>> getOverdueTodoItems(
            @PathVariable Long userId) {
        List<TodoItemResponseDTO> todoItems = todoItemService.getOverdueTodoItems(userId);
        return ResponseEntity.ok(ApiResponse.success(todoItems));
    }
    
    /**
     * 获取用户即将到期的待办事项
     * GET /api/todoitems/user/{userId}/upcoming?days=7
     */
    @GetMapping("/user/{userId}/upcoming")
    public ResponseEntity<ApiResponse<List<TodoItemResponseDTO>>> getUpcomingTodoItems(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "7") int days) {
        List<TodoItemResponseDTO> todoItems = todoItemService.getUpcomingTodoItems(userId, days);
        return ResponseEntity.ok(ApiResponse.success(todoItems));
    }
    
    /**
     * 获取用户的待办事项（按创建时间倒序）
     * GET /api/todoitems/user/{userId}/sort/createtime
     */
    @GetMapping("/user/{userId}/sort/createtime")
    public ResponseEntity<ApiResponse<List<TodoItemResponseDTO>>> getTodoItemsSortedByCreateTime(
            @PathVariable Long userId) {
        List<TodoItemResponseDTO> todoItems = todoItemService.getTodoItemsByUserIdOrderByCreateTime(userId);
        return ResponseEntity.ok(ApiResponse.success(todoItems));
    }
    
    /**
     * 获取用户的待办事项（按截止日期升序）
     * GET /api/todoitems/user/{userId}/sort/duedate
     */
    @GetMapping("/user/{userId}/sort/duedate")
    public ResponseEntity<ApiResponse<List<TodoItemResponseDTO>>> getTodoItemsSortedByDueDate(
            @PathVariable Long userId) {
        List<TodoItemResponseDTO> todoItems = todoItemService.getTodoItemsByUserIdOrderByDueDate(userId);
        return ResponseEntity.ok(ApiResponse.success(todoItems));
    }
    
    /**
     * 获取用户的待办事项（按优先级降序）
     * GET /api/todoitems/user/{userId}/sort/priority
     */
    @GetMapping("/user/{userId}/sort/priority")
    public ResponseEntity<ApiResponse<List<TodoItemResponseDTO>>> getTodoItemsSortedByPriority(
            @PathVariable Long userId) {
        List<TodoItemResponseDTO> todoItems = todoItemService.getTodoItemsByUserIdOrderByPriority(userId);
        return ResponseEntity.ok(ApiResponse.success(todoItems));
    }
    
    /**
     * 更新待办事项
     * PUT /api/todoitems/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<TodoItemResponseDTO>> updateTodoItem(
            @PathVariable Long id,
            @Valid @RequestBody TodoItemUpdateDTO dto) {
        TodoItemResponseDTO todoItem = todoItemService.updateTodoItem(id, dto);
        return ResponseEntity.ok(ApiResponse.success(todoItem));
    }
    
    /**
     * 切换待办事项完成状态
     * PATCH /api/todoitems/{id}/toggle
     */
    @PatchMapping("/{id}/toggle")
    public ResponseEntity<ApiResponse<TodoItemResponseDTO>> toggleTodoItemComplete(
            @PathVariable Long id) {
        TodoItemResponseDTO todoItem = todoItemService.toggleTodoItemComplete(id);
        return ResponseEntity.ok(ApiResponse.success(todoItem));
    }
    
    /**
     * 标记待办事项为完成
     * POST /api/todoitems/{id}/complete
     */
    @PostMapping("/{id}/complete")
    public ResponseEntity<ApiResponse<TodoItemResponseDTO>> markTodoItemComplete(
            @PathVariable Long id) {
        TodoItemResponseDTO todoItem = todoItemService.markTodoItemComplete(id);
        return ResponseEntity.ok(ApiResponse.success(todoItem));
    }
    
    /**
     * 标记待办事项为未完成
     * POST /api/todoitems/{id}/incomplete
     */
    @PostMapping("/{id}/incomplete")
    public ResponseEntity<ApiResponse<TodoItemResponseDTO>> markTodoItemIncomplete(
            @PathVariable Long id) {
        TodoItemResponseDTO todoItem = todoItemService.markTodoItemIncomplete(id);
        return ResponseEntity.ok(ApiResponse.success(todoItem));
    }
    
    /**
     * 获取用户的待办事项统计信息
     * GET /api/todoitems/user/{userId}/statistics
     */
    @GetMapping("/user/{userId}/statistics")
    public ResponseEntity<ApiResponse<java.util.Map<String, Object>>> getTodoStatistics(
            @PathVariable Long userId) {
        java.util.Map<String, Object> statistics = todoItemService.getTodoStatistics(userId);
        return ResponseEntity.ok(ApiResponse.success(statistics));
    }
    
    /**
     * 删除待办事项
     * DELETE /api/todoitems/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteTodoItem(@PathVariable Long id) {
        todoItemService.deleteTodoItem(id);
        return ResponseEntity.ok(ApiResponse.success("待办事项删除成功"));
    }
    
    /**
     * 批量删除用户的已完成待办事项
     * DELETE /api/todoitems/user/{userId}/completed
     */
    @DeleteMapping("/user/{userId}/completed")
    public ResponseEntity<ApiResponse<String>> deleteCompletedTodoItems(@PathVariable Long userId) {
        todoItemService.deleteCompletedTodoItems(userId);
        return ResponseEntity.ok(ApiResponse.success("已完成的待办事项批量删除成功"));
    }
}
