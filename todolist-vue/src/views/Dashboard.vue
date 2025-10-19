<template>
  <div class="dashboard-container">
    <!-- 欢迎卡片 -->
    <el-card class="welcome-card">
      <div class="welcome-content">
        <div class="welcome-text">
          <h1>欢迎回来，{{ currentUser?.username }}！👋</h1>
          <p>今天也要加油哦~</p>
        </div>
        <el-icon :size="80" color="#409EFF">
          <Sunny />
        </el-icon>
      </div>
    </el-card>

    <!-- 统计卡片 -->
    <el-row :gutter="20" class="stats-row" v-loading="loading">
      <el-col :xs="24" :sm="12" :lg="6">
        <el-card class="stat-card" @click="router.push('/todos')">
          <div class="stat-content">
            <el-icon :size="40" color="#409EFF">
              <Document />
            </el-icon>
            <div class="stat-info">
              <div class="stat-value">{{ statistics.totalTodos }}</div>
              <div class="stat-label">待办事项</div>
            </div>
          </div>
        </el-card>
      </el-col>

      <el-col :xs="24" :sm="12" :lg="6">
        <el-card class="stat-card" @click="router.push('/todos')">
          <div class="stat-content">
            <el-icon :size="40" color="#67C23A">
              <CircleCheck />
            </el-icon>
            <div class="stat-info">
              <div class="stat-value">{{ statistics.completedTodos }}</div>
              <div class="stat-label">已完成</div>
            </div>
          </div>
        </el-card>
      </el-col>

      <el-col :xs="24" :sm="12" :lg="6">
        <el-card class="stat-card" @click="router.push('/focus')">
          <div class="stat-content">
            <el-icon :size="40" color="#E6A23C">
              <Timer />
            </el-icon>
            <div class="stat-info">
              <div class="stat-value">{{ statistics.totalFocusMinutes }}</div>
              <div class="stat-label">专注分钟</div>
            </div>
          </div>
        </el-card>
      </el-col>

      <el-col :xs="24" :sm="12" :lg="6">
        <el-card class="stat-card" @click="router.push('/anniversaries')">
          <div class="stat-content">
            <el-icon :size="40" color="#F56C6C">
              <Calendar />
            </el-icon>
            <div class="stat-info">
              <div class="stat-value">{{ statistics.totalAnniversaries }}</div>
              <div class="stat-label">纪念日</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 快速操作 -->
    <el-card class="quick-actions">
      <template #header>
        <div class="card-header">
          <span>快速操作</span>
        </div>
      </template>
      <div class="actions-content">
        <el-button type="primary" :icon="Plus" @click="$router.push('/todos')">
          新建待办
        </el-button>
        <el-button type="success" :icon="Timer" @click="$router.push('/focus')">
          开始专注
        </el-button>
        <el-button type="warning" :icon="Calendar" @click="$router.push('/anniversaries')">
          添加纪念日
        </el-button>
      </div>
    </el-card>

    <!-- 数据展示区 -->
    <el-row :gutter="20">
      <el-col :xs="24" :md="12">
        <!-- 即将到期的待办 -->
        <el-card class="upcoming-section">
          <template #header>
            <div class="card-header">
              <span>📋 即将到期的待办</span>
              <el-button type="primary" text size="small" @click="router.push('/todos')">
                查看全部
              </el-button>
            </div>
          </template>
          <div v-loading="loading">
            <div v-if="upcomingTodos.length > 0" class="list-container">
              <div v-for="todo in upcomingTodos" :key="todo.id" class="list-item">
                <div class="item-content">
                  <div class="item-title">{{ todo.title }}</div>
                  <div class="item-meta">
                    <el-tag v-if="todo.priority === 'HIGH'" type="danger" size="small">高</el-tag>
                    <el-tag v-else-if="todo.priority === 'MEDIUM'" type="warning" size="small">中</el-tag>
                    <el-tag v-else type="info" size="small">低</el-tag>
                    <span class="item-time">{{ formatDate(todo.dueDate) }}</span>
                  </div>
                </div>
                <div class="item-countdown">
                  <span :class="getCountdownClass(todo.daysUntilDue)">
                    {{ getCountdownText(todo.daysUntilDue) }}
                  </span>
                </div>
              </div>
            </div>
            <el-empty v-else description="暂无即将到期的待办" :image-size="80" />
          </div>
        </el-card>
      </el-col>

      <el-col :xs="24" :md="12">
        <!-- 即将到来的纪念日 -->
        <el-card class="upcoming-section">
          <template #header>
            <div class="card-header">
              <span>🎊 即将到来的纪念日</span>
              <el-button type="primary" text size="small" @click="router.push('/anniversaries')">
                查看全部
              </el-button>
            </div>
          </template>
          <div v-loading="loading">
            <div v-if="upcomingAnniversaries.length > 0" class="list-container">
              <div v-for="ann in upcomingAnniversaries" :key="ann.id" class="list-item">
                <div class="item-icon">{{ getTypeIcon(ann.type) }}</div>
                <div class="item-content">
                  <div class="item-title">{{ ann.title }}</div>
                  <div class="item-meta">
                    <span class="item-time">{{ formatDate(ann.targetDate) }}</span>
                  </div>
                </div>
                <div class="item-countdown anniversary">
                  <span>{{ getCountdownText(ann.daysUntil) }}</span>
                </div>
              </div>
            </div>
            <el-empty v-else description="暂无即将到来的纪念日" :image-size="80" />
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 今日专注会话 -->
    <el-card class="upcoming-section" style="margin-top: 20px;">
      <template #header>
        <div class="card-header">
          <span>⏱️ 今日专注会话</span>
          <el-button type="primary" text size="small" @click="router.push('/focus')">
            查看全部
          </el-button>
        </div>
      </template>
      <div v-loading="loading">
        <div v-if="todayFocusSessions.length > 0" class="list-container">
          <div v-for="session in todayFocusSessions" :key="session.id" class="list-item">
            <div class="item-content">
              <div class="item-title">{{ session.duration }} 分钟专注</div>
              <div class="item-meta">
                <el-tag :type="getStatusType(session.status)" size="small">
                  {{ getStatusText(session.status) }}
                </el-tag>
                <span v-if="session.startTime" class="item-time">
                  {{ formatDate(session.startTime) }}
                </span>
              </div>
              <div v-if="session.notes" class="item-notes">{{ session.notes }}</div>
            </div>
            <div v-if="session.actualDuration" class="item-actual">
              实际: {{ session.actualDuration }}分钟
            </div>
          </div>
        </div>
        <el-empty v-else description="今天还没有专注记录" :image-size="80">
          <el-button type="primary" @click="router.push('/focus')">开始第一个专注</el-button>
        </el-empty>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useAuthStore } from '@/stores/auth'
