import api from '@/utils/api'

export const todoService = {
  /**
   * 创建待办事项
   */
  createTodo(todoData) {
    return api.post('/todoitems', todoData)
  },

  /**
   * 根据ID获取待办事项
   */
  getTodoById(id) {
    return api.get(`/todoitems/${id}`)
  },

  /**
   * 获取用户的所有待办事项
   */
  getTodosByUserId(userId) {
    return api.get(`/todoitems/user/${userId}`)
  },

  /**
   * 根据状态获取待办事项
   */
  getTodosByStatus(userId, isCompleted) {
    return api.get(`/todoitems/user/${userId}/status/${isCompleted}`)
  },

  /**
   * 根据优先级获取待办事项
   */
  getTodosByPriority(userId, priority) {
    return api.get(`/todoitems/user/${userId}/priority/${priority}`)
  },

  /**
   * 获取今天的待办事项
   */
  getTodayTodos(userId) {
    return api.get(`/todoitems/user/${userId}/today`)
  },

  /**
   * 获取即将到期的待办事项
   */
  getUpcomingTodos(userId, days) {
    return api.get(`/todoitems/user/${userId}/upcoming?days=${days}`)
  },

  /**
   * 获取已过期的待办事项
   */
  getOverdueTodos(userId) {
    return api.get(`/todoitems/user/${userId}/overdue`)
  },

  /**
   * 更新待办事项
   */
  updateTodo(id, todoData) {
    return api.put(`/todoitems/${id}`, todoData)
  },

  /**
   * 标记为完成
   */
  markComplete(id) {
    return api.post(`/todoitems/${id}/complete`)
  },

  /**
   * 标记为未完成
   */
  markIncomplete(id) {
    return api.post(`/todoitems/${id}/incomplete`)
  },

  /**
   * 删除待办事项
   */
  deleteTodo(id) {
    return api.delete(`/todoitems/${id}`)
  },

  /**
   * 获取统计信息
   */
  getStatistics(userId) {
    return api.get(`/todoitems/user/${userId}/statistics`)
  }
}
