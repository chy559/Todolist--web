<template>
  <div class="focus-container">
    <el-card>
      <template #header>
        <div class="header-content">
          <h2>⏰ 专注时间</h2>
        </div>
      </template>
      
      <el-empty description="专注时间功能开发中...">
        <el-button type="success">开始专注</el-button>
      </el-empty>
    </el-card>
  </div>
</template>

<script setup>
</script>

<style scoped>
.focus-container {
  max-width: 800px;
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
