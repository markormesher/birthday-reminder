package uk.co.markormesher.birthdayreminder

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import org.joda.time.DateTime
import org.joda.time.Days
import org.joda.time.Years
import uk.co.markormesher.birthdayreminder.data.Birthday
import uk.co.markormesher.birthdayreminder.data.DbHelper
import kotlin.concurrent.thread

class ReminderReceiver : BroadcastReceiver() {

    companion object {
        private val REMINDER_THRESHOLDS = arrayOf(0, 1, 2, 7, 14)
        private const val NOTIFICATION_CHANNEL_ID = "channel_01"
        private const val NOTIFICATION_CHANNEL_NAME = "Birthday Reminders"
    }

    private var notificationCounter = 141500

    override fun onReceive(context: Context, intent: Intent) {
        thread {
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            prepareForNotifications(notificationManager)

            val now = DateTime.now()
            val birthdays = DbHelper.getHelper(context).getBirthdays()
            birthdays.forEach { birthday ->
                val daysUntilNextOccurrence = Days.daysBetween(now, birthday.nextOccurrence()).days
                if (REMINDER_THRESHOLDS.contains(daysUntilNextOccurrence)) {
                    createNotification(context, notificationManager, birthday)
                }
            }
        }
    }

    private fun prepareForNotifications(notificationManager: NotificationManager) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(NOTIFICATION_CHANNEL_ID, NOTIFICATION_CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }

    private fun createNotification(context: Context, notificationManager: NotificationManager, birthday: Birthday) {
        val now = DateTime.now()
        val daysUntilNextOccurrence = Days.daysBetween(now, birthday.nextOccurrence()).days
        var age = 0
        if (birthday.year > 0) {
            age = Years.yearsBetween(birthday.asDate(), birthday.nextOccurrence()).years
        }

        val notificationBuilder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Notification.Builder(context, NOTIFICATION_CHANNEL_ID)
        } else {
            @Suppress("DEPRECATION")
            Notification.Builder(context)
        }

        val title = "${birthday.name}'s birthday is ${context.formatFutureDate(birthday.nextOccurrence(), false)}"

        val subtitle = if (age > 0) {
            if (daysUntilNextOccurrence == 0) {
                "They are $age"
            } else {
                "They will be $age"
            }
        } else {
            ""
        }

        val notification = notificationBuilder
                .setContentTitle(title)
                .setContentText(subtitle)
                .setSmallIcon(R.mipmap.ic_launcher)
                .build()

        notificationManager.notify(++notificationCounter, notification)
    }
}