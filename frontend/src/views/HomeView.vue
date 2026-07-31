<script setup>
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { getCurrentUser } from '../api/auth'

const router = useRouter()

const currentUser = ref(null)
const isLoading = ref(true)
const errorMessage = ref('')

onMounted(async () => {
  try {
    currentUser.value = await getCurrentUser()
  } catch (error) {
    // 401 表示当前 Session 已失效，返回登录页重新认证。
    if (error.status === 401) {
      await router.replace({ name: 'login' })
      return
    }

    errorMessage.value = '无法连接到服务器，请确认后端已启动。'
  } finally {
    isLoading.value = false
  }
})
</script>

<template>
  <main class="page-shell">
    <section class="content-card" aria-labelledby="home-title">
      <p class="eyebrow">YuKi's TODO List</p>
      <h1 id="home-title">临时首页</h1>

      <p v-if="isLoading" class="status-text">正在确认登录状态...</p>
      <p v-else-if="errorMessage" class="error-message" role="alert">
        {{ errorMessage }}
      </p>
      <template v-else-if="currentUser">
        <p class="welcome-text">你好，{{ currentUser.username }}！</p>
        <p class="helper-text">Session 验证成功。课程首页将在后续阶段实现。</p>
      </template>
    </section>
  </main>
</template>
