<template>
  <div class="chat-container">
    <!-- 页面标题 -->
    <div class="page-header">
      <div class="page-title">
        <h2>AI 对话</h2>
        <p>与 AI 助手进行智能对话</p>
      </div>
    </div>

    <div class="chat-layout">
      <!-- 左侧会话列表 -->
      <aside class="chat-sidebar">
        <div class="sidebar-header">
          <el-button type="primary" style="width: 100%" @click="handleNewConversation">
            <el-icon><Plus /></el-icon>
            新建会话
          </el-button>
        </div>
        <div class="conversation-list">
          <div
            v-for="item in conversations"
            :key="item.id"
            :class="['conversation-item', { active: currentConversation?.id === item.id }]"
            @click="handleSelectConversation(item)"
          >
            <div class="conversation-info">
              <el-icon><ChatLineRound /></el-icon>
              <span class="conversation-title">{{ item.title || '新会话' }}</span>
            </div>
            <el-button
              type="danger"
              link
              size="small"
              @click.stop="handleDeleteConversation(item.id)"
            >
              <el-icon><Delete /></el-icon>
            </el-button>
          </div>
          <el-empty v-if="conversations.length === 0" description="暂无会话" :image-size="80" />
        </div>
      </aside>

      <!-- 右侧对话区域 -->
      <main class="chat-main">
        <!-- 欢迎页 -->
        <div v-if="!currentConversation" class="welcome-page">
          <div class="welcome-icon">
            <el-icon size="64"><Service /></el-icon>
          </div>
          <h3>欢迎使用 AI 助手</h3>
          <p>选择一个会话或创建新会话开始对话</p>
        </div>

        <!-- 对话内容 -->
        <template v-else>
          <div class="chat-messages" ref="messagesContainer">
            <div
              v-for="(msg, index) in messages"
              :key="index"
              :class="['message-item', msg.role]"
            >
              <div class="message-avatar">
                <el-icon v-if="msg.role === 'user'"><User /></el-icon>
                <el-icon v-else><Service /></el-icon>
              </div>
              <div class="message-content">
                <!-- 用户消息直接展示 -->
                <div v-if="msg.role === 'user'" class="message-bubble">{{ msg.content }}</div>
                <!-- AI消息使用Markdown渲染 -->
                <div v-else class="message-bubble markdown-body" v-html="renderMarkdown(msg.content)"></div>
              </div>
            </div>
            <div v-if="loading && !hasAssistantMessage" class="message-item assistant">
              <div class="message-avatar">
                <el-icon><Service /></el-icon>
              </div>
              <div class="message-content">
                <div class="message-bubble loading">
                  <span class="dot"></span>
                  <span class="dot"></span>
                  <span class="dot"></span>
                </div>
              </div>
            </div>
          </div>

          <!-- 输入区域 -->
          <div class="chat-input-area">
            <div class="input-wrapper">
              <el-input
                v-model="inputMessage"
                type="textarea"
                :rows="3"
                placeholder="输入您的问题..."
                :disabled="loading"
                resize="none"
                @keydown.enter.ctrl="handleSend"
              />
              <el-button
                type="primary"
                :loading="loading"
                :disabled="!inputMessage.trim() || loading"
                @click="handleSend"
              >
                <el-icon><Promotion /></el-icon>
                发送
              </el-button>
            </div>
            <p class="input-hint">按 Ctrl + Enter 发送</p>
          </div>
        </template>
      </main>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick, onUnmounted, computed } from 'vue'
import { marked } from 'marked'
import { chat } from '@/api/chat'
import { createSSE } from '@/utils/sse'
import request from '@/utils/request'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, ChatLineRound, Delete, Service, User, Promotion } from '@element-plus/icons-vue'

const conversations = ref([])
const currentConversation = ref(null)
const messages = ref([])
const inputMessage = ref('')
const loading = ref(false)
const messagesContainer = ref()
let eventSource = null

// 是否有 assistant 消息（用于控制 loading 动画显示）
const hasAssistantMessage = computed(() => messages.value.some(m => m.role === 'assistant'))

