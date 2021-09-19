package net.bloople.stories

import android.app.Activity
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.LinearLayoutManager
import io.noties.markwon.Markwon
import androidx.drawerlayout.widget.DrawerLayout
import android.os.Bundle
import android.os.AsyncTask
import org.commonmark.node.Node
import java.io.BufferedReader
import java.io.FileReader
import java.io.IOException
import java.util.ArrayList

class ReadingStoryActivity : Activity() {
    private lateinit var layoutManager: LinearLayoutManager
    private lateinit var markwon: Markwon
    private lateinit var adapter: NodesAdapter
    private lateinit var drawer: DrawerLayout
    private lateinit var outlineAdapter: OutlineAdapter
    private var bookId: Long = -1
    private var savedReadPosition = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reading_story)

        val nodesView: RecyclerView = findViewById(R.id.nodes_view)
        nodesView.itemAnimator = null

        layoutManager = LinearLayoutManager(this)
        nodesView.layoutManager = layoutManager

        markwon = NodesHelper.buildMarkwon(nodesView)
        adapter = NodesAdapter(markwon)
        nodesView.adapter = adapter
        nodesView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if(newState == RecyclerView.SCROLL_STATE_IDLE) savePosition()
            }
        })

        val sidebar: RecyclerView = findViewById(R.id.sidebar)
        val sidebarLayoutManager = LinearLayoutManager(this)
        sidebar.layoutManager = sidebarLayoutManager

        outlineAdapter = OutlineAdapter(markwon)
        sidebar.adapter = outlineAdapter

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
        val currentReadPosition = layoutManager.findFirstVisibleItemPosition()
        if(savedReadPosition != currentReadPosition) {
            Book.edit(this, bookId) { lastReadPosition = currentReadPosition }
            savedReadPosition = currentReadPosition
        }
    }

    fun scrollToPosition(position: Int) {
        layoutManager.scrollToPositionWithOffset(position, 0)
    }

    fun closeDrawers() {
        drawer.closeDrawers()
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
                    val node = markwon.parse(parser.next())
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
            val countBefore = adapter.itemCount

            adapter.addAll(nodesArgs[0]!!)

            val outlineNodes: MutableList<Node> = ArrayList()
            val outlineNodesMap: MutableList<Int> = ArrayList()

            for(i in nodesArgs[0]!!.indices) {
                val node = nodesArgs[0]!![i]
                val heading = NodesHelper.findFirstHeading(node)
                if(heading != null) {
                    outlineNodes.add(heading)
                    outlineNodesMap.add(countBefore + i)
                }
            }

            outlineAdapter.addAll(outlineNodes, outlineNodesMap)

            val lastReadPosition = Book.find(this@ReadingStoryActivity, bookId).lastReadPosition
            if(!setPosition && adapter.itemCount >= lastReadPosition) {
                setPosition = true
                savedReadPosition = lastReadPosition
                scrollToPosition(lastReadPosition)
            }
        }

        override fun onPostExecute(result: Void?) {}
    }
}