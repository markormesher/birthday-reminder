package uk.co.markormesher.birthdayreminder

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import java.util.*

class AlarmSetterReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val reminderIntent = PendingIntent.getService(context, 0, Intent(context, ReminderService::class.java), 0)
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val calendar = with(Calendar.getInstance()) {
            set(Calendar.DAY_OF_YEAR, 0)
            set(Calendar.HOUR_OF_DAY, 8)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            return@with this
        }
        alarmManager.cancel(reminderIntent)
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, AlarmManager.INTERVAL_DAY, reminderIntent)
    }

}