import { useRouter } from 'vue-router'
import { todoService } from '@/services/todoService'
import { focusService } from '@/services/focusService'
import { anniversaryService } from '@/services/anniversaryService'
import { format, parseISO } from 'date-fns'
import {
  Sunny,
  Document,
  CircleCheck,
  Timer,
  Calendar,
  Plus
} from '@element-plus/icons-vue'

const authStore = useAuthStore()
const router = useRouter()
const currentUser = computed(() => authStore.currentUser)
const userId = computed(() => authStore.currentUser?.id)

// 统计数据
const statistics = reactive({
  totalTodos: 0,
  completedTodos: 0,
  overdueTodos: 0,
  totalFocusMinutes: 0,
  totalSessions: 0,
  completedSessions: 0,
  totalAnniversaries: 0,
  upcomingAnniversaries: 0
})

// 即将到来的数据
const upcomingTodos = ref([])
const todayFocusSessions = ref([])
const upcomingAnniversaries = ref([])

const loading = ref(false)

// 加载待办数据
const loadTodoStats = async () => {
  if (!userId.value) return
  try {
    const res = await todoService.getStatistics(userId.value)
    if (res.code === 200) {
      statistics.totalTodos = res.data.totalTodos
      statistics.completedTodos = res.data.completedTodos
      statistics.overdueTodos = res.data.overdueTodos
    }
  } catch (e) { console.error(e) }
}

// 加载即将到期的待办
const loadUpcomingTodos = async () => {
  if (!userId.value) return
  try {
    const res = await todoService.getUpcomingTodos(userId.value, 7)
    if (res.code === 200) {
      upcomingTodos.value = (res.data || []).slice(0, 5)
    }
  } catch (e) { console.error(e) }
}

// 加载专注数据
const loadFocusStats = async () => {
  if (!userId.value) return
  try {
    const res = await focusService.getFocusStatistics(userId.value)
    if (res.code === 200) {
      statistics.totalFocusMinutes = res.data.totalFocusMinutes
      statistics.totalSessions = res.data.totalSessions
      statistics.completedSessions = res.data.completedSessions
    }
  } catch (e) { console.error(e) }
}

// 加载今日专注会话
const loadTodayFocus = async () => {
  if (!userId.value) return
  try {
    const res = await focusService.getTodayFocusSessions(userId.value)
    if (res.code === 200) {
      todayFocusSessions.value = (res.data || []).slice(0, 5)
    }
  } catch (e) { console.error(e) }
}

// 加载纪念日数据
const loadAnniversaryStats = async () => {
  if (!userId.value) return
  try {
    const res = await anniversaryService.getAnniversaryStatistics(userId.value)
    if (res.code === 200) {
      statistics.totalAnniversaries = res.data.totalAnniversaries
    }
  } catch (e) { console.error(e) }
}

