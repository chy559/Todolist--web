<template>
  <div class="anniversary-container">
    <!-- 即将到来的纪念日倒计时 -->
    <el-row :gutter="20" class="countdown-row">
      <el-col v-for="anniversary in upcomingTop3" :key="anniversary.id" :xs="24" :sm="8">
        <el-card class="countdown-card" :class="`type-${anniversary.type.toLowerCase()}`">
          <div class="countdown-content">
            <div class="countdown-icon">{{ getTypeIcon(anniversary.type) }}</div>
            <div class="countdown-info">
              <div class="countdown-title">{{ anniversary.title }}</div>
              <div class="countdown-date">{{ formatDate(anniversary.targetDate) }}</div>
            </div>
            <div class="countdown-days">
              <div class="days-number">{{ Math.abs(anniversary.daysUntil) }}</div>
              <div class="days-label">{{ getCountdownText(anniversary.daysUntil) }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 日历和操作区 -->
    <el-row :gutter="20">
      <el-col :xs="24" :md="16">
        <el-card class="calendar-card">
          <template #header>
            <div class="header-content">
              <h2>📅 纪念日日历</h2>
              <el-button type="primary" :icon="Plus" @click="openAddDialog">添加纪念日</el-button>
            </div>
          </template>
          <el-calendar v-model="selectedDate">
            <template #date-cell="{ data }">
              <div class="calendar-day">
                <div class="day-number" :class="{ 'is-today': isToday(data.day) }">
                  {{ data.day.split('-').slice(2).join('') }}
                </div>
                <div v-if="getAnniversariesForDate(data.day).length > 0" class="day-events">
                  <div v-for="ann in getAnniversariesForDate(data.day).slice(0, 3)" :key="ann.id"
                    class="event-dot" :class="`type-${ann.type.toLowerCase()}`">
                    {{ getTypeIcon(ann.type) }}
                  </div>
                </div>
              </div>
            </template>
          </el-calendar>
        </el-card>
      </el-col>

      <el-col :xs="24" :md="8">
        <el-card>
          <template #header>
            <h2>📊 统计信息</h2>
          </template>
          <div class="stats-list">
            <div class="stat-item">
              <span class="stat-label">全部纪念日</span>
              <span class="stat-value">{{ statistics.totalAnniversaries }}</span>
            </div>
            <div class="stat-item">
              <span class="stat-label">🎂 生日</span>
              <span class="stat-value">{{ statistics.birthdayCount }}</span>
            </div>
            <div class="stat-item">
              <span class="stat-label">💍 结婚</span>
              <span class="stat-value">{{ statistics.weddingCount }}</span>
            </div>
            <div class="stat-item">
              <span class="stat-label">💕 恋爱</span>
              <span class="stat-value">{{ statistics.relationshipCount }}</span>
            </div>
            <div class="stat-item">
              <span class="stat-label">💼 工作</span>
              <span class="stat-value">{{ statistics.workCount }}</span>
            </div>
            <div class="stat-item">
              <span class="stat-label">🎉 节日</span>
              <span class="stat-value">{{ statistics.holidayCount }}</span>
            </div>
          </div>
        </el-card>

        <el-card style="margin-top: 20px;">
          <template #header>
            <h2>🔔 今日纪念日</h2>
          </template>
          <div v-if="todayAnniversaries.length > 0" class="today-list">
            <div v-for="ann in todayAnniversaries" :key="ann.id" class="today-item">
              <span class="today-icon">{{ getTypeIcon(ann.type) }}</span>
              <span class="today-title">{{ ann.title }}</span>
            </div>
          </div>
          <el-empty v-else description="今天没有纪念日" :image-size="60" />
        </el-card>
      </el-col>
    </el-row>

    <!-- 纪念日列表 -->
    <el-card style="margin-top: 20px;">
      <template #header>
        <div class="header-content">
          <h2>📋 所有纪念日</h2>
          <div class="header-actions">
            <el-select v-model="filterType" placeholder="类型筛选" clearable size="small" style="width: 120px; margin-right: 12px" @change="filterAnniversaries">
              <el-option label="生日" value="BIRTHDAY" />
              <el-option label="结婚" value="WEDDING" />
              <el-option label="恋爱" value="RELATIONSHIP" />
              <el-option label="工作" value="WORK" />
              <el-option label="节日" value="HOLIDAY" />
              <el-option label="其他" value="OTHER" />
            </el-select>
            <el-button type="primary" text :icon="Refresh" @click="loadAllData">刷新</el-button>
          </div>
        </div>
      </template>
      <div v-loading="loading">
        <el-table :data="filteredAnniversaries" stripe style="width: 100%">
          <el-table-column label="图标" width="80">
            <template #default="{ row }">
              <span style="font-size: 24px;">{{ getTypeIcon(row.type) }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="title" label="标题" min-width="150" />
          <el-table-column label="日期" width="150">
            <template #default="{ row }">
              {{ formatDate(row.targetDate) }}
            </template>
          </el-table-column>
          <el-table-column label="类型" width="100">
            <template #default="{ row }">
              <el-tag :type="getTypeTagType(row.type)" size="small">
                {{ getTypeText(row.type) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="倒计时" width="120">
            <template #default="{ row }">
              <span :style="{ color: getCountdownColor(row.daysUntil), fontWeight: 'bold' }">
                {{ getCountdownDisplay(row.daysUntil) }}
              </span>
            </template>
          </el-table-column>
          <el-table-column label="循环" width="80">
            <template #default="{ row }">
              <el-tag v-if="row.isRecurring" type="info" size="small">是</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="150" fixed="right">
            <template #default="{ row }">
              <el-button type="primary" size="small" :icon="Edit" @click="openEditDialog(row)" />
              <el-button type="danger" size="small" :icon="Delete" @click="deleteAnniversary(row)" />
            </template>
          </el-table-column>
        </el-table>
      </div>
    </el-card>

    <!-- 添加/编辑对话框 -->
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑纪念日' : '添加纪念日'" width="600px">
      <el-form :model="anniversaryForm" :rules="formRules" ref="formRef" label-width="100px">
        <el-form-item label="标题" prop="title">
          <el-input v-model="anniversaryForm.title" placeholder="如：妈妈的生日" maxlength="200" show-word-limit />
        </el-form-item>
        <el-form-item label="日期" prop="targetDate">
          <el-date-picker v-model="anniversaryForm.targetDate" type="date" placeholder="选择日期" style="width: 100%" format="YYYY-MM-DD" value-format="YYYY-MM-DD" />
        </el-form-item>
        <el-form-item label="类型" prop="type">
          <el-select v-model="anniversaryForm.type" placeholder="选择类型" style="width: 100%">
            <el-option label="🎂 生日" value="BIRTHDAY" />
            <el-option label="💍 结婚纪念日" value="WEDDING" />
            <el-option label="💕 恋爱纪念日" value="RELATIONSHIP" />
            <el-option label="💼 工作纪念日" value="WORK" />
            <el-option label="🎉 节日" value="HOLIDAY" />
            <el-option label="📌 其他" value="OTHER" />
          </el-select>
        </el-form-item>
        <el-form-item label="每年循环">
          <el-switch v-model="anniversaryForm.isRecurring" />
          <span class="form-hint">开启后每年自动循环提醒</span>
        </el-form-item>
        <el-form-item label="提前提醒">
          <el-input-number v-model="anniversaryForm.remindDaysBefore" :min="0" :max="365" :step="1" style="width: 100%" />
          <span class="form-hint">提前几天开始提醒（0表示不提前）</span>
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="anniversaryForm.description" type="textarea" :rows="3" placeholder="添加一些备注说明（可选）" maxlength="500" show-word-limit />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="handleSubmit">
          {{ isEdit ? '保存' : '添加' }}
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useAuthStore } from '@/stores/auth'
import { anniversaryService } from '@/services/anniversaryService'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Edit, Delete, Refresh } from '@element-plus/icons-vue'
import { format, parseISO, isToday as isTodayFn } from 'date-fns'

const authStore = useAuthStore()
const userId = computed(() => authStore.currentUser?.id)

const loading = ref(false)
const selectedDate = ref(new Date())
const allAnniversaries = ref([])
const upcomingAnniversaries = ref([])
const todayAnniversaries = ref([])
const filterType = ref('')

const statistics = reactive({
  totalAnniversaries: 0, birthdayCount: 0, weddingCount: 0,
  relationshipCount: 0, workCount: 0, holidayCount: 0, otherCount: 0
})

const dialogVisible = ref(false)
const isEdit = ref(false)
const submitting = ref(false)
const formRef = ref(null)

const anniversaryForm = reactive({
  title: '', targetDate: '', type: 'OTHER',
  isRecurring: false, remindDaysBefore: 0, description: ''
})

const formRules = {
  title: [{ required: true, message: '请输入纪念日标题', trigger: 'blur' }],
  targetDate: [{ required: true, message: '请选择日期', trigger: 'change' }],
  type: [{ required: true, message: '请选择类型', trigger: 'change' }]
}

const filteredAnniversaries = computed(() =>
  !filterType.value ? allAnniversaries.value : allAnniversaries.value.filter(a => a.type === filterType.value)
)

const upcomingTop3 = computed(() => upcomingAnniversaries.value.slice(0, 3))

const loadStatistics = async () => {
  if (!userId.value) return
  try {
    const res = await anniversaryService.getAnniversaryStatistics(userId.value)
    if (res.code === 200) Object.assign(statistics, res.data)
  } catch (e) { console.error(e) }
}

const loadAllAnniversaries = async () => {
  if (!userId.value) return
  loading.value = true
  try {
    const res = await anniversaryService.getAnniversariesSortedByTargetDate(userId.value)
    if (res.code === 200) allAnniversaries.value = res.data || []
  } catch (e) { console.error(e) } finally { loading.value = false }
}

const loadUpcoming = async () => {
  if (!userId.value) return
  try {
    const res = await anniversaryService.getUpcomingAnniversaries(userId.value, 30)
    if (res.code === 200) upcomingAnniversaries.value = res.data || []
  } catch (e) { console.error(e) }
}

const loadToday = async () => {
  if (!userId.value) return
  try {
    const res = await anniversaryService.getTodayAnniversaries(userId.value)
    if (res.code === 200) todayAnniversaries.value = res.data || []
  } catch (e) { console.error(e) }
}

const loadAllData = () => {
  loadStatistics()
  loadAllAnniversaries()
  loadUpcoming()
  loadToday()
}

const getAnniversariesForDate = (dateStr) => {
  const date = parseISO(dateStr)
  return allAnniversaries.value.filter(a => {
    const target = parseISO(a.targetDate)
    return a.isRecurring
      ? target.getMonth() === date.getMonth() && target.getDate() === date.getDate()
      : target.getTime() === date.getTime()
  })
}

const isToday = (dateStr) => isTodayFn(parseISO(dateStr))

const openAddDialog = () => {
  isEdit.value = false
  resetForm()
  dialogVisible.value = true
}

const openEditDialog = (ann) => {
  isEdit.value = true
  Object.assign(anniversaryForm, {
    id: ann.id, title: ann.title, targetDate: ann.targetDate,
    type: ann.type, isRecurring: ann.isRecurring,
    remindDaysBefore: ann.remindDaysBefore || 0,
    description: ann.description || ''
  })
  dialogVisible.value = true
}

const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (!valid) return
    submitting.value = true
    try {
      const data = { ...anniversaryForm, userId: userId.value }
      const res = isEdit.value
        ? await anniversaryService.updateAnniversary(anniversaryForm.id, data)
        : await anniversaryService.createAnniversary(data)
      if (res.code === 200) {
        ElMessage.success(isEdit.value ? '修改成功' : '添加成功')
        dialogVisible.value = false
        loadAllData()
      }
    } catch (e) {
      ElMessage.error(isEdit.value ? '修改失败' : '添加失败')
    } finally { submitting.value = false }
  })
}

