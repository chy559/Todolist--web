<template>
  <div class="focus-container">
    <!-- 统计卡片 -->
    <el-row :gutter="20" class="stats-row">
      <el-col :xs="24" :sm="8">
        <el-card class="stat-card">
          <div class="stat-content">
            <el-icon :size="30" color="#409EFF"><Timer /></el-icon>
            <div class="stat-info">
              <div class="stat-value">{{ statistics.totalSessions }}</div>
              <div class="stat-label">总会话数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="8">
        <el-card class="stat-card">
          <div class="stat-content">
            <el-icon :size="30" color="#67C23A"><CircleCheck /></el-icon>
            <div class="stat-info">
              <div class="stat-value">{{ statistics.completedSessions }}</div>
              <div class="stat-label">已完成</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="8">
        <el-card class="stat-card">
          <div class="stat-content">
            <el-icon :size="30" color="#E6A23C"><Clock /></el-icon>
            <div class="stat-info">
              <div class="stat-value">{{ statistics.totalFocusMinutes }}</div>
              <div class="stat-label">专注分钟</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 番茄钟计时器 -->
    <el-card class="timer-card">
      <template #header>
        <div class="header-content">
          <h2>⏱️ 番茄钟计时器</h2>
        </div>
      </template>

      <!-- 倒计时显示 -->
      <div class="timer-display">
        <div class="timer-circle" :class="{ 'timer-running': isRunning }">
          <div class="time-text">
            {{ formattedTime }}
          </div>
          <div class="time-label">
            {{ timerStatus }}
          </div>
        </div>

        <!-- 进度条 -->
        <el-progress
          v-if="currentSession"
          :percentage="progress"
          :stroke-width="8"
          :color="progressColor"
          class="timer-progress"
        />
      </div>

      <!-- 快速开始按钮 -->
      <div v-if="!currentSession" class="quick-start">
        <h3>快速开始</h3>
        <div class="quick-buttons">
          <el-button
            type="primary"
            size="large"
            @click="quickStart(25)"
          >
            <el-icon><Timer /></el-icon>
            25 分钟
          </el-button>
          <el-button
            type="success"
            size="large"
            @click="quickStart(45)"
          >
            <el-icon><Timer /></el-icon>
            45 分钟
          </el-button>
          <el-button
            type="warning"
            size="large"
            @click="quickStart(60)"
          >
            <el-icon><Timer /></el-icon>
            60 分钟
          </el-button>
          <el-button
            size="large"
            @click="showCustomDialog = true"
          >
            <el-icon><Setting /></el-icon>
            自定义
          </el-button>
        </div>
      </div>

      <!-- 控制按钮 -->
      <div v-else class="control-buttons">
        <el-button
          v-if="!isRunning"
          type="primary"
          size="large"
          :icon="VideoPlay"
          @click="resumeTimer"
        >
          继续
        </el-button>
        <el-button
          v-else
          type="warning"
          size="large"
          :icon="VideoPause"
          @click="pauseTimer"
        >
          暂停
        </el-button>
        <el-button
          type="success"
          size="large"
          :icon="CircleCheck"
          @click="completeSession"
        >
          完成
        </el-button>
        <el-button
          type="danger"
          size="large"
          :icon="Close"
          @click="cancelSession"
        >
          取消
        </el-button>
      </div>

      <!-- 备注 -->
      <div v-if="currentSession && currentSession.notes" class="session-notes">
        <el-icon><Document /></el-icon>
        <span>{{ currentSession.notes }}</span>
      </div>
    </el-card>

    <!-- 今日专注记录 -->
    <el-card>
      <template #header>
        <div class="header-content">
          <h2>📊 今日专注记录</h2>
          <el-button
            type="primary"
            text
            @click="loadTodaySessions"
          >
            <el-icon><Refresh /></el-icon>
            刷新
          </el-button>
        </div>
      </template>

      <div v-loading="loading" class="sessions-list">
        <template v-if="todaySessions.length > 0">
          <div
            v-for="session in todaySessions"
            :key="session.id"
            class="session-item"
            :class="`status-${session.status.toLowerCase()}`"
          >
            <div class="session-icon">
              <el-icon v-if="session.status === 'COMPLETED'" color="#67C23A" :size="24">
                <CircleCheck />
              </el-icon>
              <el-icon v-else-if="session.status === 'IN_PROGRESS'" color="#409EFF" :size="24">
                <Loading />
              </el-icon>
              <el-icon v-else-if="session.status === 'CANCELLED'" color="#F56C6C" :size="24">
                <Close />
              </el-icon>
              <el-icon v-else color="#909399" :size="24">
                <Clock />
              </el-icon>
            </div>

            <div class="session-content">
              <div class="session-time">
                <span class="duration">{{ session.duration }} 分钟</span>
                <el-tag :type="getStatusType(session.status)" size="small">
                  {{ getStatusText(session.status) }}
                </el-tag>
              </div>
              <div class="session-meta">
                <span v-if="session.startTime">
                  开始：{{ formatTime(session.startTime) }}
                </span>
                <span v-if="session.endTime">
                  结束：{{ formatTime(session.endTime) }}
                </span>
                <span v-if="session.actualDuration">
                  实际：{{ session.actualDuration }} 分钟
                </span>
              </div>
              <div v-if="session.notes" class="session-notes-text">
                {{ session.notes }}
              </div>
            </div>

            <div class="session-actions">
              <el-button
                v-if="session.status === 'PENDING'"
                type="primary"
                size="small"
                :icon="VideoPlay"
                @click="startExistingSession(session)"
              >
                开始
              </el-button>
              <el-button
                type="danger"
                size="small"
                :icon="Delete"
                @click="deleteSession(session)"
              >
                删除
              </el-button>
            </div>
          </div>
        </template>
        <el-empty v-else description="今天还没有专注记录">
          <el-button type="primary" @click="showCustomDialog = true">
            开始第一个专注会话
          </el-button>
        </el-empty>
      </div>
    </el-card>

    <!-- 自定义时长对话框 -->
    <el-dialog
      v-model="showCustomDialog"
      title="自定义专注时长"
      width="500px"
    >
      <el-form :model="customForm" label-width="100px">
        <el-form-item label="专注时长">
          <el-input-number
            v-model="customForm.duration"
            :min="1"
            :max="180"
            :step="5"
            style="width: 100%"
          />
          <span class="input-hint">分钟（1-180）</span>
        </el-form-item>
        <el-form-item label="备注">
          <el-input
            v-model="customForm.notes"
            type="textarea"
            :rows="3"
            placeholder="记录本次专注的目标（可选）"
            maxlength="500"
            show-word-limit
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="showCustomDialog = false">取消</el-button>
        <el-button type="primary" @click="startCustomSession">
          开始专注
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, onUnmounted } from 'vue'
import { useAuthStore } from '@/stores/auth'
import { focusService } from '@/services/focusService'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Timer,
  Clock,
  CircleCheck,
  VideoPlay,
  VideoPause,
  Close,
  Setting,
  Document,
  Refresh,
  Delete,
  Loading
} from '@element-plus/icons-vue'
import { format } from 'date-fns'

