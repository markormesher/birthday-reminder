package uk.co.markormesher.birthdayreminder

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.list_item_birthday.view.*
import org.joda.time.Years
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

		val dateString = context.formatFutureDate(birthday.nextOccurrence())
		var age = 0
		if (birthday.year > 0) {
			age = Years.yearsBetween(birthday.asDate(), birthday.nextOccurrence()).years
        }

		holder.titleView.text = birthday.name
		if (age > 0) {
			holder.detailView.text = context.getString(R.string.birthday_detail_with_age, dateString, age)
        } else {
			holder.detailView.text = context.getString(R.string.birthday_detail_without_age, dateString)
        }
	}

	class BirthdayViewHolder(v: View): RecyclerView.ViewHolder(v) {
		val titleView = v.title!!
		val detailView = v.detail!!
	}

}
