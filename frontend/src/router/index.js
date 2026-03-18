import { createRouter, createWebHistory } from 'vue-router'

import Login from '../views/Login.vue'
import ResumeUpload from '../views/ResumeUpload.vue'
import JDInput from '../views/JDInput.vue'
import InterviewChat from '../views/InterviewChat.vue'
import Result from '../views/Result.vue'

const routes = [
  { path: '/', redirect: '/login' },
  { path: '/login', component: Login },
  { path: '/resume', component: ResumeUpload },
  { path: '/jd', component: JDInput },
  { path: '/interview', component: InterviewChat },
  { path: '/result', component: Result }
]

export default createRouter({
  history: createWebHistory(),
  routes
})
