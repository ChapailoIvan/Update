package by.chapailo.update

import android.content.Context
import android.content.pm.PackageManager.PackageInfoFlags
import android.os.Build
import by.chapailo.update.model.AppMetadata
import by.chapailo.update.model.UpdateMetadata
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import kotlin.jvm.Throws

typealias AppMetadataConverter = (ByteArray) -> AppMetadata
typealias UpdateMetadataConverter = (ByteArray) -> UpdateMetadata

internal class DefaultUpdateManager(
    context: Context,
    private val updateService: UpdateService = UpdateService.build(HttpClient(OkHttp))
) : UpdateManager {

    private val applicationContext: Context =
        context.applicationContext

    @OptIn(ExperimentalSerializationApi::class)
    private val appMetadataConverter: AppMetadataConverter = { bytes ->
        Json.decodeFromStream(bytes.inputStream())
    }

    @OptIn(ExperimentalSerializationApi::class)
    private val updateMetadataConverter: UpdateMetadataConverter = { bytes ->
        Json.decodeFromStream(bytes.inputStream())
    }

    @Throws(HttpRequestException::class, DeserializationException::class)
    override suspend fun checkUpdatesAvailable(
        appMetadataUrl: String,
        updateMetadataUrl: String,
    ): UpdateMetadata? {
        val appMetadata = updateService.getAppMetadata(
            url = appMetadataUrl,
            converter = appMetadataConverter,
        ) ?: return null

        val currentVersion = applicationContext.getVersion()

        if (currentVersion >= appMetadata.appVersion)
            return null

        return updateService.getUpdateMetadata(
            url = updateMetadataUrl,
            converter = updateMetadataConverter
        )
    }

    private fun Context.getVersion(): String {
        val packageInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            packageManager.getPackageInfo(packageName, PackageInfoFlags.of(0))
        else
            @Suppress("DEPRECATION") packageManager.getPackageInfo(packageName, 0)

        return packageInfo.versionName
    }

}