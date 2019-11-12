package net.bloople.stories;

import android.graphics.Typeface;
import androidx.test.runner.AndroidJUnit4;
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
        CharSequence node = NodeFactory.paragraph("test paragraph content");

        assertEquals(node, "test paragraph content");
    }

    @Test
    public void factory_heading_h1() {
        SpannableString result = (SpannableString)NodeFactory.heading("# Test Heading");

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
        SpannableString result = (SpannableString)NodeFactory.heading("## Test Heading");

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
        SpannableString result = (SpannableString)NodeFactory.heading("########## Test Heading");

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
