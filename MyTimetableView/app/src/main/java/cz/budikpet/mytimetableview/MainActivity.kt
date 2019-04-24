package cz.budikpet.mytimetableview

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import org.joda.time.DateTime


class MainActivity : AppCompatActivity(), WeekViewFragment.OnListFragmentInteractionListener {

    private lateinit var weekViewFragment: WeekViewFragment

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sharedPreferences =  getSharedPreferences("Pref", Context.MODE_PRIVATE)
        setPreferences()

        if (savedInstanceState == null) {
            //        "${item.hourOfDay()}:${item.minuteOfHour()}"
            weekViewFragment = WeekViewFragment.newInstance(7)

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

    override fun onListFragmentInteraction() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onEmptySpaceClicked() {
        Log.i("MY_", "Empty space clicked.")
    }

    override fun onEventClicked() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}