// 渲染 Markdown
const renderMarkdown = (content) => {
  if (!content) return ''
  return marked(content)
}

const getConversationList = async () => {
  const res = await chat.getConversationList({ pageNo: 1, pageSize: 50 })
  if (res.code === 200) {
    conversations.value = res.data.records
  }
}

const handleNewConversation = async () => {
  const res = await chat.createConversation()
  if (res.code === 200) {
    conversations.value.unshift(res.data)
    handleSelectConversation(res.data)
  }
}

const handleSelectConversation = async (conversation) => {
  currentConversation.value = conversation
  const res = await chat.getConversationDetail(conversation.id)
  if (res.code === 200) {
    // 将 type 转换为 role (USER -> user, ASSISTANT -> assistant)
    messages.value = (res.data || []).map(msg => ({
      ...msg,
      role: msg.type === 'USER' ? 'user' : 'assistant'
    }))
    scrollToBottom()
  }
}

const handleDeleteConversation = async (id) => {
  await ElMessageBox.confirm('确认删除该会话?', '提示', { type: 'warning' })
  const res = await chat.deleteConversation(id)
  if (res.code === 200) {
    ElMessage.success('删除成功')
    if (currentConversation.value?.id === id) {
      currentConversation.value = null
      messages.value = []
    }
    getConversationList()
  }
}

const handleSend = async () => {
  if (!inputMessage.value.trim() || !currentConversation.value || loading.value) return

  const userMessage = inputMessage.value.trim()
  inputMessage.value = ''

  // 添加用户消息
  messages.value.push({ role: 'user', content: userMessage })

  // 设置 loading 状态
  loading.value = true

  // 用于存储 AI 回复的内容
  let aiContent = ''

  try {
    const conversationId = currentConversation.value.id
    const sessionId = Date.now().toString()

    // 创建 SSE 连接
    eventSource = createSSE(
      sessionId,
      (data, eventName) => {
        console.log('收到SSE数据:', data, '事件名:', eventName)
        // 处理 done 事件
        if (eventName === 'done') {
          console.log('对话完成')
          loading.value = false
          eventSource?.close()
          scrollToBottom()
          return
        }
        aiContent += data
        // 更新最后一条消息
        const lastIndex = messages.value.length - 1
        if (messages.value[lastIndex]?.role === 'assistant') {
          messages.value[lastIndex].content = aiContent
        } else {
          messages.value.push({ role: 'assistant', content: aiContent })
        }
        scrollToBottom()
      },
      (error) => {
        console.error('SSE error:', error)
        // SSE 连接失败时，不直接设置 loading = false
        // 因为 chat 请求可能会触发 token 刷新
      }
    )

    // 发送 chat 请求
    await request.post('/chat/chat', {
      conversationId,
      message: userMessage,
      sessionId
    })
  } catch (e) {
    loading.value = false
    eventSource?.close()
    ElMessage.error('发送失败')
  }
}

const scrollToBottom = () => {
  nextTick(() => {
    if (messagesContainer.value) {
      messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight
    }
  })
}

onMounted(() => {
  getConversationList()
})

onUnmounted(() => {
  eventSource?.close()
})
</script>

<style scoped>
.chat-container {
  max-width: 1400px;
  margin: 0 auto;
  height: calc(100vh - 140px);
  display: flex;
  flex-direction: column;
}

.page-header {
  margin-bottom: 20px;
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

.chat-layout {
  flex: 1;
  display: flex;
  gap: 20px;
  background: #ffffff;
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.04);
}

.chat-sidebar {
  width: 280px;
  background: #fafafa;
  display: flex;
  flex-direction: column;
  border-right: 1px solid #f0f0f0;
}

.sidebar-header {
  padding: 20px;
  border-bottom: 1px solid #f0f0f0;
}

.conversation-list {
  flex: 1;
  overflow-y: auto;
}

.conversation-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 14px 20px;
  cursor: pointer;
  transition: background 0.2s;
}

.conversation-item:hover {
  background: #f0f0f0;
}

