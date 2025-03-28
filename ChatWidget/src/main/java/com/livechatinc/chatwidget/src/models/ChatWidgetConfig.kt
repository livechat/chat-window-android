package com.livechatinc.chatwidget.src.models

import kotlinx.serialization.Serializable

@Serializable
data class ChatWidgetConfig @JvmOverloads constructor(
    val license: String,
    val group: String = "0",
    val visitorName: String? = null,
    val visitorEmail: String? = null,
    val customParameters: Map<String, String>?,
    val clientId: String? = null,
    val licenceId: String? = null,
    val identityGrant: IdentityGrant? = null,
) {
    val isCIPEnabled: Boolean
        get() = clientId != null && licenceId != null

    fun copyWith(
        license: String? = this.license,
        group: String? = this.group,
        visitorName: String? = this.visitorName,
        visitorEmail: String? = this.visitorEmail,
        customParameters: Map<String, String>? = this.customParameters,
        clientId: String? = this.clientId,
        licenceId: String? = this.licenceId,
        identityGrant: IdentityGrant? = this.identityGrant,
    ): ChatWidgetConfig {
        return ChatWidgetConfig(
            license = license ?: this.license,
            group = group ?: this.group,
            visitorName = visitorName ?: this.visitorName,
            visitorEmail = visitorEmail ?: this.visitorEmail,
            customParameters = customParameters ?: this.customParameters,
            clientId = clientId ?: this.clientId,
            licenceId = licenceId ?: this.licenceId,
            identityGrant = identityGrant ?: this.identityGrant,
        )
    }
}
