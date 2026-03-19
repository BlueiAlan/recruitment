import { createRouter, createWebHistory } from 'vue-router'

import ResumeUpload from '../views/ResumeUpload.vue'
import JDInput from '../views/JDInput.vue'
import InterviewChat from '../views/InterviewChat.vue'
import Result from '../views/Result.vue'

const routes = [
  { path: '/', redirect: '/resume' },
  { path: '/resume', component: ResumeUpload },
  { path: '/jd', component: JDInput },
  { path: '/interview', component: InterviewChat },
  { path: '/result', component: Result }
]

export default createRouter({
  history: createWebHistory(),
  routes
})
