package net.bloople.stories;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

class DatabaseHelper {
    private static final String DB_NAME = "books";
    private static SQLiteDatabase instance;

    private static SQLiteDatabase obtainDatabase(Context context) {
        SQLiteDatabase db = context.getApplicationContext().openOrCreateDatabase(DB_NAME,
            Context.MODE_PRIVATE, null);

        loadSchema(db);

        return db;
    }

    private static void loadSchema(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS books ( " +
            "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "path TEXT, " +
            "title TEXT, " +
            "mtime INT DEFAULT 0, " +
            "size INTEGER DEFAULT 0, " +
            "last_opened_at INTEGER DEFAULT 0, " +
            "last_read_position INTEGER DEFAULT 0" +
            ")");

        if(!hasColumn(db, "books", "starred")) {
            db.execSQL("ALTER TABLE books ADD COLUMN starred INT DEFAULT 0");
        }

        if(!hasColumn(db, "books", "opened_count")) {
            db.execSQL("ALTER TABLE books ADD COLUMN opened_count INTEGER");
            db.execSQL("UPDATE books SET opened_count=0");
        }
    }

    static SQLiteDatabase instance(Context context) {
        if(instance == null) {
            instance = obtainDatabase(context);
        }

        return instance;
    }

    static void deleteDatabase(Context context) {
        context.getApplicationContext().deleteDatabase(DB_NAME);
        instance = null;
    }

    private static boolean hasColumn(SQLiteDatabase db, String tableName, String columnName) {
        boolean success = false;
        Cursor columns = db.rawQuery("PRAGMA table_info(" + tableName +")", null);

        while(columns.moveToNext()) {
            if(columns.getString(columns.getColumnIndex("name")).equals(columnName)) {
                success = true;
                break;
            }
        }

        columns.close();

        return success;
    }
}
