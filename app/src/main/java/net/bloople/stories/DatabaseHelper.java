package net.bloople.stories;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public class DatabaseHelper {
    public static final String DB_NAME = "books";
    private static SQLiteDatabase mInstance;

    public static SQLiteDatabase obtainDatabase(Context context) {
        SQLiteDatabase db = context.getApplicationContext().openOrCreateDatabase(DB_NAME,
                Context.MODE_PRIVATE, null);

        db.execSQL("CREATE TABLE IF NOT EXISTS books ( " +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "path TEXT, " +
                "title TEXT, " +
                "mtime INT DEFAULT 0, " +
                "size INTEGER DEFAULT 0, " +
                "last_opened_at INTEGER DEFAULT 0, " +
                "last_read_position INTEGER DEFAULT 0" +
                ")");

        return db;
    }

    public static SQLiteDatabase instance(Context context) {
        if(mInstance == null) {
            mInstance = obtainDatabase(context);
        }

        return mInstance;
    }

    public static void deleteDatabase(Context context) {
        context.getApplicationContext().deleteDatabase(DB_NAME);
        mInstance = null;
    }
}
