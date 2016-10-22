package net.bloople.stories;

import android.app.Activity;
import android.content.Intent;
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
    private Book book;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reading_story);

        nodesView = (RecyclerView)findViewById(R.id.nodes_view);

        layoutManager = new LinearLayoutManager(this);
        nodesView.setLayoutManager(layoutManager);

        Intent intent = getIntent();
        book = Book.findById(this, intent.getLongExtra("_id", -1));

        ParseStoryTask parser = new ParseStoryTask();
        parser.execute(book.path());
    }

    @Override
    protected void onStop() {
        super.onStop();

        book.lastReadPosition(layoutManager.findFirstVisibleItemPosition());
        book.save(this);
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

            nodesView.scrollToPosition(book.lastReadPosition());
        }
    }

}
