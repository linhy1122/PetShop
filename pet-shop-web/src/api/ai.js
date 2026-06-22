import request from '@/utils/request'

/** 发送消息给AI */
export function aiChatApi(message, sessionId = 'default') {
  return request.post('/ai/chat', { message, sessionId })
}
