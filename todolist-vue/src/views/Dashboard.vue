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
    <el-row :gutter="20" class="stats-row">
      <el-col :xs="24" :sm="12" :lg="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <el-icon :size="40" color="#409EFF">
              <Document />
            </el-icon>
            <div class="stat-info">
              <div class="stat-value">12</div>
              <div class="stat-label">待办事项</div>
            </div>
          </div>
        </el-card>
      </el-col>

      <el-col :xs="24" :sm="12" :lg="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <el-icon :size="40" color="#67C23A">
              <CircleCheck />
            </el-icon>
            <div class="stat-info">
              <div class="stat-value">8</div>
              <div class="stat-label">已完成</div>
            </div>
          </div>
        </el-card>
      </el-col>

      <el-col :xs="24" :sm="12" :lg="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <el-icon :size="40" color="#E6A23C">
              <Timer />
            </el-icon>
            <div class="stat-info">
              <div class="stat-value">125</div>
              <div class="stat-label">专注分钟</div>
            </div>
          </div>
        </el-card>
      </el-col>

      <el-col :xs="24" :sm="12" :lg="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <el-icon :size="40" color="#F56C6C">
              <Calendar />
            </el-icon>
            <div class="stat-info">
              <div class="stat-value">5</div>
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

    <!-- 即将到来的纪念日 -->
    <el-card class="upcoming-section">
      <template #header>
        <div class="card-header">
          <span>即将到来的纪念日</span>
        </div>
      </template>
      <el-empty description="暂无即将到来的纪念日" />
    </el-card>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useAuthStore } from '@/stores/auth'
import {
  Sunny,
  Document,
  CircleCheck,
  Timer,
  Calendar,
  Plus
} from '@element-plus/icons-vue'

const authStore = useAuthStore()
const currentUser = computed(() => authStore.currentUser)
</script>

<style scoped>
.dashboard-container {
  max-width: 1400px;
  margin: 0 auto;
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
}
</style>
