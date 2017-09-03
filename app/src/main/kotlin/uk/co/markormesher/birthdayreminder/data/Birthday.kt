package uk.co.markormesher.birthdayreminder.data

import android.content.ContentValues
import android.database.Cursor

data class Birthday(val id: String, val name: String, val date: Int, val month: Int, val year: Int = 0) {

	constructor(cursor: Cursor): this(
			cursor.getString(cursor.getColumnIndexOrThrow("id")),
			cursor.getString(cursor.getColumnIndexOrThrow("name")),
			cursor.getInt(cursor.getColumnIndexOrThrow("date")),
			cursor.getInt(cursor.getColumnIndexOrThrow("month")),
			cursor.getInt(cursor.getColumnIndexOrThrow("year"))
	)

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
