package com.livechatinc.chatwidget.src.utils.extensions

import com.livechatinc.chatwidget.BuildConfig
import com.livechatinc.chatwidget.src.domain.models.LiveChatConfig
import java.net.URLEncoder

internal fun String.buildChatUrl(config: LiveChatConfig): String {
    val parameters = listOfNotNull(
        config.customerInfo?.email?.takeIf { it.isNotBlank() }
            ?.let { "email" to URLEncoder.encode(it, "UTF-8") },
        config.customerInfo?.name?.takeIf { it.isNotBlank() }
            ?.let { "name" to URLEncoder.encode(it, "UTF-8").replace("+", "%20") },
        config.customerInfo?.customParams?.takeIf { it.isNotEmpty() }?.let {
            "params" to URLEncoder.encode(
                it.map { (key, value) -> "$key=$value" }.joinToString("&"),
                "UTF-8"
            )
        },
        "wrapper_version" to URLEncoder.encode(BuildConfig.VERSION_NAME, "UTF-8")
    )

    return ensureHttps().replaceParameter("group", config.groupId)
        .replaceParameter("license", config.license)
        .let { url ->
            parameters.fold(url) { acc, (key, value) ->
                acc.addQueryParameter(key, value)
            }
        }
}

private fun String.ensureHttps(): String = if (startsWith("http")) this else "https://$this"

private fun String.replaceParameter(key: String, value: String): String = replace("{%$key%}", value)

private fun String.addQueryParameter(key: String, value: String?): String =
    if (value?.isNotBlank() == true) "$this&$key=$value" else this
