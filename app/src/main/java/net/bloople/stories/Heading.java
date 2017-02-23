package net.bloople.stories;

class Heading extends Node {
    private int level;

    Heading(CharSequence content, int level) {
        super(content);
        this.level = level;
    }

    int level() {
        return level;
    }

    public boolean isOutline() {
        return true;
    }
}
