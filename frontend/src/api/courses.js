import { requestJson } from './http'

export function getCourses() {
  return requestJson('/api/courses')
}

export function getCourseDetail(courseId) {
  return requestJson(`/api/courses/${courseId}`)
}

export function createCourse(courseData) {
  return requestJson('/api/courses', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(courseData),
  })
}
