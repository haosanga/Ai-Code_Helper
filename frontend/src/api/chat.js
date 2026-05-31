import axios from 'axios'

export function sendChat(mode, message) {
  const params = new URLSearchParams()
  params.append('mode', mode)
  params.append('message', message)
  return axios.post('/api/chat', params, {
    headers: { 'Content-Type': 'application/x-www-form-urlencoded' }
  })
}

export function healthCheck() {
  return axios.get('/api/health')
}
