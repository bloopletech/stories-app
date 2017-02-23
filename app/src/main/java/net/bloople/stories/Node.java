package net.bloople.stories;

abstract class Node {
    private CharSequence content;

    Node(CharSequence content) {
        this.content = content;
    }

    public CharSequence content() {
        return content;
    }

    public abstract boolean isOutline();
}
