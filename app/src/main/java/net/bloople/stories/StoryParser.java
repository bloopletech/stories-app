package net.bloople.stories;

import java.io.IOException;
import java.io.Reader;
import java.util.Scanner;

/**
 * Created by i on 27/05/2016.
 */
public class StoryParser {
    private Scanner scanner;

    public StoryParser(Reader reader) {
        scanner = new Scanner(reader);
        scanner.useDelimiter("(?:\r?\n){2,}+");
    }

    public boolean hasNext() throws IOException {
        return scanner.hasNext();
    }

    public Node next() throws IOException {
        String content = scanner.next();

        if(content.startsWith("#")) {
            return NodeFactory.heading(content);
        }
        else {
            return NodeFactory.paragraph(content);
        }
    }
}
