package by.chapailo.update.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AppMetadata(
    val author: String,
    @SerialName("app_version") val appVersion: String,
    @SerialName("code_version") val codeVersion: String
)
