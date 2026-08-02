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

export function updateCourse(courseId, courseData) {
  return requestJson(`/api/courses/${courseId}`, {
    method: 'PUT',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(courseData),
  })
}
