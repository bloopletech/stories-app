package net.bloople.stories;

public class Paragraph extends Node {
    public Paragraph(CharSequence content) {
        super(content);
    }

    public boolean isOutline() {
        return false;
    }
}
