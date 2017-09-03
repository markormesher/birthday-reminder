package uk.co.markormesher.birthdayreminder.extensions

import android.util.Log

fun Any.log(message: String) = Log.d(this.javaClass.name, message)
