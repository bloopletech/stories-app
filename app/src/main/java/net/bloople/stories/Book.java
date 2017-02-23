package net.bloople.stories;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class Book {
    private long _id = -1L;
    private String path;
    private String title;
    private long mtime;
    private long size;
    private long lastOpenedAt;
    private int lastReadPosition;

    public static Book findById(Context context, long id) {
        SQLiteDatabase db = DatabaseHelper.instance(context);

        Cursor result = db.rawQuery("SELECT * FROM books WHERE _id=?", new String[] { String.valueOf(id) });
        result.moveToFirst();

        if(result.getCount() > 0) {
            Book book = new Book(result);
            result.close();
            return book;
        }
        else {
            return null;
        }
    }

    public static Book findByPath(Context context, String path) {
        SQLiteDatabase db = DatabaseHelper.instance(context);

        Cursor result = db.rawQuery("SELECT * FROM books WHERE path=?", new String[] { path });
        result.moveToFirst();

        if(result.getCount() > 0) {
            Book book = new Book(result);
            result.close();
            return book;
        }
        else {
            return null;
        }
    }

    public Book() {
    }

    public Book(Cursor result) {
        _id = result.getLong(result.getColumnIndex("_id"));
        path = result.getString(result.getColumnIndex("path"));
        title = result.getString(result.getColumnIndex("title"));
        mtime = result.getLong(result.getColumnIndex("mtime"));
        size = result.getLong(result.getColumnIndex("size"));
        lastOpenedAt = result.getLong(result.getColumnIndex("last_opened_at"));
        lastReadPosition = result.getInt(result.getColumnIndex("last_read_position"));
    }

    public String path() {
        return path;
    }

    public void path(String inPath) {
        path = inPath;
    }

    public String title() {
        return title;
    }

    public void title(String inTitle) {
        title = inTitle;
    }

    public long mtime() {
        return mtime;
    }

    public void mtime(long inMtime) {
        mtime = inMtime;
    }

    public long size() {
        return size;
    }

    public void size(long inSize) {
        size = inSize;
    }

    public long lastOpenedAt() {
        return lastOpenedAt;
    }

    public void lastOpenedAt(long inLastOpenedAt) {
        lastOpenedAt = inLastOpenedAt;
    }

    public int lastReadPosition() {
        return lastReadPosition;
    }

    public void lastReadPosition(int inLastReadPosition) {
        lastReadPosition = inLastReadPosition;
    }

    public void save(Context context) {
        ContentValues values = new ContentValues();
        values.put("path", path);
        values.put("title", title);
        values.put("mtime", mtime);
        values.put("size", size);
        values.put("last_opened_at", lastOpenedAt);
        values.put("last_read_position", lastReadPosition);

        SQLiteDatabase db = DatabaseHelper.instance(context);

        if(_id == -1L) {
            _id = db.insert("books", null, values);
        }
        else {
            db.update("books", values, "_id=?", new String[] { String.valueOf(_id) });
        }
    }
}
