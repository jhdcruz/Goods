package io.github.jhdcruz.memo.service.reminders

import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import dagger.hilt.android.AndroidEntryPoint
import java.util.concurrent.TimeUnit

/**
 * Calls [ReminderWorker] to fetch tasks with due dates
 * and pass them to [ReminderSchedulerService] for scheduling.
 */
@AndroidEntryPoint
class ReminderSyncService : Service() {
    /*
     * Instantiate the sync adapter object.
     */
    override fun onCreate() {
        /*
         * Create the sync adapter as a singleton.
         * Set the sync adapter as syncable
         * Disallow parallel syncs
         */
        synchronized(reminderSyncServiceLock) {
            reminderSyncService = reminderSyncService ?: ReminderSyncService()
        }
    }

    override fun onBind(intent: Intent): IBinder? = null

    override fun onStartCommand(
        intent: Intent?,
        flags: Int,
        startId: Int,
    ): Int {
        // call worker every 30m
        val reminderWorker =
            PeriodicWorkRequestBuilder<ReminderWorker>(
                30,
                TimeUnit.MINUTES,
            ).build()

        WorkManager.getInstance(this).enqueue(reminderWorker)

        // Return START_STICKY so the service will be restarted if it is killed
        return START_STICKY
    }

    override fun onDestroy() {
        stopForeground(STOP_FOREGROUND_DETACH)
        super.onDestroy()
    }

    companion object {
        // single instance holder
        private var reminderSyncService: ReminderSyncService? = null

        // Object to use as a thread-safe lock
        private val reminderSyncServiceLock = Any()
    }
}
