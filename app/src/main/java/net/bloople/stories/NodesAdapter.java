package net.bloople.stories;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class NodesAdapter extends RecyclerView.Adapter<NodesAdapter.ViewHolder> {
    private List<Node> nodes;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView textView;
        public ViewHolder(View view) {
            super(view);
            textView = (TextView)view.findViewById(R.id.text_view);
        }
    }

    public NodesAdapter(List<Node> inNodes) {
        nodes = inNodes;
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

        tv.setText(nodes.get(position).content());

        if(position == 0) {
            tv.setPadding(
                    tv.getPaddingLeft(),
                    tv.getPaddingBottom(),
                    tv.getPaddingRight(),
                    tv.getPaddingBottom()
            );
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return nodes.size();
    }
}