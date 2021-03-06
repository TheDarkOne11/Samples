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


class MainActivity : AppCompatActivity(), MultidayViewFragment.OnListFragmentInteractionListener {

    private lateinit var multidayViewFragment: MultidayViewFragment

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sharedPreferences = getSharedPreferences("Pref", Context.MODE_PRIVATE)
        setPreferences()

        if (savedInstanceState == null) {
            multidayViewFragment = MultidayViewFragment.newInstance(7, DateTime())

            supportFragmentManager.beginTransaction()
                .add(R.id.multidayViewFragment, multidayViewFragment)
                .commitNow()
        } else {
            multidayViewFragment =
                supportFragmentManager.findFragmentById(R.id.multidayViewFragment) as MultidayViewFragment
        }
    }

    private fun setPreferences() {
        // Prepare needed preferences for the MultidayViewFragment

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