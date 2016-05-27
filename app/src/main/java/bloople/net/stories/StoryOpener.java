package bloople.net.stories;

import java.io.File;
import java.io.IOException;

/**
 * Created by i on 27/05/2016.
 */
public class StoryOpener {
    public Story open(String path) {
        try {
            StoryParser parser = new StoryParser(new File(path));
            return parser.parse();
        }
        catch(IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
