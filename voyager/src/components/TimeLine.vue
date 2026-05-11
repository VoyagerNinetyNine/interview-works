<template>
  <div class="timeline-container">
    <div class="timeline-panel">
      <div class="isobaric-selector" v-show="['temperature', 'humidity'].includes(inputStore.userInputParam)">
        <el-select popper-class="glass-select-popper" v-model="inputStore.userIsobaricIndex" size="small" class="custom-select" style="width: 120px">
          <el-option
            v-for="(item, index) in outputStore.isobaric"
            :key="index"
            :label="`${item.toFixed(2)} hPa`"
            :value="index"
          />
        </el-select>
      </div>

      <div class="frame-stepper">
        <el-input-number 
          v-model="inputStore.userPredictTimeIndex" 
          :min="momentMin" 
          :max="outputStore.output_data_len[inputStore.userInputParam]-1"
          controls-position="right"
          size="small"
          class="custom-stepper"
        />
        <span class="total-frames">/ {{ outputStore.output_data_len[inputStore.userInputParam] - 1 }}</span>
      </div>

      <button class="play-btn" @click="isplaying ? pause() : play()">
        <el-icon :size="28"><Component :is="isplaying ? 'VideoPause' : 'VideoPlay'" /></el-icon>
      </button>

      <div class="slider-section">
        <div class="time-display" v-show="outputStore.predict_start_time.length > 0">
          {{ predict_time }}
        </div>
        <el-slider 
          v-model="inputStore.userPredictTimeIndex" 
          :min="momentMin" 
          :max="outputStore.output_data_len[inputStore.userInputParam]-1" 
          :step="1"
          :show-tooltip="false"
        />
      </div>
    </div>
  </div>
</template>



<script setup lang="ts" name="TimeLine">
import { computed, ref, onUnmounted } from 'vue'
import { useInputStore } from '@/store/userinput'
import { useOutputStore} from '@/store/output'
import dayjs from 'dayjs'
const inputStore = useInputStore()
const outputStore = useOutputStore()
let momentMin = ref(0)
let isplaying = ref(false)

let timer: ReturnType<typeof setInterval> | null;              // 用来存放 setInterval 的 ID (不需要响应式)

let predict_time = computed(() => {
    const time = dayjs(outputStore.predict_start_time)
    return time.add(inputStore.userPredictTimeIndex * outputStore.timeinterval[inputStore.userInputParam], 'minute').format('YYYY-MM-DD HH:mm')
})

// 2. 播放逻辑
const play = () => {
    if (timer) return;
    isplaying.value = true;
    timer = setInterval(() => {
        if (inputStore.userPredictTimeIndex < outputStore.output_data_len[inputStore.userInputParam] - 1) {
            inputStore.userPredictTimeIndex++;
        } else {
            inputStore.userPredictTimeIndex = 0;
            isplaying.value = false;
            pause();
        }
    }, 100); // 每 100 毫秒加 1
};

// 3. 暂停逻辑
const pause = () => {
    if (timer) {
        clearInterval(timer);
        timer = null;
        isplaying.value = false;
    }
};

onUnmounted(() => {
    pause();
});
</script>


<style scoped>
/* 保持之前的 fixed 定位和毛玻璃背景不变 */
.timeline-container {
  position: fixed;
  bottom: 30px;
  left: 50%;
  transform: translateX(-50%);
  z-index: 1000;
}

.timeline-panel {
  display: flex;
  align-items: center;
  gap: 15px;
  padding: 8px 20px;
  background: rgba(15, 23, 42, 0.8);
  backdrop-filter: blur(15px);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 12px; /* 改回微圆角矩形，更适合放 InputNumber */
  box-shadow: 0 8px 32px rgba(0,0,0,0.5);
}

/* 帧数微调器样式定制 */
.frame-stepper {
  display: flex;
  align-items: center;
  background: rgba(255, 255, 255, 0.05);
  padding: 2px 8px;
  border-radius: 4px;
  border: 1px solid rgba(255, 255, 255, 0.1);
}

.total-frames {
  color: rgba(255, 255, 255, 0.4);
  font-size: 12px;
  margin-left: 5px;
}

/* 深度定制 Element Plus InputNumber */
:deep(.custom-stepper.el-input-number) {
  width: 80px;
}

:deep(.custom-stepper .el-input__wrapper) {
  background-color: transparent !important;
  box-shadow: none !important;
  padding: 0 !important;
}

:deep(.custom-stepper .el-input__inner) {
  color: #3b82f6 !important; /* 突出当前帧数字 */
  font-weight: bold;
  font-family: 'Monaco', monospace;
  text-align: left;
}

:deep(.custom-stepper .el-input-number__increase),
:deep(.custom-stepper .el-input-number__decrease) {
  background: transparent !important;
  border: none !important;
  color: rgba(255, 255, 255, 0.5) !important;
}

/* 时间轴宽度调整 */
.slider-section {
  width: 400px;
  display: flex;
  flex-direction: column;
}

.time-display {
  color: #fff;
  font-size: 13px;
  margin-bottom: -4px;
  font-family: 'Monaco', monospace;
}

.play-btn {
  background: none;
  border: none;
  color: #3b82f6;
  cursor: pointer;
  transition: color 0.3s;
}

.play-btn:hover { color: #60a5fa; }

:deep(.el-input__wrapper),
:deep(.el-select__wrapper) {
  background-color: rgba(255, 255, 255, 0.05) !important;
  box-shadow: 0 0 0 1px rgba(255, 255, 255, 0.2) inset !important;
}

:deep(.el-input__inner) {
  color: #fff !important;
}

:deep(.el-select__selected-item span){
    color: white;
}
</style>