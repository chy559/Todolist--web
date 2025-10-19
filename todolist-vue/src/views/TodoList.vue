<template>
  <div class="todolist-container">
    <!-- 头部统计 -->
    <el-row :gutter="20" class="stats-row">
      <el-col :xs="24" :sm="8">
        <el-card class="stat-card">
          <div class="stat-content">
            <el-icon :size="30" color="#409EFF"><Document /></el-icon>
            <div class="stat-info">
              <div class="stat-value">{{ statistics.totalTodos }}</div>
              <div class="stat-label">全部任务</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="8">
        <el-card class="stat-card">
          <div class="stat-content">
            <el-icon :size="30" color="#67C23A"><CircleCheck /></el-icon>
            <div class="stat-info">
              <div class="stat-value">{{ statistics.completedTodos }}</div>
              <div class="stat-label">已完成</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="8">
        <el-card class="stat-card">
          <div class="stat-content">
            <el-icon :size="30" color="#F56C6C"><Clock /></el-icon>
            <div class="stat-info">
              <div class="stat-value">{{ statistics.overdueTodos }}</div>
              <div class="stat-label">已过期</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 主卡片 -->
    <el-card>
      <template #header>
        <div class="header-content">
          <div class="header-left">
            <h2>📋 待办事项</h2>
            <!-- 筛选标签 -->
            <el-radio-group v-model="filterStatus" size="small" @change="loadTodos">
              <el-radio-button label="all">全部</el-radio-button>
              <el-radio-button label="active">进行中</el-radio-button>
              <el-radio-button label="completed">已完成</el-radio-button>
            </el-radio-group>
          </div>
          <el-button type="primary" :icon="Plus" @click="openAddDialog">
            新建待办
          </el-button>
        </div>
      </template>

      <!-- 搜索和排序 -->
      <div class="toolbar">
        <el-input
          v-model="searchKeyword"
          placeholder="搜索待办事项..."
          :prefix-icon="Search"
          clearable
          style="width: 300px"
          @input="handleSearch"
        />
        <el-select v-model="sortBy" placeholder="排序方式" style="width: 150px" @change="loadTodos">
          <el-option label="创建时间" value="createTime" />
          <el-option label="截止时间" value="dueDate" />
          <el-option label="优先级" value="priority" />
        </el-select>
      </div>

      <!-- 待办列表 -->
      <div v-loading="loading" class="todo-list">
        <template v-if="filteredTodos.length > 0">
          <div
            v-for="todo in filteredTodos"
            :key="todo.id"
            class="todo-item"
            :class="{ 'completed': todo.isCompleted, 'overdue': todo.isOverdue }"
          >
            <!-- 左侧：复选框 -->
            <el-checkbox
              :model-value="todo.isCompleted"
              @change="toggleComplete(todo)"
              size="large"
            />

            <!-- 中间：内容 -->
            <div class="todo-content" @click="openEditDialog(todo)">
              <div class="todo-title">
                <span :class="{ 'line-through': todo.isCompleted }">{{ todo.title }}</span>
                <el-tag
                  v-if="todo.priority"
                  :type="getPriorityType(todo.priority)"
                  size="small"
                  class="priority-tag"
                >
                  {{ getPriorityText(todo.priority) }}
                </el-tag>
              </div>
              <div class="todo-meta">
                <span v-if="todo.description" class="description">
                  {{ todo.description }}
                </span>
                <div class="meta-info">
                  <span v-if="todo.dueDate" class="due-date">
                    <el-icon><Clock /></el-icon>
                    {{ formatDueDate(todo.dueDate) }}
                    <span v-if="todo.daysUntilDue !== null" class="countdown">
                      ({{ getCountdownText(todo) }})
                    </span>
                  </span>
                  <span class="create-time">
                    <el-icon><Calendar /></el-icon>
                    {{ formatDate(todo.createTime) }}
                  </span>
                </div>
              </div>
            </div>

            <!-- 右侧：操作按钮 -->
            <div class="todo-actions">
              <el-button
                :icon="Edit"
                circle
                size="small"
                @click="openEditDialog(todo)"
              />
              <el-button
                :icon="Delete"
                circle
                size="small"
                type="danger"
                @click="handleDelete(todo)"
              />
            </div>
          </div>
        </template>
        <el-empty v-else description="暂无待办事项">
          <el-button type="primary" :icon="Plus" @click="openAddDialog">
            创建第一个待办
          </el-button>
        </el-empty>
      </div>
    </el-card>

    <!-- 添加/编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑待办' : '新建待办'"
      width="600px"
    >
      <el-form
        ref="todoFormRef"
        :model="todoForm"
        :rules="rules"
        label-width="100px"
      >
        <el-form-item label="标题" prop="title">
          <el-input
            v-model="todoForm.title"
            placeholder="请输入待办事项标题"
            maxlength="200"
            show-word-limit
          />
        </el-form-item>

        <el-form-item label="描述" prop="description">
          <el-input
            v-model="todoForm.description"
            type="textarea"
            :rows="3"
            placeholder="请输入描述（可选）"
          />
        </el-form-item>

        <el-form-item label="优先级" prop="priority">
          <el-select v-model="todoForm.priority" placeholder="请选择优先级">
            <el-option label="低" value="LOW" />
            <el-option label="中" value="MEDIUM" />
            <el-option label="高" value="HIGH" />
          </el-select>
        </el-form-item>

        <el-form-item label="截止时间" prop="dueDate">
          <el-date-picker
            v-model="todoForm.dueDate"
            type="datetime"
            placeholder="选择截止时间（可选）"
            format="YYYY-MM-DD HH:mm"
            value-format="YYYY-MM-DDTHH:mm:ss"
            :disabled-date="disabledDate"
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="handleSubmit">
          {{ isEdit ? '保存' : '创建' }}
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useAuthStore } from '@/stores/auth'
import { todoService } from '@/services/todoService'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Plus,
  Edit,
  Delete,
  Search,
  Document,
  CircleCheck,
  Clock,
  Calendar
} from '@element-plus/icons-vue'
import { format } from 'date-fns'

