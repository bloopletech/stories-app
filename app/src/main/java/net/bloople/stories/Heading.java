package net.bloople.stories;

public class Heading extends Node {
    private int level;

    public Heading(CharSequence content, int level) {
        super(content);
        this.level = level;
    }

    public int level() {
        return level;
    }

    public boolean isOutline() {
        return true;
    }
}
