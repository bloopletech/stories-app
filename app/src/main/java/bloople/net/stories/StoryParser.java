package bloople.net.stories;

import android.text.Html;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;

/**
 * Created by i on 27/05/2016.
 */
public class StoryParser {
    private File file;

    public StoryParser(File inFile) {
        file = inFile;
    }

    public Story parse() throws IOException {
        Document doc = Jsoup.parse(file, null);

        Elements nodes = doc.select("h1, h2, h3, h4, h5, h6, p");

        Story story = new Story();

        for(Element node : nodes) {
            CharSequence span = trimTrailingWhitespace(Html.fromHtml(node.outerHtml()));
            story.addNode(span);
        }

        return story;
    }

    /** Trims trailing whitespace. Removes any of these characters:
     * 0009, HORIZONTAL TABULATION
     * 000A, LINE FEED
     * 000B, VERTICAL TABULATION
     * 000C, FORM FEED
     * 000D, CARRIAGE RETURN
     * 001C, FILE SEPARATOR
     * 001D, GROUP SEPARATOR
     * 001E, RECORD SEPARATOR
     * 001F, UNIT SEPARATOR
     * @return "" if source is null, otherwise string with all trailing whitespace removed
     */
    private static CharSequence trimTrailingWhitespace(CharSequence source) {
        if(source == null) return "";

        int i = source.length();

        // loop back to the first non-whitespace character
        while(--i >= 0 && Character.isWhitespace(source.charAt(i)));

        return source.subSequence(0, i + 1);
    }
}
