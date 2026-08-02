import { requestJson } from './http'

export function getCourseTasks(courseId, status) {
  return requestJson(`/api/courses/${courseId}/tasks?status=${status}`)
}
