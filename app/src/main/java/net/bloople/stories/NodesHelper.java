package net.bloople.stories;

import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;

import org.commonmark.node.Heading;
import org.commonmark.node.Node;

import io.noties.markwon.AbstractMarkwonPlugin;
import io.noties.markwon.Markwon;
import io.noties.markwon.core.MarkwonTheme;

class NodesHelper {
    private static final float[] HEADER_SIZES = {
            1.5f, 1.4f, 1.3f, 1.2f, 1.1f, 1f,
    };

    static Markwon buildMarkwon(ViewGroup parent) {
        View nodeView = createNodeView(parent);
        final TextView textView = nodeView.findViewById(R.id.text_view);

        return Markwon.builder(parent.getContext())
                .usePlugin(new AbstractMarkwonPlugin() {
                    @Override
                    public void configureTheme(@NonNull MarkwonTheme.Builder builder) {
                        builder.blockMargin(textView.getPaddingBottom());

                        builder.blockQuoteColor(textView.getCurrentTextColor());

                        builder.bulletWidth((int)Math.round(textView.getPaddingBottom() * 0.4));

                        builder.headingBreakHeight(0);
                        builder.headingTextSizeMultipliers(HEADER_SIZES);
                        builder.headingTypeface(Typeface.create(textView.getTypeface(), Typeface.BOLD));

                        builder.thematicBreakColor(textView.getCurrentTextColor());
                    }
                })
                .build();
    }

    static View createNodeView(ViewGroup parent) {
        return LayoutInflater.from(parent.getContext()).inflate(R.layout.node_view, parent,
                false);

    }

    static Heading findFirstHeading(Node node) {
        if(node instanceof Heading) return (Heading)node;

        node = node.getFirstChild();
        while(node != null) {
            Heading heading = findFirstHeading(node);
            if(heading != null) return heading;
            node = node.getNext();
        }

        return null;
    }
}
