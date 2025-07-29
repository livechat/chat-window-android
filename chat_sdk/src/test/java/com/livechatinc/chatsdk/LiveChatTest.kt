package com.livechatinc.chatsdk

import com.livechatinc.chatsdk.src.domain.interfaces.managers.AppScopedLiveChatViewManager
import com.livechatinc.chatsdk.src.data.domain.NetworkClient
import com.livechatinc.chatsdk.src.domain.interfaces.managers.SessionManager
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.*

class LiveChatTest {

    private lateinit var mockNetworkClient: NetworkClient
    private lateinit var mockViewManager: AppScopedLiveChatViewManager
    private lateinit var sessionManager: SessionManager
    private lateinit var liveChat: LiveChat

    @Before
    fun setup() {
        // Create mocks
        mockNetworkClient = mock(NetworkClient::class.java)
        mockViewManager = mock(AppScopedLiveChatViewManager::class.java)
        sessionManager = mock(SessionManager::class.java)

        // Create LiveChat instance with mocks
        liveChat = LiveChat.createForTesting(
            "test-license",
            mockNetworkClient,
            mockViewManager,
            sessionManager,
        )
    }

    @Test
    fun `createLiveChatConfig should use correct values`() {
        // Arrange
        val name = "Test User"
        val email = "test@example.com"
        val groupId = "123"
        val customParams = mapOf("param1" to "value1")

        liveChat.setCustomerInfo(name, email, groupId, customParams)

        // Act
        val config = liveChat.createLiveChatConfig()

        // Assert
        assert(config.license == "test-license")
        assert(config.groupId == groupId)
        assert(config.customerInfo?.name == name)
        assert(config.customerInfo?.email == email)
        assert(config.customerInfo?.customParams == customParams)
    }
}
