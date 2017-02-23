package net.bloople.stories;

import android.content.Context;

import java.io.File;
import java.io.IOException;

public class StoriesIndexer {
    private Context context;
    private int indexed;

    public StoriesIndexer(Context context) {
        this.context = context;
    }

    public void indexDirectory(File directory) {
        File[] files = directory.listFiles();

        if(files == null) return;

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
            Book book = Book.findByPath(context, file.getCanonicalPath());
            if(book == null) book = new Book();

            book.path(file.getCanonicalPath());
            book.title(file.getName().replaceAll("\\.txt$", ""));
            book.mtime(file.lastModified());
            book.size(file.length());

            book.save(context);
            indexed++;
        }
        catch(IOException e) {
            e.printStackTrace();
        }
    }

    public int count() {
        return indexed;
    }
}
