package net.bloople.stories;

import android.graphics.Typeface;
import android.support.test.runner.AndroidJUnit4;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;

import org.junit.Test;
import org.junit.runner.RunWith;

import static junit.framework.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class NodeFactoryTest {
    @Test
    public void factory_paragraph() {
        Paragraph node = (Paragraph)NodeFactory.paragraph("test paragraph content");

        assertEquals(node.content(), "test paragraph content");
    }

    @Test
    public void factory_heading_h1() {
        Heading node = (Heading)NodeFactory.heading("# Test Heading");

        assertEquals(node.level(), 1);

        SpannableString result = (SpannableString)node.content();
        Object[] spans = result.getSpans(0, result.length(), Object.class);
        assertEquals(spans.length, 2);
        assertEquals(spans[0].getClass(), RelativeSizeSpan.class);
        assertEquals(((RelativeSizeSpan)spans[0]).getSizeChange(), 1.5f);
        assertEquals(spans[1].getClass(), StyleSpan.class);
        assertEquals(((StyleSpan)spans[1]).getStyle(), Typeface.BOLD);
        assertEquals(result.toString(), "Test Heading");
    }

    @Test
    public void factory_heading_h2() {
        Heading node = (Heading)NodeFactory.heading("## Test Heading");

        assertEquals(node.level(), 2);

        SpannableString result = (SpannableString)node.content();
        Object[] spans = result.getSpans(0, result.length(), Object.class);
        assertEquals(spans.length, 2);
        assertEquals(spans[0].getClass(), RelativeSizeSpan.class);
        assertEquals(((RelativeSizeSpan)spans[0]).getSizeChange(), 1.4f);
        assertEquals(spans[1].getClass(), StyleSpan.class);
        assertEquals(((StyleSpan)spans[1]).getStyle(), Typeface.BOLD);
        assertEquals(result.toString(), "Test Heading");
    }

    @Test
    public void factory_heading_h10() {
        Heading node = (Heading)NodeFactory.heading("########## Test Heading");

        assertEquals(node.level(), 6);

        SpannableString result = (SpannableString)node.content();
        Object[] spans = result.getSpans(0, result.length(), Object.class);
        assertEquals(spans.length, 2);
        assertEquals(spans[0].getClass(), RelativeSizeSpan.class);
        assertEquals(((RelativeSizeSpan)spans[0]).getSizeChange(), 1f);
        assertEquals(spans[1].getClass(), StyleSpan.class);
        assertEquals(((StyleSpan)spans[1]).getStyle(), Typeface.BOLD);
        assertEquals(result.toString(), "Test Heading");
    }

    @Test
    public void factory_cleanString() {
        assertEquals(NodeFactory.cleanString("Excellent potatos\n are actually not \n\ngreat\n"),
                "Excellent potatos are actually not great");
    }
}
