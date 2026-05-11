<template>
  <!-- <div class="canvas">
    <iframe src="../../public/index.html"></iframe>
  </div> -->
  <div class="head">
    <text>{{ system_name }}</text>
    <div class="weather">
      <span>{{ timeStr }}&nbsp;&nbsp;&nbsp;&nbsp;{{ weekStr }}</span>
    </div>
  </div>
</template>



<script setup lang="ts" name="Title">
import { ref } from 'vue'
import { system_name } from './config'
import dayjs from 'dayjs'
const weekDays = ['星期日', '星期一', '星期二', '星期三', '星期四', '星期五', '星期六']
let timeStr = ref(dayjs().format('YYYY-MM-DD HH:mm:ss'))
let weekStr = ref(weekDays[dayjs().day()])
setInterval(() => {
  const now = dayjs()
  timeStr.value = now.format('YYYY-MM-DD HH:mm:ss')
  weekStr.value = weekDays[now.day()]
}, 1000)

</script>



<style scoped>
.head{ 
  height:10vh; 
  background: url("@/assets/background/head_bg.png") no-repeat center center; 
  background-size: cover;
  position: relative; 
  z-index: 1;
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 20px;
}

.canvas{position: absolute; width:100%; left: 0; top: 0; height: 100%; z-index: 0;}

iframe {
    position: absolute;
    top: -2px;
    left: -2px;
    width: 100%;
    height: 100%;
    z-index: 0;
}

.head text{
  display: inline-block;
  color: white;
  font-size: 2rem;
}

.weather{ position:absolute; right:.3rem; top:35%;}
.weather img{ width:2rem; display: inline-block; vertical-align: middle;}
.weather span{color:rgba(255,255,255,.7); font-size: 1rem; padding-right: 1rem;}


</style>