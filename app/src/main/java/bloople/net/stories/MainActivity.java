package bloople.net.stories;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Utils.verifyStoragePermissions(this);

        RecyclerView nodesView = (RecyclerView)findViewById(R.id.nodes_view);

        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        nodesView.setLayoutManager(linearLayoutManager);

        final List<CharSequence> nodes = (new StoryOpener(this)).open().nodes();
        final NodesAdapter adapter = new NodesAdapter(nodes);
        nodesView.setAdapter(adapter);
    }
}
