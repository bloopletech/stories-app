package net.bloople.stories;

import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.rules.ExpectedException;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.NoSuchElementException;

import static junit.framework.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class StoryParserTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void parser_next_paragraph() throws IOException {
        Reader reader = new StringReader("first paragraph\n\nsecond paragraph");
        StoryParser parser = new StoryParser(reader);

        assertEquals(parser.next(), "first paragraph");
        assertEquals(parser.next(), "second paragraph");

        thrown.expect(NoSuchElementException.class);
        parser.next();
    }

    @Test
    public void parser_next_heading_h1() throws IOException {
        Reader reader = new StringReader("# Test Heading");
        StoryParser parser = new StoryParser(reader);

        String node = parser.next();
        assertEquals(node, "# Test Heading");
    }
}
