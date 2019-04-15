package cz.budikpet.mytimetableview

import android.content.Context
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.util.TypedValue


class MainActivity : AppCompatActivity(), WeekViewFragment.OnListFragmentInteractionListener {

    private lateinit var weekViewFragment: WeekViewFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        if (savedInstanceState == null) {
            //        "${item.hourOfDay()}:${item.minuteOfHour()}"
            weekViewFragment = WeekViewFragment.newInstance(7, arrayListOf("10:00", "12:00", "14:00"))

            supportFragmentManager.beginTransaction()
                .add(R.id.weekViewFragment, weekViewFragment)
                .commitNow()
        } else {
            weekViewFragment = supportFragmentManager.findFragmentById(R.id.weekViewFragment) as WeekViewFragment
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

fun Float.toDp(context: Context): Int {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        this,
        context.resources.displayMetrics
    ).toInt()
}