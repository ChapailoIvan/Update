package by.chapailo.update

import android.Manifest
import android.annotation.SuppressLint
import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.DrawableRes
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.net.toUri
import kotlin.jvm.Throws

class UpdateNotificationManager(
    private val applicationName: String,
    private val applicationContext: Context,
    private val notificationId: Int,
    private val channelId: String = DEFAULT_CHANNEL_ID,
    private val channelName: String = DEFAULT_CHANNEL_NAME,
    private val channelDescription: String = DEFAULT_CHANNEL_DESCRIPTION,
    private val channelImportance: Int = NotificationManager.IMPORTANCE_DEFAULT,
    @DrawableRes
    private val notificationSmallIcon: Int = R.drawable.ic_update,
    private val notificationVisibility: Int = NotificationCompat.VISIBILITY_PUBLIC,
    private val notificationAutoCancel: Boolean = true,
    private val notificationTitle: String = "%s update is available",
    private val notificationContent: String = "New version %s of %s is here!"
) {

    companion object {
        const val DEFAULT_CHANNEL_ID = "UPDATE_NOTIFICATION_CHANNEL"
        const val DEFAULT_CHANNEL_NAME = "Application updates channel"
        const val DEFAULT_CHANNEL_DESCRIPTION =
            "Channel is used to display notification about newest versions of application."
    }

    fun createUpdateNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val updateNotificationChannel = NotificationChannel(
                channelId, channelName, channelImportance
            ).apply { description = channelDescription }

            val notificationManager =
                applicationContext.getSystemService(Application.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(updateNotificationChannel)
        }
    }

    @Throws(IllegalStateException::class)
    @SuppressLint("MissingPermission")
    fun postUpdateNotification(
        updateUrl: String,
        updateVersion: String
    ) {
        val intent = Intent(Intent.ACTION_VIEW, updateUrl.toUri())
            .apply { flags = Intent.FLAG_ACTIVITY_NEW_TASK }
        val pendingIntent = PendingIntent.getActivity(
            applicationContext, 0, intent, PendingIntent.FLAG_IMMUTABLE
        )

        val notificationBuilder = NotificationCompat.Builder(applicationContext, channelId)
            .setSmallIcon(notificationSmallIcon)
            .setVisibility(notificationVisibility)
            .setAutoCancel(notificationAutoCancel)
            .setContentIntent(pendingIntent)
            .setContentTitle(notificationTitle.format(applicationName))
            .setContentText(notificationContent.format(updateVersion, applicationName))

        applicationContext.checkPermission(
            Manifest.permission.POST_NOTIFICATIONS,
            Build.VERSION_CODES.TIRAMISU
        ) {
            NotificationManagerCompat.from(applicationContext)
                .notify(notificationId, notificationBuilder.build())
        }
    }

}