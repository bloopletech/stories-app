package net.bloople.stories;

import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;

class NodeRenderer {
    private static final float[] HEADER_SIZES = {
            1.5f, 1.4f, 1.3f, 1.2f, 1.1f, 1f,
    };

    CharSequence render(String content) {
        if(content.startsWith("#")) {
            return heading(content);
        }
        else {
            return paragraph(content);
        }
    }

    static boolean isOutline(String content) {
        return content.startsWith("#");
    }

    private CharSequence heading(String content) {
        String[] elements = content.split(" ", 2);

        if(elements.length < 2) return paragraph(content);

        //TODO check more carefully
        int level = elements[0].length();
        if(level > HEADER_SIZES.length) level = HEADER_SIZES.length;

        SpannableString result = new SpannableString(elements[1]);
        result.setSpan(new RelativeSizeSpan(HEADER_SIZES[level - 1]), 0, result.length(), 0);

        result.setSpan(new StyleSpan(Typeface.BOLD), 0, result.length(), 0);

        return result;
    }

    private CharSequence paragraph(String content) {
        return content;
    }

}
