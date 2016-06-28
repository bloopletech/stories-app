package bloople.net.stories;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.nbsp.materialfilepicker.MaterialFilePicker;

import java.io.File;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Utils.verifyStoragePermissions(this);

        MaterialFilePicker picker = new MaterialFilePicker();
        picker.withActivity(this).withClass(ReadingStoryActivity.class).withFilter(
                Pattern.compile(".*\\.txt?"));

        SharedPreferences preferences = getPreferences(Context.MODE_PRIVATE);
        String lastPath = preferences.getString("last_path", null);
        if (lastPath != null) picker.withPath(new File(lastPath).getParentFile().toString());

        picker.start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        System.out.println("in onActivityResult");
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_CANCELED) {
            finish();
        }
    }
}
