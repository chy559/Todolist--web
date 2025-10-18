import api from '@/utils/api'

export const userService = {
  /**
   * 用户注册
   */
  register(userData) {
    return api.post('/users/register', userData)
  },

  /**
   * 用户登录
   */
  login(credentials) {
    return api.post('/users/login', credentials)
  },

  /**
   * 根据ID获取用户信息
   */
  getUserById(userId) {
    return api.get(`/users/${userId}`)
  },

  /**
   * 获取所有用户
   */
  getAllUsers() {
    return api.get('/users')
  },

  /**
   * 更新用户信息
   */
  updateUser(userId, userData) {
    return api.put(`/users/${userId}`, userData)
  },

  /**
   * 删除用户
   */
  deleteUser(userId) {
    return api.delete(`/users/${userId}`)
  }
}
