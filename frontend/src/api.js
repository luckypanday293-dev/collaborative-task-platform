const API_URL = import.meta.env.VITE_API_URL || 'http://localhost:8080/api'

export function saveCredentials(username, password) {
  sessionStorage.setItem('taskflow_auth', btoa(`${username}:${password}`))
}

export function clearCredentials() {
  sessionStorage.removeItem('taskflow_auth')
}

export function hasCredentials() {
  return Boolean(sessionStorage.getItem('taskflow_auth'))
}

export async function api(path, options = {}) {
  const auth = sessionStorage.getItem('taskflow_auth')
  const response = await fetch(`${API_URL}${path}`, {
    ...options,
    headers: {
      'Content-Type': 'application/json',
      ...(auth ? { Authorization: `Basic ${auth}` } : {}),
      ...(options.headers || {}),
    },
  })

  if (!response.ok) {
    let message = `Request failed (${response.status})`
    try {
      const body = await response.json()
      message = body.error || message
    } catch { /* response had no JSON body */ }
    const error = new Error(message)
    error.status = response.status
    throw error
  }

  if (response.status === 204) return null
  return response.json()
}
