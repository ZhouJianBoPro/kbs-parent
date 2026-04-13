import { token } from './token'

/**
 * 创建SSE连接（使用fetch API实现，支持认证）
 */
export const createSSE = (sessionId, onMessage, onError) => {
  const accessToken = token.getAccessToken()
  let abortController = null

  const connect = async () => {
    abortController = new AbortController()

    try {
      const response = await fetch(`/api/sse/connect?sessionId=${sessionId}`, {
        method: 'GET',
        headers: {
          'Authorization': `Bearer ${accessToken}`,
          'Accept': 'text/event-stream'
        },
        signal: abortController.signal
      })

      console.log('SSE响应状态:', response.status)

      if (!response.ok) {
        throw new Error(`SSE连接失败: ${response.status}`)
      }

      const reader = response.body.getReader()
      const decoder = new TextDecoder()
      let buffer = ''
      let currentEvent = ''

      while (true) {
        const { done, value } = await reader.read()

        if (done) {
          console.log('SSE流结束')
          break
        }

        const text = decoder.decode(value, { stream: true })
        buffer += text

        // 按行处理
        const lines = buffer.split('\n')
        buffer = lines.pop() || ''

        for (const line of lines) {
          // 解析事件名
          if (line.startsWith('event:')) {
            currentEvent = line.slice(6).trim()
            console.log('事件类型:', currentEvent)
            continue
          }

          // 解析数据
          if (line.startsWith('data:')) {
            const data = line.slice(5).trim()

            // 跳过心跳事件
            if (currentEvent === 'heartbeat') {
              console.log('跳过心跳')
              currentEvent = ''
              continue
            }

            if (data) {
              console.log('发送消息到UI:', data.substring(0, 30), '事件名:', currentEvent)
              onMessage(data, currentEvent)
            }
            currentEvent = ''
          }
        }
      }
    } catch (error) {
      console.error('SSE错误:', error)
      if (error.name !== 'AbortError') {
        onError(error)
      }
    }
  }

  connect()

  return {
    close: () => {
      console.log('关闭SSE连接')
      if (abortController) {
        abortController.abort()
      }
    }
  }
}
