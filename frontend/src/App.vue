<script setup>
import { ref, nextTick, onMounted } from 'vue'
import { sendChat } from './api/chat.js'
import { marked } from 'marked'
import hljs from 'highlight.js'

marked.setOptions({
  highlight(code, lang) {
    if (lang && hljs.getLanguage(lang)) {
      return hljs.highlight(code, { language: lang }).value
    }
    return hljs.highlightAuto(code).value
  },
  breaks: true
})

const modes = [
  { key: 'qa', label: '💬 通用问答' },
  { key: 'generate', label: '✨ 代码生成' },
  { key: 'explain', label: '📖 代码解释' },
  { key: 'review', label: '🔍 代码审查' },
  { key: 'refactor', label: '🔄 代码重构' },
  { key: 'fix', label: '🐛 Bug 修复' }
]

const currentMode = ref('qa')
const messages = ref([
  {
    role: 'ai',
    content: `你好！我是 AI 代码助手，可以帮你完成以下任务：

- **代码生成** — 根据需求生成代码
- **代码解释** — 解释代码的功能和逻辑
- **代码审查** — 审查代码质量和潜在问题
- **代码重构** — 优化和改进现有代码
- **Bug 修复** — 分析和修复代码 Bug
- **通用问答** — 回答编程相关问题

请在上方选择模式，然后开始提问吧！`
  }
])
const inputText = ref('')
const loading = ref(false)
const chatBox = ref(null)

function selectMode(mode) {
  currentMode.value = mode
}

async function send() {
  const text = inputText.value.trim()
  if (!text || loading.value) return

  messages.value.push({ role: 'user', content: text })
  inputText.value = ''
  loading.value = true
  await scrollToBottom()

  try {
    const res = await sendChat(currentMode.value, text)
    if (res.data.success) {
      messages.value.push({ role: 'ai', content: res.data.data })
    } else {
      messages.value.push({ role: 'ai', content: '出错：' + (res.data.error || '未知错误') })
    }
  } catch {
    messages.value.push({ role: 'ai', content: '网络错误，请检查后端是否启动。' })
  } finally {
    loading.value = false
    await scrollToBottom()
  }
}

function handleKeydown(e) {
  if (e.key === 'Enter' && !e.shiftKey) {
    e.preventDefault()
    send()
  }
}

async function scrollToBottom() {
  await nextTick()
  if (chatBox.value) {
    chatBox.value.scrollTop = chatBox.value.scrollHeight
  }
}

function renderMarkdown(text) {
  return marked.parse(text)
}
</script>

<template>
  <div class="container">
    <header class="header">
      <h1>AI 代码助手</h1>
      <p class="subtitle">智能编程 · 高效开发</p>
    </header>

    <div class="mode-selector">
      <button
        v-for="m in modes"
        :key="m.key"
        :class="['mode-btn', { active: currentMode === m.key }]"
        @click="selectMode(m.key)"
      >
        {{ m.label }}
      </button>
    </div>

    <div class="chat-box" ref="chatBox">
      <div
        v-for="(msg, i) in messages"
        :key="i"
        :class="['message', msg.role]"
      >
        <div class="avatar">{{ msg.role === 'ai' ? 'AI' : '你' }}</div>
        <div class="content" v-html="msg.role === 'ai' ? renderMarkdown(msg.content) : msg.content"></div>
      </div>

      <div v-if="loading" class="message ai">
        <div class="avatar">AI</div>
        <div class="content">
          <div class="thinking">
            <span class="dot"></span>
            <span class="dot"></span>
            <span class="dot"></span>
          </div>
        </div>
      </div>
    </div>

    <div class="input-area">
      <textarea
        v-model="inputText"
        rows="3"
        placeholder="输入你的问题或代码..."
        maxlength="10000"
        @keydown="handleKeydown"
        :disabled="loading"
      ></textarea>
      <button id="sendBtn" @click="send" :disabled="loading || !inputText.trim()">
        <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
          <line x1="22" y1="2" x2="11" y2="13"></line>
          <polygon points="22 2 15 22 11 13 2 9 22 2"></polygon>
        </svg>
        发送
      </button>
    </div>
  </div>
</template>

<style>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

