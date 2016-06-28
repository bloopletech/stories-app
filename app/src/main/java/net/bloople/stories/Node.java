package net.bloople.stories;

public abstract class Node {
    private CharSequence content;

    public Node(CharSequence inContent) {
        content = inContent;
    }

    public CharSequence content() {
        return content;
    }
}
