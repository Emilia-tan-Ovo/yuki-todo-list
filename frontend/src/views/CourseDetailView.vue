<script setup>
import { onMounted, ref } from 'vue'
import { RouterLink, useRoute, useRouter } from 'vue-router'
import { getCourseDetail } from '../api/courses'

const route = useRoute()
const router = useRouter()

const course = ref(null)
const isLoading = ref(true)
const errorMessage = ref('')

onMounted(async () => {
  try {
    course.value = await getCourseDetail(route.params.courseId)
  } catch (error) {
    if (error.status === 401) {
      await router.replace({ name: 'login' })
      return
    }

    errorMessage.value = error.status
      ? error.message
      : '课程详情加载失败，请确认后端已启动后刷新页面。'
  } finally {
    isLoading.value = false
  }
})
</script>

<template>
  <div class="course-detail-page">
    <header class="app-header">
      <div class="app-header__inner">
        <p class="app-brand">YuKi's TODO List</p>
      </div>
    </header>

    <main class="course-detail-content">
      <RouterLink class="back-link" :to="{ name: 'home' }">← 返回我的课程</RouterLink>

      <p v-if="isLoading" class="state-panel">正在加载课程详情...</p>

      <p v-else-if="errorMessage" class="error-message page-message">
        {{ errorMessage }}
      </p>

      <section
        v-else-if="course"
        class="course-detail-card"
        :style="{ '--course-color': course.color }"
      >
        <div class="course-detail-heading">
          <span class="course-color-dot" aria-hidden="true"></span>
          <div>
            <p class="eyebrow">课程详情</p>
            <h1>{{ course.name }}</h1>
          </div>
        </div>

        <div class="course-detail-stats">
          <div class="course-stat">
            <span>未完成任务</span>
            <strong>{{ course.todoCount }}</strong>
          </div>
          <div class="course-stat">
            <span>已完成任务</span>
            <strong>{{ course.doneCount }}</strong>
          </div>
        </div>
      </section>
    </main>
  </div>
</template>
