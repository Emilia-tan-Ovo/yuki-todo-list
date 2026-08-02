<script setup>
import { onMounted, ref } from 'vue'
import { RouterLink, useRoute, useRouter } from 'vue-router'
import { deleteCourse, getCourseDetail, getCourses, updateCourse } from '../api/courses'
import {
  createTask,
  deleteTask,
  getCourseTasks,
  updateTask,
  updateTaskStatus,
} from '../api/tasks'

const route = useRoute()
const router = useRouter()

// 课程详情状态
const course = ref(null)
const isLoading = ref(true)
const errorMessage = ref('')

// 编辑课程表单状态
const isEditCourseOpen = ref(false)
const editCourseForm = ref({
  name: '',
  color: '#5967d8',
})
const isUpdatingCourse = ref(false)
const editCourseErrorMessage = ref('')

// 删除课程确认状态
const isDeleteCourseOpen = ref(false)
const isDeletingCourse = ref(false)
const deleteCourseErrorMessage = ref('')

// 任务列表状态
const tasks = ref([])
const activeStatus = ref('TODO')
const isTasksLoading = ref(true)
const tasksErrorMessage = ref('')
const expandedTaskId = ref(null)
const updatingTaskId = ref(null)
const updateTaskStatusErrorMessage = ref('')

// 新建任务表单状态
const isCreateTaskOpen = ref(false)
const taskForm = ref({
  title: '',
  deadline: '',
})
const isCreatingTask = ref(false)
const createTaskErrorMessage = ref('')

// 编辑任务表单状态
const isEditTaskOpen = ref(false)
const editingTask = ref(null)
const editTaskForm = ref({
  title: '',
  courseId: '',
  deadline: '',
  status: 'TODO',
})
const editableCourses = ref([])
const isEditCoursesLoading = ref(false)
const isUpdatingTask = ref(false)
const editTaskErrorMessage = ref('')

// 删除任务确认状态
const isDeleteTaskOpen = ref(false)
const deletingTask = ref(null)
const isDeletingTask = ref(false)
const deleteTaskErrorMessage = ref('')

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
  expandedTaskId.value = null
  updateTaskStatusErrorMessage.value = ''
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

function toggleTaskExpansion(taskId) {
  if (updatingTaskId.value === taskId) {
    return
  }

  updateTaskStatusErrorMessage.value = ''
  expandedTaskId.value = expandedTaskId.value === taskId ? null : taskId
}

async function handleTaskStatusChange(taskId, newStatus) {
  if (updatingTaskId.value !== null) {
    return
  }

  updateTaskStatusErrorMessage.value = ''
  updatingTaskId.value = taskId

  try {
    await updateTaskStatus(taskId, newStatus)
    expandedTaskId.value = null
    await Promise.all([loadCourseDetail(), loadTasks(activeStatus.value)])
  } catch (error) {
    if (error.status === 401) {
      await router.replace({ name: 'login' })
      return
    }

    updateTaskStatusErrorMessage.value = error.status
      ? error.message
      : '任务状态更新失败，请确认后端已启动后重试。'
  } finally {
    updatingTaskId.value = null
  }
}

// 弹窗操作函数
function openEditCourseModal() {
  if (!course.value) {
    return
  }

  editCourseErrorMessage.value = ''
  editCourseForm.value = {
    name: course.value.name,
    color: course.value.color,
  }
  isEditCourseOpen.value = true
}

function closeEditCourseModal() {
  if (isUpdatingCourse.value) {
    return
  }

  isEditCourseOpen.value = false
  editCourseForm.value = {
    name: '',
    color: '#5967d8',
  }
  editCourseErrorMessage.value = ''
}

function openDeleteCourseModal() {
  deleteCourseErrorMessage.value = ''
  isDeleteCourseOpen.value = true
}

function closeDeleteCourseModal() {
  if (isDeletingCourse.value) {
    return
  }

  isDeleteCourseOpen.value = false
  deleteCourseErrorMessage.value = ''
}

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

async function openEditTaskModal(task) {
  editTaskErrorMessage.value = ''
  editingTask.value = task
  editTaskForm.value = {
    title: task.title,
    courseId: task.courseId,
    deadline: task.deadline,
    status: task.status,
  }
  isEditTaskOpen.value = true

  if (editableCourses.value.length > 0) {
    return
  }

  isEditCoursesLoading.value = true

  try {
    const data = await getCourses()
    editableCourses.value = data.courses
  } catch (error) {
    if (error.status === 401) {
      await router.replace({ name: 'login' })
      return
    }

    editTaskErrorMessage.value = error.status
      ? error.message
      : '课程列表加载失败，请确认后端已启动后重试。'
  } finally {
    isEditCoursesLoading.value = false
  }
}

