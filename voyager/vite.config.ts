import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import path from 'path'
import Components from 'unplugin-vue-components/vite'
import { AntDesignVueResolver } from 'unplugin-vue-components/resolvers'
import AutoImport from 'unplugin-auto-import/vite'
import { ElementPlusResolver } from 'unplugin-vue-components/resolvers'
import { TDesignResolver } from 'unplugin-vue-components/resolvers'
import legacy from '@vitejs/plugin-legacy'
// https://vite.dev/config/
export default defineConfig({
  plugins: [
    vue(),
    Components({
      resolvers: [
        ElementPlusResolver(),
        TDesignResolver({ library: 'vue-next' }),
        AntDesignVueResolver({
          importStyle: false, // css in js
        }),
      ],
    }),
    AutoImport({
      resolvers: [
        ElementPlusResolver(),
        TDesignResolver({ library: 'vue-next' }),
      ],
    }),
    legacy({           // 支持 IE11
      // targets: ['defaults', 'not IE 11']
        targets: ['chrome 77'],
        renderLegacyChunks: true,
        modernPolyfills: true
    }),
  ],
  resolve: {
    alias: {
      '@': path.resolve(__dirname, 'src')
    }
  },
  build: {
    rollupOptions: {
      output: {
        // 分包策略
        manualChunks(id) {
          if (id.includes('node_modules')) {
            // 将 element-plus 和 tdesign 分开打包，避免冲突
            if (id.includes('element-plus')) return 'element-plus';
            if (id.includes('tdesign')) return 'tdesign';
            return 'vendor'; 
          }
        }
      }
    },
  }
})
