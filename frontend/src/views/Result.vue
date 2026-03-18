<template>
  <div class="page">
    <el-card class="card">
      <h2>面试结果</h2>
      <el-descriptions :column="1" border>
        <el-descriptions-item label="平均分">{{ result.averageScore }}</el-descriptions-item>
        <el-descriptions-item label="总体建议">{{ result.overallAdvice }}</el-descriptions-item>
      </el-descriptions>
      <el-divider />
      <el-table :data="result.answers" stripe style="width: 100%">
        <el-table-column prop="question" label="问题" />
        <el-table-column prop="answer" label="回答" />
        <el-table-column prop="score" label="评分" width="80" />
        <el-table-column prop="feedback" label="建议" />
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { onMounted, reactive } from 'vue'
import { getResult } from '../api/interview'
import { useInterviewStore } from '../store'

const store = useInterviewStore()
const result = reactive({ averageScore: 0, overallAdvice: '', answers: [] })

onMounted(async () => {
  const res = await getResult(store.sessionId)
  Object.assign(result, res.data)
})
</script>

<style scoped>
.page {
  min-height: 100vh;
  background: #f7f8fa;
  padding: 30px;
}
.card {
  max-width: 1000px;
  margin: 0 auto;
}
</style>
