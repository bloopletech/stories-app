package net.bloople.stories;

class Paragraph extends Node {
    Paragraph(CharSequence content) {
        super(content);
    }

    public boolean isOutline() {
        return false;
    }
}
