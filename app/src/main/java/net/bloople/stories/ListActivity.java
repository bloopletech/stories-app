package net.bloople.stories;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.widget.SimpleCursorAdapter;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

public class ListActivity extends Activity {
    public static final int SORT_ALPHABETIC = 0;
    public static final int SORT_AGE = 1;
    public static final int SORT_SIZE = 2;
    public static final int SORT_LAST_OPENED = 3;

    private SimpleCursorAdapter adapter;
    private boolean sortDirectionAsc = true;
    private int sortMethod = SORT_ALPHABETIC;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        adapter = new SimpleCursorAdapter(
                this,
                R.layout.list_item,
                null,
                new String[] { "title", "size", "mtime", "last_opened_at" },
                new int[] { R.id.story_title, R.id.story_size, R.id.story_age, R.id.story_last_opened },
                0
        );
        updateCursor();

        ListView listView = (ListView) findViewById(R.id.stories_list);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SQLiteDatabase db = DatabaseHelper.instance(ListActivity.this);

                ContentValues values = new ContentValues();
                values.put("last_opened_at", System.currentTimeMillis());

                db.update("books", values, "_id=?", new String[] { String.valueOf(id) });

                Intent intent = new Intent(ListActivity.this, ReadingStoryActivity.class);
                intent.putExtra("_id", id);

                startActivity(intent);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        updateCursor();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.list_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if(menuItem.getItemId() == R.id.sort_direction) {
            sortDirectionAsc = !sortDirectionAsc;
        }
        else if(menuItem.getItemId() == R.id.sort_alphabetic) {
            sortMethod = SORT_ALPHABETIC;
        }
        else if(menuItem.getItemId() == R.id.sort_age) {
            sortMethod = SORT_AGE;
        }
        else if(menuItem.getItemId() == R.id.sort_size) {
            sortMethod = SORT_SIZE;
        }
        else if(menuItem.getItemId() == R.id.sort_last_opened) {
            sortMethod = SORT_LAST_OPENED;
        }

        updateCursor();

        return true;
    }

    private void updateCursor() {
        String orderBy = "";

        switch(sortMethod) {
            case SORT_ALPHABETIC:
                orderBy += "title";
                break;
            case SORT_AGE:
                orderBy += "mtime";
                break;
            case SORT_SIZE:
                orderBy += "size";
                break;
            case SORT_LAST_OPENED:
                orderBy += "last_opened_at";
                break;
        }

        if(sortDirectionAsc) orderBy += " ASC";
        else orderBy += " DESC";

        SQLiteDatabase db = DatabaseHelper.instance(this);
        adapter.changeCursor(db.query("books", null, null, null, null, null, orderBy));
    }
}
