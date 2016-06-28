package net.bloople.stories;

import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;

public class NodeFactory {
    private static final float[] HEADER_SIZES = {
            1.5f, 1.4f, 1.3f, 1.2f, 1.1f, 1f,
    };

    public static Node heading(String content) {
        String[] elements = cleanString(content).split(" ", 2);

        if(elements.length < 2) return new Paragraph(cleanString(content));

        //TODO check more carefully
        int level = elements[0].length();
        if(level > HEADER_SIZES.length) level = HEADER_SIZES.length;

        SpannableString result = new SpannableString(elements[1]);
        result.setSpan(new RelativeSizeSpan(HEADER_SIZES[level - 1]), 0, result.length(), 0);

        result.setSpan(new StyleSpan(Typeface.BOLD), 0, result.length(), 0);

        return new Heading(result, level);
    }

    public static Node paragraph(String content) {
        return new Paragraph(cleanString(content));
    }

    public static String cleanString(String input) {
        return input.replaceAll("\\s+", " ").trim();
    }
}
