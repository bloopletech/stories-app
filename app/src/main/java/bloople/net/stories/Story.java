package bloople.net.stories;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by i on 27/05/2016.
 */
public class Story {
    private List<CharSequence> nodes;

    public Story() {
        nodes = new ArrayList<>();
    }

    public void addNode(CharSequence spanned) {
        nodes.add(spanned);
    }

    public List<CharSequence> nodes() {
        return Collections.unmodifiableList(nodes);
    }
}
