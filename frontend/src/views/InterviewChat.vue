<template>
  <div class="page">
    <el-card class="card">
      <div class="chat">
        <ChatMessage
          v-for="(item, idx) in store.chat"
          :key="idx"
          :role="item.role"
          :content="item.content" />
      </div>
      <div class="input">
        <el-input v-model="answer" type="textarea" :rows="3" placeholder="输入你的回答" />
        <el-button type="primary" @click="send" :disabled="!canSend">发送</el-button>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { computed, ref } from 'vue'
import { useRouter } from 'vue-router'
import { submitAnswer } from '../api/interview'
import { useInterviewStore } from '../store'
import ChatMessage from '../components/ChatMessage.vue'

const store = useInterviewStore()
const router = useRouter()
const answer = ref('')

const canSend = computed(() => store.currentQuestion && answer.value.trim().length > 0)

async function send() {
  const current = store.currentQuestion
  const content = answer.value.trim()
  store.addUserMessage(content)
  answer.value = ''

  const res = await submitAnswer({
    sessionId: store.sessionId,
    questionId: current.id,
    answer: content
  })

  store.addAiMessage(`评分: ${res.data.score}，建议: ${res.data.feedback}`)

  if (res.data.nextQuestion) {
    store.addAiMessage(res.data.nextQuestion.content, res.data.nextQuestion.id)
    store.setCurrentQuestion(res.data.nextQuestion)
  } else {
    store.setCurrentQuestion(null)
    router.push('/result')
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
  max-width: 900px;
  margin: 0 auto;
  display: flex;
  flex-direction: column;
  height: calc(100vh - 60px);
}
.chat {
  flex: 1;
  overflow-y: auto;
  padding: 10px 0;
}
.input {
  display: flex;
  gap: 12px;
  align-items: flex-end;
}
</style>
