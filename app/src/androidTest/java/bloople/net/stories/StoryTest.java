package bloople.net.stories;

import android.graphics.Typeface;
import android.support.test.runner.AndroidJUnit4;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.rules.ExpectedException;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.NoSuchElementException;

import static junit.framework.Assert.assertEquals;

/**
 * Created by i on 3/06/2016.
 */
@RunWith(AndroidJUnit4.class)
public class StoryTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void story_next_paragraph() throws IOException {
        Reader reader = new StringReader("first paragraph\n\nsecond paragraph");
        Story story = new Story(reader);

        assertEquals(story.next(), "first paragraph");
        assertEquals(story.next(), "second paragraph");

        thrown.expect(NoSuchElementException.class);
        story.next();
    }

    @Test
    public void story_next_heading_h1() throws IOException {
        Reader reader = new StringReader("# Test Heading");
        Story story = new Story(reader);

        CharSequence element = story.next();
        assertEquals(element.getClass(), SpannableString.class);
        assertEquals(element.toString(), "Test Heading");
    }

    @Test
    public void story_parseHeading_h1() {
        Reader reader = new StringReader("");
        Story story = new Story(reader);

        Spanned result = story.parseHeading("# Test Heading");
        Object[] spans = result.getSpans(0, result.length(), Object.class);
        assertEquals(spans.length, 2);
        assertEquals(spans[0].getClass(), RelativeSizeSpan.class);
        assertEquals(((RelativeSizeSpan)spans[0]).getSizeChange(), 1.5f);
        assertEquals(spans[1].getClass(), StyleSpan.class);
        assertEquals(((StyleSpan)spans[1]).getStyle(), Typeface.BOLD);
        assertEquals(result.toString(), "Test Heading");
    }

    @Test
    public void story_parseHeading_h2() {
        Reader reader = new StringReader("");
        Story story = new Story(reader);

        Spanned result = story.parseHeading("## Test Heading");
        Object[] spans = result.getSpans(0, result.length(), Object.class);
        assertEquals(spans.length, 2);
        assertEquals(spans[0].getClass(), RelativeSizeSpan.class);
        assertEquals(((RelativeSizeSpan) spans[0]).getSizeChange(), 1.4f);
        assertEquals(spans[1].getClass(), StyleSpan.class);
        assertEquals(((StyleSpan)spans[1]).getStyle(), Typeface.BOLD);
        assertEquals(result.toString(), "Test Heading");
    }

    @Test
    public void story_parseHeading_h10() {
        Reader reader = new StringReader("");
        Story story = new Story(reader);

        Spanned result = story.parseHeading("########## Test Heading");
        Object[] spans = result.getSpans(0, result.length(), Object.class);
        assertEquals(spans.length, 2);
        assertEquals(spans[0].getClass(), RelativeSizeSpan.class);
        assertEquals(((RelativeSizeSpan)spans[0]).getSizeChange(), 1f);
        assertEquals(spans[1].getClass(), StyleSpan.class);
        assertEquals(((StyleSpan)spans[1]).getStyle(), Typeface.BOLD);
        assertEquals(result.toString(), "Test Heading");
    }

    @Test
    public void story_cleanString() {
        Reader reader = new StringReader("");
        Story story = new Story(reader);

        assertEquals(story.cleanString("Excellent potatos\n are actually not \n\ngreat\n"),
                "Excellent potatos are actually not great");
    }
}
