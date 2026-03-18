import { defineStore } from 'pinia'

export const useInterviewStore = defineStore('interview', {
  state: () => ({
    token: '',
    resumeId: '',
    jdText: '',
    sessionId: '',
    currentQuestion: null,
    chat: []
  }),
  actions: {
    setToken(token) {
      this.token = token
    },
    setResumeId(id) {
      this.resumeId = id
    },
    setJd(text) {
      this.jdText = text
    },
    startSession(sessionId, question) {
      this.sessionId = sessionId
      this.currentQuestion = question
      this.chat = []
      if (question) {
        this.chat.push({ role: 'ai', content: question.content, questionId: question.id })
      }
    },
    addUserMessage(text) {
      this.chat.push({ role: 'user', content: text })
    },
    addAiMessage(text, questionId) {
      this.chat.push({ role: 'ai', content: text, questionId })
    },
    setCurrentQuestion(q) {
      this.currentQuestion = q
    }
  }
})
