package `in`.arunkumarsampath.jarvis.applemusic

import android.content.Intent
import android.os.IBinder
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import timber.log.Timber


open class JarvisNotificationListenerService : NotificationListenerService() {
    override fun onBind(intent: Intent): IBinder? {
        return super.onBind(intent)
    }

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        Timber.d(sbn.toString())
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification) {
        Timber.d(sbn.toString())
    }
}
