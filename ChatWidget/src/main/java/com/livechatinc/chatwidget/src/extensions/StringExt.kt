package com.livechatinc.chatwidget.src.extensions

import com.livechatinc.chatwidget.src.models.ChatWidgetConfig
import java.net.URLEncoder

internal fun String.buildChatUrl(config: ChatWidgetConfig): String {
    val parameters = listOfNotNull(
        "group" to config.group,
        config.visitorEmail?.takeIf { it.isNotBlank() }
            ?.let { "email" to URLEncoder.encode(it, "UTF-8") },
        config.visitorName?.takeIf { it.isNotBlank() }
            ?.let { "name" to URLEncoder.encode(it, "UTF-8").replace("+", "%20") },
        config.customParameters?.takeIf { it.isNotEmpty() }?.let {
            "params" to URLEncoder.encode(
                it.map { (key, value) -> "$key=$value" }.joinToString("&"),
                "UTF-8"
            )
        }
    )

    return ensureHttps().replaceParameter("group", config.group)
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
