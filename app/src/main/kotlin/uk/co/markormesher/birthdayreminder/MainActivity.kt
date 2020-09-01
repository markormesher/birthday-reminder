package uk.co.markormesher.birthdayreminder

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*
import uk.co.markormesher.birthdayreminder.data.DbHelper
import uk.co.markormesher.birthdayreminder.extensions.checkPermissions
import uk.co.markormesher.birthdayreminder.extensions.requestPermissions


class MainActivity: AppCompatActivity() {

	companion object {
		private val PERMISSIONS = arrayOf(
				Manifest.permission.READ_CONTACTS
		)
	}

    private val listAdapter = BirthdayListAdapter(this)
	private var birthdaysLoaded = false

	private val scanStateUpdateReceiver = object: BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent?) {
            if (!BirthdayScannerService.scanInProgress) {
				updateList(force = true)
            }
			showAppropriateContentPane()
        }
    }

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		initView()

		sendBroadcast(Intent(this, AlarmSetterReceiver::class.java))
	}

	override fun onResume() {
		super.onResume()
		registerReceiver(scanStateUpdateReceiver, IntentFilter(Intents.SCAN_STATE_UPDATED))
		updateList()
	}

	override fun onPause() {
		super.onPause()
        unregisterReceiver(scanStateUpdateReceiver)
    }

	private fun initView() {
		setContentView(R.layout.activity_main)

		permission_request_btn.setOnClickListener { requestPermissions(PERMISSIONS) }
        first_sync_btn.setOnClickListener { startService(Intent(this, BirthdayScannerService::class.java)) }

		val layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        birthday_list.layoutManager = layoutManager
        birthday_list.addItemDecoration(DividerItemDecoration(birthday_list.context, layoutManager.orientation))
		birthday_list.adapter = listAdapter

        showAppropriateContentPane()
	}

	override fun onCreateOptionsMenu(menu: Menu?): Boolean {
		menuInflater.inflate(R.menu.action_bar, menu)
		return true
	}

	override fun onOptionsItemSelected(item: MenuItem): Boolean {
		when (item.itemId) {
			R.id.menu_rescan -> {
				startService(Intent(this, BirthdayScannerService::class.java))
				return true
			}
		}

		return super.onOptionsItemSelected(item)
	}

	override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        showAppropriateContentPane()
    }

    private fun showAppropriateContentPane() {
		when {
			!checkPermissions(PERMISSIONS) -> showSingleContentPane(permission_request_wrapper)
            BirthdayScannerService.scanInProgress -> showSingleContentPane(progress_spinner)
            listAdapter.birthdays.isEmpty() -> showSingleContentPane(no_birthdays_wrapper)
            else -> showSingleContentPane(birthday_list)
        }
    }

	private fun showSingleContentPane(pane: View) {
		arrayOf(
                permission_request_wrapper,
				no_birthdays_wrapper,
				progress_spinner,
				birthday_list
		).forEach { it.visibility = View.GONE }

		pane.visibility = View.VISIBLE
	}

	private fun updateList(force: Boolean = false) {
		if (birthdaysLoaded && !force) {
			return
        }

		showSingleContentPane(progress_spinner)

		listAdapter.birthdays.clear()
		listAdapter.birthdays.addAll(DbHelper.getHelper(this).getBirthdays())
		listAdapter.notifyDataSetChanged()

		showAppropriateContentPane()
	}

}
