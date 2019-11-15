package net.bloople.stories;

import android.os.AsyncTask;

import org.commonmark.node.Node;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.noties.markwon.Markwon;

class ParseStoryTask extends AsyncTask<Book, List<Node>, Void> {
    private ReadingStoryActivity readingStoryActivity;
    private Markwon markwon;
    private ParsedBook parsedBook;
    int BATCH_SIZE = 50;
    private int lastReadPosition;
    private boolean setPosition = false;

    ParseStoryTask(ReadingStoryActivity readingStoryActivity, Markwon markwon, ParsedBook parsedBook) {
        this.readingStoryActivity = readingStoryActivity;
        this.markwon = markwon;
        this.parsedBook = parsedBook;
    }

    protected Void doInBackground(Book... bookArgs) {
        Book book = bookArgs[0];

        lastReadPosition = book.lastReadPosition();

        try {
            StoryParser parser = new StoryParser(markwon, new BufferedReader(new FileReader(book.path())));

            List<Node> accumulator = new ArrayList<>();
            while(parser.hasNext()) {
                Node node = parser.next();
                if(node == null) continue;

                accumulator.add(node);

                if(accumulator.size() >= BATCH_SIZE) {
                    publishProgress(accumulator);
                    accumulator = new ArrayList<>();
                }
            }

            publishProgress(accumulator);
        }
        catch(IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    protected void onProgressUpdate(List<Node>... nodesArgs) {
        parsedBook.addNodesAll(nodesArgs[0]);

        readingStoryActivity.onParsedNodes();

        if(!setPosition && (parsedBook.size() >= lastReadPosition)) {
            setPosition = true;

            readingStoryActivity.restorePosition();
        }

    }

    protected void onPostExecute(Void result) {
    }
}