const authStore = useAuthStore()
const userId = computed(() => authStore.currentUser?.id)

// 数据
const loading = ref(false)
const currentSession = ref(null)
const todaySessions = ref([])
const isRunning = ref(false)
const remainingSeconds = ref(0)
const timer = ref(null)

// 统计数据
const statistics = reactive({
  totalSessions: 0,
  completedSessions: 0,
  totalFocusMinutes: 0
})

// 自定义对话框
const showCustomDialog = ref(false)
const customForm = reactive({
  duration: 25,
  notes: ''
})

// 计算属性
const formattedTime = computed(() => {
  const minutes = Math.floor(remainingSeconds.value / 60)
  const seconds = remainingSeconds.value % 60
  return `${String(minutes).padStart(2, '0')}:${String(seconds).padStart(2, '0')}`
})

const timerStatus = computed(() => {
  if (!currentSession.value) return '等待开始'
  if (isRunning.value) return '专注中'
  return '已暂停'
})

const progress = computed(() => {
  if (!currentSession.value) return 0
  const totalSeconds = currentSession.value.duration * 60
  const elapsed = totalSeconds - remainingSeconds.value
  return Math.min(100, Math.max(0, (elapsed / totalSeconds) * 100))
})

const progressColor = computed(() => {
  if (progress.value < 30) return '#409EFF'
  if (progress.value < 70) return '#E6A23C'
  return '#67C23A'
})

// 加载统计数据
const loadStatistics = async () => {
  if (!userId.value) return

  try {
    const response = await focusService.getFocusStatistics(userId.value)
    if (response.code === 200) {
      Object.assign(statistics, response.data)
    }
  } catch (error) {
    console.error('加载统计数据失败:', error)
  }
}

// 加载今日会话
const loadTodaySessions = async () => {
  if (!userId.value) return

  loading.value = true
  try {
    const response = await focusService.getTodayFocusSessions(userId.value)
    if (response.code === 200) {
      todaySessions.value = response.data || []
    }
  } catch (error) {
    console.error('加载今日会话失败:', error)
  } finally {
    loading.value = false
  }
}