.conversation-item.active {
  background: #f0f0ff;
}

.conversation-info {
  display: flex;
  align-items: center;
  gap: 10px;
  overflow: hidden;
}

.conversation-title {
  font-size: 14px;
  color: #1a1a1a;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.chat-main {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.welcome-page {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: #8c8c8c;
}

.welcome-icon {
  width: 120px;
  height: 120px;
  background: linear-gradient(135deg, #f0f0ff 0%, #e8e8ff 100%);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #667eea;
  margin-bottom: 24px;
}

.welcome-page h3 {
  font-size: 20px;
  font-weight: 500;
  color: #1a1a1a;
  margin: 0 0 8px 0;
}

.welcome-page p {
  font-size: 14px;
  margin: 0;
}

.chat-messages {
  flex: 1;
  overflow-y: auto;
  padding: 20px;
}

.message-item {
  display: flex;
  gap: 12px;
  margin-bottom: 20px;
}

.message-item.user {
  flex-direction: row-reverse;
}

.message-avatar {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  background: #f0f0ff;
  color: #667eea;
}

.message-item.user .message-avatar {
  background: #1a1a1a;
  color: #fff;
}

.message-content {
  max-width: 70%;
}

.message-bubble {
  padding: 12px 16px;
  border-radius: 12px;
  font-size: 14px;
  line-height: 1.6;
  color: #1a1a1a;
  background: #f5f5f5;
}

.message-item.user .message-bubble {
  background: #1a1a1a;
  color: #fff;
}

/* Markdown 样式 */
.markdown-body {
  line-height: 1.6;
}

.markdown-body :deep(h1),
.markdown-body :deep(h2),
.markdown-body :deep(h3),
.markdown-body :deep(h4),
.markdown-body :deep(h5),
.markdown-body :deep(h6) {
  margin: 12px 0 8px 0;
  font-weight: 600;
}

.markdown-body :deep(h1) { font-size: 1.5em; }
.markdown-body :deep(h2) { font-size: 1.3em; }
.markdown-body :deep(h3) { font-size: 1.1em; }

.markdown-body :deep(p) {
  margin: 8px 0;
}

.markdown-body :deep(ul),
.markdown-body :deep(ol) {
  padding-left: 20px;
  margin: 8px 0;
}

.markdown-body :deep(li) {
  margin: 4px 0;
}

.markdown-body :deep(code) {
  background: #f0f0f0;
  padding: 2px 6px;
  border-radius: 4px;
  font-family: monospace;
  font-size: 0.9em;
}

.markdown-body :deep(pre) {
  background: #f5f5f5;
  padding: 12px;
  border-radius: 8px;
  overflow-x: auto;
  margin: 8px 0;
}

.markdown-body :deep(pre code) {
  background: none;
  padding: 0;
}

.markdown-body :deep(blockquote) {
  border-left: 3px solid #667eea;
  padding-left: 12px;
  margin: 8px 0;
  color: #666;
}

.markdown-body :deep(a) {
  color: #667eea;
  text-decoration: none;
}

.markdown-body :deep(a:hover) {
  text-decoration: underline;
}

.message-bubble.loading {
  display: flex;
  gap: 4px;
}

.dot {
  width: 6px;
  height: 6px;
  background: #667eea;
  border-radius: 50%;
  animation: bounce 1.4s infinite ease-in-out both;
}

.dot:nth-child(1) { animation-delay: -0.32s; }
.dot:nth-child(2) { animation-delay: -0.16s; }

@keyframes bounce {
  0%, 80%, 100% { transform: scale(0); }
  40% { transform: scale(1); }
}

.chat-input-area {
  padding: 20px;
  border-top: 1px solid #f0f0f0;
}

.input-wrapper {
  display: flex;
  gap: 12px;
  align-items: flex-end;
}

.input-wrapper .el-textarea {
  flex: 1;
}

:deep(.el-textarea__inner) {
  border-radius: 12px;
}

.input-hint {
  font-size: 12px;
  color: #bfbfbf;
  margin: 8px 0 0 0;
  text-align: center;
}
</style>
