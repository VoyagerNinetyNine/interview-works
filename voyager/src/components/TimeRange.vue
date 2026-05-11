<template>
  <div class="side-panel" :class="{ 'is-open': isOpen }">
    <div class="panel-toggle" @click="togglePanel">
      <div class="toggle-icon">{{ isOpen ? '◀' : '▶' }}</div>
      <div class="toggle-text">数据有效时间</div>
    </div>

    <div class="chart-content">
      <div class="panel-header">
        <div class="title-row">
          <span class="main-title">有效范围</span>
          <span class="param-badge">{{ inputStore.userInputParam }}</span>
        </div>
        
        <div class="area-nav">
          <div 
            v-for="area in outputStore.areas" 
            :key="area"
            :class="['nav-item', { active: activeArea === area }]"
            @click="activeArea = area"
          >
            <!-- {{ area.toUpperCase() }} -->
              {{ area_names[area as keyof typeof area_names] }}
          </div>
        </div>
      </div>

      <div class="list-wrapper">
        <div v-if="loading" class="loading-box">
          <div class="spinner"></div>
          <span>读取中...</span>
        </div>

        <template v-else-if="displayData.length > 0">
          <div v-for="(item, index) in displayData" :key="index" class="time-item">
            <div class="item-index">{{ (Number(index) + 1).toString().padStart(2, '0') }}</div>
            <div class="item-body">
              <div class="time-row">
                <span class="time-label">START</span>
                <span class="time-value">{{ item.start }}</span>
              </div>
              <div class="time-row">
                <span class="time-label">END</span>
                <span class="time-value">{{ item.end }}</span>
              </div>
            </div>
          </div>
        </template>

        <div v-else class="empty-status">
          {{ inputStore.userInputParam ? '暂无匹配的有效记录' : '请先选择变量' }}
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { useOutputStore } from "@/store/output"
import { useInputStore } from "@/store/userinput"
import { serverUrl, area_names } from './config'

const outputStore = useOutputStore()
const inputStore = useInputStore()

const isOpen = ref(false)
const loading = ref(false)
const activeArea = ref('')
const timeRangeData = ref<Record<string, any>>({})

// 切换开关
const togglePanel = () => {
  isOpen.value = !isOpen.value
}

// 核心数据获取逻辑
const updateTimeRanges = async (variable: string) => {
  if (!variable) return
  
  loading.value = true
  try {
    // 1. 请求 API 获取 JSON 文件的 URL
    const apiUrl = `${serverUrl}/predict/timerange/?variable=${variable}&request_source="front"`
    const response = await fetch(apiUrl)
    const result = await response.json()

    if (result.status === 'success') {
      // 2. Fetch 真正的 JSON 数据
      const dataRes = await fetch(result.time_range_url)
      const data = await dataRes.json()
      timeRangeData.value = data
      
      // 如果获取到了新数据，且面板关着，可以考虑自动打开提示用户
      // if (!isOpen.value) isOpen.value = true
    } else {
      ElMessage.warning(result.message || '获取时间范围失败')
      timeRangeData.value = {}
    }
  } catch (error) {
    console.error('Fetch error:', error)
    ElMessage.error('网络请求异常')
  } finally {
    loading.value = false
  }
}

// 监听变量名变化
watch(() => inputStore.userInputParam, (newVal) => {
  updateTimeRanges(newVal)
}, { immediate: true })

// 计算属性：当前选中区域的数据
const displayData = computed(() => {
  if (!activeArea.value || !timeRangeData.value) return []
  return timeRangeData.value[activeArea.value] || []
})

onMounted(() => {
  // 初始化默认区域
  if (outputStore.areas && outputStore.areas.length > 0) {
    activeArea.value = outputStore.areas[0]
  }
})
</script>

<style scoped>
.side-panel {
  position: fixed;
  left: -420px; /* 根据需要调整 */
  bottom: 80px;  /* 位置偏下方 */
  width: 420px;
  height: 450px; /* 较第一版调矮了一些 */
  z-index: 1000;
  transition: left 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  display: flex;
}

.side-panel.is-open {
  left: 20px;
}

/* 侧边拉手 */
.panel-toggle {
  position: absolute;
  right: -35px;
  top: 50%;
  transform: translateY(-50%);
  width: 35px;
  height: 120px;
  background: rgba(15, 23, 42, 0.8);
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-left: none;
  border-radius: 0 8px 8px 0;
  cursor: pointer;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: #fff;
}

.toggle-text {
  writing-mode: vertical-lr;
  font-size: 12px;
  letter-spacing: 2px;
  margin-top: 8px;
  color: rgba(255, 255, 255, 0.7);
}

/* 主体容器 */
.chart-content {
  flex: 1;
  background: rgba(15, 23, 42, 0.7);
  backdrop-filter: blur(12px);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 12px;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

/* 头部样式 */
.panel-header {
  padding: 20px;
  background: rgba(255, 255, 255, 0.03);
}

.title-row {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 15px;
}

.main-title {
  color: #fff;
  font-size: 16px;
  font-weight: bold;
}

.param-badge {
  font-size: 11px;
  padding: 2px 8px;
  background: rgba(0, 210, 255, 0.2);
  color: #00d2ff;
  border: 1px solid rgba(0, 210, 255, 0.3);
  border-radius: 4px;
}

.area-nav {
  display: flex;
  gap: 8px;
}

.nav-item {
  padding: 4px 12px;
  font-size: 12px;
  color: rgba(255, 255, 255, 0.5);
  cursor: pointer;
  border-bottom: 2px solid transparent;
  transition: all 0.3s;
}

.nav-item:hover {
  color: #fff;
}

.nav-item.active {
  color: #00d2ff;
  border-bottom-color: #00d2ff;
}

/* 列表区域 */
.list-wrapper {
  flex: 1;
  overflow-y: auto;
  padding: 15px 20px;
}

.list-wrapper::-webkit-scrollbar {
  width: 4px;
}
.list-wrapper::-webkit-scrollbar-thumb {
  background: rgba(255, 255, 255, 0.1);
}

.time-item {
  display: flex;
  align-items: flex-start;
  gap: 15px;
  padding: 12px;
  margin-bottom: 10px;
  background: rgba(255, 255, 255, 0.03);
  border-radius: 8px;
  border: 1px solid rgba(255, 255, 255, 0.05);
}

.item-index {
  font-family: 'Courier New', monospace;
  font-size: 14px;
  color: rgba(0, 210, 255, 0.4);
}

.item-body {
  flex: 1;
}

.time-row {
  display: flex;
  justify-content: space-between;
  margin: 2px 0;
}

.time-label {
  font-size: 10px;
  color: rgba(255, 255, 255, 0.3);
}

.time-value {
  font-size: 13px;
  color: rgba(255, 255, 255, 0.8);
  font-family: monospace;
}

/* 加载与空状态 */
.loading-box {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-top: 50px;
  color: #00d2ff;
  gap: 10px;
}

.spinner {
  width: 20px;
  height: 20px;
  border: 2px solid rgba(0, 210, 255, 0.3);
  border-top-color: #00d2ff;
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

.empty-status {
  text-align: center;
  margin-top: 50px;
  color: rgba(255,255,255,0.4);
  font-size: 14px;
}
</style>