// 检查是否有进行中的会话
const checkInProgressSession = async () => {
  if (!userId.value) return

  try {
    const response = await focusService.getInProgressSession(userId.value)
    if (response.code === 200 && response.data) {
      currentSession.value = response.data
      // 计算剩余时间
      const endTime = new Date(response.data.endTime)
      const now = new Date()
      const remaining = Math.max(0, Math.floor((endTime - now) / 1000))
      remainingSeconds.value = remaining
      
      if (remaining > 0) {
        startCountdown()
      } else {
        // 时间已到，自动完成
        await completeSession()
      }
    }
  } catch (error) {
    console.error('检查进行中会话失败:', error)
  }
}

// 快速开始
const quickStart = async (duration) => {
  if (!userId.value) return

  try {
    // 创建会话
    const createResponse = await focusService.createFocusSession({
      duration: duration,
      userId: userId.value
    })

    if (createResponse.code === 200) {
      // 立即开始
      const startResponse = await focusService.startFocusSession(createResponse.data.id)
      if (startResponse.code === 200) {
        currentSession.value = startResponse.data
        remainingSeconds.value = duration * 60
        startCountdown()
        ElMessage.success(`开始 ${duration} 分钟专注时间`)
        loadTodaySessions()
        loadStatistics()
      }
    }
  } catch (error) {
    console.error('快速开始失败:', error)
  }
}

// 自定义开始
const startCustomSession = async () => {
  if (!userId.value) return

  try {
    // 创建会话
    const createResponse = await focusService.createFocusSession({
      duration: customForm.duration,
      notes: customForm.notes || null,
      userId: userId.value
    })

    if (createResponse.code === 200) {
      // 立即开始
      const startResponse = await focusService.startFocusSession(createResponse.data.id)
      if (startResponse.code === 200) {
        currentSession.value = startResponse.data
        remainingSeconds.value = customForm.duration * 60
        startCountdown()
        showCustomDialog.value = false
        ElMessage.success(`开始 ${customForm.duration} 分钟专注时间`)
        
        // 重置表单
        customForm.duration = 25
        customForm.notes = ''
        
        loadTodaySessions()
        loadStatistics()
      }
    }
  } catch (error) {
    console.error('自定义开始失败:', error)
  }
}

// 开始已存在的会话
const startExistingSession = async (session) => {
  try {
    const response = await focusService.startFocusSession(session.id)
    if (response.code === 200) {
      currentSession.value = response.data
      remainingSeconds.value = session.duration * 60
      startCountdown()
      ElMessage.success('专注开始')
      loadTodaySessions()
    }
  } catch (error) {
    console.error('开始会话失败:', error)
  }
}

// 开始倒计时
const startCountdown = () => {
  isRunning.value = true
  
  timer.value = setInterval(() => {
    remainingSeconds.value--
    
    if (remainingSeconds.value <= 0) {
      // 时间到，自动完成
      completeSession()
    }
  }, 1000)
}

// 暂停计时器
const pauseTimer = () => {
  isRunning.value = false
  if (timer.value) {
    clearInterval(timer.value)
    timer.value = null
  }
}

// 继续计时器
const resumeTimer = () => {
  startCountdown()
}

// 完成会话
const completeSession = async () => {
  if (!currentSession.value) return

  pauseTimer()

  try {
    const response = await focusService.completeFocusSession(currentSession.value.id)
    if (response.code === 200) {
      ElMessage.success('恭喜完成专注！')
      resetTimer()
      loadTodaySessions()
      loadStatistics()
    }
  } catch (error) {
    console.error('完成会话失败:', error)
  }
}

// 取消会话
const cancelSession = () => {
  ElMessageBox.confirm(
    '确定要取消当前专注会话吗？',
    '提示',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    if (!currentSession.value) return

    pauseTimer()

    try {
      const response = await focusService.cancelFocusSession(currentSession.value.id)
      if (response.code === 200) {
        ElMessage.info('已取消专注')
        resetTimer()
        loadTodaySessions()
        loadStatistics()
      }
    } catch (error) {
      console.error('取消会话失败:', error)
    }
  }).catch(() => {
    // 取消操作
  })
}

// 删除会话
const deleteSession = (session) => {
  ElMessageBox.confirm(
    '确定要删除这条专注记录吗？',
    '提示',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    try {
      const response = await focusService.deleteFocusSession(session.id)
      if (response.code === 200) {
        ElMessage.success('删除成功')
        loadTodaySessions()
        loadStatistics()
      }
    } catch (error) {
      console.error('删除失败:', error)
    }
  }).catch(() => {
    // 取消删除
  })
}

// 重置计时器
const resetTimer = () => {
  pauseTimer()
  currentSession.value = null
  remainingSeconds.value = 0
}

// 获取状态类型
const getStatusType = (status) => {
  const types = {
    PENDING: 'info',
    IN_PROGRESS: 'primary',
    COMPLETED: 'success',
    CANCELLED: 'danger'
  }
  return types[status] || 'info'
}

// 获取状态文本
const getStatusText = (status) => {
  const texts = {
    PENDING: '待开始',
    IN_PROGRESS: '进行中',
    COMPLETED: '已完成',
    CANCELLED: '已取消'
  }
  return texts[status] || status
}

