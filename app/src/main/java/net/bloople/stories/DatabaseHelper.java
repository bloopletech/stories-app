package net.bloople.stories;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.io.File;

import static android.database.sqlite.SQLiteDatabase.openOrCreateDatabase;

/**
 * Created by i on 19/10/2016.
 */

public class DatabaseHelper {
    private static SQLiteDatabase mInstance;

    public static SQLiteDatabase obtainDatabase(Context context) {
        File dbFile = context.getApplicationContext().getDatabasePath("books");
        SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(dbFile, null);

        db.execSQL("CREATE TABLE IF NOT EXISTS books ( " +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "path TEXT, " +
                "title TEXT, " +
                "mtime INT DEFAULT 0, " +
                "size INTEGER DEFAULT 0, " +
                "last_opened_at INTEGER DEFAULT 0" +
                ")");

        return db;
    }

    public static SQLiteDatabase instance(Context context) {
        if(mInstance == null) {
            mInstance = obtainDatabase(context);
        }

        return mInstance;
    }

    private SQLiteDatabase db;

    public DatabaseHelper(Context context) {
        db = instance(context);
    }

    public void upsertBook(ContentValues values) {
        String path = values.getAsString("path");

        Cursor result = db.rawQuery("SELECT _id FROM books WHERE path=?", new String[] { path });
        result.moveToFirst();

        if(result.getCount() > 0) {
            long bookId = result.getLong(0);
            db.update("books", values, "_id=?", new String[] { String.valueOf(bookId) });
        }
        else {
            db.insert("books", null, values);
        }

        result.close();
    }


}
