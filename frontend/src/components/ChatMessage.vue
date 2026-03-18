<template>
  <div class="msg" :class="role">
    <div class="bubble">{{ content }}</div>
  </div>
</template>

<script setup>
defineProps({
  role: { type: String, required: true },
  content: { type: String, required: true }
})
</script>

<style scoped>
.msg {
  display: flex;
  margin: 8px 0;
}
.msg.ai {
  justify-content: flex-start;
}
.msg.user {
  justify-content: flex-end;
}
.bubble {
  max-width: 70%;
  padding: 10px 12px;
  border-radius: 12px;
  background: #f2f3f5;
  line-height: 1.6;
  white-space: pre-wrap;
}
.msg.user .bubble {
  background: #dfefff;
}
</style>
