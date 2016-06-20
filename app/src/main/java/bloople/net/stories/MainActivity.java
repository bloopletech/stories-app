package bloople.net.stories;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.nbsp.materialfilepicker.MaterialFilePicker;
import com.nbsp.materialfilepicker.ui.FilePickerActivity;
import com.nbsp.materialfilepicker.utils.FileComparator;
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

        //if(lastPath != null) pickerState.setPath(new File(lastPath).getParentFile().toString());
        //picker.withPickerState(pickerState);

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
