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
import android.support.annotation.NonNull;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class IndexingActivity extends Activity {
    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    private ProgressBar progressBar;
    private Button indexButton;
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

        progressBar = (ProgressBar)findViewById(R.id.indexing_progress);

        indexButton = (Button)findViewById(R.id.index_button);
        indexButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                indexButton.setEnabled(false);
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

        final EditText indexDirectoryText = (EditText)findViewById(R.id.index_directory);
        indexDirectoryText.setText(indexRoot);

        indexDirectoryText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) ||
                        (actionId == EditorInfo.IME_ACTION_DONE)) {
                    indexRoot = indexDirectoryText.getText().toString();
                    savePreferences();
                }
                return false;
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_CANCELED) finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
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

    private class IndexingTask extends AsyncTask<Void, Integer, Void> {
        private int indexed;
        private int count;

        protected Void doInBackground(Void... params) {
            indexDirectory(new File(indexRoot));
            publishProgress(indexed, count);
            return null;
        }

        protected void onProgressUpdate(Integer... args) {
            progressBar.setProgress(args[0]);
            progressBar.setMax(args[1]);
        }

        protected void onPostExecute(Void result) {
            indexButton.setEnabled(true);
            Toast.makeText(IndexingActivity.this, "Indexing complete, " + count + " stories indexed.",
                    Toast.LENGTH_LONG).show();
        }

        void indexDirectory(File directory) {
            File[] files = directory.listFiles();

            if(files == null) return;

            ArrayList<File> filesToIndex = new ArrayList<>();

            for(File f : files) {
                if(f.isDirectory()) {
                    indexDirectory(f);
                }
                else {
                    String name = f.getName();
                    String ext = name.substring(name.lastIndexOf('.') + 1);

                    if(ext.equals("txt")) filesToIndex.add(f);
                }
            }

            count += filesToIndex.size();
            publishProgress(indexed, count);

            for(File f : filesToIndex) indexFile(f);
        }

        void indexFile(File file) {
            try {
                Book book = Book.findByPath(IndexingActivity.this, file.getCanonicalPath());
                if(book == null) book = new Book();

                book.path(file.getCanonicalPath());
                book.title(file.getName().replaceAll("\\.txt$", ""));
                book.mtime(file.lastModified());
                book.size(file.length());

                book.save(IndexingActivity.this);
                indexed++;
            }
            catch(IOException e) {
                e.printStackTrace();
            }

            publishProgress(indexed, count);
        }
    }
}
