package com.livechatinc.chatwidget

import android.content.Context
import com.livechatinc.chatwidget.src.listeners.FileChooserActivityNotFoundListener
import com.livechatinc.chatwidget.src.models.IdentityGrant

abstract class LiveChatInterface {

    /**
     * Stores licence number
     * Stores application scope context
     * Potentially fetches chat url
     * */
    suspend fun initialize(licence: String, context: Context) {}

    /**
     * Stores params that are used when initializing chat
     */
    abstract fun setCustomerInfo(
        name: String? = null,
        email: String? = null,
        groupId: String? = null,
        customParams: Map<String, String>? = null
    )

    /**
     * Opens full screen activity
     *
     * Limitations:
     * - no callbacks - [FileChooserActivityNotFoundListener]
     * */
    abstract fun show(context: Context)

    /**
     * Clears webView cookies
     * Clears cookieGrant, customerToken and chatWidgetToken
     * Unregisters push token
     * */
    abstract suspend fun signOutCustomer()

    /******* Embedding view mode *******/
    /**
     * Custom View
     *
     * @see [LiveChatView]
     * */
    /**
     * Callback listener for live chat view
     *
     * @see [FileChooserActivityNotFoundListener]
     * */


    /******* Advanced usage *******/

    /**
     * Reports back [IdentityGrant] to the host app
     * Needs to be called before [logInCustomer] and launching chat window
     * */
    abstract fun configureIdentityProvider(
        licenceId: String,
        clientId: String,
        onIdentityGrantChange: (IdentityGrant) -> Unit
    )

    /**
     * Fetches token, creates new one if no cookieGrant
     *
     * Required:
     * @see configureIdentityProvider
     * */
    abstract fun logInCustomer(identityGrant: IdentityGrant?)

    /******* Push Notification *******/

    /**
     * Only if not using ChatWidgetDefaultMessagingService
     * Required:
     * @see configureIdentityProvider
     * */
    suspend fun registerPushToken(token: String) {}

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
    suspend fun unreadMessageCount(token: String): Int {
        return 0
    }

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
//    - Customizable error view in Activity component
//    - Consider configuration changes - android:configChanges="orientation|screenSize"
//    - LiveChatActivity should follow app theme
//    -


//    Topics to consult:
//    1. Naming convention
//      - package name - to decide closer to the release
//      - how to name cookieGrant - RestorationGrant
//      - licence or licenceNumber?

}
