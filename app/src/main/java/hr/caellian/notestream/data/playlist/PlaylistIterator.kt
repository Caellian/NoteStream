/*
 * Copyright (C) 2018 Tin Svagelj
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation the
 * rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit
 * persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package hr.caellian.notestream.data.playlist

import hr.caellian.notestream.data.playable.Playable

open class PlaylistIterator(protected var source: Playlist, var ascending: Boolean = true) : Iterator<Playable>, Iterable<Playable> {
    override fun iterator(): Iterator<Playable> = this

    protected var ordered = mutableListOf<Int>().also {
        it.addAll(0 until source.size())
    }

    init {
        @Suppress("LeakingThis")
        reorder()
    }

    var current = if (ascending) 0 else ordered.lastIndex

    open fun reassign(source: Playlist, ascending: Boolean = this.ascending): PlaylistIterator {
        val prev = current()
        ordered = mutableListOf<Int>().also {
            it.addAll(0 until source.size())
        }
        reorder()
        switchTo(prev)
        this.source = source
        this.ascending = ascending
        return this
    }

    open fun reorder(): PlaylistIterator {
        return this
    }

    open fun defaultAddIndex(): Int {
        return if (ascending) ordered.size else 0
    }

    fun add(sourcePlayable: Playable, index: Int = defaultAddIndex()): PlaylistIterator {
        source.add(sourcePlayable)
        add(index, source.playlist.indexOf(sourcePlayable))
        return this
    }

    open fun add(sourceIndex: Int, index: Int = defaultAddIndex()): PlaylistIterator {
        ordered.add(index, sourceIndex)
        return this
    }

    fun move(origin: Int, index: Int): PlaylistIterator {
        ordered.add(index, ordered[origin])
        return this
    }

    fun remove(sourcePlayable: Playable): PlaylistIterator {
        remove(source.playlist.indexOf(sourcePlayable))
        return this
    }

    open fun remove(sourceIndex: Int): PlaylistIterator {
        ordered = ordered.map {
            if (it > sourceIndex) it - 1 else it
        }.toMutableList()
        return this
    }

    fun switchTo(sourcePlayable: Playable?): PlaylistIterator {
        if (sourcePlayable != null) switchTo(source.playlist.indexOf(sourcePlayable))
        return this
    }

    fun switchTo(sourceIndex: Int): PlaylistIterator {
        if (sourceIndex >= 0 && sourceIndex < source.size()) {
            current = ordered.indexOf(sourceIndex)
        }
        return this
    }

    fun switchPrevious(): Playable {
        return if (ascending) source.playlist[ordered[--current]] else source.playlist[ordered[++current]]
    }

    fun hasPrevious(): Boolean {
        return current > 0
    }

    fun current(): Playable? {
        return source.playlist[ordered[current]]
    }

    fun switchNext(): Playable {
        return if (ascending) source.playlist[ordered[++current]] else source.playlist[ordered[--current]]
    }

    override fun hasNext(): Boolean {
        return current < ordered.size
    }

    override fun next(): Playable {
        return if (ascending) source.playlist[ordered[current++]] else source.playlist[ordered[current--]]
    }

    open fun setRandom(shuffle: Boolean): PlaylistIterator {
        return if (shuffle) Random(source, ascending, this) else this
    }

    class Random(source: Playlist, ascending: Boolean = true, val previous: PlaylistIterator? = null) : PlaylistIterator(source, ascending) {
        private val random = java.util.Random()

        init {
            ordered.shuffle(random)
        }

        override fun defaultAddIndex(): Int {
            return random.nextInt(ordered.size)
        }

        override fun setRandom(shuffle: Boolean): PlaylistIterator {
            return if (shuffle) this else previous!!
        }
    }

    class Title(source: Playlist, ascending: Boolean = true) : PlaylistIterator(source, ascending) {
        override fun reorder(): Title {
            ordered.sortBy { index ->
                source.playlist[index].title
            }
            return this
        }
    }

    class Author(source: Playlist, ascending: Boolean = true) : PlaylistIterator(source, ascending) {
        override fun reorder(): Author {
            ordered.sortBy { index ->
                source.playlist[index].author
            }
            return this
        }
    }

    class Time(source: Playlist, ascending: Boolean = true) : PlaylistIterator(source, ascending) {
        override fun reorder(): Time {
            ordered.sortBy { index ->
                source.timestamps[source.playlist[index]]
            }
            return this
        }
    }
}