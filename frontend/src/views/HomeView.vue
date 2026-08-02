<script setup>
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { getCurrentUser, logout } from '../api/auth'
import { createCourse, getCourses } from '../api/courses'
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

const isCreateCourseOpen = ref(false)
const courseForm = ref({
  name: '',
  useCustomColor: false,
  color: '#5967d8',
})
const isCreatingCourse = ref(false)
const createCourseErrorMessage = ref('')

async function loadCourses() {
  coursesErrorMessage.value = ''
  isCoursesLoading.value = true

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
}

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

  await loadCourses()
})

function openCreateCourseModal() {
  createCourseErrorMessage.value = ''
  isCreateCourseOpen.value = true
}

function closeCreateCourseModal() {
  if (isCreatingCourse.value) {
    return
  }

  isCreateCourseOpen.value = false
  courseForm.value = {
    name: '',
    useCustomColor: false,
    color: '#5967d8',
  }
  createCourseErrorMessage.value = ''
}

async function handleCreateCourse() {
  if (isCreatingCourse.value) {
    return
  }

  const name = courseForm.value.name.trim()

  if (!name) {
    createCourseErrorMessage.value = '请输入课程名称。'
    return
  }

  if (name.length > 100) {
    createCourseErrorMessage.value = '课程名称不能超过 100 个字符。'
    return
  }

  const courseData = { name }

  if (courseForm.value.useCustomColor) {
    courseData.color = courseForm.value.color
  }

  createCourseErrorMessage.value = ''
  isCreatingCourse.value = true

  try {
    await createCourse(courseData)
    isCreatingCourse.value = false
    closeCreateCourseModal()
    await loadCourses()
  } catch (error) {
    if (error.status === 401) {
      await router.replace({ name: 'login' })
      return
    }

    createCourseErrorMessage.value = error.status
      ? error.message
      : '课程创建失败，请确认后端已启动后重试。'
  } finally {
    isCreatingCourse.value = false
  }
}

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
            <button
              type="button"
              class="primary-button create-course-button"
              @click="openCreateCourseModal"
            >
              新建课程
            </button>
          </div>

          <p v-if="isCoursesLoading" class="state-panel">正在加载课程...</p>

          <p v-else-if="coursesErrorMessage" class="error-message page-message" role="alert">
            {{ coursesErrorMessage }}
          </p>

          <div v-else-if="courses.length === 0" class="empty-state">
            <h2>还没有课程</h2>
            <p>之后可以在这里创建第一门课程。</p>
            <button
              type="button"
              class="primary-button empty-state__action"
              @click="openCreateCourseModal"
            >
              创建第一门课程
            </button>
          </div>

          <div v-else class="courses-grid">
            <CourseCard v-for="course in courses" :key="course.id" :course="course" />
          </div>
        </section>
      </template>
    </main>

    <div v-if="isCreateCourseOpen" class="modal-backdrop">
      <section
        class="create-task-modal"
        role="dialog"
        aria-modal="true"
        aria-labelledby="create-course-modal-title"
      >
        <div class="create-task-modal__header">
          <h2 id="create-course-modal-title">新建课程</h2>
        </div>

        <form class="create-task-form" @submit.prevent="handleCreateCourse">
          <div class="create-task-form__field">
            <label for="create-course-name">课程名称</label>
            <input
              id="create-course-name"
              v-model="courseForm.name"
              type="text"
              maxlength="100"
              autocomplete="off"
              :disabled="isCreatingCourse"
            />
          </div>

          <label
            class="course-color-toggle"
            :class="{ 'course-color-toggle--disabled': isCreatingCourse }"
            for="use-custom-course-color"
          >
            <input
              id="use-custom-course-color"
              v-model="courseForm.useCustomColor"
              type="checkbox"
              :disabled="isCreatingCourse"
            />
            <span>使用自定义课程颜色</span>
          </label>

          <p v-if="!courseForm.useCustomColor" class="status-text">
            课程颜色将由系统自动分配。
          </p>

          <div v-else class="create-task-form__field course-color-picker">
            <label for="create-course-color">课程颜色</label>
            <div>
              <input
                id="create-course-color"
                v-model="courseForm.color"
                type="color"
                :disabled="isCreatingCourse"
              />
              <span>{{ courseForm.color }}</span>
            </div>
          </div>

          <p v-if="createCourseErrorMessage" class="error-message" role="alert">
            {{ createCourseErrorMessage }}
          </p>

          <div class="create-task-modal__actions">
            <button
              type="button"
              class="secondary-button"
              :disabled="isCreatingCourse"
              @click="closeCreateCourseModal"
            >
              取消
            </button>
            <button type="submit" class="primary-button" :disabled="isCreatingCourse">
              {{ isCreatingCourse ? '保存中...' : '保存' }}
            </button>
          </div>
        </form>
      </section>
    </div>
  </div>
</template>
