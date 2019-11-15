package net.bloople.stories;

import org.commonmark.node.Node;

import java.io.IOException;
import java.io.Reader;
import java.util.Scanner;

import io.noties.markwon.Markwon;

class StoryParser {
    private Markwon markwon;
    private Scanner scanner;

    StoryParser(Markwon markwon, Reader reader) {
        this.markwon = markwon;
        scanner = new Scanner(reader);
        scanner.useDelimiter("(?:\r?\n[\u200B\uFEFF]*){2,}+");
    }

    boolean hasNext() throws IOException {
        return scanner.hasNext();
    }

    Node next() throws IOException {
        Node node = markwon.parse(scanner.next());
        if(node.getFirstChild() == null) return null;
        return node;
    }
}
