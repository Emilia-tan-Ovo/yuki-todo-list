// 认证请求统一使用相对路径，让 Vite 在开发环境中代理到 Spring Boot。
async function requestJson(path, options = {}) {
  const response = await fetch(path, {
    credentials: 'include',
    ...options,
  })

  // 退出登录成功时后端返回 204，没有响应体，不需要尝试解析 JSON。
  const data = response.status === 204 ? null : await response.json().catch(() => null)

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
