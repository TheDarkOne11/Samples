package cz.budikpet.mytimetableview

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.dgreenhalgh.android.simpleitemdecoration.grid.GridDividerItemDecoration
import cz.budikpet.mytimetableview.dummy.DummyContent
import cz.budikpet.mytimetableview.dummy.DummyContent.DummyItem


/**
 * A fragment representing a list of Items.
 * Activities containing this fragment MUST implement the
 * [TimetableFragment.OnListFragmentInteractionListener] interface.
 */
class TimetableFragment : Fragment() {

    // TODO: Customize parameters
    private var columnCount = 1

    private var listener: OnListFragmentInteractionListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            columnCount = it.getInt(ARG_COLUMN_COUNT)
        }
    }

    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_timetable_list, container, false) as RecyclerView

        // Set the adapter
        with(view) {
            layoutManager = when {
                columnCount <= 1 -> LinearLayoutManager(context)
                else -> GridLayoutManager(context, columnCount)
            }
            adapter = MyTimetableRecyclerViewAdapter(DummyContent.ITEMS, listener)

            val horizontalDivider = ContextCompat.getDrawable(context, R.drawable.row_item_divider)
            val verticalDivider = ContextCompat.getDrawable(context, R.drawable.row_item_divider)
            this.addItemDecoration(GridDividerItemDecoration(horizontalDivider, verticalDivider, columnCount))
        }

        recyclerView = view
        return view
    }

    fun testFindView(pos: Int) {
        val container = recyclerView.findViewHolderForAdapterPosition(pos)?.itemView
        container?.setBackgroundColor(Color.BLUE)

        val button = Button(this.context)
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

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson
     * [Communicating with Other Fragments](http://developer.android.com/training/basics/fragments/communicating.html)
     * for more information.
     */
    interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onListFragmentInteraction(item: DummyItem?)
    }

    companion object {

        // TODO: Customize parameter argument names
        const val ARG_COLUMN_COUNT = "column-count"

        // TODO: Customize parameter initialization
        @JvmStatic
        fun newInstance(columnCount: Int) =
            TimetableFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_COLUMN_COUNT, columnCount)
                }
            }
    }
}
