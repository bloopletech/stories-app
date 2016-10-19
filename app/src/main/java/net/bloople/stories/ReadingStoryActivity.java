package net.bloople.stories;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

public class ReadingStoryActivity extends Activity {
    private RecyclerView nodesView;
    private LinearLayoutManager layoutManager;
    private long bookId;
    private String path = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reading_story);

        nodesView = (RecyclerView)findViewById(R.id.nodes_view);

        layoutManager = new LinearLayoutManager(this);
        nodesView.setLayoutManager(layoutManager);

        Intent intent = getIntent();
        bookId = intent.getLongExtra("_id", -1);

        SQLiteDatabase db = DatabaseHelper.instance(this);

        Cursor result = db.rawQuery("SELECT path FROM books WHERE _id=?", new String[] { String.valueOf(bookId) });
        result.moveToFirst();
        path = result.getString(0);
        result.close();

        ParseStoryTask parser = new ParseStoryTask();
        parser.execute(path);
    }

    @Override
    protected void onStop() {
        super.onStop();

        SQLiteDatabase db = DatabaseHelper.instance(this);
        ContentValues values = new ContentValues();
        values.put("last_read_position", layoutManager.findFirstVisibleItemPosition());

        db.update("books", values, "_id=?", new String[] { String.valueOf(bookId) });
    }

    private static Story parseStory(String path) {
        try {
            Story story = new Story();
            StoryParser parser = new StoryParser(new BufferedReader(new FileReader(path)));
            parser.parse(story);

            return story;
        }
        catch(IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private class ParseStoryTask extends AsyncTask<String, Void, List<Node>> {
        protected List<Node> doInBackground(String... paths) {
            String path = paths[0];

            final Story story = parseStory(path);
            return story.nodes();
        }

        protected void onPostExecute(List<Node> nodes) {

            final NodesAdapter adapter = new NodesAdapter(nodes);
            nodesView.setAdapter(adapter);

            View view = findViewById(R.id.loading_text);
            view.setVisibility(View.GONE);

            SQLiteDatabase db = DatabaseHelper.instance(ReadingStoryActivity.this);

            Cursor result = db.rawQuery("SELECT last_read_position FROM books WHERE _id=?", new String[] { String.valueOf(bookId) });
            result.moveToFirst();

            nodesView.scrollToPosition(result.getInt(0));

            result.close();
        }
    }

}