function closeEditTaskModal() {
  if (isUpdatingTask.value) {
    return
  }

  isEditTaskOpen.value = false
  editingTask.value = null
  editTaskForm.value = {
    title: '',
    courseId: '',
    deadline: '',
    status: 'TODO',
  }
  editTaskErrorMessage.value = ''
}

function openDeleteTaskModal(task) {
  deleteTaskErrorMessage.value = ''
  deletingTask.value = task
  isDeleteTaskOpen.value = true
}

function closeDeleteTaskModal() {
  if (isDeletingTask.value) {
    return
  }

  isDeleteTaskOpen.value = false
  deletingTask.value = null
  deleteTaskErrorMessage.value = ''
}

// 表单提交函数
async function handleUpdateCourse() {
  if (isUpdatingCourse.value) {
    return
  }

  const name = editCourseForm.value.name.trim()
  const color = editCourseForm.value.color.toUpperCase()

  if (!name) {
    editCourseErrorMessage.value = '请输入课程名称。'
    return
  }

  if (name.length > 100) {
    editCourseErrorMessage.value = '课程名称不能超过 100 个字符。'
    return
  }

  if (!/^#[0-9A-F]{6}$/.test(color)) {
    editCourseErrorMessage.value = '课程颜色必须使用 #RRGGBB 格式。'
    return
  }

  editCourseErrorMessage.value = ''
  isUpdatingCourse.value = true

  try {
    await updateCourse(Number(route.params.courseId), {
      name,
      color,
    })

    isUpdatingCourse.value = false
    closeEditCourseModal()
    await Promise.all([loadCourseDetail(), loadTasks(activeStatus.value)])
  } catch (error) {
    if (error.status === 401) {
      await router.replace({ name: 'login' })
      return
    }

    editCourseErrorMessage.value = error.status
      ? error.message
      : '课程修改失败，请确认后端已启动后重试。'
  } finally {
    isUpdatingCourse.value = false
  }
}

async function handleDeleteCourse() {
  if (isDeletingCourse.value) {
    return
  }

  deleteCourseErrorMessage.value = ''
  isDeletingCourse.value = true

  try {
    await deleteCourse(Number(route.params.courseId))
    isDeletingCourse.value = false
    closeDeleteCourseModal()
    await router.replace({ name: 'home' })
  } catch (error) {
    if (error.status === 401) {
      await router.replace({ name: 'login' })
      return
    }

    deleteCourseErrorMessage.value = error.status
      ? error.message
      : '课程删除失败，请确认后端已启动后重试。'
  } finally {
    isDeletingCourse.value = false
  }
}

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

async function handleUpdateTask() {
  if (isUpdatingTask.value || !editingTask.value) {
    return
  }

  const title = editTaskForm.value.title.trim()

  if (!title) {
    editTaskErrorMessage.value = '请输入任务名称。'
    return
  }

  if (title.length > 200) {
    editTaskErrorMessage.value = '任务名称不能超过 200 个字符。'
    return
  }

  if (editTaskForm.value.courseId === '') {
    editTaskErrorMessage.value = '请选择所属课程。'
    return
  }

  if (!editTaskForm.value.deadline) {
    editTaskErrorMessage.value = '请选择截止日期。'
    return
  }

  if (!['TODO', 'DONE'].includes(editTaskForm.value.status)) {
    editTaskErrorMessage.value = '请选择有效的任务状态。'
    return
  }

  const taskId = editingTask.value.id
  editTaskErrorMessage.value = ''
  isUpdatingTask.value = true

  try {
    await updateTask(taskId, {
      title,
      courseId: Number(editTaskForm.value.courseId),
      deadline: editTaskForm.value.deadline,
      status: editTaskForm.value.status,
    })

    isUpdatingTask.value = false
    closeEditTaskModal()
    expandedTaskId.value = null
    await Promise.all([loadCourseDetail(), loadTasks(activeStatus.value)])
  } catch (error) {
    if (error.status === 401) {
      await router.replace({ name: 'login' })
      return
    }

    editTaskErrorMessage.value = error.status
      ? error.message
      : '任务修改失败，请确认后端已启动后重试。'
  } finally {
    isUpdatingTask.value = false
  }
}

