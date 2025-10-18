<template>
  <div class="dashboard-container">
    <h1>欢迎回来，{{ currentUser?.username }}！</h1>
    <p>这是首页仪表盘（待实现）</p>
    
    <el-button type="danger" @click="handleLogout">退出登录</el-button>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const router = useRouter()
const authStore = useAuthStore()

const currentUser = computed(() => authStore.currentUser)

const handleLogout = () => {
  authStore.logout()
  router.push('/login')
}
</script>

<style scoped>
.dashboard-container {
  padding: 20px;
}
</style>
