package com.example.todolist.service;

import com.example.todolist.dto.TodoItemCreateDTO;
import com.example.todolist.dto.TodoItemResponseDTO;
import com.example.todolist.dto.TodoItemUpdateDTO;
import com.example.todolist.entity.TodoItem;
import com.example.todolist.entity.User;
import com.example.todolist.exception.ResourceNotFoundException;
import com.example.todolist.repository.TodoItemRepository;
import com.example.todolist.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 待办事项业务逻辑层
 */
@Service
@Transactional
public class TodoItemService {
    
    private final TodoItemRepository todoItemRepository;
    private final UserRepository userRepository;
    
    @Autowired
    public TodoItemService(TodoItemRepository todoItemRepository, UserRepository userRepository) {
        this.todoItemRepository = todoItemRepository;
        this.userRepository = userRepository;
    }
    
    /**
     * 创建待办事项
     */
    public TodoItemResponseDTO createTodoItem(TodoItemCreateDTO dto) {
        // 验证用户是否存在
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("用户", "id", dto.getUserId()));
        
        // 创建待办事项实体
        TodoItem todoItem = new TodoItem();
        todoItem.setTitle(dto.getTitle());
        todoItem.setDescription(dto.getDescription());
        todoItem.setDueDate(dto.getDueDate());
        todoItem.setStartTime(dto.getStartTime());
        todoItem.setPriority(dto.getPriority());
        todoItem.setUser(user);
        
        // 保存待办事项
        TodoItem savedTodoItem = todoItemRepository.save(todoItem);
        
