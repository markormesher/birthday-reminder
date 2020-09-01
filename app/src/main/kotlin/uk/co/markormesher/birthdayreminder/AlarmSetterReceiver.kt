package uk.co.markormesher.birthdayreminder

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import org.joda.time.DateTime

class AlarmSetterReceiver : BroadcastReceiver() {

    @SuppressLint("UnsafeProtectedBroadcastReceiver") // we don't care what the action is
    override fun onReceive(context: Context, intent: Intent) {
        val reminderIntent = PendingIntent.getBroadcast(context, 0, Intent(context, ReminderReceiver::class.java), PendingIntent.FLAG_UPDATE_CURRENT)
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        var firstAlarm = DateTime.now().withHourOfDay(8).withMinuteOfHour(0)
        if (firstAlarm.isBeforeNow) {
            firstAlarm = firstAlarm.plusDays(1)
        }
        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, firstAlarm.millis, AlarmManager.INTERVAL_DAY, reminderIntent)
    }

}