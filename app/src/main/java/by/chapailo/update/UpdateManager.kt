package by.chapailo.update

import android.content.Context
import by.chapailo.update.model.UpdateMetadata

interface UpdateManager {

    suspend fun checkUpdatesAvailable(
        appMetadataUrl: String,
        updateMetadataUrl: String
    ): UpdateMetadata?

    companion object Builder {
        private val mutex: Any = Any()

        @Volatile
        private var instance: UpdateManager? = null

        fun build(
            context: Context,
            updateService: UpdateService
        ): UpdateManager {
            var temporaryInstance = instance

            if (temporaryInstance == null) {
                synchronized(mutex) {
                    temporaryInstance = instance

                    if (temporaryInstance == null) {
                        temporaryInstance = DefaultUpdateManager(
                            context = context,
                            updateService = updateService
                        ).also { instance = it }
                    }
                }
            }
            return temporaryInstance as UpdateManager
        }
    }

}