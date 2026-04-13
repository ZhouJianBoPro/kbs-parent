<template>
  <div class="document-container">
    <!-- 页面标题 -->
    <div class="page-header">
      <div class="page-title">
        <h2>文档管理</h2>
        <p>管理您的知识库文档</p>
      </div>
      <el-button type="primary" size="large" @click="showUploadDialog = true">
        <el-icon><Plus /></el-icon>
        上传文档
      </el-button>
    </div>

    <!-- 筛选卡片 -->
    <div class="filter-card">
      <el-input
        v-model="queryParams.docName"
        placeholder="搜索文档名称"
        clearable
        style="width: 240px"
        :prefix-icon="Search"
      />
      <el-select v-model="queryParams.docType" placeholder="文档类型" clearable style="width: 140px">
        <el-option label="PDF" value="PDF" />
        <el-option label="Word" value="WORD" />
        <el-option label="TXT" value="TXT" />
        <el-option label="Markdown" value="MD" />
      </el-select>
      <el-select v-model="queryParams.status" placeholder="处理状态" clearable style="width: 140px">
        <el-option label="待处理" :value="0" />
        <el-option label="处理中" :value="1" />
        <el-option label="成功" :value="2" />
        <el-option label="失败" :value="3" />
      </el-select>
      <el-button type="primary" @click="handleQuery">搜索</el-button>
      <el-button @click="handleReset">重置</el-button>
    </div>

    <!-- 文档列表 -->
    <div class="document-grid" v-loading="loading">
      <div
        v-for="doc in tableData"
        :key="doc.id"
        class="document-card"
      >
        <div class="doc-icon">
          <el-icon size="32"><Document /></el-icon>
        </div>
        <div class="doc-info">
          <h4 class="doc-name">{{ doc.docName }}</h4>
          <div class="doc-meta">
            <span class="doc-type">{{ doc.docType }}</span>
            <span class="doc-size">{{ formatSize(doc.fileSize) }}</span>
          </div>
          <div class="doc-time">{{ doc.createTime }}</div>
        </div>
        <div class="doc-status">
          <el-tag :type="getStatusType(doc.status)" size="small">
            {{ getStatusText(doc.status) }}
          </el-tag>
        </div>
        <div class="doc-actions">
          <el-button type="primary" link @click="handleDownload(doc)">
            <el-icon><Download /></el-icon>
            下载
          </el-button>
          <el-button type="danger" link @click="handleDelete(doc)">
            <el-icon><Delete /></el-icon>
            删除
          </el-button>
        </div>
      </div>

      <!-- 空状态 -->
      <el-empty v-if="!loading && tableData.length === 0" description="暂无文档" />
    </div>

    <!-- 分页 -->
    <div class="pagination-wrap" v-if="total > 0">
      <el-pagination
        v-model:current-page="queryParams.pageNo"
        v-model:page-size="queryParams.pageSize"
        :total="total"
        :page-sizes="[10, 20, 50]"
        layout="total, sizes, prev, pager, next"
        @change="getList"
      />
    </div>

    <!-- 上传弹窗 -->
    <el-dialog v-model="showUploadDialog" title="上传文档" width="480px" :close-on-click-modal="false">
      <div class="upload-area">
        <el-upload
          ref="uploadRef"
          :auto-upload="false"
          :limit="1"
          :on-change="handleFileChange"
          :file-list="fileList"
          drag
          accept=".pdf,.doc,.docx,.txt,.md"
          class="upload-dragger"
        >
          <el-icon class="el-icon--upload"><UploadFilled /></el-icon>
          <div class="el-upload__text">将文件拖到此处，或<em>点击上传</em></div>
          <template #tip>
            <div class="el-upload__tip">支持 PDF、Word、TXT、Markdown 文件</div>
          </template>
        </el-upload>
      </div>
      <template #footer>
        <el-button @click="showUploadDialog = false">取消</el-button>
        <el-button type="primary" :loading="uploading" @click="handleUpload">上传</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { document } from '@/api/document'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Search, Document, Download, Delete, UploadFilled } from '@element-plus/icons-vue'

const loading = ref(false)
const uploading = ref(false)
const showUploadDialog = ref(false)
const fileList = ref([])
const tableData = ref([])
const total = ref(0)

const queryParams = reactive({
  docName: '',
  docType: '',
  status: null,
  pageNo: 1,
  pageSize: 10
})

const formatSize = (size) => {
  if (!size) return '0 B'
  const units = ['B', 'KB', 'MB', 'GB', 'TB']
  let unitIndex = 0
  let fileSize = size
  while (fileSize >= 1024 && unitIndex < units.length - 1) {
    fileSize /= 1024
    unitIndex++
  }
  return fileSize.toFixed(2) + ' ' + units[unitIndex]
}

