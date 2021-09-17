package net.bloople.stories;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

class BooksAdapter extends CursorRecyclerAdapter<BooksAdapter.ViewHolder> {
    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView titleView;
        TextView sizeView;
        TextView ageView;
        TextView lastOpenedView;
        TextView openedCountView;
        ImageButton starView;
        ViewHolder(View view) {
            super(view);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Context context = v.getContext();
                    long id = getItemId();

                    Intent intent = new Intent(context, ReadingStoryActivity.class);
                    intent.putExtra("_id", id);

                    context.startActivity(intent);
                }
            });

            titleView = (TextView)view.findViewById(R.id.story_title);
            sizeView = (TextView)view.findViewById(R.id.story_size);
            ageView = (TextView)view.findViewById(R.id.story_age);
            lastOpenedView = (TextView)view.findViewById(R.id.story_last_opened);
            openedCountView = (TextView)view.findViewById(R.id.story_opened_count);
            starView = (ImageButton)view.findViewById(R.id.story_star);

            starView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Context context = view.getContext();
                    long id = getItemId();

                    Book book = Book.findById(context, id);
                    boolean starred = !book.starred();

                    book.starred(starred);
                    book.save(context);

                    view.setActivated(starred);
                }
            });
        }
    }

    private SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("d MMMM yyyy",
            Locale.getDefault());

    BooksAdapter(Cursor cursor) {
        super(cursor);
    }

    // Create new views (invoked by the layout manager)
    @Override
    public BooksAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_book, parent,
                false);

        return new BooksAdapter.ViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(BooksAdapter.ViewHolder holder, Cursor cursor) {
        Book book = new Book(cursor);

        holder.titleView.setText(book.title());

        holder.sizeView.setText(getReadableFileSize(book.size()));

        if(holder.ageView != null) {
            String age = DATE_FORMAT.format(new Date(book.mtime()));
            holder.ageView.setText(age);
        }

        long lastOpenedMillis = book.lastOpenedAt();
        if(lastOpenedMillis > 0L) {
            String lastOpened = DATE_FORMAT.format(new Date(lastOpenedMillis));
            holder.lastOpenedView.setText(lastOpened);
        }
        else {
            holder.lastOpenedView.setText("Never");
        }

        if(holder.openedCountView != null) {
            holder.openedCountView.setText(String.valueOf(book.openedCount()));
        }

        holder.starView.setActivated(book.starred());
    }

    //Copied from https://github.com/nbsp-team/MaterialFilePicker
    static String getReadableFileSize(long size) {
        if (size <= 0) return "0";
        final String[] units = new String[]{"B", "KB", "MB", "GB", "TB"};
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        return new DecimalFormat("#").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }
}
