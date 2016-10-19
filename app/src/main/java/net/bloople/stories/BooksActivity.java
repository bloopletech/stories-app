package net.bloople.stories;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class BooksActivity extends Activity {
    public static final int SORT_ALPHABETIC = 0;
    public static final int SORT_AGE = 1;
    public static final int SORT_SIZE = 2;
    public static final int SORT_LAST_OPENED = 3;

    private int sortMethod;
    private boolean sortDirectionAsc;

    private RecyclerView listView;
    private LinearLayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_books);

        listView = (RecyclerView)findViewById(R.id.stories_list);

        layoutManager = new LinearLayoutManager(this);
        listView.setLayoutManager(layoutManager);
    }

    @Override
    public void onResume() {
        super.onResume();

        loadPreferences();
        updateCursor();
    }

    @Override
    public void onStop() {
        super.onStop();

        savePreferences();
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

    private SharedPreferences preferences() {
        return getApplicationContext().getSharedPreferences("books-list", Context.MODE_PRIVATE);
    }

    private void loadPreferences() {
        SharedPreferences preferences = preferences();

        sortMethod = preferences.getInt("last-sort-method", SORT_LAST_OPENED);
        sortDirectionAsc = preferences.getBoolean("last-sort-direction-asc", false);
    }

    private void savePreferences() {
        SharedPreferences.Editor editor = preferences().edit();
        editor.putInt("last-sort-method", sortMethod);
        editor.putBoolean("last-sort-direction-asc", sortDirectionAsc);
        editor.apply();
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

        SearchBooksTask searcher = new SearchBooksTask();
        searcher.execute(orderBy);
    }

    private class SearchBooksTask extends AsyncTask<String, Void, Cursor> {
        protected Cursor doInBackground(String... ordersBy) {
            String orderBy = ordersBy[0];

            SQLiteDatabase db = DatabaseHelper.instance(BooksActivity.this);
            Cursor cursor = db.query("books", null, null, null, null, null, orderBy);
            cursor.moveToFirst();

            return cursor;
        }

        protected void onPostExecute(Cursor cursor) {
            BooksAdapter adapter = new BooksAdapter(cursor, BooksActivity.this);
            listView.setAdapter(adapter);
        }
    }
}
