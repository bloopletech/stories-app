package bloople.net.stories;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.nbsp.materialfilepicker.MaterialFilePicker;
import com.nbsp.materialfilepicker.utils.PickerState;

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

        picker.start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_CANCELED) {
            finish();
        }
    }
}
