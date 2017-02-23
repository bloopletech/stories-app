package net.bloople.stories;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class BooksAdapter extends CursorRecyclerAdapter<BooksAdapter.ViewHolder> {
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView titleView;
        public TextView sizeView;
        public TextView ageView;
        public TextView lastOpenedView;
        public ViewHolder(View view) {
            super(view);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Context context = v.getContext();
                    long id = getItemId();

                    Book book = Book.findById(context, id);
                    book.lastOpenedAt(System.currentTimeMillis());
                    book.save(context);

                    Intent intent = new Intent(context, ReadingStoryActivity.class);
                    intent.putExtra("_id", id);

                    context.startActivity(intent);
                }
            });

            titleView = (TextView)view.findViewById(R.id.story_title);
            sizeView = (TextView)view.findViewById(R.id.story_size);
            ageView = (TextView)view.findViewById(R.id.story_age);
            lastOpenedView = (TextView)view.findViewById(R.id.story_last_opened);
        }
    }

    private SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("d MMMM yyyy",
            Locale.getDefault());

    public BooksAdapter(Cursor cursor) {
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
    }

    //Copied from https://github.com/nbsp-team/MaterialFilePicker
    public static String getReadableFileSize(long size) {
        if (size <= 0) return "0";
        final String[] units = new String[]{"B", "KB", "MB", "GB", "TB"};
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        return new DecimalFormat("#").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }
}
