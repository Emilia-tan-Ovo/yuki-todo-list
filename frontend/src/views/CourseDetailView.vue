<script setup>
import { onMounted, ref } from 'vue'
import { RouterLink, useRoute, useRouter } from 'vue-router'
import { getCourseDetail } from '../api/courses'
import { createTask, getCourseTasks } from '../api/tasks'

const route = useRoute()
const router = useRouter()

// 课程详情状态
const course = ref(null)
const isLoading = ref(true)
const errorMessage = ref('')

// 任务列表状态
const tasks = ref([])
const activeStatus = ref('TODO')
const isTasksLoading = ref(true)
const tasksErrorMessage = ref('')

// 新建任务表单状态
const isCreateTaskOpen = ref(false)
const taskForm = ref({
  title: '',
  deadline: '',
})
const isCreatingTask = ref(false)
const createTaskErrorMessage = ref('')

// 数据加载函数
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

// 弹窗操作函数
function openCreateTaskModal() {
  createTaskErrorMessage.value = ''
  isCreateTaskOpen.value = true
}

function closeCreateTaskModal() {
  if (isCreatingTask.value) {
    return
  }

  isCreateTaskOpen.value = false
  taskForm.value = {
    title: '',
    deadline: '',
  }
  createTaskErrorMessage.value = ''
}

// 表单提交函数
async function handleCreateTask() {
  if (isCreatingTask.value) {
    return
  }

  const title = taskForm.value.title.trim()

  if (!title) {
    createTaskErrorMessage.value = '请输入任务名称。'
    return
  }

  if (title.length > 200) {
    createTaskErrorMessage.value = '任务名称不能超过 200 个字符。'
    return
  }

  if (!taskForm.value.deadline) {
    createTaskErrorMessage.value = '请选择截止日期。'
    return
  }

  createTaskErrorMessage.value = ''
  isCreatingTask.value = true

  try {
    await createTask({
      title,
      courseId: Number(route.params.courseId),
      deadline: taskForm.value.deadline,
    })

    isCreatingTask.value = false
    closeCreateTaskModal()
    await Promise.all([loadCourseDetail(), loadTasks('TODO')])
  } catch (error) {
    if (error.status === 401) {
      await router.replace({ name: 'login' })
      return
    }

    createTaskErrorMessage.value = error.status
      ? error.message
      : '任务创建失败，请确认后端已启动后重试。'
  } finally {
    isCreatingTask.value = false
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
          <div class="course-detail-title">
            <span class="course-color-dot" aria-hidden="true"></span>
            <div>
              <p class="eyebrow">课程详情</p>
              <h1>{{ course.name }}</h1>
            </div>
          </div>
          <button type="button" class="create-task-button" @click="openCreateTaskModal">
            新建任务
          </button>
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

    <div v-if="isCreateTaskOpen" class="modal-backdrop">
      <section
        class="create-task-modal"
        role="dialog"
        aria-modal="true"
        aria-labelledby="create-task-modal-title"
      >
        <div class="create-task-modal__header">
          <div>
            <h2 id="create-task-modal-title">新建任务</h2>
            <p class="create-task-modal__course">所属课程：{{ course.name }}</p>
          </div>
        </div>

        <form class="create-task-form" @submit.prevent="handleCreateTask">
          <div class="create-task-form__field">
            <label for="create-task-title">任务名称</label>
            <input
              id="create-task-title"
              v-model="taskForm.title"
              type="text"
              maxlength="200"
              autocomplete="off"
              :disabled="isCreatingTask"
            />
          </div>

          <div class="create-task-form__field">
            <label for="create-task-deadline">截止日期</label>
            <input
              id="create-task-deadline"
              v-model="taskForm.deadline"
              type="date"
              :disabled="isCreatingTask"
            />
          </div>

          <p v-if="createTaskErrorMessage" class="error-message">
            {{ createTaskErrorMessage }}
          </p>

          <div class="create-task-modal__actions">
            <button
              type="button"
              class="secondary-button"
              :disabled="isCreatingTask"
              @click="closeCreateTaskModal"
            >
              取消
            </button>
            <button type="submit" class="primary-button" :disabled="isCreatingTask">
              {{ isCreatingTask ? '保存中...' : '保存' }}
            </button>
          </div>
        </form>
      </section>
    </div>
  </div>
</template>
