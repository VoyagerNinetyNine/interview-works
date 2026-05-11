<template>
    <div class="map-background">
        <ScatterMap />
    </div>
    <Title />
    <Tables />
    <ProfileCharts />
    <TimeLineCharts />
    <TimeLine />
    <OverLay />
    <ColorBar />
    <TimeRange />
</template>



<script setup lang="ts">
import Title from './components/Title.vue'

import TimeLine from './components/TimeLine.vue'
import ProfileCharts from './components/ProfileCharts.vue'
import ScatterMap from './components/ScatterMap.vue'
import OverLay from './components/OverLay.vue'
import TimeLineCharts from './components/TimeLineCharts.vue'
import ColorBar from './components/ColorBar.vue'
import Tables from './components/Tables.vue'
import TimeRange from './components/TimeRange.vue'
</script>


<style scoped>
.data-visualization {
    margin-left: 2.5%;
    margin-top: 2.5vh;
    margin-right: 2.5%;
    margin-bottom: 1%;
    height: 70%;
    display: grid;
    grid-template-columns: 1fr 1fr; 
}

.map-background {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  z-index: 0; /* 置于底层 */
}
</style>

<style>
html, body {
  margin: 0;
  padding: 0;
  width: 100%;
  height: 100%;
  overflow: hidden;
}

#app {
  width: 100%;
  height: 100%;
  overflow: hidden;
}

/* 针对下拉弹窗的容器 */
.glass-select-popper.el-popper {
  background: rgba(15, 23, 42, 0.6) !important;
  backdrop-filter: blur(12px) !important;
  border: 1px solid rgba(255, 255, 255, 0.1) !important;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.5) !important;
}
/* 确保选项本身背景透明 */
.glass-select-popper .el-select-dropdown__item {
  background-color: transparent !important;
  color: rgba(255, 255, 255, 0.8) !important;
}
/* 强制覆盖：鼠标悬浮 (Hover) 和 选中 (Selected) 状态 */
.glass-select-popper .el-select-dropdown__item.hover,
.glass-select-popper .el-select-dropdown__item:hover,
.glass-select-popper .el-select-dropdown__item.is-selected {
  background-color: rgba(59, 130, 246, 0.3) !important; /* 统一使用带透明度的蓝色 */
  color: #fff !important;
}

.t-date-picker__footer {
  justify-content: flex-end !important;
}

</style>