package net.bloople.stories;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

public class ListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        final SQLiteDatabase db = DatabaseHelper.instance(this);

        SimpleCursorAdapter adapter = new SimpleCursorAdapter(this,
                R.layout.list_item,
                db.query("books", null, null, null, null, null, null),
                new String[] { "title" },
                new int[] { R.id.story_title });

        ListView listView = (ListView) findViewById(R.id.stories_list);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(ListActivity.this, ReadingStoryActivity.class);
                intent.putExtra("_id", id);

                startActivity(intent);
            }
        });
    }
}
