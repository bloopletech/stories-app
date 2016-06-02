package bloople.net.stories;

import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Scanner;

/**
 * Created by i on 27/05/2016.
 */
public class Story {
    private static final float[] HEADER_SIZES = {
            1.5f, 1.4f, 1.3f, 1.2f, 1.1f, 1f,
    };

    private Scanner scanner;

    public Story(Reader reader) {
        scanner = new Scanner(new BufferedReader(reader));
        scanner.useDelimiter("\n\n");
    }

    public boolean hasNext() throws IOException {
        return scanner.hasNext();
    }

    public CharSequence next() throws IOException {
        String node = cleanString(scanner.next());

        if(node.startsWith("#")) {
            return parseHeading(node);
        }
        else {
            return node;
        }
    }

    public Spanned parseHeading(String node) {
        String[] elements = node.split("\\s+", 2);
        //TODO check more carefully
        int level = elements[0].length() - 1;
        if(level >= HEADER_SIZES.length) level = HEADER_SIZES.length - 1;

        SpannableString result = new SpannableString(elements[1]);
        result.setSpan(new RelativeSizeSpan(HEADER_SIZES[level]), 0, result.length(), 0);

        result.setSpan(new StyleSpan(Typeface.BOLD), 0, result.length(), 0);

        return result;
    }

    public String cleanString(String input) {
        return input.replaceAll("\\s+", " ").trim();
    }
}
