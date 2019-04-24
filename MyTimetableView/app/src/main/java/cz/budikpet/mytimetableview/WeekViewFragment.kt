package cz.budikpet.mytimetableview

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.constraint.ConstraintSet
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView

import kotlinx.android.synthetic.main.fragment_weekview_list.view.*
import kotlinx.android.synthetic.main.week_row.view.*
import org.joda.time.DateTime

/**
 * A fragment representing a list of Items.
 * Activities containing this fragment MUST implement the
 * [WeekViewFragment.OnListFragmentInteractionListener] interface.
 */
class WeekViewFragment : Fragment() {
    private var columnCount = MAX_COLUMN

    private var listener: OnListFragmentInteractionListener? = null
    private lateinit var onEmptySpaceClickListener: View.OnClickListener

    private lateinit var eventsColumns: LinearLayout
    private var padding = 2

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        padding = 2f.toDp(context!!)

        sharedPreferences = context!!.getSharedPreferences("Pref", Context.MODE_PRIVATE)

        arguments?.let {
            columnCount = it.getInt(ARG_COLUMN_COUNT)
        }

        onEmptySpaceClickListener = View.OnClickListener {
            listener?.onEmptySpaceClicked()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val layout = inflater.inflate(R.layout.fragment_weekview_list, container, false)
        val listLayout = layout.rowsList
        eventsColumns = layout.events_columns

        val numOfLessons = sharedPreferences.getInt(SharedPreferencesKeys.NUM_OF_LESSONS.toString(), 0)
        val lessonsStartTime = sharedPreferences.getInt(SharedPreferencesKeys.LESSONS_START_TIME.toString(), 0)
        val breakLength = sharedPreferences.getInt(SharedPreferencesKeys.LENGTH_OF_BREAK.toString(), 0)
        val lessonLength = sharedPreferences.getInt(SharedPreferencesKeys.LENGTH_OF_LESSON.toString(), 0)

        val rowTime = DateTime().withMillisOfDay(lessonsStartTime)

        for(i in 0 until numOfLessons) {
            val rowView = getTimeRow(inflater)  // TODO: Create copies of this view
            rowView.timeTextView.text = rowTime.plusMinutes(i*(lessonLength + breakLength)).toString("HH:mm")

            listLayout.addView(rowView)
        }

        createDynamicEvents()

        return layout
    }

    private fun getTimeRow(inflater: LayoutInflater): View {
        val rowView = inflater.inflate(R.layout.week_row, null, false)

        val layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        layoutParams.bottomMargin = 10f.toDp(context!!)
        layoutParams.height = 100f.toDp(context!!)
        rowView.layoutParams = layoutParams

        for (i in 1..MAX_COLUMN) {
            Log.i("MY_test", "$i")

            val spaceView = rowView
                .findViewById<View>(resources.getIdentifier("space$i", "id", context!!.packageName))

            if (i <= columnCount) {
                spaceView.setOnClickListener(onEmptySpaceClickListener)
                spaceView.visibility = View.INVISIBLE
            } else {
                spaceView.visibility = View.GONE
            }

        }

        return rowView
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnListFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException("$context must implement OnListFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    interface OnListFragmentInteractionListener {
        fun onListFragmentInteraction()

        fun onEmptySpaceClicked()

        fun onEventClicked()
    }

    // MARK: Add dynamic events

    private fun createDynamicEvents() {
        val column = eventsColumns.findViewById<ConstraintLayout>(R.id.eventColumn1)

        val view0 = TextView(context!!)
        view0.text = "Lonely event, How are you doing?"
        view0.setBackgroundColor(Color.RED)
        view0.height = 150f.toDp(context!!)

        val view = TextView(context!!)
        view.text = "Chain one, How are you doing?"
        view.setBackgroundColor(Color.LTGRAY)
        view.height = 150f.toDp(context!!)

        val view2 = TextView(context!!)
        view2.text = "Chain two, This is second event?"
        view2.setBackgroundColor(Color.CYAN)
        view2.height = 200f.toDp(context!!)

        addOverlapingEvents(column, arrayOf(view, view2), arrayOf(200f.toDp(context!!), 150f.toDp(context!!)))
        addEvent(column, view0, 100f.toDp(context!!))
    }

    private fun addEvent(constLayout: ConstraintLayout, event: View, startsDp: Int) {
        val set = ConstraintSet()

        if (event.id == -1) {
            event.id = View.generateViewId()
        }

        constLayout.addView(event)

        set.clone(constLayout)

        set.connect(event.id, ConstraintSet.TOP, constLayout.id, ConstraintSet.TOP, startsDp)
        set.constrainWidth(event.id, 0f.toDp(context!!))
        set.connect(event.id, ConstraintSet.START, constLayout.id, ConstraintSet.START, padding)
        set.connect(event.id, ConstraintSet.END, constLayout.id, ConstraintSet.END, padding)

        set.applyTo(constLayout)
    }

    private fun addOverlapingEvents(constLayout: ConstraintLayout, events: Array<View>, startsDp: Array<Int>) {
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
            set.constrainWidth(id, 0f.toDp(context!!))
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

    companion object {

        // TODO: Customize parameter argument names
        const val ARG_COLUMN_COUNT = "column-count"

        const val MAX_COLUMN = 7

        // TODO: Customize parameter initialization
        /**
         *
         */
        @JvmStatic
        fun newInstance(columnCount: Int) =
            WeekViewFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_COLUMN_COUNT, columnCount)
                }
            }
    }
}