body {
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
  background: linear-gradient(135deg, #0f0c29, #302b63, #24243e);
  color: #e0e0e0;
  min-height: 100vh;
}

.container {
  max-width: 900px;
  margin: 0 auto;
  padding: 20px;
  height: 100vh;
  display: flex;
  flex-direction: column;
}

.header {
  text-align: center;
  padding: 20px 0 10px;
}

.header h1 {
  font-size: 28px;
  background: linear-gradient(90deg, #667eea, #764ba2);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.subtitle {
  font-size: 14px;
  color: #888;
  margin-top: 4px;
}

.mode-selector {
  display: flex;
  gap: 8px;
  padding: 10px 0;
  overflow-x: auto;
  flex-shrink: 0;
}

.mode-btn {
  padding: 8px 16px;
  border: 1px solid #444;
  border-radius: 20px;
  background: rgba(255, 255, 255, 0.05);
  color: #ccc;
  cursor: pointer;
  white-space: nowrap;
  transition: all 0.3s;
  font-size: 13px;
}

.mode-btn:hover {
  background: rgba(102, 126, 234, 0.15);
  border-color: #667eea;
  color: #fff;
}

.mode-btn.active {
  background: linear-gradient(90deg, #667eea, #764ba2);
  border-color: transparent;
  color: #fff;
}

.chat-box {
  flex: 1;
  overflow-y: auto;
  padding: 15px 0;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.message {
  display: flex;
  gap: 12px;
  animation: fadeIn 0.3s ease;
}

@keyframes fadeIn {
  from { opacity: 0; transform: translateY(10px); }
  to { opacity: 1; transform: translateY(0); }
}

.message.user {
  flex-direction: row-reverse;
}

.avatar {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
  font-weight: bold;
  flex-shrink: 0;
}

.message.ai .avatar {
  background: linear-gradient(135deg, #667eea, #764ba2);
  color: #fff;
}

.message.user .avatar {
  background: #555;
  color: #fff;
}

.message .content {
  max-width: 80%;
  padding: 12px 16px;
  border-radius: 12px;
  line-height: 1.6;
  font-size: 14px;
}

.message.ai .content {
  background: rgba(255, 255, 255, 0.08);
  border: 1px solid rgba(255, 255, 255, 0.1);
}

.message.user .content {
  background: rgba(102, 126, 234, 0.2);
  border: 1px solid rgba(102, 126, 234, 0.3);
  white-space: pre-wrap;
}

.message .content p { margin-bottom: 8px; }
.message .content p:last-child { margin-bottom: 0; }
.message .content ul, .message .content ol { margin: 8px 0; padding-left: 20px; }
.message .content li { margin-bottom: 4px; }
.message .content code {
  background: rgba(0, 0, 0, 0.3);
  padding: 2px 6px;
  border-radius: 4px;
  font-family: 'Fira Code', 'Consolas', monospace;
  font-size: 13px;
}
.message .content pre {
  margin: 8px 0;
  border-radius: 8px;
  overflow: hidden;
  background: #1e1e1e;
  padding: 12px;
}
.message .content pre code {
  background: none;
  padding: 0;
  display: block;
  overflow-x: auto;
}

.input-area {
  display: flex;
  gap: 10px;
  padding: 15px 0;
  flex-shrink: 0;
}

.input-area textarea {
  flex: 1;
  padding: 12px 16px;
  border: 1px solid #444;
  border-radius: 12px;
  background: rgba(255, 255, 255, 0.05);
  color: #e0e0e0;
  font-family: inherit;
  font-size: 14px;
  resize: none;
  outline: none;
  transition: border-color 0.3s;
}

.input-area textarea:focus {
  border-color: #667eea;
}

.input-area textarea::placeholder {
  color: #666;
}

.input-area textarea:disabled {
  opacity: 0.5;
}

#sendBtn {
  padding: 12px 24px;
  border: none;
  border-radius: 12px;
  background: linear-gradient(90deg, #667eea, #764ba2);
  color: #fff;
  font-size: 14px;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 8px;
  transition: opacity 0.3s;
  flex-shrink: 0;
}

#sendBtn:hover:not(:disabled) {
  opacity: 0.9;
}

#sendBtn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.thinking {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 0;
}

.thinking .dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: #667eea;
  animation: bounce 1.4s infinite ease-in-out;
}

.thinking .dot:nth-child(2) { animation-delay: 0.16s; }
.thinking .dot:nth-child(3) { animation-delay: 0.32s; }

@keyframes bounce {
  0%, 80%, 100% { transform: scale(0); }
  40% { transform: scale(1); }
}

::-webkit-scrollbar { width: 6px; }
::-webkit-scrollbar-track { background: transparent; }
::-webkit-scrollbar-thumb { background: #555; border-radius: 3px; }
::-webkit-scrollbar-thumb:hover { background: #777; }
</style>
