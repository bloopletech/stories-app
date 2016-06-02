package bloople.net.stories;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.nbsp.materialfilepicker.MaterialFilePicker;
import com.nbsp.materialfilepicker.ui.FilePickerActivity;

import java.io.File;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {
    public final static String EXTRA_MESSAGE = "net.bloople.stories.MESSAGE";
    public final static int PICKER_CODE = 1;

    private String pendingPath = null;
    private String lastPath = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Utils.verifyStoragePermissions(this);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == PICKER_CODE) {
            if(resultCode == RESULT_OK) {
                pendingPath = data.getStringExtra(FilePickerActivity.RESULT_FILE_PATH);
            }
            else if(resultCode == RESULT_CANCELED) {
                finish();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(pendingPath != null) {
            lastPath = pendingPath;
            Intent intent = new Intent(this, ReadingStoryActivity.class);
            intent.putExtra(EXTRA_MESSAGE, pendingPath);
            startActivity(intent);
        }
        else {
            MaterialFilePicker picker = new MaterialFilePicker();
            picker.withActivity(this).withRequestCode(PICKER_CODE).withFilter(
                    Pattern.compile(".*\\.txt?"));

            if(lastPath != null) picker.withRootPath(new File(lastPath).getParentFile().toString());

            picker.start();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        pendingPath = null;
    }
}
