package net.bloople.stories;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by i on 27/05/2016.
 */
public class Story {
    private ArrayList<Node> nodes;

    public Story() {
        nodes = new ArrayList<>();
    }

    public void add(Node node) {
        nodes.add(node);
    }

    public List<Node> nodes() {
        return nodes;
    }
}
