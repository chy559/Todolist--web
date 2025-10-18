<template>
  <div class="anniversary-container">
    <el-card>
      <template #header>
        <div class="header-content">
          <h2>🎉 纪念日</h2>
          <el-button type="warning" :icon="Plus">添加纪念日</el-button>
        </div>
      </template>
      
      <el-empty description="纪念日功能开发中...">
        <el-button type="warning">开始创建</el-button>
      </el-empty>
    </el-card>
  </div>
</template>

<script setup>
import { Plus } from '@element-plus/icons-vue'
</script>

<style scoped>
.anniversary-container {
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
