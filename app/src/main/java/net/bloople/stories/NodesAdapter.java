package net.bloople.stories;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

class NodesAdapter extends RecyclerView.Adapter<NodesAdapter.ViewHolder> {
    private List<String> nodes;

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        ViewHolder(View view) {
            super(view);
            textView = (TextView)view.findViewById(R.id.text_view);
        }
    }

    NodesAdapter() {
        nodes = new ArrayList<>();
    }

    void addAll(List<String> newNodes) {
        nodes.addAll(newNodes);
        notifyItemRangeInserted(nodes.size() - 1, newNodes.size());
    }

    // Create new views (invoked by the layout manager)
    @Override
    public NodesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.node_view, parent,
                false);

        return new ViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        TextView tv = holder.textView;

        Node node = NodeFactory.create(nodes.get(position));

        tv.setText(node.content());

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

    int getItemPosition(String node) {
        return nodes.lastIndexOf(node);
    }
}