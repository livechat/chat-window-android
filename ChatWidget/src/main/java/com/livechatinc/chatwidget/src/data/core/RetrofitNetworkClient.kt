package com.livechatinc.chatwidget.src.data.core

import com.livechatinc.chatwidget.src.common.BuildInfo
import com.livechatinc.chatwidget.src.data.domain.NetworkClient
import com.livechatinc.chatwidget.src.models.ChatWidgetUrls
import com.livechatinc.chatwidget.src.models.IdentityGrant
import com.livechatinc.chatwidget.src.models.CustomerTokenResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path


internal class RetrofitNetworkClient(json: Json, private val buildInfo: BuildInfo) : NetworkClient {
    private val service: ChatWidgetService

    init {
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BASIC

        val client = OkHttpClient()
            .newBuilder()
            .addInterceptor(interceptor)
            .build()

        service = Retrofit.Builder().baseUrl(buildInfo.mobileConfigHost).client(client)
            .addConverterFactory(json.asConverterFactory("application/json; charset=UTF8".toMediaType()))
            .build().create(ChatWidgetService::class.java)
    }


    override suspend fun fetchChatUrl(): String {
        return withContext(Dispatchers.IO) {
            val urls: ChatWidgetUrls = service.chatUrl(buildInfo.mobileConfigPath)

            return@withContext urls.chatUrl!!
        }
    }

    override suspend fun getCustomerToken(
        license: String,
        licenseId: String,
        clientId: String,
        identityGrant: IdentityGrant?
    ): Result<CustomerTokenResponse> {
        TODO("Not yet implemented")
    }
}

internal interface ChatWidgetService {
    @GET("{path}")
    suspend fun chatUrl(@Path("path", encoded = true) path: String): ChatWidgetUrls
}
