<script setup>
import { ref } from 'vue'
import { RouterLink, useRouter } from 'vue-router'
import { register } from '../api/auth'

const router = useRouter()

const username = ref('')
const email = ref('')
const password = ref('')
const confirmPassword = ref('')
const errorMessage = ref('')
const isSubmitting = ref(false)

async function handleSubmit() {
  errorMessage.value = ''
  isSubmitting.value = true

  try {
    await register({
      username: username.value,
      email: email.value,
      password: password.value,
      confirmPassword: confirmPassword.value,
    })

    // 注册成功后后端已经建立 Session，可以直接进入首页。
    await router.push({ name: 'home' })
  } catch (error) {
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
    <section class="content-card" aria-labelledby="register-title">
      <p class="eyebrow">YuKi's TODO List</p>
      <h1 id="register-title">注册</h1>
      <p class="helper-text">创建账号，开始整理你的课程和学习任务。</p>

      <form class="auth-form" @submit.prevent="handleSubmit">
        <label for="username">用户名</label>
        <input
          id="username"
          v-model="username"
          name="username"
          type="text"
          autocomplete="username"
          placeholder="请输入用户名"
          required
        />

        <label for="email">邮箱</label>
        <input
          id="email"
          v-model="email"
          name="email"
          type="email"
          autocomplete="email"
          placeholder="请输入邮箱"
          required
        />

        <label for="register-password">密码</label>
        <input
          id="register-password"
          v-model="password"
          name="password"
          type="password"
          autocomplete="new-password"
          placeholder="请输入密码"
          required
        />

        <label for="confirm-password">确认密码</label>
        <input
          id="confirm-password"
          v-model="confirmPassword"
          name="confirmPassword"
          type="password"
          autocomplete="new-password"
          placeholder="请再次输入密码"
          required
        />

        <p v-if="errorMessage" class="error-message" role="alert" aria-live="polite">
          {{ errorMessage }}
        </p>

        <button type="submit" :disabled="isSubmitting">
          {{ isSubmitting ? '注册中...' : '注册' }}
        </button>
      </form>

      <p class="auth-switch">
        已有账号？
        <RouterLink class="text-link" :to="{ name: 'login' }">返回登录</RouterLink>
      </p>
    </section>
  </main>
</template>
