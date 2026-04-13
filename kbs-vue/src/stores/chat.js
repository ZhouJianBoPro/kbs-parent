import { defineStore } from 'pinia'

export const useChatStore = defineStore('chat', {
  state: () => ({
    currentConversation: null,
    conversations: [],
    messages: [],
    loading: false
  }),

  actions: {
    setCurrentConversation(conversation) {
      this.currentConversation = conversation
    },

    setConversations(conversations) {
      this.conversations = conversations
    },

    addMessage(message) {
      this.messages.push(message)
    },

    setMessages(messages) {
      this.messages = messages
    },

    setLoading(loading) {
      this.loading = loading
    },

    clearChat() {
      this.currentConversation = null
      this.messages = []
    }
  }
})
