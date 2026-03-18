<template>
  <div class="page">
    <el-card class="card">
      <h2>上传简历</h2>
      <el-upload
        class="upload"
        drag
        :auto-upload="false"
        :on-change="onFileChange"
        accept=".pdf,.txt">
        <div>拖拽文件到此处，或点击选择</div>
      </el-upload>
      <el-divider>或者直接粘贴简历文本</el-divider>
      <el-input v-model="resumeText" type="textarea" :rows="8" />
      <div class="actions">
        <el-button type="primary" :loading="loading" @click="submit">下一步</el-button>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { uploadResume, saveResumeText } from '../api/resume'
import { useInterviewStore } from '../store'

const router = useRouter()
const store = useInterviewStore()
const fileRef = ref(null)
const resumeText = ref('')
const loading = ref(false)

function onFileChange(file) {
  fileRef.value = file.raw
}

async function submit() {
  if (!fileRef.value && (!resumeText.value || !resumeText.value.trim())) {
    ElMessage.warning('请上传简历文件或粘贴简历文本')
    return
  }
  loading.value = true
  try {
    let res
    if (fileRef.value) {
      res = await uploadResume(fileRef.value)
    } else {
      res = await saveResumeText(resumeText.value.trim())
    }
    store.setResumeId(res.data.resumeId)
    ElMessage.success('简历已保存')
    router.push('/jd')
  } catch (e) {
    ElMessage.error(e?.message || '简历保存失败，请重试')
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
