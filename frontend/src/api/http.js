// 所有前端请求都通过这个最小封装发送，统一携带 Session Cookie。
export async function requestJson(path, options = {}) {
  const response = await fetch(path, {
    credentials: 'include',
    ...options,
  })

  // 204 No Content 没有响应体，不应该继续尝试解析 JSON。
  const data = response.status === 204 ? null : await response.json().catch(() => null)

  if (!response.ok) {
    const error = new Error(data?.message || '请求失败，请稍后重试。')
    error.status = response.status
    error.code = data?.code
    throw error
  }

  return data
}
