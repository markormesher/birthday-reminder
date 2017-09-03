package uk.co.markormesher.birthdayreminder.data

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

const val DB_VERSION = 1
const val DB_NAME = "birthday_db"

class DbHelper private constructor(context: Context): SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {

	companion object {
		private var instance: DbHelper? = null

		fun getHelper(context: Context): DbHelper {
			if (instance == null) {
				instance = DbHelper(context)
			}
			return instance!!
		}
	}

	override fun onCreate(db: SQLiteDatabase?) = onUpgrade(db, 0, DB_VERSION)

	/**
	 * Applies sequential updates from $old to $new by applying update for version $old,
	 * then calling itself again to apply update $old + 1 until $old == $new.
	 */
	override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
		if (oldVersion == newVersion) {
			// updates have finished
			return
		}

		// sanity checks
		if (db == null) {
			throw IllegalArgumentException("Cannot operate on null DB")
		}
		if (oldVersion > newVersion) {
			throw IllegalArgumentException("Old version ($oldVersion) cannot be greater than new version ($newVersion)")
		}
		if (newVersion > DB_VERSION) {
			throw IllegalArgumentException("New version ($newVersion) cannot be greater than $DB_VERSION")
		}

		when (oldVersion) {
			0 -> {
				db.execSQL("CREATE TABLE birthdays (" +
						"id TEXT PRIMARY KEY," +
						"name TEXT," +
						"date INTEGER," +
						"month INTEGER," +
						"year INTEGER" +
						");")
			}
		}

		if (oldVersion < newVersion) {
			onUpgrade(db, oldVersion + 1, newVersion)
		}
	}

	fun updateBirthdays(birthdays: List<Birthday>) {
		writableDatabase.delete("birthdays", null, null)
		birthdays.map { it.toContentValues() }.forEach { writableDatabase.insert("birthdays", null, it) }
	}

	fun getBirthdays(): List<Birthday> {
		val birthdays = ArrayList<Birthday>()
		val cursor = readableDatabase.query("birthdays", null, null, null, null, null, null)
		if (cursor.moveToFirst()) {
			do {
				birthdays.add(Birthday(cursor))
			} while (cursor.moveToNext())
		}
		cursor.close()
		return birthdays
	}

}
