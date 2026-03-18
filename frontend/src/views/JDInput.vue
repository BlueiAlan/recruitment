<template>
  <div class="page">
    <el-card class="card">
      <h2>输入岗位 JD</h2>
      <el-input v-model="jdText" type="textarea" :rows="10" placeholder="粘贴岗位描述" />
      <div class="actions">
        <el-button type="primary" :loading="loading" @click="start">开始面试</el-button>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { startInterview } from '../api/interview'
import { useInterviewStore } from '../store'

const router = useRouter()
const store = useInterviewStore()
const jdText = ref(store.jdText)
const loading = ref(false)

async function start() {
  if (!store.resumeId) {
    ElMessage.warning('请先上传或填写简历，再开始面试')
    router.push('/resume')
    return
  }
  if (!jdText.value || !jdText.value.trim()) {
    ElMessage.warning('请输入岗位 JD')
    return
  }
  loading.value = true
  try {
    const res = await startInterview({ resumeId: store.resumeId, jdText: jdText.value.trim() })
    store.setJd(jdText.value.trim())
    store.startSession(res.data.sessionId, res.data.firstQuestion)
    ElMessage.success('面试已开始')
    router.push('/interview')
  } catch (e) {
    ElMessage.error(e?.message || '启动面试失败，请重试')
  } finally {
    loading.value = false
  }
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
