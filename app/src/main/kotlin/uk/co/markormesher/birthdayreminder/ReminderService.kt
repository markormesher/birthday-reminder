package uk.co.markormesher.birthdayreminder

import android.app.IntentService
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import org.joda.time.DateTime
import org.joda.time.Days
import org.joda.time.Years
import uk.co.markormesher.birthdayreminder.data.Birthday
import uk.co.markormesher.birthdayreminder.data.DbHelper
import kotlin.concurrent.thread

class ReminderService: IntentService("ReminderService") {

    private val notificationManager by lazy { getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager }
    private var notificationCounter = 141500
    private val reminderThresholds = arrayOf(0, 1, 2, 7, 14)

    override fun onHandleIntent(intent: Intent?) {
        thread {
            prepareForNotifications()

            val now = DateTime.now()
            val birthdays = DbHelper.getHelper(applicationContext).getBirthdays()
            birthdays.forEach { birthday ->
                val daysUntilNextOccurrence = Days.daysBetween(now, birthday.nextOccurrence()).days
                if (reminderThresholds.contains(daysUntilNextOccurrence)) {
                    createNotification(birthday)
                }
            }
        }
    }

    private fun prepareForNotifications() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel("channel_01", "Birthday Reminders", NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }

    private fun createNotification(birthday: Birthday) {
        val now = DateTime.now()
        val daysUntilNextOccurrence = Days.daysBetween(now, birthday.nextOccurrence()).days
        var age = 0
        if (birthday.year > 0) {
            age = Years.yearsBetween(birthday.asDate(), birthday.nextOccurrence()).years
        }

        val notificationBuilder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Notification.Builder(applicationContext, "channel_01")
        } else {
            @Suppress("DEPRECATION")
            Notification.Builder(applicationContext)
        }

        val title = when (daysUntilNextOccurrence) {
            0 -> "${birthday.name}'s birthday is today"
            1 -> "${birthday.name}'s birthday is tomorrow"
            else -> "${birthday.name}'s birthday is in $daysUntilNextOccurrence days"
        }

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