package net.bloople.stories;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;

import static android.R.attr.duration;

public class MainActivity extends Activity {
    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    private boolean canAccessFiles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(android.os.Build.VERSION.SDK_INT >= 23) {
            int permission = checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);

            canAccessFiles = false;
            if(permission == PackageManager.PERMISSION_GRANTED) canAccessFiles = true;
            else requestPermissions(PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE);
        }
        else {
            canAccessFiles = true;
        }

        Button indexButton = (Button)findViewById(R.id.index_button);
        indexButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startIndexing();
            }
        });

        Button deleteIndexButton = (Button)findViewById(R.id.delete_index_button);
        deleteIndexButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseHelper.deleteDatabase(MainActivity.this);
                Toast.makeText(MainActivity.this, "Index deleted.", Toast.LENGTH_SHORT).show();
            }
        });

        Button listStoriesButton = (Button)findViewById(R.id.list_stories_button);
        listStoriesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, BooksActivity.class);
                startActivity(intent);
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_CANCELED) finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if(requestCode == REQUEST_EXTERNAL_STORAGE && grantResults.length > 0 &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED) canAccessFiles = true;
    }

    public void startIndexing() {
        if(canAccessFiles) {
            IndexingTask indexer = new IndexingTask();
            indexer.execute();
        }
    }

    private class IndexingTask extends AsyncTask<Void, Void, Integer> {
        protected Integer doInBackground(Void... params) {
            StoriesIndexer indexer = new StoriesIndexer(MainActivity.this);
            indexer.indexDirectory(
                    new File(Environment.getExternalStorageDirectory().getAbsolutePath())
            );

            return indexer.count();
        }

        protected void onPostExecute(Integer count) {
            Toast.makeText(MainActivity.this, "Indexing complete, " + count + " stories indexed.",
                    Toast.LENGTH_SHORT).show();
        }

    }
}