// 格式化时间
const formatTime = (dateStr) => {
  if (!dateStr) return ''
  try {
    return format(new Date(dateStr), 'HH:mm:ss')
  } catch {
    return dateStr
  }
}

// 初始化
onMounted(() => {
  loadStatistics()
  loadTodaySessions()
  checkInProgressSession()
})

// 清理
onUnmounted(() => {
  pauseTimer()
})
</script>

<style scoped>
.focus-container {
  max-width: 1400px;
  margin: 0 auto;
  padding: 0;
}

/* 统计卡片 */
.stats-row {
  margin-bottom: 20px;
}

.stat-card {
  cursor: pointer;
  transition: all 0.3s;
}

.stat-card:hover {
  transform: translateY(-3px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.stat-content {
  display: flex;
  align-items: center;
  gap: 16px;
}

.stat-info {
  flex: 1;
}

.stat-value {
  font-size: 24px;
  font-weight: bold;
  color: #303133;
  margin-bottom: 4px;
}

.stat-label {
  font-size: 14px;
  color: #909399;
}

/* 计时器卡片 */
.timer-card {
  margin-bottom: 20px;
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

h2 {
  margin: 0;
  font-size: 20px;
  color: #303133;
}

/* 计时器显示 */
.timer-display {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 40px 20px;
}

.timer-circle {
  width: 280px;
  height: 280px;
  border-radius: 50%;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  box-shadow: 0 8px 32px rgba(102, 126, 234, 0.3);
  transition: all 0.3s;
  margin-bottom: 30px;
}

.timer-circle.timer-running {
  animation: pulse 2s infinite;
}

@keyframes pulse {
  0%, 100% {
    transform: scale(1);
  }
  50% {
    transform: scale(1.02);
  }
}

.time-text {
  font-size: 64px;
  font-weight: bold;
  color: white;
  font-family: 'Monaco', 'Courier New', monospace;
}

.time-label {
  font-size: 18px;
  color: rgba(255, 255, 255, 0.9);
  margin-top: 8px;
}

.timer-progress {
  width: 100%;
  max-width: 400px;
}

/* 快速开始 */
.quick-start {
  text-align: center;
  padding: 20px 0;
}

.quick-start h3 {
  margin: 0 0 20px;
  color: #606266;
  font-size: 16px;
}

.quick-buttons {
  display: flex;
  gap: 12px;
  justify-content: center;
  flex-wrap: wrap;
}

/* 控制按钮 */
.control-buttons {
  display: flex;
  gap: 12px;
  justify-content: center;
  flex-wrap: wrap;
  padding: 20px 0;
}

/* 会话备注 */
.session-notes {
  display: flex;
  align-items: center;
  gap: 8px;
  justify-content: center;
  margin-top: 16px;
  padding: 12px;
  background-color: #F5F7FA;
  border-radius: 8px;
  color: #606266;
  font-size: 14px;
}

/* 今日记录列表 */
.sessions-list {
  min-height: 200px;
}

.session-item {
  display: flex;
  align-items: flex-start;
  gap: 16px;
  padding: 16px;
  border-bottom: 1px solid #EBEEF5;
  transition: all 0.3s;
}

.session-item:hover {
  background-color: #F5F7FA;
}

.session-item:last-child {
  border-bottom: none;
}

.session-icon {
  flex-shrink: 0;
}

.session-content {
  flex: 1;
  min-width: 0;
}

.session-time {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 8px;
}

.duration {
  font-size: 16px;
  font-weight: 500;
  color: #303133;
}

.session-meta {
  display: flex;
  gap: 16px;
  flex-wrap: wrap;
  font-size: 13px;
  color: #909399;
  margin-bottom: 4px;
}

.session-notes-text {
  margin-top: 8px;
  padding: 8px 12px;
  background-color: #F5F7FA;
  border-radius: 4px;
  font-size: 13px;
  color: #606266;
}

.session-actions {
  display: flex;
  gap: 8px;
  flex-shrink: 0;
}

/* 输入提示 */
.input-hint {
  margin-left: 8px;
  font-size: 12px;
  color: #909399;
}

/* 响应式 */
@media (max-width: 768px) {
  .timer-circle {
    width: 220px;
    height: 220px;
  }

  .time-text {
    font-size: 48px;
  }

  .time-label {
    font-size: 14px;
  }

  .quick-buttons {
    flex-direction: column;
  }

  .quick-buttons .el-button {
    width: 100%;
  }

  .control-buttons {
    flex-direction: column;
  }

  .control-buttons .el-button {
    width: 100%;
  }

  .session-item {
    flex-direction: column;
  }

  .session-actions {
    width: 100%;
    justify-content: flex-end;
  }
}
</style>
