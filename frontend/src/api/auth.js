// 认证请求统一使用相对路径，让 Vite 在开发环境中代理到 Spring Boot。
async function requestJson(path, options = {}) {
  const response = await fetch(path, {
    credentials: 'include',
    ...options,
  })

  const data = await response.json().catch(() => null)

  if (!response.ok) {
    const error = new Error(data?.message || '请求失败，请稍后重试。')
    error.status = response.status
    error.code = data?.code
    throw error
  }

  return data
}

export function login(credentials) {
  return requestJson('/api/auth/login', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(credentials),
  })
}

export function getCurrentUser() {
  return requestJson('/api/auth/me')
}
