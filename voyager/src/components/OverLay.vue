<template>
  <div v-if="inputStore.overlay" class="overlay">
    <div class="spinner"></div>
    <div class="text">
      <slot>加载中...</slot>
    </div>
  </div>
</template>

<script setup name="OverLay" lang="js">
import { useInputStore } from '@/store/userinput'
const inputStore = useInputStore()
</script>

<style scoped>
.overlay {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.4);

  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;

  z-index: 9999;
}

.spinner {
  width: 40px;
  height: 40px;

  border: 4px solid rgba(255, 255, 255, 0.3);
  border-top-color: #fff;
  border-radius: 50%;

  animation: spin 1s linear infinite;
}

.text {
  margin-top: 10px;
  color: #fff;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}
</style>