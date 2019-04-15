package cz.budikpet.mytimetableview

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.constraint.ConstraintSet
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.widget.TextView
import cz.budikpet.mytimetableview.dummy.DummyContent


class MainActivity : AppCompatActivity(), WeekViewFragment.OnListFragmentInteractionListener {
    private var padding = 2

    private lateinit var weekViewFragment: WeekViewFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        padding = 2f.toDp(this)
        val column = findViewById<ConstraintLayout>(R.id.eventColumn1)

        val view0 = TextView(this)
        view0.text = "Lonely event, How are you doing?"
        view0.setBackgroundColor(Color.LTGRAY)
        view0.height = 150f.toDp(this)

        val view = TextView(this)
        view.text = "Chain one, How are you doing?"
        view.setBackgroundColor(Color.LTGRAY)
        view.height = 150f.toDp(this)

        val view2 = TextView(this)
        view2.text = "Chain two, This is second event?"
        view2.setBackgroundColor(Color.CYAN)
        view2.height = 200f.toDp(this)

        addOverlapingEvents(column, arrayOf(view, view2), arrayOf(200f.toDp(this), 150f.toDp(this)))
        addEvent(column, view0, 900f.toDp(this))


        if (savedInstanceState == null) {
            //        "${item.hourOfDay()}:${item.minuteOfHour()}"
            weekViewFragment = WeekViewFragment.newInstance(7, arrayListOf("time"))

            supportFragmentManager.beginTransaction()
                .add(R.id.weekViewFragment, weekViewFragment)
                .commit()
        } else {
            weekViewFragment = supportFragmentManager.findFragmentById(R.id.weekViewFragment) as WeekViewFragment
        }
    }

    fun addEvent(constLayout: ConstraintLayout, event: View, startsDp: Int) {
        val set = ConstraintSet()

        if (event.id == -1) {
            event.id = View.generateViewId()
        }

        constLayout.addView(event)

        set.clone(constLayout)

        set.connect(event.id, ConstraintSet.TOP, constLayout.id, ConstraintSet.TOP, startsDp)
        set.constrainWidth(event.id, 0f.toDp(this))
        set.connect(event.id, ConstraintSet.START, constLayout.id, ConstraintSet.START, padding)
        set.connect(event.id, ConstraintSet.END, constLayout.id, ConstraintSet.END, padding)

        set.applyTo(constLayout)
    }

    fun addOverlapingEvents(constLayout: ConstraintLayout, events: Array<View>, startsDp: Array<Int>) {
        val set = ConstraintSet()

        // Add events to the constLayout and get a list of ids in the process
        val eventIds = events
            .map {
                if (it.id == -1) {
                    it.id = View.generateViewId()
                }

                constLayout.addView(it)

                return@map it.id
            }

        set.clone(constLayout)

        // Set constraints
        eventIds.forEachIndexed { index, id ->
            set.constrainWidth(id, 0f.toDp(this))
            set.connect(id, ConstraintSet.TOP, constLayout.id, ConstraintSet.TOP, startsDp[index])
            set.setMargin(id, ConstraintSet.START, padding)
            set.setMargin(id, ConstraintSet.END, padding)
        }

        // Create chain
        set.createHorizontalChain(
            constLayout.id, ConstraintSet.LEFT, constLayout.id, ConstraintSet.RIGHT,
            eventIds.toIntArray(), null, ConstraintSet.CHAIN_SPREAD
        )

        set.applyTo(constLayout)

    }

    override fun onListFragmentInteraction(item: DummyContent.DummyItem?) {
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