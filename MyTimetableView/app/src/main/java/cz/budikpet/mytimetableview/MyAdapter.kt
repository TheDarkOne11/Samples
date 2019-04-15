package cz.budikpet.mytimetableview

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup

class MyAdapter : RecyclerView.Adapter<MyAdapter.ViewHolder>() {

    val items = 10

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getItemCount(): Int {
        return items
    }

    override fun onBindViewHolder(p0: ViewHolder, p1: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    }
}