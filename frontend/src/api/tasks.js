import { requestJson } from './http'

export function getCourseTasks(courseId, status) {
  return requestJson(`/api/courses/${courseId}/tasks?status=${status}`)
}

export function createTask(taskData) {
  return requestJson('/api/tasks', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(taskData),
  })
}

export function updateTaskStatus(taskId, status) {
  return requestJson(`/api/tasks/${taskId}/status`, {
    method: 'PATCH',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify({ status }),
  })
}
