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
import cz.budikpet.mytimetableview.data.EventType
import cz.budikpet.mytimetableview.data.TimetableEvent

import kotlinx.android.synthetic.main.fragment_weekview_list.view.*
import kotlinx.android.synthetic.main.week_row.view.*
import org.joda.time.DateTime

/**
 * A fragment representing a list of Items.
 * Activities containing this fragment MUST implement the
 * [WeekViewFragment.OnListFragmentInteractionListener] interface.
 */
class WeekViewFragment : Fragment() {
    private var listener: OnListFragmentInteractionListener? = null
    private lateinit var onEmptySpaceClickListener: View.OnClickListener
    private val events = mutableListOf<TimetableEvent>()

    private var eventsColumnsCount = MAX_COLUMN
    private var firstDate = DateTime()
    private val dpPerMinRatio = 1
    private val numOfLessons by lazy { sharedPreferences.getInt(SharedPreferencesKeys.NUM_OF_LESSONS.toString(), 0) }
    private val lessonsStartTime by lazy { sharedPreferences.getInt(SharedPreferencesKeys.LESSONS_START_TIME.toString(), 0) }
    private val breakLength by lazy { sharedPreferences.getInt(SharedPreferencesKeys.LENGTH_OF_BREAK.toString(), 0) }
    private val lessonLength by lazy { sharedPreferences.getInt(SharedPreferencesKeys.LENGTH_OF_LESSON.toString(), 0) }
    private val eventPadding by lazy { 2f.toDp(context!!) }

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var eventsColumns: List<ConstraintLayout>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sharedPreferences = context!!.getSharedPreferences("Pref", Context.MODE_PRIVATE)

        arguments?.let {
            eventsColumnsCount = it.getInt(ARG_COLUMN_COUNT)
            firstDate = DateTime().withMillis(it.getLong(ARG_START_DATE))
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

        // Store references to dynamic event columns
        eventsColumns = listOf(layout.eventColumn1, layout.eventColumn2, layout.eventColumn3, layout.eventColumn4,
            layout.eventColumn5, layout.eventColumn6, layout.eventColumn7)

        // Create rows
        val currRowTime = DateTime().withMillisOfDay(lessonsStartTime)
        for(i in 0 until numOfLessons) {
            val rowView = getTimeRow(inflater)  // TODO: Create copies of this view?
            rowView.timeTextView.text = currRowTime.plusMinutes(i*(lessonLength + breakLength)).toString("HH:mm")

            listLayout.addView(rowView)
        }

        createDummyEvents()
        updateEventsView()

        return layout
    }

