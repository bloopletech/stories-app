package net.bloople.stories

import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.LinearLayoutManager
import android.widget.TextView
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import android.view.inputmethod.EditorInfo
import android.content.Intent
import android.database.Cursor
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.appcompat.widget.Toolbar

class BooksActivity : AppCompatActivity() {
    private var model: IndexViewModel? = null
    private var listView: RecyclerView? = null
    private var adapter: BooksAdapter? = null
    private var layoutManager: LinearLayoutManager? = null
    private var searchResultsToolbar: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_books)

        model = ViewModelProvider(this).get(IndexViewModel::class.java)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        searchResultsToolbar = findViewById(R.id.search_results_toolbar)

        model!!.sorterDescription.observe(this, { description: String -> searchResultsToolbar!!.setText(description) })

        val searchField: EditText = findViewById(R.id.searchText)
        searchField.setOnEditorActionListener { v: TextView, actionId: Int, _: KeyEvent? ->
            var handled = false
            if(actionId == EditorInfo.IME_ACTION_SEARCH) {
                val `in` = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                `in`.hideSoftInputFromWindow(searchField.windowToken, 0)
                searchField.clearFocus()

                model!!.setSearchText(v.text.toString())

                handled = true
            }
            handled
        }

        searchField.setOnTouchListener { _: View?, event: MotionEvent ->
            val DRAWABLE_RIGHT = 2
            if(event.action == MotionEvent.ACTION_UP) {
                val clickIndex = searchField.right - searchField.compoundDrawables[DRAWABLE_RIGHT].bounds.width()
                if(event.rawX >= clickIndex) {
                    searchField.setText("")
                    searchField.clearFocus()
                    model!!.setSearchText("")
                    return@setOnTouchListener true
                }
            }
            false
        }

        listView = findViewById(R.id.stories_list)
        adapter = BooksAdapter(null)
        listView!!.adapter = adapter

        layoutManager = LinearLayoutManager(this)
        listView!!.layoutManager = layoutManager

        model!!.searchResults.observe(this, { searchResults: Cursor -> adapter!!.swapCursor(searchResults) })
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        model!!.setSort(savedInstanceState.getInt("sortMethod"), savedInstanceState.getBoolean("sortDirectionAsc"))
    }

    public override fun onSaveInstanceState(savedInstanceState: Bundle) {
        savedInstanceState.putInt("sortMethod", model!!.sortMethod)
        savedInstanceState.putBoolean("sortDirectionAsc", model!!.sortDirectionAsc)
        super.onSaveInstanceState(savedInstanceState)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        super.onCreateOptionsMenu(menu)
        val inflater = menuInflater
        inflater.inflate(R.menu.list_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(menuItem: MenuItem): Boolean {
        val sortMethod = model!!.sortMethod
        var newSortMethod = sortMethod

        if(menuItem.itemId == R.id.sort_alphabetic) {
            newSortMethod = BooksSearcher.SORT_ALPHABETIC
        }
        else if(menuItem.itemId == R.id.sort_age) {
            newSortMethod = BooksSearcher.SORT_AGE
        }
        else if(menuItem.itemId == R.id.sort_size) {
            newSortMethod = BooksSearcher.SORT_SIZE
        }
        else if(menuItem.itemId == R.id.sort_last_opened) {
            newSortMethod = BooksSearcher.SORT_LAST_OPENED
        }
        else if(menuItem.itemId == R.id.sort_starred) {
            newSortMethod = BooksSearcher.SORT_STARRED
        }
        else if(menuItem.itemId == R.id.sort_opened_count) {
            newSortMethod = BooksSearcher.SORT_OPENED_COUNT
        }
        else if(menuItem.itemId == R.id.manage_indexing) {
            val intent = Intent(this@BooksActivity, IndexingActivity::class.java)
            startActivityForResult(intent, REQUEST_CODE_INDEXING)
            return true
        }

        var sortDirectionAsc = model!!.sortDirectionAsc
        if(sortMethod == newSortMethod) sortDirectionAsc = !sortDirectionAsc
        model!!.setSort(newSortMethod, sortDirectionAsc)

        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == REQUEST_CODE_INDEXING && resultCode == RESULT_OK) {
            model!!.refresh()
        }
    }

    companion object {
        private const val REQUEST_CODE_INDEXING = 0
    }
}