const authStore = useAuthStore()
const userId = computed(() => authStore.currentUser?.id)

// 数据
const todos = ref([])
const loading = ref(false)
const filterStatus = ref('all') // all, active, completed
const searchKeyword = ref('')
const sortBy = ref('createTime')

// 统计数据
const statistics = reactive({
  totalTodos: 0,
  completedTodos: 0,
  overdueTodos: 0
})

// 对话框
const dialogVisible = ref(false)
const isEdit = ref(false)
const submitting = ref(false)
const todoFormRef = ref(null)
const todoForm = reactive({
  id: null,
  title: '',
  description: '',
  priority: 'MEDIUM',
  dueDate: null,
  userId: null
})

// 表单验证规则
const rules = {
  title: [
    { required: true, message: '请输入标题', trigger: 'blur' },
    { max: 200, message: '标题不能超过200个字符', trigger: 'blur' }
  ],
  priority: [
    { required: true, message: '请选择优先级', trigger: 'change' }
  ]
}

// 计算属性：过滤后的待办列表
const filteredTodos = computed(() => {
  let result = todos.value

  // 按状态筛选
  if (filterStatus.value === 'active') {
    result = result.filter(todo => !todo.isCompleted)
  } else if (filterStatus.value === 'completed') {
    result = result.filter(todo => todo.isCompleted)
  }

  // 搜索
  if (searchKeyword.value) {
    const keyword = searchKeyword.value.toLowerCase()
    result = result.filter(todo =>
      todo.title.toLowerCase().includes(keyword) ||
      (todo.description && todo.description.toLowerCase().includes(keyword))
    )
  }

  return result
})

