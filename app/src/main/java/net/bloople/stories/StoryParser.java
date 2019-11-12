package net.bloople.stories;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.io.Reader;
import java.util.Scanner;
import java.util.regex.Pattern;

class StoryParser {
    private Scanner scanner;
    private Pattern cleanPattern;

    StoryParser(Reader reader) {
        scanner = new Scanner(reader);
        scanner.useDelimiter("(?:\r?\n[\u200B\uFEFF]*){2,}+");
        cleanPattern = Pattern.compile("\\s+");
    }

    boolean hasNext() throws IOException {
        return scanner.hasNext();
    }

    String next() throws IOException {
        return cleanString(scanner.next());
    }

    @NonNull
    private String cleanString(String input) {
        return cleanPattern.matcher(input).replaceAll(" ").trim();
    }
}
