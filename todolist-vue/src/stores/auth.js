import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { userService } from '@/services/userService'
import { ElMessage } from 'element-plus'

export const useAuthStore = defineStore('auth', () => {
  // State
  const user = ref(null)
  const token = ref(localStorage.getItem('token') || null)

  // Getters
  const isAuthenticated = computed(() => !!token.value)
  const currentUser = computed(() => user.value)

  // Actions
  function setUser(userData) {
    user.value = userData
    localStorage.setItem('user', JSON.stringify(userData))
  }

  function setToken(tokenValue) {
    token.value = tokenValue
    localStorage.setItem('token', tokenValue)
  }

  function clearAuth() {
    user.value = null
    token.value = null
    localStorage.removeItem('user')
    localStorage.removeItem('token')
  }

  async function login(credentials) {
    try {
      const response = await userService.login(credentials)
      
      if (response.code === 200) {
        const userData = response.data
        setUser(userData)
        setToken(userData.id.toString()) // 使用用户ID作为临时token
        ElMessage.success('登录成功！')
        return userData
      } else {
        ElMessage.error(response.message || '登录失败')
        return null
      }
    } catch (error) {
      console.error('登录失败:', error)
      return null
    }
  }

  async function register(userData) {
    try {
      const response = await userService.register(userData)
      
      if (response.code === 200) {
        ElMessage.success('注册成功，请登录')
        return true
      } else {
        ElMessage.error(response.message || '注册失败')
        return false
      }
    } catch (error) {
      console.error('注册失败:', error)
      return false
    }
  }

  function logout() {
    clearAuth()
    ElMessage.info('已退出登录')
  }

  // 初始化：从 localStorage 恢复用户信息
  function init() {
    const storedUser = localStorage.getItem('user')
    if (storedUser) {
      try {
        user.value = JSON.parse(storedUser)
      } catch (e) {
        console.error('解析用户信息失败:', e)
        clearAuth()
      }
    }
  }

  // 调用初始化
  init()

  return {
    user,
    token,
    isAuthenticated,
    currentUser,
    login,
    register,
    logout,
    setUser,
    setToken,
    clearAuth
  }
})
