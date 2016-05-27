package bloople.net.stories;

import android.content.res.Resources;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class NodesAdapter extends RecyclerView.Adapter<NodesAdapter.ViewHolder> {
    private List<CharSequence> nodes;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView textView;
        public ViewHolder(View view) {
            super(view);
            textView = (TextView)view;
        }
    }

    public NodesAdapter(List<CharSequence> inNodes) {
        nodes = inNodes;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public NodesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        TextView tv = new TextView(parent.getContext());
        tv.setTextSize(18.0f);
        tv.setLineSpacing(0f, 1.2f);

        Resources r = parent.getResources();
        int horizontalPadding = Math.round(TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                15.0f,
                r.getDisplayMetrics()
        ));
        int paddingBottom = Math.round(TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_SP,
                16.0f,
                r.getDisplayMetrics()
        ));
        tv.setPadding(horizontalPadding, 0, horizontalPadding, paddingBottom);

        tv.setTextColor(Color.BLACK);

        return new ViewHolder(tv);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        TextView tv = holder.textView;

        tv.setText(nodes.get(position));

        if(position == 0) {
            int paddingTop = Math.round(TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    15.0f,
                    tv.getResources().getDisplayMetrics()
            ));
            tv.setPadding(
                    tv.getPaddingLeft(),
                    paddingTop,
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