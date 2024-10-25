package com.livechatinc.chatwidget.src.extensions

import com.livechatinc.chatwidget.src.models.ChatWidgetConfig
import java.net.URLEncoder

fun String.buildChatUrl(config: ChatWidgetConfig): String {
    val joinedCustomParameters =
        config.customParameters.map { (key, value) -> "$key=$value" }.joinToString { "&$it" }

    return ensureHttps().replaceParameter("group", config.group)
        .replaceParameter("license", config.license)
        .addQueryParameter("email", URLEncoder.encode(config.visitorEmail, "UTF-8"))
        .addQueryParameter(
            "name",
            URLEncoder.encode(config.visitorName, "UTF-8").replace("+", "%20")
        )
        .addQueryParameter("params", URLEncoder.encode(joinedCustomParameters, "UTF-8"))
}

fun String.ensureHttps(): String = if (startsWith("http")) this else "https://$this"

fun String.replaceParameter(key: String, value: String): String = replace("{%$key%}", value)

fun String.addQueryParameter(key: String, value: String?): String =
    if (value?.isNotBlank() == true) "$this&$key=$value" else this