async function handleDeleteTask() {
  if (isDeletingTask.value || !deletingTask.value) {
    return
  }

  const taskId = deletingTask.value.id
  deleteTaskErrorMessage.value = ''
  isDeletingTask.value = true

  try {
    await deleteTask(taskId)
    isDeletingTask.value = false
    closeDeleteTaskModal()
    expandedTaskId.value = null
    await Promise.all([loadCourseDetail(), loadTasks(activeStatus.value)])
  } catch (error) {
    if (error.status === 401) {
      await router.replace({ name: 'login' })
      return
    }

    deleteTaskErrorMessage.value = error.status
      ? error.message
      : '任务删除失败，请确认后端已启动后重试。'
  } finally {
    isDeletingTask.value = false
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
          <div class="course-detail-actions">
            <button type="button" class="secondary-button" @click="openEditCourseModal">
              编辑课程
            </button>
            <button type="button" class="create-task-button" @click="openCreateTaskModal">
              新建任务
            </button>
            <button
              type="button"
              class="danger-button delete-course-button"
              @click="openDeleteCourseModal"
            >
              删除课程
            </button>
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

          <p v-if="updateTaskStatusErrorMessage" class="error-message page-message">
            {{ updateTaskStatusErrorMessage }}
          </p>

          <p v-if="isTasksLoading" class="task-list-state">正在加载任务...</p>

          <p v-else-if="tasksErrorMessage" class="error-message page-message">
            {{ tasksErrorMessage }}
          </p>

          <p v-else-if="tasks.length === 0" class="task-list-state">
            {{ activeStatus === 'TODO' ? '暂无未完成任务' : '暂无已完成任务' }}
          </p>

          <ul v-else class="course-task-list">
            <li v-for="task in tasks" :key="task.id" class="course-task-item">
              <button
                type="button"
                class="course-task-summary"
                :aria-expanded="expandedTaskId === task.id"
                :aria-controls="`task-details-${task.id}`"
                :disabled="updatingTaskId === task.id"
                @click="toggleTaskExpansion(task.id)"
              >
                <span class="course-task-summary__main">
                  <span class="course-task-summary__title">{{ task.title }}</span>
                  <time :datetime="task.deadline">{{ task.deadline }}</time>
                </span>
                <span class="course-task-summary__hint">
                  {{ expandedTaskId === task.id ? '收起' : '展开' }}
                </span>
              </button>

              <div
                v-if="expandedTaskId === task.id"
                :id="`task-details-${task.id}`"
                class="course-task-details"
              >
                <dl>
                  <div>
                    <dt>任务名称</dt>
                    <dd>{{ task.title }}</dd>
                  </div>
                  <div>
                    <dt>所属课程</dt>
                    <dd>{{ course.name }}</dd>
                  </div>
                  <div>
                    <dt>截止日期</dt>
                    <dd>{{ task.deadline }}</dd>
                  </div>
                </dl>

                <div class="task-detail-controls">
                  <div class="task-status-field">
                    <label :for="`task-status-${task.id}`">当前状态</label>
                    <select
                      :id="`task-status-${task.id}`"
                      :value="task.status"
                      :disabled="updatingTaskId === task.id"
                      @change="handleTaskStatusChange(task.id, $event.target.value)"
                    >
                      <option value="TODO">TODO</option>
                      <option value="DONE">DONE</option>
                    </select>
                    <span v-if="updatingTaskId === task.id" class="task-update-status">
                      状态更新中...
                    </span>
                  </div>

                  <div class="task-action-buttons">
                    <button
                      type="button"
                      class="edit-task-button"
                      :disabled="updatingTaskId === task.id"
                      @click="openEditTaskModal(task)"
                    >
                      编辑任务
                    </button>
                    <button
                      type="button"
                      class="delete-task-button"
                      :disabled="updatingTaskId === task.id"
                      @click="openDeleteTaskModal(task)"
                    >
                      删除任务
                    </button>
                  </div>
                </div>
              </div>
            </li>
          </ul>
        </div>
      </section>
    </main>

    <div v-if="isDeleteCourseOpen" class="modal-backdrop">
      <section
        class="create-task-modal"
        role="dialog"
        aria-modal="true"
        aria-labelledby="delete-course-modal-title"
      >
        <div class="create-task-modal__header">
          <h2 id="delete-course-modal-title">删除课程</h2>
        </div>

        <p class="delete-confirmation-text">
          确定要删除课程“<strong>{{ course.name }}</strong>”吗？
        </p>
        <p class="delete-course-warning">
          删除课程会同时永久删除课程下的全部任务，无法恢复。
        </p>

        <p v-if="deleteCourseErrorMessage" class="error-message" role="alert">
          {{ deleteCourseErrorMessage }}
        </p>

        <div class="create-task-modal__actions">
          <button
            type="button"
            class="secondary-button"
            :disabled="isDeletingCourse"
            @click="closeDeleteCourseModal"
          >
            取消
          </button>
          <button
            type="button"
            class="danger-button"
            :disabled="isDeletingCourse"
            @click="handleDeleteCourse"
          >
            {{ isDeletingCourse ? '删除中...' : '确认删除课程' }}
          </button>
        </div>
      </section>
    </div>

    <div v-if="isEditCourseOpen" class="modal-backdrop">
      <section
        class="create-task-modal"
        role="dialog"
        aria-modal="true"
        aria-labelledby="edit-course-modal-title"
      >
        <div class="create-task-modal__header">
          <h2 id="edit-course-modal-title">编辑课程</h2>
        </div>

        <form class="create-task-form" @submit.prevent="handleUpdateCourse">
          <div class="create-task-form__field">
            <label for="edit-course-name">课程名称</label>
            <input
              id="edit-course-name"
              v-model="editCourseForm.name"
              type="text"
              maxlength="100"
              autocomplete="off"
              :disabled="isUpdatingCourse"
            />
          </div>

          <div class="create-task-form__field course-color-picker">
            <label for="edit-course-color">课程颜色</label>
            <div>
              <input
                id="edit-course-color"
                v-model="editCourseForm.color"
                type="color"
                :disabled="isUpdatingCourse"
              />
              <span>{{ editCourseForm.color }}</span>
            </div>
          </div>

          <p v-if="editCourseErrorMessage" class="error-message" role="alert">
            {{ editCourseErrorMessage }}
          </p>

          <div class="create-task-modal__actions">
            <button
              type="button"
              class="secondary-button"
              :disabled="isUpdatingCourse"
              @click="closeEditCourseModal"
            >
              取消
            </button>
            <button type="submit" class="primary-button" :disabled="isUpdatingCourse">
              {{ isUpdatingCourse ? '保存中...' : '保存' }}
            </button>
          </div>
        </form>
      </section>
    </div>

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

    <div v-if="isEditTaskOpen" class="modal-backdrop">
      <section
        class="create-task-modal"
        role="dialog"
        aria-modal="true"
        aria-labelledby="edit-task-modal-title"
      >
        <div class="create-task-modal__header">
          <h2 id="edit-task-modal-title">编辑任务</h2>
        </div>

        <form class="create-task-form" @submit.prevent="handleUpdateTask">
          <div class="create-task-form__field">
            <label for="edit-task-title">任务名称</label>
            <input
              id="edit-task-title"
              v-model="editTaskForm.title"
              type="text"
              maxlength="200"
              autocomplete="off"
              :disabled="isEditCoursesLoading || isUpdatingTask"
            />
          </div>

          <div class="create-task-form__field">
            <label for="edit-task-course">所属课程</label>
            <select
              id="edit-task-course"
              v-model="editTaskForm.courseId"
              :disabled="isEditCoursesLoading || isUpdatingTask"
            >
              <option
                v-for="courseOption in editableCourses"
                :key="courseOption.id"
                :value="courseOption.id"
              >
                {{ courseOption.name }}
              </option>
            </select>
          </div>

          <p v-if="isEditCoursesLoading" class="status-text">正在加载课程列表...</p>

          <div class="create-task-form__field">
            <label for="edit-task-deadline">截止日期</label>
            <input
              id="edit-task-deadline"
              v-model="editTaskForm.deadline"
              type="date"
              :disabled="isEditCoursesLoading || isUpdatingTask"
            />
          </div>

          <div class="create-task-form__field">
            <label for="edit-task-status">状态</label>
            <select
              id="edit-task-status"
              v-model="editTaskForm.status"
              :disabled="isEditCoursesLoading || isUpdatingTask"
            >
              <option value="TODO">TODO</option>
              <option value="DONE">DONE</option>
            </select>
          </div>

          <p v-if="editTaskErrorMessage" class="error-message">
            {{ editTaskErrorMessage }}
          </p>

          <div class="create-task-modal__actions">
            <button
              type="button"
              class="secondary-button"
              :disabled="isUpdatingTask"
              @click="closeEditTaskModal"
            >
              取消
            </button>
            <button
              type="submit"
              class="primary-button"
              :disabled="
                isEditCoursesLoading || isUpdatingTask || editableCourses.length === 0
              "
            >
              {{ isUpdatingTask ? '保存中...' : '保存' }}
            </button>
          </div>
        </form>
      </section>
    </div>

    <div v-if="isDeleteTaskOpen" class="modal-backdrop">
      <section
        class="create-task-modal"
        role="dialog"
        aria-modal="true"
        aria-labelledby="delete-task-modal-title"
      >
        <div class="create-task-modal__header">
          <h2 id="delete-task-modal-title">删除任务</h2>
        </div>

        <p class="delete-confirmation-text">
          确定要删除任务“<strong>{{ deletingTask.title }}</strong>”吗？删除后无法恢复。
        </p>

        <p v-if="deleteTaskErrorMessage" class="error-message">
          {{ deleteTaskErrorMessage }}
        </p>

        <div class="create-task-modal__actions">
          <button
            type="button"
            class="secondary-button"
            :disabled="isDeletingTask"
            @click="closeDeleteTaskModal"
          >
            取消
          </button>
          <button
            type="button"
            class="danger-button"
            :disabled="isDeletingTask"
            @click="handleDeleteTask"
          >
            {{ isDeletingTask ? '删除中...' : '确认删除' }}
          </button>
        </div>
      </section>
    </div>
  </div>
</template>
