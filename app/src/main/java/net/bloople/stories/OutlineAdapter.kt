package net.bloople.stories

import io.noties.markwon.Markwon
import androidx.recyclerview.widget.RecyclerView
import android.widget.TextView
import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.View
import org.commonmark.node.Node
import java.util.ArrayList

internal class OutlineAdapter(private val markwon: Markwon) : RecyclerView.Adapter<OutlineAdapter.ViewHolder>() {
    private val nodes: MutableList<Node>
    private val nodeIndexes: MutableList<Int>

    internal inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var textView: TextView

        init {
            textView = view.findViewById(R.id.text_view)
            textView.setOnClickListener { view ->
                val activity = view.context as ReadingStoryActivity
                activity.scrollToPosition(nodeIndexes[bindingAdapterPosition])
                activity.closeDrawers()
            }
        }
    }

    fun addAll(newNodes: List<Node>, newNodeIndexes: List<Int>) {
        nodes.addAll(newNodes)
        nodeIndexes.addAll(newNodeIndexes)
        notifyItemRangeInserted(nodes.size - 1, newNodes.size)
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.outline_node_view, parent,
            false
        )
        return ViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val tv = holder.textView
        markwon.setParsedMarkdown(tv, markwon.render(nodes[position]))
        tv.setPadding(
            tv.paddingLeft,
            if (position == 0) tv.paddingBottom else 0,
            tv.paddingRight,
            tv.paddingBottom
        )
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount(): Int {
        return nodes.size
    }

    init {
        nodes = ArrayList()
        nodeIndexes = ArrayList()
    }
}