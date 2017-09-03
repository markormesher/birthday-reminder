package uk.co.markormesher.birthdayreminder

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.list_item_birthday.view.*
import uk.co.markormesher.birthdayreminder.data.Birthday

class BirthdayListAdapter(private val context: Context): RecyclerView.Adapter<BirthdayListAdapter.BirthdayViewHolder>() {

	private val layoutInflater by lazy { LayoutInflater.from(context)!! }

	val birthdays = ArrayList<Birthday>()

	override fun getItemCount() = birthdays.size

	override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): BirthdayListAdapter.BirthdayViewHolder {
		return BirthdayViewHolder(layoutInflater.inflate(R.layout.list_item_birthday, parent, false))
	}

	override fun onBindViewHolder(holder: BirthdayListAdapter.BirthdayViewHolder, position: Int) {
		val birthday = birthdays[position]
		holder.nameView.text = birthday.name
		holder.dateView.text = "${birthday.year}-${birthday.month}-${birthday.date}"
	}

	class BirthdayViewHolder(v: View): RecyclerView.ViewHolder(v) {
		val rootView = v
		val nameView = v.name!!
		val dateView = v.date!!
	}

}