// 加载即将到来的纪念日
const loadUpcomingAnniversaries = async () => {
  if (!userId.value) return
  try {
    const res = await anniversaryService.getUpcomingAnniversaries(userId.value, 30)
    if (res.code === 200) {
      upcomingAnniversaries.value = (res.data || []).slice(0, 5)
      statistics.upcomingAnniversaries = (res.data || []).length
    }
  } catch (e) { console.error(e) }
}

// 加载所有数据
const loadAllData = async () => {
  loading.value = true
  try {
    await Promise.all([
      loadTodoStats(),
      loadUpcomingTodos(),
      loadFocusStats(),
      loadTodayFocus(),
      loadAnniversaryStats(),
      loadUpcomingAnniversaries()
    ])
  } finally {
    loading.value = false
  }
}

// 格式化日期
const formatDate = (d) => {
  if (!d) return ''
  try { return format(parseISO(d), 'MM月dd日 HH:mm') } catch { return d }
}

// 获取倒计时文本
const getCountdownText = (days) => {
  if (days === 0) return '今天'
  if (days < 0) return `过期${Math.abs(days)}天`
  return `还剩${days}天`
}

// 获取倒计时样式类
const getCountdownClass = (days) => {
  if (days === 0) return 'countdown-today'
  if (days < 0) return 'countdown-overdue'
  if (days <= 3) return 'countdown-urgent'
  return 'countdown-normal'
}

// 获取类型图标
const getTypeIcon = (t) => ({ 
  BIRTHDAY: '🎂', WEDDING: '💍', RELATIONSHIP: '💕', 
  WORK: '💼', HOLIDAY: '🎉', OTHER: '📌' 
}[t] || '📌')

// 获取会话状态文本
const getStatusText = (s) => ({
  PENDING: '待开始', IN_PROGRESS: '进行中',
  COMPLETED: '已完成', CANCELLED: '已取消'
}[s] || s)

// 获取会话状态类型
const getStatusType = (s) => ({
  PENDING: 'info', IN_PROGRESS: 'primary',
  COMPLETED: 'success', CANCELLED: 'danger'
}[s] || 'info')

onMounted(loadAllData)
</script>

<style scoped>
.dashboard-container {
  max-width: 1400px;
  margin: 0 auto;
  padding: 0;
}

/* 欢迎卡片 */
.welcome-card {
  margin-bottom: 20px;
}

.welcome-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px;
}

.welcome-text h1 {
  margin: 0 0 10px 0;
  font-size: 28px;
  color: #303133;
}

.welcome-text p {
  margin: 0;
  font-size: 16px;
  color: #909399;
}

/* 统计卡片 */
.stats-row {
  margin-bottom: 20px;
}

.stat-card {
  margin-bottom: 20px;
  cursor: pointer;
  transition: all 0.3s;
}

.stat-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.stat-content {
  display: flex;
  align-items: center;
  gap: 20px;
  padding: 10px;
}

.stat-info {
  flex: 1;
}

.stat-value {
  font-size: 28px;
  font-weight: bold;
  color: #303133;
  margin-bottom: 5px;
}

.stat-label {
  font-size: 14px;
  color: #909399;
}

/* 快速操作 */
.quick-actions {
  margin-bottom: 20px;
}

.card-header {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.actions-content {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
}

/* 即将到来的纪念日 */
.upcoming-section {
  margin-bottom: 20px;
}

/* 列表容器 */
.list-container {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.list-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px;
  background-color: #F5F7FA;
  border-radius: 8px;
  transition: all 0.3s;
}

.list-item:hover {
  background-color: #E4E7ED;
  transform: translateX(5px);
}

.item-icon {
  font-size: 24px;
  flex-shrink: 0;
}

.item-content {
  flex: 1;
  min-width: 0;
}

.item-title {
  font-size: 14px;
  font-weight: 500;
  color: #303133;
  margin-bottom: 4px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.item-meta {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 12px;
  color: #909399;
}

.item-time {
  font-size: 12px;
  color: #909399;
}

.item-notes {
  font-size: 12px;
  color: #909399;
  margin-top: 4px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.item-countdown {
  flex-shrink: 0;
  font-size: 14px;
  font-weight: bold;
}

.item-countdown.anniversary {
  color: #F56C6C;
}

.item-actual {
  flex-shrink: 0;
  font-size: 12px;
  color: #67C23A;
  font-weight: 500;
}

/* 倒计时样式 */
.countdown-today {
  color: #F56C6C;
}

.countdown-overdue {
  color: #909399;
}

.countdown-urgent {
  color: #E6A23C;
}

.countdown-normal {
  color: #409EFF;
}

/* 响应式 */
@media (max-width: 768px) {
  .welcome-content {
    flex-direction: column;
    text-align: center;
    gap: 20px;
  }

  .actions-content {
    flex-direction: column;
  }

  .actions-content .el-button {
    width: 100%;
  }
  
  .list-item {
    flex-direction: column;
    align-items: flex-start;
  }
  
  .item-countdown {
    width: 100%;
    text-align: right;
  }
}
</style>
