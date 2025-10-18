<template>
  <div class="layout-container">
    <!-- 侧边导航栏 -->
    <el-aside :width="isCollapse ? '64px' : '240px'" class="sidebar">
      <div class="logo-container">
        <el-icon v-if="!isCollapse" :size="32" color="#409EFF">
          <Notebook />
        </el-icon>
        <h2 v-if="!isCollapse" class="logo-title">TodoList</h2>
      </div>

      <!-- 导航菜单 -->
      <el-menu
        :default-active="activeMenu"
        :collapse="isCollapse"
        :collapse-transition="false"
        class="sidebar-menu"
        router
        background-color="#304156"
        text-color="#bfcbd9"
        active-text-color="#409EFF"
      >
        <el-menu-item index="/dashboard">
          <el-icon><HomeFilled /></el-icon>
          <template #title>首页</template>
        </el-menu-item>

        <el-menu-item index="/todos">
          <el-icon><List /></el-icon>
          <template #title>待办事项</template>
        </el-menu-item>

        <el-menu-item index="/focus">
          <el-icon><Timer /></el-icon>
          <template #title>专注时间</template>
        </el-menu-item>

        <el-menu-item index="/anniversaries">
          <el-icon><Calendar /></el-icon>
          <template #title>纪念日</template>
        </el-menu-item>
      </el-menu>

      <!-- 底部用户信息 -->
      <div class="user-info">
        <el-dropdown trigger="click" @command="handleCommand">
          <div class="user-avatar">
            <el-avatar :size="isCollapse ? 32 : 40" :icon="UserFilled" />
            <div v-if="!isCollapse" class="user-details">
              <div class="username">{{ currentUser?.username }}</div>
              <div class="user-email">{{ currentUser?.email }}</div>
            </div>
          </div>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item command="profile">
                <el-icon><User /></el-icon>
                个人资料
              </el-dropdown-item>
              <el-dropdown-item command="settings">
                <el-icon><Setting /></el-icon>
                设置
              </el-dropdown-item>
              <el-dropdown-item divided command="logout">
                <el-icon><SwitchButton /></el-icon>
                退出登录
              </el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
    </el-aside>

    <!-- 主内容区域 -->
    <el-container class="main-container">
      <!-- 顶部栏 -->
      <el-header class="header">
        <div class="header-left">
          <el-button
            :icon="isCollapse ? Expand : Fold"
            circle
            @click="toggleCollapse"
          />
          <el-breadcrumb separator="/">
            <el-breadcrumb-item :to="{ path: '/dashboard' }">首页</el-breadcrumb-item>
            <el-breadcrumb-item v-if="breadcrumbTitle">{{ breadcrumbTitle }}</el-breadcrumb-item>
          </el-breadcrumb>
        </div>

        <div class="header-right">
          <el-badge :value="notificationCount" class="notification-badge">
            <el-button :icon="Bell" circle />
          </el-badge>
        </div>
      </el-header>

      <!-- 内容区域 -->
      <el-main class="content">
        <router-view v-slot="{ Component }">
          <transition name="fade-transform" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>
      </el-main>
    </el-container>
  </div>
</template>

<script setup>
import { ref, computed, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import {
  Notebook,
  HomeFilled,
  List,
  Timer,
  Calendar,
  UserFilled,
  User,
  Setting,
  SwitchButton,
  Fold,
  Expand,
  Bell
} from '@element-plus/icons-vue'
import { ElMessageBox } from 'element-plus'

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()

// 侧边栏折叠状态
const isCollapse = ref(false)

// 当前用户
const currentUser = computed(() => authStore.currentUser)

// 通知数量（示例）
const notificationCount = ref(3)

// 当前激活的菜单
const activeMenu = computed(() => route.path)

// 面包屑标题
const breadcrumbTitle = computed(() => {
  const titleMap = {
    '/dashboard': '',
    '/todos': '待办事项',
    '/focus': '专注时间',
    '/anniversaries': '纪念日'
  }
  return titleMap[route.path] || ''
})

// 切换侧边栏折叠
const toggleCollapse = () => {
  isCollapse.value = !isCollapse.value
}

// 处理下拉菜单命令
const handleCommand = (command) => {
  switch (command) {
    case 'profile':
      // 跳转个人资料页（待实现）
      console.log('个人资料')
      break
    case 'settings':
      // 跳转设置页（待实现）
      console.log('设置')
      break
    case 'logout':
      handleLogout()
      break
  }
}

// 退出登录
const handleLogout = () => {
  ElMessageBox.confirm('确定要退出登录吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  })
    .then(() => {
      authStore.logout()
      router.push('/login')
    })
    .catch(() => {
      // 取消退出
    })
}
</script>

<style scoped>
.layout-container {
  display: flex;
  height: 100vh;
  width: 100%;
  background-color: #f0f2f5;
}

/* 侧边栏 */
.sidebar {
  background-color: #304156;
  display: flex;
  flex-direction: column;
  transition: width 0.3s;
  box-shadow: 2px 0 6px rgba(0, 0, 0, 0.1);
}

.logo-container {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
  height: 64px;
  padding: 0 20px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.logo-title {
  color: #fff;
  font-size: 20px;
  font-weight: 600;
  margin: 0;
  white-space: nowrap;
}

.sidebar-menu {
  flex: 1;
  border-right: none;
  overflow-y: auto;
}

.sidebar-menu:not(.el-menu--collapse) {
  width: 240px;
}

/* 用户信息 */
.user-info {
  padding: 16px;
  border-top: 1px solid rgba(255, 255, 255, 0.1);
}

.user-avatar {
  display: flex;
  align-items: center;
  gap: 12px;
  cursor: pointer;
  padding: 8px;
  border-radius: 8px;
  transition: background-color 0.3s;
}

.user-avatar:hover {
  background-color: rgba(255, 255, 255, 0.1);
}

.user-details {
  flex: 1;
  min-width: 0;
}

.username {
  color: #fff;
  font-size: 14px;
  font-weight: 500;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.user-email {
  color: #bfcbd9;
  font-size: 12px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

/* 主容器 */
.main-container {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

/* 顶部栏 */
.header {
  background-color: #fff;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.08);
  z-index: 10;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 16px;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 12px;
}

.notification-badge {
  cursor: pointer;
}

/* 内容区域 */
.content {
  flex: 1;
  padding: 20px;
  overflow-y: auto;
  background-color: #f0f2f5;
}

/* 过渡动画 */
.fade-transform-enter-active,
.fade-transform-leave-active {
  transition: all 0.3s;
}

.fade-transform-enter-from {
  opacity: 0;
  transform: translateX(-30px);
}

.fade-transform-leave-to {
  opacity: 0;
  transform: translateX(30px);
}

/* 滚动条样式 */
.sidebar-menu::-webkit-scrollbar {
  width: 6px;
}

.sidebar-menu::-webkit-scrollbar-thumb {
  background-color: rgba(255, 255, 255, 0.2);
  border-radius: 3px;
}

.sidebar-menu::-webkit-scrollbar-thumb:hover {
  background-color: rgba(255, 255, 255, 0.3);
}

.content::-webkit-scrollbar {
  width: 8px;
}

.content::-webkit-scrollbar-thumb {
  background-color: rgba(0, 0, 0, 0.2);
  border-radius: 4px;
}

.content::-webkit-scrollbar-thumb:hover {
  background-color: rgba(0, 0, 0, 0.3);
}
</style>
