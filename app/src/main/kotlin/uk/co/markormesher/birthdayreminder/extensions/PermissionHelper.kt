package uk.co.markormesher.birthdayreminder.extensions

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

private val PERMISSION_REQUEST_CODE = 1415

private fun shouldUseRuntimePermissions() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M

private fun hasPermission(context: Context, permission: String): Boolean {
	return ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
}

fun Activity.checkPermissions(permissions: Array<String>): Boolean {
	return if (shouldUseRuntimePermissions()) {
		permissions.all { p -> hasPermission(this, p) }
	} else {
		true
	}
}

fun Activity.requestPermissions(permissions: Array<String>) {
	ActivityCompat.requestPermissions(this, permissions, PERMISSION_REQUEST_CODE)
}
