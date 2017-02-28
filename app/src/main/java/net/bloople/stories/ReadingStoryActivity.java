package net.bloople.stories;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static android.support.v7.widget.RecyclerView.SCROLL_STATE_IDLE;

public class ReadingStoryActivity extends Activity {
    private RecyclerView nodesView;
    private LinearLayoutManager layoutManager;
    private NodesAdapter adapter;
    private DrawerLayout drawer;
    private RecyclerView sidebar;
    private LinearLayoutManager sidebarLayoutManager;
    private OutlineAdapter outlineAdapter;
    private Book book;
    private int savedReadPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reading_story);

        nodesView = (RecyclerView)findViewById(R.id.nodes_view);
        nodesView.setItemAnimator(null);

        layoutManager = new LinearLayoutManager(this);
        nodesView.setLayoutManager(layoutManager);

        adapter = new NodesAdapter();
        nodesView.setAdapter(adapter);

        nodesView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if(newState == SCROLL_STATE_IDLE) savePosition();
            }
        });

        sidebar = (RecyclerView)findViewById(R.id.sidebar);

        sidebarLayoutManager = new LinearLayoutManager(this);
        sidebar.setLayoutManager(sidebarLayoutManager);

        outlineAdapter = new OutlineAdapter();
        sidebar.setAdapter(outlineAdapter);

        drawer = (DrawerLayout)findViewById(R.id.drawer_layout);

        Intent intent = getIntent();
        book = Book.findById(this, intent.getLongExtra("_id", -1));

        ParseStoryTask parser = new ParseStoryTask();
        parser.execute(book);
    }

    @Override
    protected void onStop() {
        super.onStop();
        savePosition();
    }

    public void savePosition() {
        int currentReadPosition = layoutManager.findFirstVisibleItemPosition();

        if(savedReadPosition != currentReadPosition) {
            book.lastReadPosition(currentReadPosition);
            book.save(this);

            savedReadPosition = currentReadPosition;
        }
    }

    public void scrollToPosition(int position) {
        layoutManager.scrollToPositionWithOffset(position, 0);
    }

    public void closeDrawers() {
        drawer.closeDrawers();
    }

    private class ParseStoryTask extends AsyncTask<Book, List<String>, Void> {
        int BATCH_SIZE = 50;
        private boolean setPosition = false;

        protected Void doInBackground(Book... bookArgs) {
            Book book = bookArgs[0];

            try {
                StoryParser parser = new StoryParser(new BufferedReader(new FileReader(book.path())));

                List<String> accumulator = new ArrayList<>();
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

        protected void onProgressUpdate(List<String>... nodesArgs) {
            int countBefore = adapter.getItemCount();
            adapter.addAll(nodesArgs[0]);

            List<String> outlineNodes = new ArrayList<>();
            List<Integer> outlineNodesMap = new ArrayList<>();

            for(int i = 0; i < nodesArgs[0].size(); i++) {
                String node = nodesArgs[0].get(i);

                if(NodeRenderer.isOutline(node)) {
                    outlineNodes.add(node);
                    outlineNodesMap.add(countBefore + i);
                }
            }

            outlineAdapter.addAll(outlineNodes, outlineNodesMap);

            if(!setPosition && (adapter.getItemCount() >= book.lastReadPosition())) {
                setPosition = true;

                int lastReadPosition = book.lastReadPosition();
                savedReadPosition = lastReadPosition;

                scrollToPosition(lastReadPosition);
            }
        }

        protected void onPostExecute(Void result) {
        }
    }
}
