package net.bloople.stories;

public class Heading extends Node {
    private int level;

    public Heading(CharSequence inContent, int inLevel) {
        super(inContent);
        level = inLevel;
    }

    public int level() {
        return level;
    }
}
