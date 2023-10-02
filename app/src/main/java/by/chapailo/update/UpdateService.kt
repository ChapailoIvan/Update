package by.chapailo.update

import by.chapailo.update.model.AppMetadata
import by.chapailo.update.model.UpdateMetadata
import io.ktor.client.HttpClient
import kotlin.jvm.Throws

interface UpdateService {

    @Throws(HttpRequestException::class, DeserializationException::class)
    suspend fun getAppMetadata(
        url: String,
        converter: (ByteArray) -> AppMetadata
    ): AppMetadata?

    @Throws(HttpRequestException::class, DeserializationException::class)
    suspend fun getUpdateMetadata(
        url: String,
        converter: (ByteArray) -> UpdateMetadata
    ): UpdateMetadata?

    companion object Builder {
        private val mutex: Any = Any()

        @Volatile
        private var instance: UpdateService? = null

        fun build(httpClient: HttpClient): UpdateService {
            var temporaryInstance = instance

            if (temporaryInstance == null) {
                synchronized(mutex) {
                    temporaryInstance = instance

                    if (temporaryInstance == null) {
                        temporaryInstance = DefaultUpdateService(httpClient = httpClient)
                            .also { instance = it }
                    }
                }
            }
            return temporaryInstance as UpdateService
        }
    }
}