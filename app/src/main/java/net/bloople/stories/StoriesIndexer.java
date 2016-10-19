package net.bloople.stories;

import android.content.ContentValues;
import android.content.Context;

import java.io.File;
import java.io.IOException;

/**
 * Created by i on 7/10/2016.
 */
public class StoriesIndexer {
    private Context context;

    public StoriesIndexer(Context inContext) {
        context = inContext;
    }

    public void indexDirectory(File directory) {
        File[] files = directory.listFiles();

        for(File f : files) {
            if(f.isDirectory()) {
                indexDirectory(f);
            }
            else {
                String name = f.getName();
                String ext = name.substring(name.lastIndexOf('.') + 1);

                if(ext.equals("txt")) indexFile(f);
            }
        }
    }

    public void indexFile(File file) {
        try {
            DatabaseHelper helper = new DatabaseHelper(context);

            ContentValues values = new ContentValues();
            values.put("path", file.getCanonicalPath());
            values.put("title", file.getName().replaceAll("\\.txt$", ""));
            values.put("mtime", file.lastModified());
            values.put("size", file.length());

            helper.upsertBook(values);
        }
        catch(IOException e) {
            e.printStackTrace();
        }
    }
}
