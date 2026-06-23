<template>
  <div id="app">
    <NavBar v-if="showChrome" />
    <main :class="['app-main', { 'admin-main': !showChrome }]">
      <router-view />
    </main>
    <Footer v-if="showChrome" />
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import NavBar from '@/components/NavBar.vue'
import Footer from '@/components/Footer.vue'

const route = useRoute()
const showChrome = computed(() => !route.path.startsWith('/admin'))
</script>

<style>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

#app {
  min-height: 100vh;
}

.app-main {
  min-height: calc(100vh - 88px);
  padding-top: 88px;
}

.app-main.admin-main {
  padding-top: 0;
}
</style>