    private fun getTimeRow(inflater: LayoutInflater): View {
        val rowView = inflater.inflate(R.layout.week_row, null, false)

        val layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        layoutParams.bottomMargin = (breakLength*dpPerMinRatio).toFloat().toDp(context!!)
        layoutParams.height = (lessonLength*dpPerMinRatio).toFloat().toDp(context!!)
        rowView.layoutParams = layoutParams

        for (i in 1..MAX_COLUMN) {
            Log.i("MY_test", "$i")

            val spaceView = rowView
                .findViewById<View>(resources.getIdentifier("space$i", "id", context!!.packageName))

            if (i <= eventsColumnsCount) {
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

    // MARK: Dynamically added events

//    private fun createDummyEvents() {
//        val column = eventsColumns.findViewById<ConstraintLayout>(R.id.eventColumn1)
//
//        val view0 = TextView(context!!)
//        view0.text = "Lonely event, How are you doing?"
//        view0.setBackgroundColor(Color.RED)
//        view0.height = 100f.toDp(context!!)
//
//        val view = TextView(context!!)
//        view.text = "Chain one, How are you doing?"
//        view.setBackgroundColor(Color.LTGRAY)
//        view.height = 150f.toDp(context!!)
//
//        val view2 = TextView(context!!)
//        view2.text = "Chain two, This is second event?"
//        view2.setBackgroundColor(Color.CYAN)
//        view2.height = 200f.toDp(context!!)
//
//        addOverlappingEvents(column, arrayOf(view, view2), arrayOf(200f.toDp(context!!), 150f.toDp(context!!)))
//        addEvent(column, view0, 30f.toDp(context!!))
//    }

    private fun createDummyEvents() {
        val event1Start = firstDate.withTime(11, 0, 0, 0)
        val event1End = event1Start.plusMinutes(90)

        val event1 = TimetableEvent(room = "T9:151", fullName = "BI-BAP", acronym = "BI-BAP_1", capacity = 90,
            starts_at = event1Start, ends_at = event1End, event_type = EventType.COURSE_EVENT, teachers = arrayListOf("balikm"),
            occupied = 49)

        val event2Start = event1Start.withTime(9, 15, 0, 0)
        val event2End = event2Start.plusMinutes(195)

        val event2 = TimetableEvent(room = "T9:153", fullName = "BI-END", acronym = "BI-END_2", capacity = 90,
            starts_at = event2Start, ends_at = event2End, event_type = EventType.COURSE_EVENT, teachers = arrayListOf("bulim"),
            occupied = 49)

        val event3Start = firstDate.withTime(9, 15, 0, 0).plusDays(3)
        val event3End = event3Start.plusMinutes(195)

        val event3 = TimetableEvent(room = "T9:153", fullName = "BI-LONE", acronym = "BI-LONE_3", capacity = 90,
            starts_at = event3Start, ends_at = event3End, event_type = EventType.COURSE_EVENT, teachers = arrayListOf("bulim"),
            occupied = 49)

        val event4Start = firstDate.withTime(11, 0, 0, 0).plusDays(5)
        val event4End = event4Start.plusMinutes(90)

        val event4 = TimetableEvent(room = "T9:151", fullName = "BI-BAP", acronym = "BI-BAP_4", capacity = 90,
            starts_at = event4Start, ends_at = event4End, event_type = EventType.COURSE_EVENT, teachers = arrayListOf("balikm"),
            occupied = 49)

        val event5Start = event4Start.withTime(9, 15, 0, 0)
        val event5End = event5Start.plusMinutes(195)

        val event5 = TimetableEvent(room = "T9:153", fullName = "BI-END", acronym = "BI-END_5", capacity = 90,
            starts_at = event5Start, ends_at = event5End, event_type = EventType.COURSE_EVENT, teachers = arrayListOf("bulim"),
            occupied = 49)

        val event6Start = event4Start.withTime(14, 15, 0, 0)
        val event6End = event6Start.plusMinutes(90)

        val event6 = TimetableEvent(room = "T9:153", fullName = "BI-LONE", acronym = "BI-LONE_6", capacity = 90,
            starts_at = event6Start, ends_at = event6End, event_type = EventType.COURSE_EVENT, teachers = arrayListOf("bulim"),
            occupied = 49)


        events.add(event1)
        events.add(event2)

        events.add(event3)
        events.add(event6)

        events.add(event4)
        events.add(event5)
    }

    /**
     * Add events from the collection into the view.
     */
    private fun updateEventsView() {
        var currIndex = 0
        val preparedCollection = events.map { return@map IndexedTimetableEvent(-1, it) }

        preparedCollection.forEach { indexedTimetableEvent1: IndexedTimetableEvent ->
            if(indexedTimetableEvent1.index == -1) {
                indexedTimetableEvent1.index = currIndex

                preparedCollection.forEach { indexedTimetableEvent2: IndexedTimetableEvent ->
                    if(indexedTimetableEvent2.index == -1) {
                        if(indexedTimetableEvent1.timetableEvent.overlapsWith(indexedTimetableEvent2.timetableEvent)) {
                            indexedTimetableEvent2.index = currIndex
                        }
                    }
                }
            }

            currIndex++
        }

        preparedCollection.groupBy { it.index }
            .forEach { mapEntry ->
                val currEvents = mapEntry.value.map { it.timetableEvent }

                // TODO: Call methods
                if(currEvents.size == 1) {
                    // Add lone event

                } else {
                    // Add overlapping event
                }
            }
    }

    private fun addEvent(event: TimetableEvent) {
        val startsDp = 100f.toDp(context!!)
        val constLayout = eventsColumns.elementAt(1)

        val eventView = TextView(context!!)
        eventView.text = event.acronym
        eventView.setBackgroundColor(Color.RED)
        eventView.height = 100f.toDp(context!!)

        val set = ConstraintSet()

        if (eventView.id == -1) {
            eventView.id = View.generateViewId()
        }

        constLayout.addView(eventView)

        set.clone(constLayout)

        set.connect(eventView.id, ConstraintSet.TOP, constLayout.id, ConstraintSet.TOP, startsDp)
        set.constrainWidth(eventView.id, 0f.toDp(context!!))
        set.connect(eventView.id, ConstraintSet.START, constLayout.id, ConstraintSet.START, eventPadding)
        set.connect(eventView.id, ConstraintSet.END, constLayout.id, ConstraintSet.END, eventPadding)

        set.applyTo(constLayout)
    }

    private fun addOverlappingEvents(constLayout: ConstraintLayout, events: Array<View>, startsDp: Array<Int>) {
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
            set.setMargin(id, ConstraintSet.START, eventPadding)
            set.setMargin(id, ConstraintSet.END, eventPadding)
        }

        // Create chain
        set.createHorizontalChain(
            constLayout.id, ConstraintSet.LEFT, constLayout.id, ConstraintSet.RIGHT,
            eventIds.toIntArray(), null, ConstraintSet.CHAIN_SPREAD
        )

        set.applyTo(constLayout)

    }

    private fun getEventViewHeight() {

    }

    private fun getEventViewStart() {

    }

    // MARK: Initializers

    interface OnListFragmentInteractionListener {
        fun onListFragmentInteraction()

        fun onEmptySpaceClicked()

        fun onEventClicked()
    }

    companion object {

        // TODO: Customize parameter argument names
        const val ARG_COLUMN_COUNT = "column-count"
        const val ARG_START_DATE = "start-date"

        const val MAX_COLUMN = 7

        // TODO: Customize parameter initialization
        /**
         *
         */
        @JvmStatic
        fun newInstance(columnCount: Int, startDate: DateTime) =
            WeekViewFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_COLUMN_COUNT, columnCount)
                    putLong(ARG_START_DATE, startDate.millis)
                }
            }
    }
}

data class IndexedTimetableEvent(var index: Int, val timetableEvent: TimetableEvent)