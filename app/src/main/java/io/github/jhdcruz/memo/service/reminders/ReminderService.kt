package io.github.jhdcruz.memo.service.reminders

import android.app.AlarmManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.os.SystemClock
import android.util.Log
import dagger.hilt.android.AndroidEntryPoint

/**
 * Service that runs reminder sync every 30m
 * to check for tasks with due dates.
 */
@AndroidEntryPoint
class ReminderService : Service() {
    private lateinit var alarmManager: AlarmManager

    override fun onCreate() {
        super.onCreate()
        alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
    }

    override fun onBind(intent: Intent): IBinder? = null

    override fun onStartCommand(
        intent: Intent?,
        flags: Int,
        startId: Int,
    ): Int {
        val everyHour = 60 * 60 * 1000
        val reminderSyncIntent =
            Intent(this, ReminderSyncService::class.java)

        try {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                System.currentTimeMillis() + everyHour,
                PendingIntent.getService(
                    this,
                    6,
                    reminderSyncIntent,
                    PendingIntent.FLAG_IMMUTABLE,
                ),
            )

            Log.i("ReminderService", "Scheduled next sync in 1 hour")
        } catch (e: SecurityException) {
            Log.e("ReminderService", "Permission denied for setting alarm", e)
        }

        return START_STICKY
    }

    override fun onDestroy() {
        stopForeground(STOP_FOREGROUND_DETACH)
        super.onDestroy()
    }

    override fun onTaskRemoved(rootIntent: Intent) {
        Log.i("ReminderService", "Restarting ReminderService")

        val restartServiceIntent =
            Intent(applicationContext, ReminderService::class.java).also {
                it.setPackage(packageName)
            }

        val restartServicePendingIntent: PendingIntent =
            PendingIntent.getService(
                this,
                1,
                restartServiceIntent,
                PendingIntent.FLAG_IMMUTABLE,
            )
        applicationContext.getSystemService(Context.ALARM_SERVICE)

        val alarmService: AlarmManager =
            applicationContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmService.set(
            AlarmManager.ELAPSED_REALTIME,
            SystemClock.elapsedRealtime() + 1000,
            restartServicePendingIntent,
        )
    }
}
