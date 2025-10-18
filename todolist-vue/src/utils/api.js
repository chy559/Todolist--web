import axios from 'axios'
import { ElMessage } from 'element-plus'

const api = axios.create({
  baseURL: '/api',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json'
  }
})

// 请求拦截器
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error) => {
    console.error('请求错误:', error)
    return Promise.reject(error)
  }
)

// 响应拦截器
api.interceptors.response.use(
  (response) => {
    // 直接返回后端的 ApiResponse 数据
    return response.data
  },
  (error) => {
    if (error.response) {
      const { status, data } = error.response
      
      // 处理 401 未授权
      if (status === 401) {
        ElMessage.error('登录已过期，请重新登录')
        localStorage.removeItem('token')
        localStorage.removeItem('user')
        window.location.href = '/login'
      } else {
        // 显示后端返回的错误信息
        ElMessage.error(data.message || '请求失败')
      }
    } else {
      ElMessage.error('网络错误，请检查网络连接')
    }
    
    return Promise.reject(error)
  }
)

export default api
