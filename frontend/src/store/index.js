import { defineStore } from 'pinia'

const STORE_KEY = 'ai-interview-store'

function loadPersistedState() {
  try {
    const raw = localStorage.getItem(STORE_KEY)
    if (!raw) {
      return {}
    }
    return JSON.parse(raw)
  } catch (_) {
    return {}
  }
}

function persistState(state) {
  localStorage.setItem(
    STORE_KEY,
    JSON.stringify({
      token: state.token,
      resumeId: state.resumeId,
      jdText: state.jdText,
      sessionId: state.sessionId,
      currentQuestion: state.currentQuestion,
      chat: state.chat
    })
  )
}

export const useInterviewStore = defineStore('interview', {
  state: () => {
    const persisted = loadPersistedState()
    return {
      token: persisted.token || '',
      resumeId: persisted.resumeId || '',
      jdText: persisted.jdText || '',
      sessionId: persisted.sessionId || '',
      currentQuestion: persisted.currentQuestion || null,
      chat: Array.isArray(persisted.chat) ? persisted.chat : []
    }
  },
  actions: {
    setToken(token) {
      this.token = token
      persistState(this)
    },
    setResumeId(id) {
      this.resumeId = id
      persistState(this)
    },
    setJd(text) {
      this.jdText = text
      persistState(this)
    },
    startSession(sessionId, question) {
      this.sessionId = sessionId
      this.currentQuestion = question
      this.chat = []
      if (question) {
        this.chat.push({ role: 'ai', content: question.content, questionId: question.id })
      }
      persistState(this)
    },
    addUserMessage(text) {
      this.chat.push({ role: 'user', content: text })
      persistState(this)
    },
    addAiMessage(text, questionId) {
      this.chat.push({ role: 'ai', content: text, questionId })
      persistState(this)
    },
    setCurrentQuestion(q) {
      this.currentQuestion = q
      persistState(this)
    }
  }
})
