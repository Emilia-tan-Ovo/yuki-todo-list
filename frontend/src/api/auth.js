import { requestJson } from './http'

export function login(credentials) {
  return requestJson('/api/auth/login', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(credentials),
  })
}

export function register(credentials) {
  return requestJson('/api/auth/register', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(credentials),
  })
}

export function logout() {
  return requestJson('/api/auth/logout', {
    method: 'POST',
  })
}

export function getCurrentUser() {
  return requestJson('/api/auth/me')
}
