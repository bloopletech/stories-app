package net.bloople.stories;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.commonmark.node.Node;

import java.util.ArrayList;
import java.util.List;

import io.noties.markwon.Markwon;

class NodesAdapter extends RecyclerView.Adapter<NodesAdapter.ViewHolder> {
    private Markwon markwon;
    private List<Node> nodes;

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        ViewHolder(View view) {
            super(view);
            textView = (TextView)view.findViewById(R.id.text_view);
        }
    }

    NodesAdapter(Markwon markwon) {
        this.markwon = markwon;
        nodes = new ArrayList<>();
    }

    void addAll(List<Node> newNodes) {
        nodes.addAll(newNodes);
        notifyItemRangeInserted(nodes.size() - 1, newNodes.size());
    }

    // Create new views (invoked by the layout manager)
    @Override
    public NodesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(NodesHelper.createNodeView(parent));
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        TextView tv = holder.textView;

        markwon.setParsedMarkdown(tv, markwon.render(nodes.get(position)));

        tv.setPadding(
            tv.getPaddingLeft(),
            (position == 0 ? tv.getPaddingBottom() : 0),
            tv.getPaddingRight(),
            tv.getPaddingBottom()
        );
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return nodes.size();
    }
}