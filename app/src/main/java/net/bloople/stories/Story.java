package net.bloople.stories;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
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

    public static Story parseStory(String path) {
        try {
            Story story = new Story();
            StoryParser parser = new StoryParser(new BufferedReader(new FileReader(path)));
            parser.parse(story);

            return story;
        }
        catch(IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
