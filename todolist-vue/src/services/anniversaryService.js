import api from '@/utils/api'

export const anniversaryService = {
  /**
   * 创建纪念日
   */
  createAnniversary(anniversaryData) {
    return api.post('/anniversaries', anniversaryData)
  },

  /**
   * 根据ID获取纪念日
   */
  getAnniversaryById(id) {
    return api.get(`/anniversaries/${id}`)
  },

  /**
   * 获取用户的所有纪念日
   */
  getAnniversariesByUserId(userId) {
    return api.get(`/anniversaries/user/${userId}`)
  },

  /**
   * 获取纪念日（按类型）
   */
  getAnniversariesByType(userId, type) {
    return api.get(`/anniversaries/user/${userId}/type/${type}`)
  },

  /**
   * 获取纪念日（按是否循环）
   */
  getAnniversariesByRecurring(userId, isRecurring) {
    return api.get(`/anniversaries/user/${userId}/recurring/${isRecurring}`)
  },

  /**
   * 获取纪念日（按目标日期升序）
   */
  getAnniversariesSortedByTargetDate(userId) {
    return api.get(`/anniversaries/user/${userId}/sort/targetdate`)
  },

  /**
   * 获取纪念日（按创建时间倒序）
   */
  getAnniversariesSortedByCreateTime(userId) {
    return api.get(`/anniversaries/user/${userId}/sort/createtime`)
  },

  /**
   * 获取指定日期范围内的纪念日
   */
  getAnniversariesByDateRange(userId, start, end) {
    return api.get(`/anniversaries/user/${userId}/range`, {
      params: { start, end }
    })
  },

  /**
   * 获取即将到来的纪念日
   */
  getUpcomingAnniversaries(userId, days = 30) {
    return api.get(`/anniversaries/user/${userId}/upcoming`, {
      params: { days }
    })
  },

  /**
   * 获取今天的纪念日
   */
  getTodayAnniversaries(userId) {
    return api.get(`/anniversaries/user/${userId}/today`)
  },

  /**
   * 获取本月的纪念日
   */
  getThisMonthAnniversaries(userId) {
    return api.get(`/anniversaries/user/${userId}/thismonth`)
  },

  /**
   * 获取需要提醒的纪念日
   */
  getAnniversariesToRemind(userId) {
    return api.get(`/anniversaries/user/${userId}/remind`)
  },

  /**
   * 获取纪念日统计信息
   */
  getAnniversaryStatistics(userId) {
    return api.get(`/anniversaries/user/${userId}/statistics`)
  },

  /**
   * 更新纪念日
   */
  updateAnniversary(id, anniversaryData) {
    return api.put(`/anniversaries/${id}`, anniversaryData)
  },

  /**
   * 删除纪念日
   */
  deleteAnniversary(id) {
    return api.delete(`/anniversaries/${id}`)
  }
}
