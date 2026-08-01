import { requestJson } from './http'

export function getCourses() {
  return requestJson('/api/courses')
}

export function getCourseDetail(courseId) {
  return requestJson(`/api/courses/${courseId}`)
}
