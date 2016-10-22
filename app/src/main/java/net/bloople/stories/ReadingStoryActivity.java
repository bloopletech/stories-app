package net.bloople.stories;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ReadingStoryActivity extends Activity {
    private RecyclerView nodesView;
    private LinearLayoutManager layoutManager;
    private NodesAdapter adapter;
    private Book book;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reading_story);

        nodesView = (RecyclerView)findViewById(R.id.nodes_view);

        layoutManager = new LinearLayoutManager(this);
        nodesView.setLayoutManager(layoutManager);

        adapter = new NodesAdapter();
        nodesView.setAdapter(adapter);

        Intent intent = getIntent();
        book = Book.findById(this, intent.getLongExtra("_id", -1));

        ParseStoryTask parser = new ParseStoryTask();
        parser.execute(book);
    }

    @Override
    protected void onStop() {
        super.onStop();

        book.lastReadPosition(layoutManager.findFirstVisibleItemPosition());
        book.save(this);
    }

    private class ParseStoryTask extends AsyncTask<Book, List<Node>, Void> {
        public int BATCH_SIZE = 50;
        private boolean setPosition = false;

        protected Void doInBackground(Book... bookArgs) {
            Book book = bookArgs[0];

            try {
                StoryParser parser = new StoryParser(new BufferedReader(new FileReader(book.path())));

                List<Node> accumulator = new ArrayList<>();
                while(parser.hasNext()) {
                    accumulator.add(parser.next());

                    if(accumulator.size() >= BATCH_SIZE) {
                        publishProgress(accumulator);
                        accumulator = new ArrayList<>();
                    }
                }

                publishProgress(accumulator);
            }
            catch(IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        protected void onProgressUpdate(List<Node>... nodesArgs) {
            adapter.addAll(nodesArgs[0]);

            if(!setPosition && (adapter.getItemCount() >= book.lastReadPosition())) {
                setPosition = true;
                nodesView.scrollToPosition(book.lastReadPosition());
            }
        }

        protected void onPostExecute(Void result) {
        }
    }

}