const getStatusType = (status) => {
  const types = { 0: 'info', 1: 'warning', 2: 'success', 3: 'danger' }
  return types[status] || 'info'
}

const getStatusText = (status) => {
  const texts = { 0: '待处理', 1: '处理中', 2: '成功', 3: '失败' }
  return texts[status] || '未知'
}

const getList = async () => {
  loading.value = true
  try {
    const res = await document.getList(queryParams)
    if (res.code === 200) {
      tableData.value = res.data.records
      total.value = res.data.total
    }
  } finally {
    loading.value = false
  }
}

const handleQuery = () => {
  queryParams.pageNo = 1
  getList()
}

const handleReset = () => {
  queryParams.docName = ''
  queryParams.docType = ''
  queryParams.status = null
  handleQuery()
}

const handleFileChange = (file) => {
  fileList.value = [file]
}

const handleUpload = async () => {
  if (fileList.value.length === 0) {
    ElMessage.warning('请选择文件')
    return
  }
  uploading.value = true
  try {
    const res = await document.upload(fileList.value[0].raw)
    if (res.code === 200) {
      ElMessage.success('上传成功')
      showUploadDialog.value = false
      fileList.value = []
      getList()
    }
  } finally {
    uploading.value = false
  }
}

const handleDownload = async (doc) => {
  try {
    // 获取token
    const accessToken = localStorage.getItem('access_token')
    console.log('Token:', accessToken)
    console.log('DocID:', doc.id)

    const response = await fetch(`/api/document/download/${doc.id}`, {
      method: 'GET',
      headers: {
        'Authorization': `Bearer ${accessToken}`
      }
    })

    console.log('Response status:', response.status)

    if (!response.ok) {
      throw new Error(`HTTP ${response.status}`)
    }

    const blob = await response.blob()
    console.log('Blob size:', blob.size)

    // 创建下载链接
    const url = window.URL.createObjectURL(blob)
    const link = window.document.createElement('a')
    link.href = url
    link.download = doc.docName || 'download'
    link.style.display = 'none'
    window.document.body.appendChild(link)
    link.click()

    // 清理
    setTimeout(() => {
      window.document.body.removeChild(link)
      window.URL.revokeObjectURL(url)
    }, 100)

    ElMessage.success('下载成功')
  } catch (e) {
    console.error('下载失败:', e)
    ElMessage.error('下载失败: ' + e.message)
  }
}

const handleDelete = async (doc) => {
  await ElMessageBox.confirm('确认删除该文档?', '提示', { type: 'warning' })
  const res = await document.delete(doc.id)
  if (res.code === 200) {
    ElMessage.success('删除成功')
    getList()
  }
}

onMounted(() => {
  getList()
})
</script>

<style scoped>
.document-container {
  max-width: 1200px;
  margin: 0 auto;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.page-title h2 {
  font-size: 24px;
  font-weight: 600;
  color: #1a1a1a;
  margin: 0 0 4px 0;
}

.page-title p {
  font-size: 14px;
  color: #8c8c8c;
  margin: 0;
}

.filter-card {
  background: #ffffff;
  border-radius: 12px;
  padding: 20px;
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
  margin-bottom: 24px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.04);
}

.document-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 16px;
  min-height: 200px;
}

.document-card {
  background: #ffffff;
  border-radius: 12px;
  padding: 20px;
  display: flex;
  flex-direction: column;
  gap: 12px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.04);
  transition: all 0.2s;
}

.document-card:hover {
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.08);
  transform: translateY(-2px);
}

.doc-icon {
  width: 48px;
  height: 48px;
  background: #f0f0ff;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #667eea;
}

.doc-info {
  flex: 1;
}

.doc-name {
  font-size: 15px;
  font-weight: 500;
  color: #1a1a1a;
  margin: 0 0 8px 0;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.doc-meta {
  display: flex;
  gap: 12px;
  font-size: 13px;
  color: #8c8c8c;
  margin-bottom: 4px;
}

.doc-type {
  background: #f5f5f5;
  padding: 2px 8px;
  border-radius: 4px;
}

.doc-time {
  font-size: 12px;
  color: #bfbfbf;
}

.doc-status {
  padding-top: 8px;
  border-top: 1px solid #f0f0f0;
}

.doc-actions {
  display: flex;
  gap: 8px;
}

.pagination-wrap {
  display: flex;
  justify-content: center;
  margin-top: 24px;
}

.upload-area {
  padding: 20px 0;
}

.upload-dragger {
  width: 100%;
}

:deep(.el-upload-dragger) {
  border-radius: 12px;
  border: 2px dashed #d9d9d9;
}

:deep(.el-upload-dragger:hover) {
  border-color: #667eea;
}
</style>
