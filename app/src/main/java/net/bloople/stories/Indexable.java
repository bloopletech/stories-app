package net.bloople.stories;

 interface Indexable {
    void onIndexingProgress(int progress, int max);
    void onIndexingComplete(int count);
}
