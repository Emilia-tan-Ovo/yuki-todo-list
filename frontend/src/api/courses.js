import { requestJson } from './http'

export function getCourses() {
  return requestJson('/api/courses')
}