        return convertToDTO(savedTodoItem);
    }
    
    /**
     * 根据ID获取待办事项
     */
    public TodoItemResponseDTO getTodoItemById(Long id) {
        TodoItem todoItem = todoItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("待办事项", "id", id));
        return convertToDTO(todoItem);
    }
    
    /**
     * 获取用户的所有待办事项
     */
    public List<TodoItemResponseDTO> getTodoItemsByUserId(Long userId) {
        // 验证用户是否存在
        userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("用户", "id", userId));
        
        return todoItemRepository.findByUserId(userId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * 获取用户的待办事项（按完成状态）
     */
    public List<TodoItemResponseDTO> getTodoItemsByUserIdAndCompleted(Long userId, Boolean completed) {
        userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("用户", "id", userId));
        
        return todoItemRepository.findByUserIdAndCompleted(userId, completed).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * 获取用户的待办事项（按优先级）
     */
    public List<TodoItemResponseDTO> getTodoItemsByUserIdAndPriority(Long userId, TodoItem.Priority priority) {
        userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("用户", "id", userId));
        
        return todoItemRepository.findByUserIdAndPriority(userId, priority).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * 获取用户的已过期未完成待办事项
     */
    public List<TodoItemResponseDTO> getOverdueTodoItems(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("用户", "id", userId));
        
        return todoItemRepository.findByUserIdAndCompletedFalseAndDueDateBefore(userId, LocalDateTime.now())
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * 获取用户即将到期的待办事项（未来N天内）
     */
    public List<TodoItemResponseDTO> getUpcomingTodoItems(Long userId, int days) {
        userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("用户", "id", userId));
        
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime future = now.plusDays(days);
        
        return todoItemRepository.findByUserIdAndCompletedFalseAndDueDateBetween(userId, now, future)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * 获取用户的待办事项（按创建时间倒序）
     */
    public List<TodoItemResponseDTO> getTodoItemsByUserIdOrderByCreateTime(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("用户", "id", userId));
        
        return todoItemRepository.findByUserIdOrderByCreateTimeDesc(userId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * 获取用户的待办事项（按截止日期升序）
     */
    public List<TodoItemResponseDTO> getTodoItemsByUserIdOrderByDueDate(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("用户", "id", userId));
        
        return todoItemRepository.findByUserIdOrderByDueDateAsc(userId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * 获取用户的待办事项（按优先级降序）
     */
    public List<TodoItemResponseDTO> getTodoItemsByUserIdOrderByPriority(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("用户", "id", userId));
        
        return todoItemRepository.findByUserIdOrderByPriorityDesc(userId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * 更新待办事项
     */
    public TodoItemResponseDTO updateTodoItem(Long id, TodoItemUpdateDTO dto) {
        TodoItem todoItem = todoItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("待办事项", "id", id));
        
        // 更新标题
        if (dto.getTitle() != null && !dto.getTitle().isEmpty()) {
            todoItem.setTitle(dto.getTitle());
        }
        
        // 更新描述
        if (dto.getDescription() != null) {
            todoItem.setDescription(dto.getDescription());
        }
        
        // 更新完成状态
        if (dto.getCompleted() != null) {
            todoItem.setCompleted(dto.getCompleted());
        }
        
        // 更新截止日期
        if (dto.getDueDate() != null) {
            todoItem.setDueDate(dto.getDueDate());
        }
        
        // 更新开始时间
        if (dto.getStartTime() != null) {
            todoItem.setStartTime(dto.getStartTime());
        }
        
        // 更新优先级
        if (dto.getPriority() != null) {
            todoItem.setPriority(dto.getPriority());
        }
        
        TodoItem updatedTodoItem = todoItemRepository.save(todoItem);
        return convertToDTO(updatedTodoItem);
    }
    
    /**
     * 切换待办事项完成状态
     */
    public TodoItemResponseDTO toggleTodoItemComplete(Long id) {
        TodoItem todoItem = todoItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("待办事项", "id", id));
        
        todoItem.setCompleted(!todoItem.getCompleted());
        
        TodoItem updatedTodoItem = todoItemRepository.save(todoItem);
        return convertToDTO(updatedTodoItem);
    }
    
    /**
     * 标记待办事项为完成
     */
    public TodoItemResponseDTO markTodoItemComplete(Long id) {
        TodoItem todoItem = todoItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("待办事项", "id", id));
        
        todoItem.setCompleted(true);
        
        TodoItem updatedTodoItem = todoItemRepository.save(todoItem);
        return convertToDTO(updatedTodoItem);
    }
    
    /**
     * 标记待办事项为未完成
     */
    public TodoItemResponseDTO markTodoItemIncomplete(Long id) {
        TodoItem todoItem = todoItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("待办事项", "id", id));
        
        todoItem.setCompleted(false);
        
        TodoItem updatedTodoItem = todoItemRepository.save(todoItem);
        return convertToDTO(updatedTodoItem);
    }
    
    /**
     * 获取用户的待办事项统计信息
     */
    public java.util.Map<String, Object> getTodoStatistics(Long userId) {
        // 验证用户是否存在
        userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("用户", "id", userId));
        
        // 获取所有待办事项
        List<TodoItem> allTodos = todoItemRepository.findByUserId(userId);
        
        // 统计总数
        long totalTodos = allTodos.size();
        
        // 统计已完成数
        long completedTodos = allTodos.stream()
                .filter(TodoItem::getCompleted)
                .count();
        
        // 统计已过期未完成数
        long overdueTodos = allTodos.stream()
                .filter(todo -> !todo.getCompleted() 
                        && todo.getDueDate() != null 
                        && todo.getDueDate().isBefore(LocalDateTime.now()))
                .count();
        
        // 创建统计数据Map
        java.util.Map<String, Object> statistics = new java.util.HashMap<>();
        statistics.put("totalTodos", totalTodos);
        statistics.put("completedTodos", completedTodos);
        statistics.put("overdueTodos", overdueTodos);
        
        return statistics;
    }
    
    /**
     * 删除待办事项
     */
    public void deleteTodoItem(Long id) {
        TodoItem todoItem = todoItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("待办事项", "id", id));
        todoItemRepository.delete(todoItem);
    }
    
    /**
     * 批量删除用户的已完成待办事项
     */
    public void deleteCompletedTodoItems(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("用户", "id", userId));
        
        List<TodoItem> completedItems = todoItemRepository.findByUserIdAndCompleted(userId, true);
        todoItemRepository.deleteAll(completedItems);
    }
    
    /**
     * 将TodoItem实体转换为TodoItemResponseDTO
     */
    private TodoItemResponseDTO convertToDTO(TodoItem todoItem) {
        TodoItemResponseDTO dto = new TodoItemResponseDTO();
        dto.setId(todoItem.getId());
        dto.setTitle(todoItem.getTitle());
        dto.setDescription(todoItem.getDescription());
        dto.setCompleted(todoItem.getCompleted());
        dto.setCreateTime(todoItem.getCreateTime());
        dto.setUpdateTime(todoItem.getUpdateTime());
        dto.setDueDate(todoItem.getDueDate());
        dto.setStartTime(todoItem.getStartTime());
        dto.setCompletedTime(todoItem.getCompletedTime());
        dto.setPriority(todoItem.getPriority());
        dto.setUserId(todoItem.getUser().getId());
        dto.setUsername(todoItem.getUser().getUsername());
        return dto;
    }
}
