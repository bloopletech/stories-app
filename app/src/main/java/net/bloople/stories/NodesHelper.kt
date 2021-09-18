package net.bloople.stories

import android.view.ViewGroup
import io.noties.markwon.Markwon
import android.widget.TextView
import io.noties.markwon.AbstractMarkwonPlugin
import io.noties.markwon.core.MarkwonTheme
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import org.commonmark.node.Heading
import org.commonmark.node.Node

internal object NodesHelper {
    private val HEADER_SIZES = floatArrayOf(
        1.5f, 1.4f, 1.3f, 1.2f, 1.1f, 1f
    )

    @JvmStatic
    fun buildMarkwon(parent: ViewGroup): Markwon {
        val nodeView = createNodeView(parent)
        val textView = nodeView.findViewById<TextView>(R.id.text_view)
        return Markwon.builder(parent.context).usePlugin(object : AbstractMarkwonPlugin() {
            override fun configureTheme(builder: MarkwonTheme.Builder) {
                builder.blockMargin(textView.paddingBottom)
                builder.blockQuoteColor(textView.currentTextColor)
                builder.bulletWidth(Math.round(textView.paddingBottom * 0.4).toInt())
                builder.headingBreakHeight(0)
                builder.headingTextSizeMultipliers(HEADER_SIZES)
                builder.headingTypeface(Typeface.create(textView.typeface, Typeface.BOLD))
                builder.thematicBreakColor(textView.currentTextColor)
            }
        }).build()
    }

    @JvmStatic
    fun createNodeView(parent: ViewGroup): View {
        return LayoutInflater.from(parent.context).inflate(R.layout.node_view, parent,false)
    }

    @JvmStatic
    fun findFirstHeading(node: Node): Heading? {
        if (node is Heading) return node
        var node: Node? = node.firstChild
        while (node != null) {
            val heading = findFirstHeading(node)
            if (heading != null) return heading
            node = node.next
        }
        return null
    }
}