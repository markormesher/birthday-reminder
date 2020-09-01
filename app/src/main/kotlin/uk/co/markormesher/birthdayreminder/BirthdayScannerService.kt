package uk.co.markormesher.birthdayreminder

import android.app.IntentService
import android.content.Intent
import android.provider.ContactsContract
import uk.co.markormesher.birthdayreminder.data.Birthday
import uk.co.markormesher.birthdayreminder.data.DbHelper
import kotlin.concurrent.thread

class BirthdayScannerService: IntentService("BirthdayScannerService") {

	companion object {
	    var scanInProgress = false
	}

	override fun onHandleIntent(intent: Intent?) {
		thread { updateBirthdays() }
	}

	private fun broadcastStateUpdate() {
		sendBroadcast(Intent(Intents.SCAN_STATE_UPDATED))
	}

	private fun updateBirthdays() {
		scanInProgress = true
		broadcastStateUpdate()

		val birthdays = getBirthdays()
        DbHelper.getHelper(applicationContext).updateBirthdays(birthdays)

		scanInProgress = false
		broadcastStateUpdate()
    }

	private fun getBirthdays(): List<Birthday> {
		val idKey = ContactsContract.CommonDataKinds.Event.CONTACT_ID
		val displayNameKey = ContactsContract.Contacts.DISPLAY_NAME_PRIMARY
		val startDateKey = ContactsContract.CommonDataKinds.Event.START_DATE

		val uri = ContactsContract.Data.CONTENT_URI
		val projection = arrayOf(idKey, displayNameKey, startDateKey)
		val where = "${ContactsContract.Contacts.Data.MIMETYPE} = '${ContactsContract.CommonDataKinds.Event.CONTENT_ITEM_TYPE}' AND " +
				"${ContactsContract.CommonDataKinds.Event.TYPE} = ${ContactsContract.CommonDataKinds.Event.TYPE_BIRTHDAY}"

		val cursor = application.contentResolver.query(uri, projection, where, null, null)
		val birthdays = ArrayList<Birthday>()
		if (cursor?.moveToFirst() == true) {
			val idIndex = cursor.getColumnIndexOrThrow(idKey)
			val nameIndex = cursor.getColumnIndexOrThrow(displayNameKey)
			val birthdayIndex = cursor.getColumnIndexOrThrow(startDateKey)

			do {
				val id = cursor.getString(idIndex)
				val name = cursor.getString(nameIndex)
				val birthday = cursor.getString(birthdayIndex)
				val (year, month, date) = birthday.replace("--", "0-").split("-").map { it.toInt() }

				birthdays.add(Birthday(id, name, date, month, year))
			} while (cursor.moveToNext())
		}
		cursor?.close()
		return birthdays
	}

}
