package net.bloople.stories;

public abstract class Node {
    private CharSequence content;

    public Node(CharSequence content) {
        this.content = content;
    }

    public CharSequence content() {
        return content;
    }

    public abstract boolean isOutline();
}
