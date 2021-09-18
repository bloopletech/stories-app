package net.bloople.stories

import java.io.IOException
import java.io.Reader
import java.util.*
import kotlin.Throws

internal class StoryParser(reader: Reader) {
    private val scanner: Scanner

    @Throws(IOException::class)
    operator fun hasNext(): Boolean {
        return scanner.hasNext()
    }

    @Throws(IOException::class)
    operator fun next(): String {
        return scanner.next()
    }

    init {
        scanner = Scanner(reader)
        scanner.useDelimiter("(?:\r?\n[\u200B\uFEFF]*){2,}+")
    }
}