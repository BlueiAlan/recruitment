<template>
  <div class="page">
    <el-card class="card">
      <h2>登录</h2>
      <el-form :model="form" label-width="80px">
        <el-form-item label="用户名">
          <el-input v-model="form.username" />
        </el-form-item>
        <el-form-item label="密码">
          <el-input v-model="form.password" type="password" />
        </el-form-item>
        <el-button type="primary" @click="onLogin" :loading="loading">登录</el-button>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { login } from '../api/auth'
import { useInterviewStore } from '../store'

const router = useRouter()
const store = useInterviewStore()
const loading = ref(false)
const form = reactive({ username: 'demo', password: 'demo' })

async function onLogin() {
  loading.value = true
  try {
    const res = await login(form)
    store.setToken(res.data.token)
    router.push('/resume')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f7f8fa;
}
.card {
  width: 420px;
}
</style>
