package bloople.net.stories;

import android.app.Activity;
import android.os.Environment;

import java.io.File;
import java.io.IOException;

/**
 * Created by i on 27/05/2016.
 */
public class StoryOpener {
    private Activity activity;

    public StoryOpener(Activity inActivity) {
        activity = inActivity;
    }

    public Story open() {
        try {
            File file = new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOWNLOADS), "test2.html");

            StoryParser parser = new StoryParser(file);
            return parser.parse();
        }
        catch(IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
