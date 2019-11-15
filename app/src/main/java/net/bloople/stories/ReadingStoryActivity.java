package net.bloople.stories;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import io.noties.markwon.Markwon;

import static androidx.recyclerview.widget.RecyclerView.SCROLL_STATE_IDLE;

public class ReadingStoryActivity extends Activity {
    private RecyclerView nodesView;
    private LinearLayoutManager layoutManager;
    private Markwon markwon;
    private NodesAdapter adapter;
    private DrawerLayout drawer;
    private RecyclerView sidebar;
    private LinearLayoutManager sidebarLayoutManager;
    private OutlineAdapter outlineAdapter;
    private Book book;
    private int savedReadPosition;
    private ParsedBook parsedBook;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reading_story);

        parsedBook = new ParsedBook();

        nodesView = (RecyclerView)findViewById(R.id.nodes_view);
        nodesView.setItemAnimator(null);

        layoutManager = new LinearLayoutManager(this);
        nodesView.setLayoutManager(layoutManager);

        markwon = NodesHelper.buildMarkwon(nodesView);
        adapter = new NodesAdapter(markwon, parsedBook);
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

        outlineAdapter = new OutlineAdapter(markwon, parsedBook);
        sidebar.setAdapter(outlineAdapter);

        drawer = (DrawerLayout)findViewById(R.id.drawer_layout);

        Intent intent = getIntent();
        book = Book.findById(this, intent.getLongExtra("_id", -1));

        book.lastOpenedAt(System.currentTimeMillis());
        book.save(this);

        ParseStoryTask parser = new ParseStoryTask(this, markwon, parsedBook);
        parser.execute(book);
    }

    @Override
    protected void onStop() {
        super.onStop();

        book.lastOpenedAt(System.currentTimeMillis());
        book.save(this);

        savePosition();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    void onParsedNodes() {
        adapter.notifyDataSetChanged();
        outlineAdapter.notifyDataSetChanged();
    }

    void restorePosition() {
        int lastReadPosition = book.lastReadPosition();
        savedReadPosition = lastReadPosition;

        scrollToPosition(lastReadPosition);
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

}
