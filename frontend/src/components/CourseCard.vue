<script setup>
defineProps({
  course: {
    type: Object,
    required: true,
  },
})
</script>

<template>
  <article class="course-card" :style="{ '--course-color': course.color }">
    <header class="course-card__header">
      <div>
        <p class="course-card__label">课程</p>
        <h2>{{ course.name }}</h2>
      </div>
      <span class="course-card__count">{{ course.todoCount }} 项未完成</span>
    </header>

    <div class="course-card__tasks">
      <p class="course-card__section-title">最近任务</p>

      <ul v-if="course.nearestTasks.length > 0" class="course-card__task-list">
        <li v-for="task in course.nearestTasks.slice(0, 3)" :key="task.id">
          <span class="course-card__task-title">{{ task.title }}</span>
          <time :datetime="task.deadline">{{ task.deadline }}</time>
        </li>
      </ul>

      <p v-else class="course-card__empty">暂无待办任务</p>
    </div>
  </article>
</template>

<style scoped>
.course-card {
  position: relative;
  min-width: 0;
  overflow: hidden;
  padding: 24px;
  border: 1px solid #e4e8f1;
  border-radius: 16px;
  background: #ffffff;
  box-shadow: 0 10px 28px rgba(41, 52, 78, 0.07);
}

.course-card::before {
  position: absolute;
  inset: 0 auto 0 0;
  width: 5px;
  background: var(--course-color);
  content: '';
}

.course-card__header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 16px;
}

.course-card__label,
.course-card__section-title {
  margin: 0;
  color: #7a8498;
  font-size: 0.78rem;
  font-weight: 700;
  letter-spacing: 0.06em;
}

.course-card h2 {
  margin: 5px 0 0;
  color: #1f2a40;
  font-size: 1.25rem;
  line-height: 1.35;
}

.course-card__count {
  flex: 0 0 auto;
  padding: 6px 9px;
  border-radius: 999px;
  color: #4150b7;
  background: #eef0ff;
  font-size: 0.78rem;
  font-weight: 700;
}

.course-card__tasks {
  margin-top: 24px;
  padding-top: 18px;
  border-top: 1px solid #edf0f5;
}

.course-card__task-list {
  display: grid;
  gap: 13px;
  margin: 14px 0 0;
  padding: 0;
  list-style: none;
}

.course-card__task-list li {
  display: flex;
  align-items: baseline;
  justify-content: space-between;
  gap: 16px;
}

.course-card__task-title {
  min-width: 0;
  overflow: hidden;
  color: #354057;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.course-card time {
  flex: 0 0 auto;
  color: #7a8498;
  font-size: 0.82rem;
}

.course-card__empty {
  margin: 14px 0 0;
  color: #7a8498;
}

@media (max-width: 420px) {
  .course-card__header,
  .course-card__task-list li {
    align-items: flex-start;
    flex-direction: column;
    gap: 8px;
  }
}
</style>
