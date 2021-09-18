package net.bloople.stories

internal interface Indexable {
    fun onIndexingProgress(progress: Int, max: Int)
    fun onIndexingComplete(count: Int)
}