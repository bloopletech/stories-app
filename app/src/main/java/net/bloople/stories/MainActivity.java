package net.bloople.stories;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import java.io.File;

public class MainActivity extends AppCompatActivity {
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

        int permission = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        canAccessFiles = false;
        if(permission == PackageManager.PERMISSION_GRANTED) canAccessFiles = true;
        else ActivityCompat.requestPermissions(this, PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE);

        Button indexButton = (Button)findViewById(R.id.button);
        indexButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startIndexing();
            }
        });

        Button listStoriesButton = (Button)findViewById(R.id.button2);
        listStoriesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ListActivity.class);
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
            new StoriesIndexer(this).indexDirectory(
                    new File(Environment.getExternalStorageDirectory().getAbsolutePath())
            );
        }
    }
}
