package cz.budikpet.mytimetableview

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import cz.budikpet.mytimetableview.data.TimetableEvent
import cz.budikpet.mytimetableview.util.SharedPreferencesKeys
import cz.budikpet.mytimetableview.util.edit
import org.joda.time.DateTime
import org.joda.time.DateTimeConstants


class MainActivity : AppCompatActivity(), WeekViewFragment.OnListFragmentInteractionListener {

    private lateinit var weekViewFragment: WeekViewFragment

    private lateinit var sharedPreferences: SharedPreferences

    private val mondayDate = DateTime().withDayOfWeek(DateTimeConstants.MONDAY)
        .withTime(0, 0, 0, 0)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sharedPreferences = getSharedPreferences("Pref", Context.MODE_PRIVATE)
        setPreferences()

        if (savedInstanceState == null) {
            //        "${item.hourOfDay()}:${item.minuteOfHour()}"
            weekViewFragment = WeekViewFragment.newInstance(7, mondayDate)

            supportFragmentManager.beginTransaction()
                .add(R.id.weekViewFragment, weekViewFragment)
                .commitNow()
        } else {
            weekViewFragment = supportFragmentManager.findFragmentById(R.id.weekViewFragment) as WeekViewFragment
        }
    }

    private fun setPreferences() {
        // Prepare needed preferences for the WeekViewFragment

        val lessonsStartTime = DateTime().withTime(7, 30, 0, 0).millisOfDay

        sharedPreferences.edit {
            it.putInt(SharedPreferencesKeys.NUM_OF_LESSONS.toString(), 8)
            it.putInt(SharedPreferencesKeys.LESSONS_START_TIME.toString(), lessonsStartTime)
            it.putInt(SharedPreferencesKeys.LENGTH_OF_BREAK.toString(), 15)
            it.putInt(SharedPreferencesKeys.LENGTH_OF_LESSON.toString(), 90)
        }

    }

    override fun onAddEventClicked(startTime: DateTime, endTime: DateTime) {
        Log.i(
            "MY_",
            "Add event clicked: ${startTime.toString("dd.MM")}<${startTime.toString("HH:mm")} – ${endTime.toString("HH:mm")}>"
        )
    }

    override fun onEventClicked(event: TimetableEvent) {
        Log.i("MY_", "Event clicked: $event")
    }
}