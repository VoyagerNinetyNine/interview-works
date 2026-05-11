<template>
  <div class="side-panel" :class="{ 'is-open': isOpen }">
    <div class="panel-toggle" @click="togglePanel">
      <div class="toggle-icon">{{ isOpen ? '▶' : '◀' }}</div>
      <div class="toggle-text">数据廓线</div>
    </div>

    <div class="chart-content">
      <div class="profile-chart" ref="chartRef"></div>
      <div v-if="!outputStore.profileData.length" class="empty-status">
        请在地图上点击坐标以获取数据
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, shallowRef, watch, onMounted, onBeforeUnmount } from 'vue'
import * as echarts from 'echarts'
import { useOutputStore } from "@/store/output"
import { useInputStore } from "@/store/userinput"
import { profile_charts_text } from "@/components/config"

const outputStore = useOutputStore()
const inputStore = useInputStore()
const chartRef = shallowRef<HTMLElement | null>(null)
let myChart: echarts.ECharts | null = null
const isOpen = ref(false) // 控制开关
const axisColor = "#b6b8bd";

let option = {
    title: {
      text: '廓线图',
      left: 'center',
      top: '10',
      textStyle: {
        color: '#fff'
      }
    },
    tooltip: {
      trigger: 'item',
      formatter: (params: any) => {
                  return `温度：${params.data[1].toFixed(2)}°C<br/>气压：${params.data[0].toFixed(2)} hPa`
                }
    },
    xAxis: {
      type: 'value',
      nameLocation: 'middle',
      name: '',
      boundaryGap: ['5%', '5%'],
      nameGap: "24",
      nameTextStyle: {
        color: axisColor,
      },
      scale: true,
      axisLine:{
        show: true,
        symbol: ['none', 'arrow'],
        symbolOffset: 5,
        lineStyle: {
          color: axisColor,
          width: 2,
        },
      },
      axisLabel: {
        color: axisColor,
        fontSize: 11,
      },
      splitLine: {
        show: true,
        showMinLine: false,
        // showMaxLine: false,
        lineStyle: {
          color: '#ccc',
          width: 1,
          type: 'dashed',
          opacity: 0.7,
        }
      }
    },
    yAxis: {
      type: 'category',
      nameLocation: 'middle',
      name: '气压 (hPa)',
      inverse: true, 
      nameGap: "48",
      nameTextStyle: {
        color: axisColor,
      },
      boundaryGap: false,
      axisLine:{
        show: true,
        symbol: ['none', 'arrow'],
        symbolOffset: 5,
        lineStyle: {
          color: axisColor,
          width: 2,
        }
      },
      axisTick: {
        show: false,
        inside: true,
      },
      axisLabel: {
        interval: 2,
        color: axisColor,
        formatter: (value: number) => {
          return `${Number(value).toFixed(2)}`
        },
        fontSize: 11,
      },
      splitLine: {
        show: true,
        showMaxLine: false,
        lineStyle: {
          color: '#ccc',
          width: 1,
          type: 'dashed',
          opacity: 0.7,
        },
        interval: 2,
      }
    },
    series: [{
        type: 'line',
        smooth: true,
        symbol: 'emptyCircle',
        color: '#00d2ff',
        symbolSize: 6,
        encode: {
          x: 1, // 数据数组的第 0 项对应 X 轴
          y: 0  // 数据数组的第 1 项对应 Y 轴
        },
        data: [] as number[][],
        areaStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 1, 0, [
            { offset: 0, color: 'rgba(0, 210, 255, 0.3)' },
            { offset: 1, color: 'rgba(0, 210, 255, 0)' }
          ])
        },
      }],
    grid: {
        top: '6%',    // 默认值通常较大
        bottom: '5%', // 调小这个值可以让坐标轴拉长
        left: '10%',
        right: '10%',
        containLabel: true // 确保标签不会超出容器
    },
  }
const togglePanel = () => {
    isOpen.value = !isOpen.value
    // 展开面板时，稍等动画结束再 resize
    setTimeout(() => {
        myChart?.resize()
    }, 300)
  }

// 监听窗口缩放，防止变形
const handleResize = () => {
  myChart?.resize()
}

watch(() => [outputStore.profileData, inputStore.submittedValues.param], () => {
  const variable = inputStore.submittedValues.param;
  if (!["temperature", "humidity"].includes(variable)) return; // 只处理温度和湿度
  if (outputStore.profileData.length > 0 && myChart) {
    const combined = outputStore.isobaric.map((item, index) => [item, outputStore.profileData[index]])
    option.title.text = `${profile_charts_text.name[variable  as keyof typeof profile_charts_text.name]}廓线`
    if(variable === "humidity"){
      // @ts-ignore
      option.xAxis.min = 0;
    }
    option.xAxis.name = `${profile_charts_text.name[variable  as keyof typeof profile_charts_text.name]} (${profile_charts_text.units[variable  as keyof typeof profile_charts_text.units]})`
    option.tooltip.formatter = (params: any) => {
                  return `${profile_charts_text.name[variable as keyof typeof profile_charts_text.name]}：${params.data[1].toFixed(2)}${profile_charts_text.units[variable as keyof typeof profile_charts_text.units]}<br/>气压：${params.data[0].toFixed(2)} hPa`
                }
    option.series[0].data = combined
    myChart.setOption(option)
    if(!isOpen.value) isOpen.value = true
  }
}, { deep: true })

watch(() => inputStore.userPredictTimeIndex, ()=>{
  if (inputStore.submittedValues.param === "") return;
  outputStore.getPointData("profile", false, true);
})

onMounted(() => {
  if (chartRef.value) {
    myChart = echarts.init(chartRef.value)
    myChart.setOption(option)
    window.addEventListener('resize', handleResize)
  }
})

onBeforeUnmount(() => {
  window.removeEventListener('resize', handleResize)
  myChart?.dispose()
})
</script>

<style scoped>
.side-panel {
  position: fixed;
  right: -450px; /* 默认隐藏在屏幕外 */
  top: 80px;
  bottom: 120px;
  width: 450px;
  z-index: 100;
  transition: right 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  display: flex;
}

.side-panel.is-open {
  right: 20px; /* 展开后的位置 */
}

/* 侧边拉手按钮 */
.panel-toggle {
  position: absolute;
  left: -35px;
  top: 50%;
  transform: translateY(-50%);
  width: 35px;
  height: 120px;
  background: rgba(15, 23, 42, 0.8);
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-right: none;
  border-radius: 8px 0 0 8px;
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

/* 图表容器主体 */
.chart-content {
  flex: 1;
  background: rgba(15, 23, 42, 0.7);
  backdrop-filter: blur(12px);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 12px;
  position: relative;
}

.profile-chart {
  width: 100%;
  height: 100%;
}

.empty-status {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  color: rgba(255,255,255,0.4);
  font-size: 14px;
}
</style>