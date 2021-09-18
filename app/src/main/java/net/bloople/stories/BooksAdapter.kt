package net.bloople.stories

import androidx.recyclerview.widget.RecyclerView
import android.widget.TextView
import android.widget.ImageButton
import android.content.Intent
import android.database.Cursor
import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.View
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

internal class BooksAdapter(cursor: Cursor?) : CursorRecyclerAdapter<BooksAdapter.ViewHolder?>(cursor) {
    internal class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var titleView: TextView
        var sizeView: TextView
        var ageView: TextView?
        var lastOpenedView: TextView
        var openedCountView: TextView?
        var starView: ImageButton

        init {
            view.setOnClickListener { v: View ->
                val intent = Intent(v.context, ReadingStoryActivity::class.java)
                intent.putExtra("_id", itemId)
                v.context.startActivity(intent)
            }

            titleView = view.findViewById(R.id.story_title)
            sizeView = view.findViewById(R.id.story_size)
            ageView = view.findViewById(R.id.story_age)
            lastOpenedView = view.findViewById(R.id.story_last_opened)
            openedCountView = view.findViewById(R.id.story_opened_count)
            starView = view.findViewById(R.id.story_star)

            starView.setOnClickListener { v: View ->
                Book.edit(v.context, itemId) {
                    starred = !starred
                    v.isActivated = starred
                }
            }
        }
    }

    private val DATE_FORMAT = SimpleDateFormat("d MMMM yyyy", Locale.getDefault())

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_book, parent,false)
        return ViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: ViewHolder?, cursor: Cursor) {
        val book = Book(cursor)
        holder!!.titleView.text = book.title
        holder.sizeView.text = getReadableFileSize(book.size)

        if(holder.ageView != null) {
            val age = DATE_FORMAT.format(Date(book.mtime))
            holder.ageView!!.text = age
        }

        val lastOpenedMillis = book.lastOpenedAt
        if(lastOpenedMillis > 0L) {
            val lastOpened = DATE_FORMAT.format(Date(lastOpenedMillis))
            holder.lastOpenedView.text = lastOpened
        }
        else {
            holder.lastOpenedView.text = "Never"
        }

        if(holder.openedCountView != null) {
            holder.openedCountView!!.text = book.openedCount.toString()
        }

        holder.starView.isActivated = book.starred
    }

    companion object {
        //Copied from https://github.com/nbsp-team/MaterialFilePicker
        fun getReadableFileSize(size: Long): String {
            if(size <= 0) return "0"
            val units = arrayOf("B", "KB", "MB", "GB", "TB")
            val digitGroups = (Math.log10(size.toDouble()) / Math.log10(1024.0)).toInt()
            return DecimalFormat("#").format(size / Math.pow(1024.0, digitGroups.toDouble())) + " " + units[digitGroups]
        }
    }
}