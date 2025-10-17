package com.example.todolist.service;

import com.example.todolist.dto.UserLoginDTO;
import com.example.todolist.dto.UserRegisterDTO;
import com.example.todolist.dto.UserResponseDTO;
import com.example.todolist.dto.UserUpdateDTO;
import com.example.todolist.entity.User;
import com.example.todolist.exception.BusinessException;
import com.example.todolist.exception.ResourceNotFoundException;
import com.example.todolist.repository.UserRepository;
import com.example.todolist.util.PasswordUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户业务逻辑层
 */
@Service
@Transactional // 简化事务管理
public class UserService {
    
    private final UserRepository userRepository;
    
    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    
    /**
     * 用户注册
     */
    public UserResponseDTO registerUser(UserRegisterDTO dto) {
        // 检查用户名是否已存在
        if (userRepository.existsByUsername(dto.getUsername())) {
            throw new BusinessException("用户名已存在");
        }
        
        // 检查邮箱是否已存在
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new BusinessException("邮箱已被注册");
        }
        
        // 创建用户实体
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        
        // 加密密码
        String encryptedPassword = PasswordUtil.encryptPassword(dto.getPassword());
        user.setPassword(encryptedPassword);
        
        // 保存用户
        User savedUser = userRepository.save(user);
        
        return convertToDTO(savedUser);
    }
    
    /**
     * 用户登录
     */
    public UserResponseDTO loginUser(UserLoginDTO dto) {
        // 查找用户
        User user = userRepository.findByUsername(dto.getUsername())
                .orElseThrow(() -> new BusinessException("用户名或密码错误"));
        
        // 验证密码
        if (!PasswordUtil.verifyPassword(dto.getPassword(), user.getPassword())) {
            throw new BusinessException("用户名或密码错误");
        }
        
        return convertToDTO(user);
    }
    
    /**
     * 根据ID获取用户
     */
    public UserResponseDTO getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("用户", "id", id));
        return convertToDTO(user);
    }
    
    /**
     * 根据用户名获取用户
     */
    public UserResponseDTO getUserByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("用户", "username", username));
        return convertToDTO(user);
    }
    
    /**
     * 获取所有用户
     */
    public List<UserResponseDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * 更新用户信息
     */
    public UserResponseDTO updateUser(Long id, UserUpdateDTO dto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("用户", "id", id));
        
        // 更新邮箱
        if (dto.getEmail() != null && !dto.getEmail().isEmpty()) {
            // 检查新邮箱是否已被其他用户使用
            if (!dto.getEmail().equals(user.getEmail()) && 
                userRepository.existsByEmail(dto.getEmail())) {
                throw new BusinessException("邮箱已被使用");
            }
            user.setEmail(dto.getEmail());
        }
        
        // 更新密码
        if (dto.getPassword() != null && !dto.getPassword().isEmpty()) {
            String encryptedPassword = PasswordUtil.encryptPassword(dto.getPassword());
            user.setPassword(encryptedPassword);
        }
        
        User updatedUser = userRepository.save(user);
        return convertToDTO(updatedUser);
    }
    
    /**
     * 删除用户
     */
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("用户", "id", id));
        userRepository.delete(user);
    }
    
    /**
     * 将User实体转换为UserResponseDTO
     */
    private UserResponseDTO convertToDTO(User user) { // 将user的实体打包为dto
        UserResponseDTO dto = new UserResponseDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setCreateTime(user.getCreateTime());
        dto.setUpdateTime(user.getUpdateTime());
        return dto;
    }
}

