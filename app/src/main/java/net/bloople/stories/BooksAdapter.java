package net.bloople.stories;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by i on 19/10/2016.
 */

public class BooksAdapter extends CursorRecyclerAdapter<BooksAdapter.ViewHolder> {
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView titleView;
        public TextView sizeView;
        public TextView ageView;
        public TextView lastOpenedView;
        public ViewHolder(View view, final Activity activity) {
            super(view);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    long id = getItemId();

                    SQLiteDatabase db = DatabaseHelper.instance(activity);

                    ContentValues values = new ContentValues();
                    values.put("last_opened_at", System.currentTimeMillis());

                    db.update("books", values, "_id=?", new String[] { String.valueOf(id) });

                    Intent intent = new Intent(activity, ReadingStoryActivity.class);
                    intent.putExtra("_id", id);

                    activity.startActivity(intent);
                }
            });

            titleView = (TextView)view.findViewById(R.id.story_title);
            sizeView = (TextView)view.findViewById(R.id.story_size);
            ageView = (TextView)view.findViewById(R.id.story_age);
            lastOpenedView = (TextView)view.findViewById(R.id.story_last_opened);
        }
    }

    private Activity activity;

    public BooksAdapter(Cursor cursor, Activity inActivity) {
        super(cursor);
        activity = inActivity;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public BooksAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_book, parent,
                false);

        return new BooksAdapter.ViewHolder(view, activity);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(BooksAdapter.ViewHolder holder, Cursor cursor) {
        holder.titleView.setText(cursor.getString(cursor.getColumnIndex("title")));
        holder.sizeView.setText(cursor.getString(cursor.getColumnIndex("size")));
        holder.ageView.setText(cursor.getString(cursor.getColumnIndex("mtime")));
        holder.lastOpenedView.setText(cursor.getString(cursor.getColumnIndex("last_opened_at")));
    }
}
