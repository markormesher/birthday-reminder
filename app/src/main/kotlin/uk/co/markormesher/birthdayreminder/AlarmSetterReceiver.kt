package uk.co.markormesher.birthdayreminder

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import org.joda.time.DateTime

class AlarmSetterReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val reminderIntent = PendingIntent.getService(context, 0, Intent(context, ReminderService::class.java), 0)
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        var firstAlarm = DateTime.now().withHourOfDay(8).withMinuteOfHour(0)
        if (firstAlarm.isBeforeNow) {
            firstAlarm = firstAlarm.plusDays(1)
        }
        alarmManager.cancel(reminderIntent)
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, firstAlarm.millis, AlarmManager.INTERVAL_DAY, reminderIntent)
    }

}