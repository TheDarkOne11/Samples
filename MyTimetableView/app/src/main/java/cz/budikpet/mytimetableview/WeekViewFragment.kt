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
import org.joda.time.Days
import org.joda.time.LocalTime
import org.joda.time.Minutes

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
    private val eventPadding by lazy { 2f.toDp(context!!) }
    private var firstDate = DateTime()
    private val dpPerMinRatio = 1
    private val numOfLessons by lazy { sharedPreferences.getInt(SharedPreferencesKeys.NUM_OF_LESSONS.toString(), 0) }
    private val breakLength by lazy { sharedPreferences.getInt(SharedPreferencesKeys.LENGTH_OF_BREAK.toString(), 0) }
    private val lessonLength by lazy { sharedPreferences.getInt(SharedPreferencesKeys.LENGTH_OF_LESSON.toString(), 0) }
    private val lessonsStartTime by lazy {
        LocalTime().withMillisOfDay(
            sharedPreferences.getInt(SharedPreferencesKeys.LESSONS_START_TIME.toString(), 0)
        )
    }

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
        eventsColumns = listOf(
            layout.eventColumn1, layout.eventColumn2, layout.eventColumn3, layout.eventColumn4,
            layout.eventColumn5, layout.eventColumn6, layout.eventColumn7
        )

        // Create rows
        val currRowTime = DateTime().withTime(lessonsStartTime)
        for (i in 0 until numOfLessons) {
            val rowView = getTimeRow(inflater)  // TODO: Create copies of this view?
            rowView.timeTextView.text = currRowTime.plusMinutes(i * (lessonLength + breakLength)).toString("HH:mm")

            listLayout.addView(rowView)
        }

        createDummyEvents()
        updateEventsView()

        return layout
    }

    private fun getTimeRow(inflater: LayoutInflater): View {
        val rowView = inflater.inflate(R.layout.week_row, null, false)

        val layoutParams =
            LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        layoutParams.bottomMargin = (breakLength * dpPerMinRatio).toFloat().toDp(context!!)
        layoutParams.height = (lessonLength * dpPerMinRatio).toFloat().toDp(context!!)
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

    private fun createDummyEvents() {
        val event1Start = firstDate.withTime(11, 0, 0, 0)
        val event1End = event1Start.plusMinutes(90)

        val event1 = TimetableEvent(
            room = "T9:151",
            fullName = "BI-BAP",
            acronym = "BI-BAP_1",
            capacity = 90,
            starts_at = event1Start,
            ends_at = event1End,
            event_type = EventType.COURSE_EVENT,
            teachers = arrayListOf("balikm"),
            occupied = 49
        )

        val event2Start = event1Start.withTime(9, 15, 0, 0)
        val event2End = event2Start.plusMinutes(195)

        val event2 = TimetableEvent(
            room = "T9:153",
            fullName = "BI-END",
            acronym = "BI-END_2",
            capacity = 90,
            starts_at = event2Start,
            ends_at = event2End,
            event_type = EventType.COURSE_EVENT,
            teachers = arrayListOf("bulim"),
            occupied = 49
        )

        val event3Start = firstDate.withTime(9, 15, 0, 0).plusDays(3)
        val event3End = event3Start.plusMinutes(195)

        val event3 = TimetableEvent(
            room = "T9:153",
            fullName = "BI-LONE",
            acronym = "BI-LONE_3",
            capacity = 90,
            starts_at = event3Start,
            ends_at = event3End,
            event_type = EventType.COURSE_EVENT,
            teachers = arrayListOf("bulim"),
            occupied = 49
        )

        val event4Start = firstDate.withTime(11, 0, 0, 0).plusDays(5)
        val event4End = event4Start.plusMinutes(90)

        val event4 = TimetableEvent(
            room = "T9:151",
            fullName = "BI-BAP",
            acronym = "BI-BAP_4",
            capacity = 90,
            starts_at = event4Start,
            ends_at = event4End,
            event_type = EventType.COURSE_EVENT,
            teachers = arrayListOf("balikm"),
            occupied = 49
        )

        val event5Start = event4Start.withTime(9, 15, 0, 0)
        val event5End = event5Start.plusMinutes(195)

        val event5 = TimetableEvent(
            room = "T9:153",
            fullName = "BI-END",
            acronym = "BI-END_5",
            capacity = 90,
            starts_at = event5Start,
            ends_at = event5End,
            event_type = EventType.COURSE_EVENT,
            teachers = arrayListOf("bulim"),
            occupied = 49
        )

        val event6Start = event4Start.withTime(14, 15, 0, 0)
        val event6End = event6Start.plusMinutes(90)

        val event6 = TimetableEvent(
            room = "T9:153",
            fullName = "BI-LONE",
            acronym = "BI-LONE_6",
            capacity = 90,
            starts_at = event6Start,
            ends_at = event6End,
            event_type = EventType.COURSE_EVENT,
            teachers = arrayListOf("bulim"),
            occupied = 49
        )


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
            if (indexedTimetableEvent1.index == -1) {
                indexedTimetableEvent1.index = currIndex

                preparedCollection.forEach { indexedTimetableEvent2: IndexedTimetableEvent ->
                    if (indexedTimetableEvent2.index == -1) {
                        if (indexedTimetableEvent1.timetableEvent.overlapsWith(indexedTimetableEvent2.timetableEvent)) {
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
                if (currEvents.size == 1) {
                    // Add lone event
                    addEvent(currEvents.first())
                } else {
                    // Add overlapping event
                    addOverlappingEvents(currEvents)
                }
            }
    }

    private fun addEvent(event: TimetableEvent) {
        // Create event view
        val eventView = TextView(context!!)
        eventView.text = event.acronym
        eventView.setBackgroundColor(Color.RED)
        eventView.height = getEventViewHeight(event)

        if (eventView.id == -1) {
            eventView.id = View.generateViewId()
        }

        // Add event view into the correct column
        val column = getEventsColumn(event)
        column.addView(eventView)

        val set = ConstraintSet()
        set.clone(column)

        set.connect(eventView.id, ConstraintSet.TOP, column.id, ConstraintSet.TOP, getEventViewStart(event))
        set.constrainWidth(eventView.id, 0f.toDp(context!!))
        set.connect(eventView.id, ConstraintSet.START, column.id, ConstraintSet.START, eventPadding)
        set.connect(eventView.id, ConstraintSet.END, column.id, ConstraintSet.END, eventPadding)

        set.applyTo(column)
    }

    private fun addOverlappingEvents(events: List<TimetableEvent>) {
        // Add event view into the correct column
        val column = getEventsColumn(events.first())

        // Add events to the constLayout and get a list of ids in the process
        val eventIds = events
            .map {
                val eventView = TextView(context!!)
                eventView.text = it.acronym
                eventView.setBackgroundColor(Color.RED)
                eventView.height = getEventViewHeight(it)

                if (eventView.id == -1) {
                    eventView.id = View.generateViewId()
                }

                column.addView(eventView)

                return@map Pair(eventView.id, getEventViewStart(it))
            }

        val set = ConstraintSet()
        set.clone(column)

        // Set constraints
        val idsArray = eventIds.map { pair ->
            set.constrainWidth(pair.first, 0f.toDp(context!!))
            set.connect(pair.first, ConstraintSet.TOP, column.id, ConstraintSet.TOP, pair.second)
            set.setMargin(pair.first, ConstraintSet.START, eventPadding)
            set.setMargin(pair.first, ConstraintSet.END, eventPadding)

            return@map pair.first
        }.toIntArray()

        // Create chain
        set.createHorizontalChain(
            column.id, ConstraintSet.LEFT, column.id, ConstraintSet.RIGHT,
            idsArray, null, ConstraintSet.CHAIN_SPREAD
        )

        set.applyTo(column)

    }

    private fun getEventsColumn(event: TimetableEvent): ConstraintLayout {
        val index = Days.daysBetween(firstDate, event.starts_at).days
        return eventsColumns.elementAt(index + 1)
    }

    private fun getEventViewHeight(event: TimetableEvent): Int {
        return Minutes.minutesBetween(event.starts_at, event.ends_at)
            .minutes
            .toFloat()
            .toDp(context!!)
    }

    private fun getEventViewStart(event: TimetableEvent): Int {
        return Minutes.minutesBetween(event.starts_at.withTime(lessonsStartTime), event.starts_at)
            .minutes
            .toFloat()
            .toDp(context!!)
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