// 加载待办列表
const loadTodos = async () => {
  if (!userId.value) return

  loading.value = true
  try {
    const response = await todoService.getTodosByUserId(userId.value)
    if (response.code === 200) {
      todos.value = response.data || []
      sortTodos()
    }
  } catch (error) {
    console.error('加载待办列表失败:', error)
  } finally {
    loading.value = false
  }
}

// 加载统计数据
const loadStatistics = async () => {
  if (!userId.value) return

  try {
    const response = await todoService.getStatistics(userId.value)
    if (response.code === 200) {
      Object.assign(statistics, response.data)
    }
  } catch (error) {
    console.error('加载统计数据失败:', error)
  }
}

// 排序
const sortTodos = () => {
  todos.value.sort((a, b) => {
    if (sortBy.value === 'priority') {
      const priorityOrder = { HIGH: 3, MEDIUM: 2, LOW: 1 }
      return (priorityOrder[b.priority] || 0) - (priorityOrder[a.priority] || 0)
    } else if (sortBy.value === 'dueDate') {
      if (!a.dueDate) return 1
      if (!b.dueDate) return -1
      return new Date(a.dueDate) - new Date(b.dueDate)
    } else {
      // createTime
      return new Date(b.createTime) - new Date(a.createTime)
    }
  })
}

// 搜索
const handleSearch = () => {
  // 由计算属性自动处理
}

// 打开添加对话框
const openAddDialog = () => {
  isEdit.value = false
  resetForm()
  todoForm.userId = userId.value
  dialogVisible.value = true
}

// 打开编辑对话框
const openEditDialog = (todo) => {
  isEdit.value = true
  todoForm.id = todo.id
  todoForm.title = todo.title
  todoForm.description = todo.description || ''
  todoForm.priority = todo.priority
  todoForm.dueDate = todo.dueDate
  todoForm.userId = userId.value
  dialogVisible.value = true
}

// 提交表单
const handleSubmit = async () => {
  if (!todoFormRef.value) return

  try {
    await todoFormRef.value.validate()
    submitting.value = true

    const data = {
      title: todoForm.title,
      description: todoForm.description || null,
      priority: todoForm.priority,
      dueDate: todoForm.dueDate || null,
      userId: todoForm.userId
    }

    let response
    if (isEdit.value) {
      response = await todoService.updateTodo(todoForm.id, data)
    } else {
      response = await todoService.createTodo(data)
    }

    if (response.code === 200) {
      ElMessage.success(isEdit.value ? '更新成功' : '创建成功')
      dialogVisible.value = false
      loadTodos()
      loadStatistics()
    }
  } catch (error) {
    console.error('提交失败:', error)
  } finally {
    submitting.value = false
  }
}

// 切换完成状态
const toggleComplete = async (todo) => {
  const originalStatus = todo.isCompleted
  
  try {
    let response
    // 根据当前状态决定调用哪个API
    // 如果当前已完成，则标记为未完成；否则标记为完成
    if (originalStatus) {
      response = await todoService.markIncomplete(todo.id)
      ElMessage.success('已标记为未完成')
    } else {
      response = await todoService.markComplete(todo.id)
      ElMessage.success('已标记为完成')
    }

    if (response.code === 200) {
      // 重新加载列表和统计数据
      await loadTodos()
      await loadStatistics()
    }
  } catch (error) {
    console.error('切换状态失败:', error)
    // 如果失败，重新加载以恢复原状态
    loadTodos()
  }
}

// 删除待办
const handleDelete = (todo) => {
  ElMessageBox.confirm(
    `确定要删除待办"${todo.title}"吗？`,
    '提示',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    try {
      const response = await todoService.deleteTodo(todo.id)
      if (response.code === 200) {
        ElMessage.success('删除成功')
        loadTodos()
        loadStatistics()
      }
    } catch (error) {
      console.error('删除失败:', error)
    }
  }).catch(() => {
    // 取消删除
  })
}

