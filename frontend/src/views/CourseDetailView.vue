<script setup>
import { onMounted, ref } from 'vue'
import { RouterLink, useRoute, useRouter } from 'vue-router'
import { getCourseDetail } from '../api/courses'
import { getCourseTasks } from '../api/tasks'

const route = useRoute()
const router = useRouter()

const course = ref(null)
const isLoading = ref(true)
const errorMessage = ref('')

const tasks = ref([])
const activeStatus = ref('TODO')
const isTasksLoading = ref(true)
const tasksErrorMessage = ref('')

async function loadCourseDetail() {
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
}

// 首次进入和标签切换共用同一条任务加载流程。
async function loadTasks(status) {
  activeStatus.value = status
  tasksErrorMessage.value = ''
  isTasksLoading.value = true

  try {
    const data = await getCourseTasks(route.params.courseId, status)
    tasks.value = data.tasks
  } catch (error) {
    if (error.status === 401) {
      await router.replace({ name: 'login' })
      return
    }

    tasksErrorMessage.value = error.status
      ? error.message
      : '任务列表加载失败，请确认后端已启动后重试。'
  } finally {
    isTasksLoading.value = false
  }
}

onMounted(() => {
  loadCourseDetail()
  loadTasks('TODO')
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

        <div class="course-tasks-section">
          <div class="task-status-tabs" aria-label="任务状态筛选">
            <button
              type="button"
              :class="{ 'task-status-tab--active': activeStatus === 'TODO' }"
              :aria-pressed="activeStatus === 'TODO'"
              :disabled="isTasksLoading"
              @click="loadTasks('TODO')"
            >
              未完成 {{ course.todoCount }}
            </button>
            <button
              type="button"
              :class="{ 'task-status-tab--active': activeStatus === 'DONE' }"
              :aria-pressed="activeStatus === 'DONE'"
              :disabled="isTasksLoading"
              @click="loadTasks('DONE')"
            >
              已完成 {{ course.doneCount }}
            </button>
          </div>

          <p v-if="isTasksLoading" class="task-list-state">正在加载任务...</p>

          <p v-else-if="tasksErrorMessage" class="error-message page-message">
            {{ tasksErrorMessage }}
          </p>

          <p v-else-if="tasks.length === 0" class="task-list-state">
            {{ activeStatus === 'TODO' ? '暂无未完成任务' : '暂无已完成任务' }}
          </p>

          <ul v-else class="course-task-list">
            <li v-for="task in tasks" :key="task.id">
              <span>{{ task.title }}</span>
              <time :datetime="task.deadline">{{ task.deadline }}</time>
            </li>
          </ul>
        </div>
      </section>
    </main>
  </div>
</template>
