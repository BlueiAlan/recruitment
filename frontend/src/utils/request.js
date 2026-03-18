import axios from 'axios'

const service = axios.create({
  baseURL: '/api',
  timeout: 8000
})

service.interceptors.response.use(
  (resp) => {
    const res = resp.data
    if (res && res.code !== 0) {
      return Promise.reject(new Error(res.message || 'request error'))
    }
    return res
  },
  (error) => Promise.reject(error)
)

export default service