const deleteAnniversary = (ann) => {
  ElMessageBox.confirm(`确定要删除纪念日"${ann.title}"吗？`, '提示', {
    confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning'
  }).then(async () => {
    try {
      const res = await anniversaryService.deleteAnniversary(ann.id)
      if (res.code === 200) {
        ElMessage.success('删除成功')
        loadAllData()
      }
    } catch (e) { ElMessage.error('删除失败') }
  }).catch(() => {})
}

const resetForm = () => {
  Object.assign(anniversaryForm, {
    title: '', targetDate: '', type: 'OTHER',
    isRecurring: false, remindDaysBefore: 0, description: ''
  })
  formRef.value?.clearValidate()
}

const filterAnniversaries = () => {}

const formatDate = (d) => {
  if (!d) return ''
  try { return format(parseISO(d), 'yyyy年MM月dd日') } catch { return d }
}

const getTypeIcon = (t) => ({ BIRTHDAY: '🎂', WEDDING: '💍', RELATIONSHIP: '💕', WORK: '💼', HOLIDAY: '🎉', OTHER: '📌' }[t] || '📌')
const getTypeText = (t) => ({ BIRTHDAY: '生日', WEDDING: '结婚', RELATIONSHIP: '恋爱', WORK: '工作', HOLIDAY: '节日', OTHER: '其他' }[t] || '其他')
const getTypeTagType = (t) => ({ BIRTHDAY: 'danger', WEDDING: 'danger', RELATIONSHIP: 'warning', WORK: 'primary', HOLIDAY: 'success', OTHER: 'info' }[t] || 'info')
const getCountdownText = (d) => d === 0 ? '今天' : d < 0 ? '天前' : '天后'
const getCountdownDisplay = (d) => d === 0 ? '今天' : d < 0 ? `${Math.abs(d)}天前` : `还有${d}天`
const getCountdownColor = (d) => d === 0 ? '#F56C6C' : d < 0 ? '#909399' : d <= 7 ? '#E6A23C' : d <= 30 ? '#409EFF' : '#67C23A'

