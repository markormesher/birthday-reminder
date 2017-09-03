package uk.co.markormesher.birthdayreminder.data

import android.content.ContentValues
import android.database.Cursor
import org.joda.time.DateTime

data class Birthday(val id: String, val name: String, val date: Int, val month: Int, val year: Int = 0) {

	constructor(cursor: Cursor): this(
			cursor.getString(cursor.getColumnIndexOrThrow("id")),
			cursor.getString(cursor.getColumnIndexOrThrow("name")),
			cursor.getInt(cursor.getColumnIndexOrThrow("date")),
			cursor.getInt(cursor.getColumnIndexOrThrow("month")),
			cursor.getInt(cursor.getColumnIndexOrThrow("year"))
	)

	fun nextOccurrence(): DateTime {
		val now = DateTime.now()
		var next = DateTime(now.year, month, date, 23, 59, 59)
		if (next.isBeforeNow) {
			next = next.withYear(now.year + 1)
        }
		return next
	}

	fun asDate(): DateTime {
		return DateTime(year, month, date, 23, 59, 59)
	}

	fun toContentValues(): ContentValues {
		with(ContentValues()) {
			put("id", id)
			put("name", name)
			put("date", date)
			put("month", month)
			put("year", year)
			return this
		}
	}

}
