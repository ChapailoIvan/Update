package by.chapailo.update

import android.content.Context
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.os.Build.VERSION.SDK_INT

internal fun Context.checkPermission(
    permission: String,
    requiresApi: Int? = null,
    block: () -> Unit
) {
    if (requiresApi != null && SDK_INT < requiresApi)
        return

    if (checkSelfPermission(permission) == PERMISSION_GRANTED)
        block()
}