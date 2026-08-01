<script setup>
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { getCurrentUser, logout } from '../api/auth'
import { getCourses } from '../api/courses'
import CourseCard from '../components/CourseCard.vue'

const router = useRouter()

const currentUser = ref(null)
const isLoading = ref(true)
const errorMessage = ref('')
const logoutErrorMessage = ref('')
const isLoggingOut = ref(false)
const courses = ref([])
const isCoursesLoading = ref(true)
const coursesErrorMessage = ref('')

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

  // 当前用户加载失败时，不再发送依赖登录状态的课程请求。
  if (!currentUser.value) {
    isCoursesLoading.value = false
    return
  }

  try {
    const data = await getCourses()
    courses.value = data.courses
  } catch (error) {
    if (error.status === 401) {
      await router.replace({ name: 'login' })
      return
    }

    coursesErrorMessage.value = error.status
      ? error.message
      : '课程加载失败，请确认后端已启动后刷新页面。'
  } finally {
    isCoursesLoading.value = false
  }
})

async function handleLogout() {
  logoutErrorMessage.value = ''
  isLoggingOut.value = true

  try {
    await logout()
    await router.replace({ name: 'login' })
  } catch (error) {
    logoutErrorMessage.value = error.status
      ? error.message
      : '无法连接到服务器，请确认后端已启动。'
  } finally {
    isLoggingOut.value = false
  }
}
</script>

<template>
  <div class="home-page">
    <header class="app-header">
      <div class="app-header__inner">
        <p class="app-brand">YuKi's TODO List</p>

        <div v-if="currentUser" class="user-actions">
          <span>你好，{{ currentUser.username }}</span>
          <button
            class="logout-button"
            type="button"
            :disabled="isLoggingOut"
            @click="handleLogout"
          >
            {{ isLoggingOut ? '退出中...' : '退出登录' }}
          </button>
        </div>
      </div>
    </header>

    <main class="home-content">
      <p v-if="isLoading" class="state-panel">正在确认登录状态...</p>

      <p v-else-if="errorMessage" class="error-message page-message" role="alert">
        {{ errorMessage }}
      </p>

      <template v-else-if="currentUser">
        <p
          v-if="logoutErrorMessage"
          class="error-message page-message"
          role="alert"
          aria-live="polite"
        >
          {{ logoutErrorMessage }}
        </p>

        <section aria-labelledby="courses-title">
          <div class="page-heading">
            <div>
              <p class="eyebrow">学习空间</p>
              <h1 id="courses-title">我的课程</h1>
              <p class="helper-text">查看每门课程的待办数量和最近截止任务。</p>
            </div>
          </div>

          <p v-if="isCoursesLoading" class="state-panel">正在加载课程...</p>

          <p v-else-if="coursesErrorMessage" class="error-message page-message" role="alert">
            {{ coursesErrorMessage }}
          </p>

          <div v-else-if="courses.length === 0" class="empty-state">
            <h2>还没有课程</h2>
            <p>之后可以在这里创建第一门课程。</p>
          </div>

          <div v-else class="courses-grid">
            <CourseCard v-for="course in courses" :key="course.id" :course="course" />
          </div>
        </section>
      </template>
    </main>
  </div>
</template>