// 重置表单
const resetForm = () => {
  todoForm.id = null
  todoForm.title = ''
  todoForm.description = ''
  todoForm.priority = 'MEDIUM'
  todoForm.dueDate = null
  todoForm.userId = null
  todoFormRef.value?.clearValidate()
}

// 禁用过去的日期
const disabledDate = (time) => {
  return time.getTime() < Date.now() - 24 * 60 * 60 * 1000
}

// 格式化日期
const formatDate = (dateStr) => {
  if (!dateStr) return ''
  try {
    return format(new Date(dateStr), 'yyyy-MM-dd HH:mm')
  } catch {
    return dateStr
  }
}

const formatDueDate = (dateStr) => {
  if (!dateStr) return ''
  try {
    return format(new Date(dateStr), 'MM月dd日 HH:mm')
  } catch {
    return dateStr
  }
}

// 获取倒计时文本
const getCountdownText = (todo) => {
  if (todo.isOverdue) {
    return `已过期 ${Math.abs(todo.daysUntilDue)} 天`
  } else if (todo.daysUntilDue === 0) {
    return '今天到期'
  } else if (todo.daysUntilDue === 1) {
    return '明天到期'
  } else {
    return `还剩 ${todo.daysUntilDue} 天`
  }
}

// 获取优先级类型
const getPriorityType = (priority) => {
  const types = {
    HIGH: 'danger',
    MEDIUM: 'warning',
    LOW: 'info'
  }
  return types[priority] || 'info'
}

// 获取优先级文本
const getPriorityText = (priority) => {
  const texts = {
    HIGH: '高',
    MEDIUM: '中',
    LOW: '低'
  }
  return texts[priority] || priority
}

// 初始化
onMounted(() => {
  loadTodos()
  loadStatistics()
})
</script>

<style scoped>
.todolist-container {
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

/* 头部 */
.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: 16px;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 16px;
  flex-wrap: wrap;
}

.header-left h2 {
  margin: 0;
  font-size: 20px;
  color: #303133;
}

/* 工具栏 */
.toolbar {
  display: flex;
  gap: 12px;
  margin-bottom: 20px;
  flex-wrap: wrap;
}

/* 待办列表 */
.todo-list {
  min-height: 300px;
}

.todo-item {
  display: flex;
  align-items: flex-start;
  gap: 16px;
  padding: 16px;
  border-bottom: 1px solid #EBEEF5;
  transition: all 0.3s;
}

.todo-item:hover {
  background-color: #F5F7FA;
}

.todo-item:last-child {
  border-bottom: none;
}

.todo-item.completed {
  opacity: 0.6;
}

.todo-item.overdue .todo-title {
  color: #F56C6C;
}

/* 待办内容 */
.todo-content {
  flex: 1;
  cursor: pointer;
  min-width: 0;
}

.todo-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 16px;
  font-weight: 500;
  color: #303133;
  margin-bottom: 8px;
  flex-wrap: wrap;
}

.todo-title .line-through {
  text-decoration: line-through;
}

.priority-tag {
  flex-shrink: 0;
}

.todo-meta {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.description {
  font-size: 14px;
  color: #606266;
  word-break: break-word;
}

.meta-info {
  display: flex;
  gap: 16px;
  flex-wrap: wrap;
  font-size: 13px;
  color: #909399;
}

.meta-info span {
  display: flex;
  align-items: center;
  gap: 4px;
}

.due-date {
  font-weight: 500;
}

.countdown {
  color: #F56C6C;
}

/* 操作按钮 */
.todo-actions {
  display: flex;
  gap: 8px;
  flex-shrink: 0;
}

/* 响应式 */
@media (max-width: 768px) {
  .header-left {
    width: 100%;
    flex-direction: column;
    align-items: flex-start;
  }

  .toolbar {
    flex-direction: column;
  }

  .toolbar .el-input,
  .toolbar .el-select {
    width: 100% !important;
  }

  .todo-item {
    flex-direction: column;
  }

  .todo-actions {
    width: 100%;
    justify-content: flex-end;
  }
}
</style>
