package net.bloople.stories

import net.bloople.stories.NodesHelper.buildMarkwon
import net.bloople.stories.NodesHelper.findFirstHeading
import android.app.Activity
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.LinearLayoutManager
import io.noties.markwon.Markwon
import androidx.drawerlayout.widget.DrawerLayout
import android.os.Bundle
import android.os.AsyncTask
import android.view.View
import org.commonmark.node.Node
import java.io.BufferedReader
import java.io.FileReader
import java.io.IOException
import java.util.ArrayList

class ReadingStoryActivity : Activity() {
    private var nodesView: RecyclerView? = null
    private var layoutManager: LinearLayoutManager? = null
    private var markwon: Markwon? = null
    private var adapter: NodesAdapter? = null
    private var drawer: DrawerLayout? = null
    private var sidebar: RecyclerView? = null
    private var sidebarLayoutManager: LinearLayoutManager? = null
    private var outlineAdapter: OutlineAdapter? = null
    private var bookId: Long = -1
    private var savedReadPosition = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reading_story)

        nodesView = findViewById(R.id.nodes_view)
        nodesView!!.itemAnimator = null

        layoutManager = LinearLayoutManager(this)
        nodesView!!.layoutManager = layoutManager

        markwon = buildMarkwon(nodesView!!)
        adapter = NodesAdapter(markwon!!)
        nodesView!!.adapter = adapter
        nodesView!!.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if(newState == RecyclerView.SCROLL_STATE_IDLE) savePosition()
            }
        })

        sidebar = findViewById<View>(R.id.sidebar) as RecyclerView
        sidebarLayoutManager = LinearLayoutManager(this)
        sidebar!!.layoutManager = sidebarLayoutManager

        outlineAdapter = OutlineAdapter(markwon!!)
        sidebar!!.adapter = outlineAdapter

        drawer = findViewById(R.id.drawer_layout)

        bookId = Book.idFrom(intent)

        val book = Book.find(this, bookId)
        book.edit(this) {
            lastOpenedAt = System.currentTimeMillis()
            openedCount += 1
        }

        val parser = ParseStoryTask()
        parser.execute(book)
    }

    override fun onStop() {
        super.onStop()
        Book.edit(this, bookId) { lastOpenedAt = System.currentTimeMillis() }
        savePosition()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    fun savePosition() {
        val currentReadPosition = layoutManager!!.findFirstVisibleItemPosition()
        if(savedReadPosition != currentReadPosition) {
            Book.edit(this, bookId) { lastReadPosition = currentReadPosition }
            savedReadPosition = currentReadPosition
        }
    }

    fun scrollToPosition(position: Int) {
        layoutManager!!.scrollToPositionWithOffset(position, 0)
    }

    fun closeDrawers() {
        drawer!!.closeDrawers()
    }

    private inner class ParseStoryTask : AsyncTask<Book?, List<Node>?, Void?>() {
        var BATCH_SIZE = 50
        private var setPosition = false

        override fun doInBackground(vararg bookArgs: Book?): Void? {
            val book = bookArgs[0]
            try {
                val parser = StoryParser(BufferedReader(FileReader(book!!.path)))
                var accumulator: MutableList<Node> = ArrayList()

                while(parser.hasNext()) {
                    val node = markwon!!.parse(parser.next())
                    if(node.firstChild == null) continue

                    accumulator.add(node)

                    if(accumulator.size >= BATCH_SIZE) {
                        publishProgress(accumulator)
                        accumulator = ArrayList()
                    }
                }

                publishProgress(accumulator)
            }
            catch(e: IOException) {
                e.printStackTrace()
            }
            return null
        }

        override fun onProgressUpdate(vararg nodesArgs: List<Node>?) {
            val countBefore = adapter!!.itemCount

            adapter!!.addAll(nodesArgs[0]!!)

            val outlineNodes: MutableList<Node> = ArrayList()
            val outlineNodesMap: MutableList<Int> = ArrayList()

            for(i in nodesArgs[0]!!.indices) {
                val node = nodesArgs[0]!![i]
                val heading = findFirstHeading(node)
                if(heading != null) {
                    outlineNodes.add(heading)
                    outlineNodesMap.add(countBefore + i)
                }
            }

            outlineAdapter!!.addAll(outlineNodes, outlineNodesMap)

            val lastReadPosition = Book.find(this@ReadingStoryActivity, bookId).lastReadPosition
            if(!setPosition && adapter!!.itemCount >= lastReadPosition) {
                setPosition = true
                savedReadPosition = lastReadPosition
                scrollToPosition(lastReadPosition)
            }
        }

        override fun onPostExecute(result: Void?) {}
    }
}