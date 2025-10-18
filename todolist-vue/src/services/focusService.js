import api from '@/utils/api'

export const focusService = {
  /**
   * 创建专注会话
   */
  createFocusSession(sessionData) {
    return api.post('/focus', sessionData)
  },

  /**
   * 开始专注会话
   */
  startFocusSession(id) {
    return api.post(`/focus/${id}/start`)
  },

  /**
   * 完成专注会话
   */
  completeFocusSession(id) {
    return api.post(`/focus/${id}/complete`)
  },

  /**
   * 取消专注会话
   */
  cancelFocusSession(id) {
    return api.post(`/focus/${id}/cancel`)
  },

  /**
   * 根据ID获取专注会话
   */
  getFocusSessionById(id) {
    return api.get(`/focus/${id}`)
  },

  /**
   * 获取用户的所有专注会话
   */
  getFocusSessionsByUserId(userId) {
    return api.get(`/focus/user/${userId}`)
  },

  /**
   * 获取用户的专注会话（按状态）
   */
  getFocusSessionsByStatus(userId, status) {
    return api.get(`/focus/user/${userId}/status/${status}`)
  },

  /**
   * 获取用户正在进行的专注会话
   */
  getInProgressSession(userId) {
    return api.get(`/focus/user/${userId}/inprogress`)
  },

  /**
   * 获取用户今天的专注会话
   */
  getTodayFocusSessions(userId) {
    return api.get(`/focus/user/${userId}/today`)
  },

  /**
   * 获取用户的专注统计信息
   */
  getFocusStatistics(userId) {
    return api.get(`/focus/user/${userId}/statistics`)
  },

  /**
   * 获取用户的专注会话（按创建时间倒序）
   */
  getFocusSessionsSortedByCreateTime(userId) {
    return api.get(`/focus/user/${userId}/sort/createtime`)
  },

  /**
   * 获取用户的专注会话（按开始时间倒序）
   */
  getFocusSessionsSortedByStartTime(userId) {
    return api.get(`/focus/user/${userId}/sort/starttime`)
  },

  /**
   * 更新专注会话
   */
  updateFocusSession(id, sessionData) {
    return api.put(`/focus/${id}`, sessionData)
  },

  /**
   * 删除专注会话
   */
  deleteFocusSession(id) {
    return api.delete(`/focus/${id}`)
  }
}
