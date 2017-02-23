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
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ReadingStoryActivity extends Activity {
    public static final int SAVE_POSITION_PERIOD_SECONDS = 5;

    private RecyclerView nodesView;
    private LinearLayoutManager layoutManager;
    private NodesAdapter adapter;
    private DrawerLayout drawer;
    private RecyclerView sidebar;
    private LinearLayoutManager sidebarLayoutManager;
    private OutlineAdapter outlineAdapter;
    private Book book;
    private ScheduledThreadPoolExecutor executor;
    private ScheduledFuture<?> future;
    private int savedReadPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reading_story);

        nodesView = (RecyclerView)findViewById(R.id.nodes_view);

        layoutManager = new LinearLayoutManager(this);
        nodesView.setLayoutManager(layoutManager);

        adapter = new NodesAdapter();
        nodesView.setAdapter(adapter);

        sidebar = (RecyclerView)findViewById(R.id.sidebar);

        sidebarLayoutManager = new LinearLayoutManager(this);
        sidebar.setLayoutManager(sidebarLayoutManager);

        outlineAdapter = new OutlineAdapter();
        sidebar.setAdapter(outlineAdapter);

        drawer = (DrawerLayout)findViewById(R.id.drawer_layout);

        Intent intent = getIntent();
        book = Book.findById(this, intent.getLongExtra("_id", -1));

        executor = new ScheduledThreadPoolExecutor(1);

        ParseStoryTask parser = new ParseStoryTask();
        parser.execute(book);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(future != null) future.cancel(false);
        savePosition();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(future != null) future.cancel(false);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(future != null) future.cancel(false);
        future = executor.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        savePosition();
                    }
                });
            }
        }, SAVE_POSITION_PERIOD_SECONDS, SAVE_POSITION_PERIOD_SECONDS, TimeUnit.SECONDS);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(future != null) future.cancel(false);
        executor.shutdownNow();
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
        layoutManager.scrollToPositionWithOffset(position, 10);
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
            adapter.addAll(nodesArgs[0]);

            List<String> outlineNodes = new ArrayList<>();
            List<Integer> outlineNodesMap = new ArrayList<>();

            for(String node : nodesArgs[0]) {
                if(NodeFactory.isOutline(node)) {
                    outlineNodes.add(node);
                    outlineNodesMap.add(adapter.getItemPosition(node));
                }
            }

            outlineAdapter.addAll(outlineNodes, outlineNodesMap);

            if(!setPosition && (adapter.getItemCount() >= book.lastReadPosition())) {
                setPosition = true;

                int lastReadPosition = book.lastReadPosition();
                savedReadPosition = lastReadPosition;

                nodesView.scrollToPosition(lastReadPosition);
            }
        }

        protected void onPostExecute(Void result) {
        }
    }
}
