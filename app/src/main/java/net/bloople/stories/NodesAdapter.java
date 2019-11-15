package net.bloople.stories;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import io.noties.markwon.Markwon;

class NodesAdapter extends RecyclerView.Adapter<NodesAdapter.ViewHolder> {
    private Markwon markwon;
    private ParsedBook parsedBook;

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        ViewHolder(View view) {
            super(view);
            textView = (TextView)view.findViewById(R.id.text_view);
        }
    }

    NodesAdapter(Markwon markwon, ParsedBook parsedBook) {
        this.markwon = markwon;
        this.parsedBook = parsedBook;
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

        markwon.setParsedMarkdown(tv, markwon.render(parsedBook.get(position)));

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
        return parsedBook.size();
    }
}