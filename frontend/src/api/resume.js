import request from '../utils/request'

export function uploadResume(file) {
  const form = new FormData()
  form.append('file', file)
  return request({
    url: '/resume/upload',
    method: 'post',
    data: form,
    headers: { 'Content-Type': 'multipart/form-data' }
  })
}

export function saveResumeText(text) {
  return request({
    url: '/resume/text',
    method: 'post',
    data: { text }
  })
}
