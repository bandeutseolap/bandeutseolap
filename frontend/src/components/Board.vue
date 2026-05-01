<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'

const data = ref(null)
const loading = ref(false)
const error = ref(null)

async function fetchBoardTest() {
  loading.value = true
  error.value = null
  try {
    const res = await axios.get('http://localhost:8080/board/test') // 직접 호출
    data.value = res.data
  } catch (e) {
    error.value = e?.response?.data ?? e.message
  } finally {
    loading.value = false
  }
}

onMounted(fetchBoardTest)
</script>

<template>
  <div class="p-4">
    <h2>/board/test</h2>
    <button @click="fetchBoardTest" :disabled="loading">
      {{ loading ? 'Loading...' : 'Reload' }}
    </button>
    <pre v-if="data">{{ JSON.stringify(data, null, 2) }}</pre>
    <pre v-else-if="error" style="color:red">{{ error }}</pre>
  </div>
</template>
