package cz.budikpet.mytimetableview

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout

import cz.budikpet.mytimetableview.dummy.DummyContent.DummyItem
import kotlinx.android.synthetic.main.week_row.view.*

/**
 * A fragment representing a list of Items.
 * Activities containing this fragment MUST implement the
 * [WeekViewFragment.OnListFragmentInteractionListener] interface.
 */
class WeekViewFragment : Fragment() {

    // TODO: Customize parameters
    private var columnCount = MAX_COLUMN
    private lateinit var timeRows: ArrayList<String>

    private var listener: OnListFragmentInteractionListener? = null

    private lateinit var onEmptySpaceClickListener: View.OnClickListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            columnCount = it.getInt(ARG_COLUMN_COUNT)
            timeRows = it.getStringArrayList(ARG_TIME_ROWS)
        }

        onEmptySpaceClickListener = View.OnClickListener {
            listener?.onEmptySpaceClicked()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val listLayout = inflater.inflate(R.layout.fragment_weekview_list, container, false) as LinearLayout

        timeRows.forEach {
            val rowView = getTimeRow(inflater)
            rowView.timeText.text = it

            listLayout.addView(rowView)
        }

        return listLayout
    }

    fun getTimeRow(inflater: LayoutInflater): View {
        val rowView = inflater.inflate(R.layout.week_row, null, false)

        rowView.layoutParams =
            LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)

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
        fun onListFragmentInteraction(item: DummyItem?)

        fun onEmptySpaceClicked()

        fun onEventClicked()
    }

    companion object {

        // TODO: Customize parameter argument names
        const val ARG_COLUMN_COUNT = "column-count"
        const val ARG_TIME_ROWS = "timeRows"

        const val MAX_COLUMN = 7

        // TODO: Customize parameter initialization
        /**
         *
         */
        @JvmStatic
        fun newInstance(columnCount: Int, timeRows: ArrayList<String>) =
            WeekViewFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_COLUMN_COUNT, columnCount)
                    putStringArrayList(ARG_TIME_ROWS, timeRows)
                }
            }
    }
}
