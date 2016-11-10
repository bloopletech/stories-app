package net.bloople.stories;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;

public class IndexingActivity extends Activity {
    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    private String indexRoot;
    private boolean canAccessFiles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_indexing);

        if(android.os.Build.VERSION.SDK_INT >= 23) {
            int permission = checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);

            canAccessFiles = false;
            if(permission == PackageManager.PERMISSION_GRANTED) canAccessFiles = true;
            else requestPermissions(PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE);
        }
        else {
            canAccessFiles = true;
        }

        final Button indexButton = (Button)findViewById(R.id.index_button);
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
                DatabaseHelper.deleteDatabase(IndexingActivity.this);
                Toast.makeText(IndexingActivity.this, "Index deleted.", Toast.LENGTH_SHORT).show();
            }
        });

        loadPreferences();

        EditText indexDirectoryText = (EditText)findViewById(R.id.index_directory);
        indexDirectoryText.setText(indexRoot);
        indexDirectoryText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                indexRoot = s.toString();
                savePreferences();
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

    private SharedPreferences preferences() {
        return getApplicationContext().getSharedPreferences("main", Context.MODE_PRIVATE);
    }

    private void loadPreferences() {
        SharedPreferences preferences = preferences();

        indexRoot = preferences.getString("index-root",
                Environment.getExternalStorageDirectory().getAbsolutePath());
    }

    private void savePreferences() {
        SharedPreferences.Editor editor = preferences().edit();
        editor.putString("index-root", indexRoot);
        editor.apply();
    }

    public void startIndexing() {
        if(canAccessFiles) {
            IndexingTask indexer = new IndexingTask();
            indexer.execute();
        }
    }

    private class IndexingTask extends AsyncTask<Void, Void, Integer> {
        protected Integer doInBackground(Void... params) {
            StoriesIndexer indexer = new StoriesIndexer(IndexingActivity.this);
            indexer.indexDirectory(new File(indexRoot));

            return indexer.count();
        }

        protected void onPostExecute(Integer count) {
            Toast.makeText(IndexingActivity.this, "Indexing complete, " + count + " stories indexed.",
                    Toast.LENGTH_SHORT).show();
        }

    }
}