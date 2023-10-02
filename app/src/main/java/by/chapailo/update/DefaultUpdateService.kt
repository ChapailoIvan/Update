package by.chapailo.update

import android.util.Log
import by.chapailo.update.model.AppMetadata
import by.chapailo.update.model.UpdateMetadata
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.url
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.readBytes
import kotlin.jvm.Throws

internal class DefaultUpdateService(
    private val httpClient: HttpClient
): UpdateService {

    @Throws(HttpRequestException::class, DeserializationException::class)
    override suspend fun getAppMetadata(
        url: String,
        converter: (ByteArray) -> AppMetadata
    ): AppMetadata? {
        Log.d(TAG, "$TAG::getAppMetadata")

        return getData(
            url = url,
            converter = converter
        )
    }

    @Throws(HttpRequestException::class, DeserializationException::class)
    override suspend fun getUpdateMetadata(
        url: String,
        converter: (ByteArray) -> UpdateMetadata
    ): UpdateMetadata? {
        Log.d(TAG, "$TAG::getUpdateMetadata")

        return getData(
            url = url,
            converter = converter,
        )
    }

    @Throws(HttpRequestException::class, DeserializationException::class)
    private suspend fun <T> getData(
        url: String,
        converter: (ByteArray) -> T
    ): T? {
        val httpResponse: HttpResponse
        val data: T

        try {
            httpResponse = httpClient.get { url(url) }
            Log.d(TAG, "$TAG::getData::httpResponse::${httpResponse.status.value}")
        } catch (throwable: Throwable) {
            Log.e(TAG, "$TAG::getData::${throwable.message}")
            throw HttpRequestException
        }

        try {
            val httpResponseBodyBytes = httpResponse.readBytes()
            data = converter(httpResponseBodyBytes)
        } catch (throwable: Throwable) {
            Log.e(TAG, "$TAG::getData::${throwable.message}")
            throw DeserializationException
        }

        return data
    }

    companion object {
        private const val TAG = "DefaultUpdateService"
    }

}