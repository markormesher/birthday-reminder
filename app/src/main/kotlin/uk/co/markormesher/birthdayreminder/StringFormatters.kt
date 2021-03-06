package uk.co.markormesher.birthdayreminder

import android.content.Context
import org.joda.time.DateTime
import org.joda.time.Days

fun Context.formatFutureDate(date: DateTime, capitalise: Boolean = true): String {
    val now = DateTime.now()
    val output = when (val daysUntilNextOccurrence = Days.daysBetween(now, date).days) {
        0 -> getString(R.string.future_date_0_days)
        1 -> getString(R.string.future_date_1_day)
        in 2..7 -> getString(R.string.future_date_n_small_days, daysUntilNextOccurrence)
        else -> getString(R.string.future_date_n_many_days, date.toString("dd MMM yyyy"))
    }
    return if (capitalise) {
        output.substring(0, 1).toUpperCase() + output.substring(1)
    } else {
        output
    }
}

fun Int.formatWithOrdinal(): String {
    val suffixes = arrayOf("th", "st", "nd", "rd", "th", "th", "th", "th", "th", "th")
    return when (this % 100) {
        11, 12, 13 -> this.toString() + "th"
        else -> this.toString() + suffixes[this % 10]
    }
}