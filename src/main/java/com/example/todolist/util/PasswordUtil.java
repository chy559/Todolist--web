package com.example.todolist.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * 密码加密工具类
 * 使用 SHA-256 + 盐值的方式加密密码
 */
public class PasswordUtil {
    
    private static final String ALGORITHM = "SHA-256";
    private static final int SALT_LENGTH = 16;
    
    /**
     * 生成盐值
     */
    private static String generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[SALT_LENGTH];
        random.nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }
    
    /**
     * 加密密码（返回格式：盐值$加密后的密码）
     */
    public static String encryptPassword(String password) {
        try {
            String salt = generateSalt();
            String encrypted = hashPassword(password, salt);
            return salt + "$" + encrypted;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("密码加密失败", e);
        }
    }
    
    /**
     * 验证密码
     */
    public static boolean verifyPassword(String password, String encryptedPassword) {
        try {
            String[] parts = encryptedPassword.split("\\$");
            if (parts.length != 2) {
                return false;
            }
            String salt = parts[0];
            String hash = parts[1];
            String testHash = hashPassword(password, salt);
            return hash.equals(testHash);
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * 使用盐值对密码进行哈希
     */
    private static String hashPassword(String password, String salt) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance(ALGORITHM);
        md.update(salt.getBytes());
        byte[] hashedPassword = md.digest(password.getBytes());
        return Base64.getEncoder().encodeToString(hashedPassword);
    }
}

