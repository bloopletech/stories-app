package net.bloople.stories;

public class Heading extends Node {
    private int level;

    public Heading(CharSequence content, int inLevel) {
        super(content);
        level = inLevel;
    }

    public int level() {
        return level;
    }

    public boolean isOutline() {
        return true;
    }
}
