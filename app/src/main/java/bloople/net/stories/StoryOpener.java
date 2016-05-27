package bloople.net.stories;

import java.io.File;
import java.io.IOException;

/**
 * Created by i on 27/05/2016.
 */
public class StoryOpener {
    public Story open(File file) {
        try {
            StoryParser parser = new StoryParser(file);
            return parser.parse();
        }
        catch(IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
