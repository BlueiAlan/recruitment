import request from '../utils/request'

export function startInterview(data) {
  return request({
    url: '/interview/start',
    method: 'post',
    data
  })
}

export function submitAnswer(data) {
  return request({
    url: '/interview/answer',
    method: 'post',
    data
  })
}

export function getResult(sessionId) {
  return request({
    url: '/interview/result',
    method: 'get',
    params: { sessionId }
  })
}
