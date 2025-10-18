<template>
  <div class="todolist-container">
    <el-card>
      <template #header>
        <div class="header-content">
          <h2>📋 待办事项</h2>
          <el-button type="primary" :icon="Plus">新建待办</el-button>
        </div>
      </template>
      
      <el-empty description="待办事项功能开发中...">
        <el-button type="primary">开始创建</el-button>
      </el-empty>
    </el-card>
  </div>
</template>

<script setup>
import { Plus } from '@element-plus/icons-vue'
</script>

<style scoped>
.todolist-container {
  max-width: 1200px;
  margin: 0 auto;
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-content h2 {
  margin: 0;
  font-size: 20px;
  color: #303133;
}
</style>
