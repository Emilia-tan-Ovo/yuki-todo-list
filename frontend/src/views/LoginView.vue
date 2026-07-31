<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { login } from '../api/auth'

const router = useRouter()

const account = ref('')
const password = ref('')
const errorMessage = ref('')
const isSubmitting = ref(false)

async function handleSubmit() {
  errorMessage.value = ''
  isSubmitting.value = true

  try {
    await login({
      account: account.value,
      password: password.value,
    })

    await router.push({ name: 'home' })
  } catch (error) {
    // 后端业务错误直接展示统一响应中的 message；网络错误使用前端提示。
    errorMessage.value = error.status
      ? error.message
      : '无法连接到服务器，请确认后端已启动。'
  } finally {
    isSubmitting.value = false
  }
}
</script>

<template>
  <main class="page-shell">
    <section class="content-card login-card" aria-labelledby="login-title">
      <p class="eyebrow">YuKi's TODO List</p>
      <h1 id="login-title">登录</h1>
      <p class="helper-text">使用用户名或邮箱进入你的学习任务空间。</p>

      <form class="login-form" @submit.prevent="handleSubmit">
        <label for="account">用户名或邮箱</label>
        <input
          id="account"
          v-model="account"
          name="account"
          type="text"
          autocomplete="username"
          placeholder="请输入用户名或邮箱"
          required
        />

        <label for="password">密码</label>
        <input
          id="password"
          v-model="password"
          name="password"
          type="password"
          autocomplete="current-password"
          placeholder="请输入密码"
          required
        />

        <p v-if="errorMessage" class="error-message" role="alert" aria-live="polite">
          {{ errorMessage }}
        </p>

        <button type="submit" :disabled="isSubmitting">
          {{ isSubmitting ? '登录中...' : '登录' }}
        </button>
      </form>
    </section>
  </main>
</template>
