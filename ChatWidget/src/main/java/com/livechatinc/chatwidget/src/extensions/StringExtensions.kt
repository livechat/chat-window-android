package com.livechatinc.chatwidget.src.extensions

fun String.buildChatUrl(licenceId: String): String =
    replaceParameter("group", "0")
        .replaceParameter("license", licenceId)
        .ensureHttps()

fun String.ensureHttps(): String = if (startsWith("http")) this else "https://$this"

fun String.replaceParameter(key: String, value: String): String = replace("{%$key%}", value)