onMounted(loadAllData)
</script>

<style scoped>
.anniversary-container { max-width: 1400px; margin: 0 auto; }
.countdown-row { margin-bottom: 20px; }
.countdown-card { cursor: pointer; transition: all 0.3s; }
.countdown-card:hover { transform: translateY(-3px); box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15); }
.countdown-content { display: flex; align-items: center; gap: 12px; }
.countdown-icon { font-size: 32px; }
.countdown-info { flex: 1; min-width: 0; }
.countdown-title { font-size: 16px; font-weight: 500; color: #303133; margin-bottom: 4px; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }
.countdown-date { font-size: 13px; color: #909399; }
.countdown-days { text-align: center; }
.days-number { font-size: 28px; font-weight: bold; line-height: 1; }
.days-label { font-size: 12px; color: #909399; margin-top: 4px; }
.type-birthday .days-number { color: #F56C6C; }
.type-wedding .days-number { color: #E6A23C; }
.type-relationship .days-number { color: #F56C6C; }
.type-work .days-number { color: #409EFF; }
.type-holiday .days-number { color: #67C23A; }
.type-other .days-number { color: #909399; }
.calendar-card { margin-bottom: 20px; }
.header-content { display: flex; justify-content: space-between; align-items: center; flex-wrap: wrap; gap: 12px; }
.header-actions { display: flex; align-items: center; gap: 8px; }
h2 { margin: 0; font-size: 20px; color: #303133; }
:deep(.el-calendar-table .el-calendar-day) { padding: 4px; height: 80px; }
.calendar-day { height: 100%; display: flex; flex-direction: column; }
.day-number { font-size: 16px; color: #606266; margin-bottom: 4px; }
.day-number.is-today { color: #409EFF; font-weight: bold; }
.day-events { display: flex; flex-wrap: wrap; gap: 2px; }
.event-dot { font-size: 14px; line-height: 1; cursor: pointer; }
.event-dot.type-birthday { color: #F56C6C; }
.event-dot.type-wedding { color: #E6A23C; }
.event-dot.type-relationship { color: #F56C6C; }
.event-dot.type-work { color: #409EFF; }
.event-dot.type-holiday { color: #67C23A; }
.event-dot.type-other { color: #909399; }
.stats-list { display: flex; flex-direction: column; gap: 12px; }
.stat-item { display: flex; justify-content: space-between; align-items: center; padding: 8px 0; border-bottom: 1px solid #EBEEF5; }
.stat-item:last-child { border-bottom: none; }
.stat-label { font-size: 14px; color: #606266; }
.stat-value { font-size: 18px; font-weight: bold; color: #409EFF; }
.today-list { display: flex; flex-direction: column; gap: 8px; }
.today-item { display: flex; align-items: center; gap: 8px; padding: 8px; background-color: #FEF0F0; border-radius: 4px; }
.today-icon { font-size: 20px; }
.today-title { flex: 1; font-size: 14px; color: #303133; }
.form-hint { margin-left: 8px; font-size: 12px; color: #909399; }
</style>
