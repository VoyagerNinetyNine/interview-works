import { createApp } from 'vue'
// import './style.css'
import App from './App.vue'
import 'tdesign-vue-next/es/style/index.css'
import 'element-plus/dist/index.css'
import TDesign from 'tdesign-vue-next'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'
import { createPinia } from 'pinia'
import ElementPlus from 'element-plus'


const app = createApp(App)
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}
const pinia = createPinia()
app.use(TDesign)
app.use(ElementPlus)
app.use(pinia)
app.mount('#app')