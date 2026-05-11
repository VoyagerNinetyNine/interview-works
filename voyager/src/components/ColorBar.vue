<template>
  <div class="colorbar" v-if="vmin !== null && vmax !== null">
    <!-- 尖头 -->
    <div class="arrow"></div>

    <!-- 渐变 -->
    <canvas ref="canvasRef" class="gradient"></canvas>
    <div class="bottom-arrow"></div>

    <!-- 刻度 -->
    <div class="ticks">
      <div
        v-for="t in ticks"
        :key="t"
        class="tick"
        :style="{ top: getPosition(t) + 'px', color: 'white' }"
      >
      <div class="line"></div>
      <span class="label">{{ formatTick(t) }}</span>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, watchEffect } from 'vue'
import { useOutputStore } from '@/store/output'
import { scaleSequential } from 'd3-scale'
import { interpolateViridis } from 'd3-scale-chromatic'
import { ticks as d3ticks } from 'd3-array'

const canvasRef = ref(null)
const outputStore = useOutputStore()

const area = computed(() => outputStore.pointArea)

const vmin = computed(() => outputStore.extremaValue[area.value]?.min ?? null)
const vmax = computed(() => outputStore.extremaValue[area.value]?.max ?? null)

const height = 300
const width = 20

// ✅ 自动生成刻度（关键）
const ticks = computed(() => {
  if (vmin.value === null || vmax.value === null) return []
  return d3ticks(vmin.value, vmax.value, 5) // 5个刻度
})

function draw() {
  if (vmin.value === null || vmax.value === null) return

  const canvas = canvasRef.value
  if (!canvas) return

  canvas.width = width
  canvas.height = height

  const ctx = canvas.getContext('2d')

  const colorScale = scaleSequential(interpolateViridis)
    .domain([vmin.value, vmax.value])

  for (let i = 0; i < height; i++) {
    const t = i / (height - 1)
    const value = vmax.value - t * (vmax.value - vmin.value)

    ctx.fillStyle = colorScale(value)
    ctx.fillRect(0, i, width, 1)
  }
}

// ✅ 计算刻度位置
function getPosition(value) {
  const ratio = (vmax.value - value) / (vmax.value - vmin.value)
  return ratio * height
}

// ✅ 格式化（尽量整数）
function formatTick(t) {
  return Number.isInteger(t) ? t : t.toFixed(1)
}

watchEffect(draw)
</script>

<style scoped>
.colorbar {
  position: absolute;
  left: 20px;
  bottom: 20px;
  height: 320px;
}

/* 尖头 */
.arrow {
  width: 0;
  height: 0;
  border-left: 10px solid transparent;
  border-right: 10px solid transparent;
  border-bottom: 20px solid #fde725; /* viridis 顶色 */
  margin-left: 0px;
}

.bottom-arrow {
  width: 0;
  height: 0;
  border-left: 10px solid transparent;
  border-right: 10px solid transparent;
  border-top: 20px solid #440154;
  margin-left: 0px;
}

/* 渐变条 */
.gradient {
  display: block;
  /* border-radius: 4px; */
}

/* 刻度容器 */
.ticks {
  position: absolute;
  left: 25px;
  top: 12px; /* 避开箭头 */
  height: 300px;
  width: 60px;
}

/* 单个刻度 */
.tick {
  position: absolute;
  display: flex;
  align-items: center;
}

/* 刻度线 */
.line {
  width: 6px;
  height: 1px;
  background: #333;
  margin-left: -5px;
}

/* 文本 */
.label {
  font-size: 12px;
}
</style>