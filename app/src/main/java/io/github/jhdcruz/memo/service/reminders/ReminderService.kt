package io.github.jhdcruz.memo.service.reminders

import android.app.AlarmManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import dagger.hilt.android.AndroidEntryPoint

/**
 * Service that runs reminder sync every 30m
 * to check for tasks with due dates.
 */
@AndroidEntryPoint
class ReminderService : Service() {
    private lateinit var alarmManager: AlarmManager

    override fun onBind(intent: Intent): IBinder? = null

    override fun onStartCommand(
        intent: Intent?,
        flags: Int,
        startId: Int,
    ): Int {
        val every30m = 30 * 60 * 1000L
        val reminderSyncIntent =
            Intent(this, ReminderSyncService::class.java)

        try {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                System.currentTimeMillis() + every30m,
                PendingIntent.getService(
                    this,
                    0,
                    reminderSyncIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT,
                ),
            )
        } catch (e: SecurityException) {
            Log.e("ReminderService", "Permission denied for setting alarm", e)
        }

        return START_STICKY
    }

    override fun onDestroy() {
        stopForeground(STOP_FOREGROUND_DETACH)
        super.onDestroy()
    }
}
