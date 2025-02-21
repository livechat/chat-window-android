package com.livechatinc.chatwidget.src.data.core

import com.livechatinc.chatwidget.src.common.BuildInfo
import com.livechatinc.chatwidget.src.data.domain.NetworkClient
import com.livechatinc.chatwidget.src.models.CustomerTokenRequest
import com.livechatinc.chatwidget.src.models.ChatWidgetUrls
import com.livechatinc.chatwidget.src.models.CookieGrant
import com.livechatinc.chatwidget.src.models.CustomerToken
import com.livechatinc.chatwidget.src.models.CustomerTokenResponse
import com.livechatinc.chatwidget.src.models.toChatWidgetToken
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.cookie
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.Cookie
import io.ktor.http.setCookie
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json

class KtorNetworkClient(val json: Json, val buildInfo: BuildInfo) : NetworkClient {
    private val client = HttpClient(CIO) {
        install(Logging) {
            logger = Logger.ANDROID
            level = LogLevel.ALL
        }
        install(ContentNegotiation) {
            json(json)

        }
    }

    override suspend fun fetchChatUrl(): String {
        return withContext(Dispatchers.IO) {
            val urls: ChatWidgetUrls = client.get("${buildInfo.apiHost}${buildInfo.apiPath}").body()

            return@withContext urls.chatUrl!!
        }
    }

    override suspend fun getVisitorToken(
        license: String,
        licenceId: String,
        clientId: String,
        lcCookies: List<Cookie>?,
    ): CustomerTokenResponse {
        return withContext(Dispatchers.IO) {

            val response = client.post(buildInfo.accountsApiUrl, block = {
                headers {
                    append("Content-Type", "application/json")
                }

                lcCookies?.forEach { cookie ->
                    this.cookie(
                        name = cookie.name,
                        value = cookie.value,
                        //TODO: deal with maxAge null assertion
                        maxAge = cookie.maxAge!!,
                        domain = cookie.domain,
                        path = cookie.path,
                        expires = cookie.expires,
                        secure = cookie.secure,
                        httpOnly = cookie.httpOnly,
                        extensions = cookie.extensions
                    )
                }
                setBody(
                    CustomerTokenRequest(
                        grantType = "cookie",
                        responseType = "token",
                        clientId = clientId,
                        licenceId = licenceId
                    )
                )
            })

            val customerToken: CustomerToken = response.body()
            val widgetToken = customerToken.toChatWidgetToken(
                license
            )

            val setCookies = response.setCookie()
            val lcCid = setCookies.firstOrNull { it.name == "__lc_cid" }
            val lcCst = setCookies.firstOrNull { it.name == "__lc_cst" }

            return@withContext CustomerTokenResponse(
                token = widgetToken,
                cookieGrant = CookieGrant(
                    cookies = listOfNotNull(
                        lcCid,
                        lcCst
                    )
                )
            );
        }
    }
}
