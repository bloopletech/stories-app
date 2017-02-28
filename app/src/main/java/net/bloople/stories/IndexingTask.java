package net.bloople.stories;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

class IndexingTask extends AsyncTask<String, Integer, Void> {
    private Context context;
    private Indexable indexable;
    private int progress;
    private int max;
    private int indexed;

    IndexingTask(Context context, Indexable indexable) {
        this.context = context;
        this.indexable = indexable;
    }

    protected Void doInBackground(String... params) {
        destroyDeleted();
        indexDirectory(new File(params[0]));
        publishProgress(progress, max);
        return null;
    }

    protected void onProgressUpdate(Integer... args) {
        indexable.onIndexingProgress(args[0], args[1]);
    }

    protected void onPostExecute(Void result) {
        indexable.onIndexingComplete(indexed);
    }

    private void destroyDeleted() {
        SQLiteDatabase db = DatabaseHelper.instance(context);

        Cursor cursor = db.query("books", null, null, null, null, null, null);

        max += cursor.getCount();

        while(cursor.moveToNext()) {
            Book book = new Book(cursor);

            File file = new File(book.path());
            if(!file.exists()) book.destroy(context);

            progress++;
            publishProgress(progress, max);
        }

        cursor.close();
    }

    private void indexDirectory(File directory) {
        File[] files = directory.listFiles();

        if(files == null) return;

        ArrayList<File> filesToIndex = new ArrayList<>();

        for(File f : files) {
            if(f.isDirectory()) {
                indexDirectory(f);
            }
            else {
                String name = f.getName();
                String ext = name.substring(name.lastIndexOf('.') + 1);

                if(ext.equals("txt")) filesToIndex.add(f);
            }
        }

        max += filesToIndex.size();
        publishProgress(progress, max);

        for(File f : filesToIndex) indexFile(f);
    }

    private void indexFile(File file) {
        try {
            Book book = Book.findByPath(context, file.getCanonicalPath());
            if(book == null) book = new Book();

            book.path(file.getCanonicalPath());
            book.title(file.getName().replaceAll("\\.txt$", ""));
            book.mtime(file.lastModified());
            book.size(file.length());

            book.save(context);
            progress++;
            indexed++;
        }
        catch(IOException e) {
            e.printStackTrace();
        }

        publishProgress(progress, max);
    }
}