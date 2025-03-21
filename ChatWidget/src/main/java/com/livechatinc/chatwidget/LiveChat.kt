package com.livechatinc.chatwidget

import android.content.Context
import android.view.View
import com.livechatinc.chatwidget.src.models.CookieGrant

abstract class LiveChat {

    /**
     * Stores licence number
     * Stores application scope context
     * Potentially fetches chat url
     * */
    abstract suspend fun initialize(licence: String, context: Context)

    /**
     * Stores params that are used when initializing chat
     */
    abstract suspend fun setCustomerInfo(
        name: String,
        email: String,
        groupId: String,
        customParams: Map<String, String>?
    )

    /**
     * Opens full screen activity
     *
     * Limitations:
     * - no callbacks - [LiveChatView.LiveChatViewCallbackListener]
     * */
    abstract suspend fun show(
        context: Context,
        errorViewBuilder: (message: String) -> View
    )

    /**
     * Clears webView cookies
     * Clears cookieGrant, customerToken and chatWidgetToken
     * Unregisters push token
     * */
    abstract suspend fun signOutCustomer()

    /******* Embedding view mode *******/

    abstract class LiveChatView {
        /**
         * Fetches chat url if not present, loads chat window
         * Depends on:
         * - licence from [LiveChat.initialize] SDK
         * - uses [setCustomerInfo]
         * - check if [configureIdentityProvider] is configured
         * */
        abstract fun initialize(listener: LiveChatViewCallbackListener?)

        abstract fun onBackPressed(): Boolean

        abstract class LiveChatViewCallbackListener {
            abstract fun onChatLoaded()
            abstract fun onNewMessage(message: String)
            abstract fun onHideChat()
            abstract fun handleUrl(): Boolean // Potentially useful when launched in activity
            abstract fun onError()
            abstract fun onFileChooserActivityNotFound()

            // New callbacks
            // Potentially useful when launched in activity
            abstract fun onChatId(chatId: String)
            abstract fun onChatState(chatState: String)
        }
    }

    /******* Advanced usage *******/

    /**
     * Reports back [CookieGrant] to the host app
     * Needs to be called before [logInCustomer] and launching chat window
     * */
    abstract suspend fun configureIdentityProvider(
        licenceId: String,
        clientId: String,
        cookieGrantCallback: (CookieGrant) -> Unit?
    )

    /**
     * Fetches token, creates new one if no cookieGrant
     *
     * Required:
     * @see configureIdentityProvider
     * */
    abstract suspend fun logInCustomer(cookieGrant: CookieGrant?)

    /******* Push Notification *******/

    /**
     * Only if not using ChatWidgetDefaultMessagingService
     * Required:
     * @see configureIdentityProvider
     * */
    abstract suspend fun registerPushToken(token: String)

    class ChatWidgetDefaultMessagingService {// : FirebaseMessagingService() {

        //override
        fun onNewToken(token: String) {}

        //override
        fun onMessageReceived() {}
    }

    /******* Potential features *******/

    /**
     * Will chatId be needed?
     * Required:
     * @see configureIdentityProvider
     * */
    abstract suspend fun unreadMessageCount(token: String): Int

//    Open questions:
//    - do SDK instance should have ability to receive chat window callbacks?
//    - should SDK be able to handle external links
//    - handling errors on SDK side
//      - network errors
//          - fetch chat url
//          - fetch token
//      - webView errors
//          - file picker activity not found
//    - how to store local info? Chat Url, chat token, FCM token
//    - Will name change when providing with cookieGrant?
//    - Different packages for Base, UI, FCM Messaging


//    Topics to consult:
//    1. Naming convention
//      - package name - to decide closer to the release
//      - how to name cookieGrant - RestorationGrant
//      - licence or licenceNumber?

}
