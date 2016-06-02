package bloople.net.stories;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by i on 27/05/2016.
 */
public class StoryParser {
    public static List<CharSequence> parse(String path) {
        try {
            ArrayList<CharSequence> nodes = new ArrayList<>();

            Story story = new Story(new FileReader(new File(path)));

            while(story.hasNext()) {
                nodes.add(story.next());
            }

            return nodes;
        }
        catch(IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
