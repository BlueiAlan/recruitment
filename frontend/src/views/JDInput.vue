<template>
  <div class="page">
    <el-card class="card">
      <h2>输入岗位 JD</h2>
      <el-input v-model="jdText" type="textarea" :rows="10" placeholder="粘贴岗位描述" />
      <div class="actions">
        <el-button type="primary" @click="start">开始面试</el-button>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { startInterview } from '../api/interview'
import { useInterviewStore } from '../store'

const router = useRouter()
const store = useInterviewStore()
const jdText = ref(store.jdText)

async function start() {
  const res = await startInterview({ resumeId: store.resumeId, jdText: jdText.value })
  store.setJd(jdText.value)
  store.startSession(res.data.sessionId, res.data.firstQuestion)
  router.push('/interview')
}
</script>

<style scoped>
.page {
  min-height: 100vh;
  background: #f7f8fa;
  padding: 30px;
}
.card {
  max-width: 800px;
  margin: 0 auto;
}
.actions {
  margin-top: 16px;
  text-align: right;
}
</style>
