package by.chapailo.update.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UpdateMetadata(
    val author: String,
    @SerialName("app_version") val appVersion: String,
    @SerialName("whats_new") val whatsNew: String,
    @SerialName("download_url") val downloadUrl: String
)
