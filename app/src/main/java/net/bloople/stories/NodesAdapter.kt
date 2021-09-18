package net.bloople.stories

import android.view.View
import net.bloople.stories.NodesHelper.createNodeView
import io.noties.markwon.Markwon
import androidx.recyclerview.widget.RecyclerView
import android.widget.TextView
import android.view.ViewGroup
import org.commonmark.node.Node
import java.util.ArrayList

internal class NodesAdapter(private val markwon: Markwon) : RecyclerView.Adapter<NodesAdapter.ViewHolder>() {
    private val nodes: MutableList<Node>

    internal class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var textView: TextView = view.findViewById(R.id.text_view)
    }

    fun addAll(newNodes: List<Node>) {
        nodes.addAll(newNodes)
        notifyItemRangeInserted(nodes.size - 1, newNodes.size)
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(createNodeView(parent))
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
    }
}