package bloople.net.stories;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.nbsp.materialfilepicker.MaterialFilePicker;
import com.nbsp.materialfilepicker.ui.FilePickerActivity;

import java.io.File;
import java.util.List;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {
    private RecyclerView nodesView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Utils.verifyStoragePermissions(this);

        nodesView = (RecyclerView)findViewById(R.id.nodes_view);

        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        nodesView.setLayoutManager(linearLayoutManager);

        new MaterialFilePicker()
                .withActivity(this)
                .withRequestCode(1)
                .withFilter(Pattern.compile(".*\\.html?")) // Filtering files and directories by file name using regexp
                .start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK) {
            String path = data.getStringExtra(FilePickerActivity.RESULT_FILE_PATH);

            final List<CharSequence> nodes = (new StoryOpener()).open(new File(path)).nodes();
            final NodesAdapter adapter = new NodesAdapter(nodes);
            nodesView.setAdapter(adapter);
        }
    }
}
