package bloople.net.stories;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.List;

public class ReadingStoryActivity extends AppCompatActivity {
    private RecyclerView nodesView;
    private LinearLayoutManager layoutManager;
    private String path = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reading_story);

        nodesView = (RecyclerView)findViewById(R.id.nodes_view);

        layoutManager = new LinearLayoutManager(this);
        nodesView.setLayoutManager(layoutManager);

        Intent intent = getIntent();
        path = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);

        final List<CharSequence> nodes = (new StoryOpener()).open(path).nodes();
        final NodesAdapter adapter = new NodesAdapter(nodes);
        nodesView.setAdapter(adapter);

        SharedPreferences preferences = getPreferences(Context.MODE_PRIVATE);
        nodesView.scrollToPosition(preferences.getInt(path, 0));
    }

    @Override
    protected void onStop() {
        super.onStop();

        SharedPreferences preferences = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(path, layoutManager.findFirstVisibleItemPosition());
        editor.apply();
    }
}
