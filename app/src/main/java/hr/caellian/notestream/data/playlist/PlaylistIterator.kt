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
import hr.caellian.notestream.lib.Constants

open class PlaylistIterator(protected var source: List<Playable>, var ascending: Boolean = true, val parent: Playlist? = null) : MutableListIterator<Playable>, Iterable<Playable>  {

    constructor(pl: Playlist, ascending: Boolean = true): this(pl.playlist, ascending, pl)

    val size: Int
        get() = ordered.size

    val isEmpty: Boolean
        get() = ordered.isEmpty()

    open val id: String = Constants.ITERATOR_DEFAULT_ID

    var ordered: MutableList<Playable> = ArrayList(source)
        protected set

    init {
        @Suppress("LeakingThis") // This is intentional.
        reorder()
    }

    var current = if (ascending) -1 else ordered.size

    val playlist: Playlist?
        get() = parent

    override fun iterator(): MutableIterator<Playable> = this

    open fun reassign(pl: Playlist, ascending: Boolean = this.ascending): PlaylistIterator {
        val prev = current()
        ordered = ArrayList(pl.playlist)
        reorder()
        switchTo(prev)
        this.source = pl.playlist
        this.ascending = ascending
        return this
    }

    fun reset(): PlaylistIterator {
        current = if (ascending) -1 else ordered.size
        return this
    }

    open fun reorder(): PlaylistIterator {
        return this
    }

    private fun defaultAddIndex(): Int {
        return if (ascending) ordered.size else 0
    }

    override fun add(element: Playable) = add(element, defaultAddIndex()).let{Unit}

    fun add(playable: Playable, index: Int = defaultAddIndex()): PlaylistIterator {
        ordered.add(index, playable)
        return this
    }

    fun addAll(elements: Collection<Playable>, index: Int = defaultAddIndex()): Boolean {
        var counter = 0
        elements.forEach {
            add(it, index + counter++)
        }
        return true
    }

    override fun set(element: Playable) = set(current, element).let{Unit}

    fun set(index: Int, element: Playable): Playable? {
        val old = ordered.getOrNull(index)
        ordered[index] = element
        return old
    }

    fun move(origin: Int, end: Int): PlaylistIterator {
        val moved = ordered[origin]
        if (origin > end) {
            ordered.removeAt(origin)
            ordered.add(end, moved)
        } else if (origin < end) {
            ordered.add(end, moved)
            ordered.removeAt(origin)
        }
        return this
    }

    override fun remove() {
        ordered.removeAt(current)
    }

    fun remove(sourcePlayable: Playable): PlaylistIterator {
        ordered.removeAll {it == sourcePlayable}
        return this
    }

    fun removeAt(index: Int): Playable {
        return ordered.removeAt(index)
    }

    fun removeAll(elements: Collection<Playable>): Boolean {
        elements.forEach { element ->
            ordered.removeIf { it == element }
        }
        return true
    }

    fun retainAll(elements: Collection<Playable>): Boolean {
        elements.forEach { element ->
            ordered.removeIf { it != element }
        }
        return true
    }

    fun clear() {
        ordered.clear()
    }

    fun switchTo(playable: Playable?): PlaylistIterator {
        if (playable != null) current = ordered.indexOf(playable).takeIf { it > -1 } ?: current
        return this
    }

    fun switchNext(): Playable {
        return if (ascending) {
            ordered[nextIndex()].also {
                current = nextIndex()
            }
        } else {
            ordered[previousIndex()].also {
                current = previousIndex()
            }
        }
    }

    override fun hasNext(): Boolean {
        return current < ordered.lastIndex
    }

    override fun next(): Playable = switchNext()

    override fun nextIndex(): Int = if (hasNext()) current + 1 else 0

    fun current(): Playable? {
        return if (0 <= current && current <= ordered.lastIndex) {
            ordered[current]
        } else {
            null
        }
    }

    override fun previous(): Playable = switchPrevious()

    fun switchPrevious(): Playable {
        return if (ascending) {
            ordered[previousIndex()].also {
                current = previousIndex()
            }
        } else {
            ordered[nextIndex()].also {
                current = nextIndex()
            }
        }

    }

    override fun hasPrevious(): Boolean = current > 0

    override fun previousIndex(): Int = if (hasPrevious()) current - 1 else ordered.lastIndex

    fun contains(element: Playable): Boolean = ordered.contains(element)

    fun containsAll(elements: Collection<Playable>): Boolean = ordered.containsAll(elements)

    fun indexOf(element: Playable): Int = ordered.indexOf(element)

    fun lastIndexOf(element: Playable): Int = ordered.lastIndexOf(element)

    fun get(index: Int): Playable = ordered[index]

    fun subList(fromIndex: Int, toIndex: Int): MutableList<Playable> {
        return ordered.subList(fromIndex, toIndex).toMutableList()
    }

    open fun setRandom(shuffle: Boolean): PlaylistIterator {
        return if (shuffle) Random(source, ascending, this) else this
    }

    override fun toString(): String {
        return "[${ordered.joinToString(", ") { "${it.title} - ${it.author}" }}]"
    }

    class Random(source: List<Playable>, ascending: Boolean = true, val previous: PlaylistIterator? = null, parent: Playlist? = null) : PlaylistIterator(source, ascending, parent) {
        private val random = java.util.Random()

        override val id: String = Constants.ITERATOR_RANDOM_ID

        constructor(pl: Playlist, ascending: Boolean = true, previous: PlaylistIterator? = null): this(pl.playlist, ascending, previous, pl)

        init {
            ordered.shuffle(random)
        }

        override fun setRandom(shuffle: Boolean): PlaylistIterator {
            return if (shuffle) this else previous ?: this
        }
    }

    class Title(source: List<Playable>, ascending: Boolean = true, parent: Playlist? = null) : PlaylistIterator(source, ascending, parent) {
        override val id: String = Constants.ITERATOR_TITLE_ID

        constructor(pl: Playlist, ascending: Boolean = true): this(pl.playlist, ascending, pl)

        override fun reorder(): Title {
            ordered.sortBy { it ->
                it.title
            }
            return this
        }
    }

    class Author(source: List<Playable>, ascending: Boolean = true, parent: Playlist? = null) : PlaylistIterator(source, ascending, parent) {
        override val id: String = Constants.ITERATOR_AUTHOR_ID

        constructor(pl: Playlist, ascending: Boolean = true): this(pl.playlist, ascending, pl)

        override fun reorder(): Author {
            ordered.sortBy { it ->
                it.author
            }
            return this
        }
    }

    class Time(source: List<Playable>, ascending: Boolean = true, parent: Playlist? = null) : PlaylistIterator(source, ascending, parent) {
        override val id: String = Constants.ITERATOR_TIME_ID

        constructor(pl: Playlist, ascending: Boolean = true): this(pl.playlist, ascending, pl)

        override fun reorder(): Time {
            val time = System.currentTimeMillis()
            ordered.sortBy { it ->
                parent?.timestamps?.getOrDefault(it, time) ?: 0
            }
            return this
        }
    }

    companion object {
        fun get(id: String, playlist: Playlist, ascending: Boolean): PlaylistIterator {
            return when (id) {
                Constants.ITERATOR_RANDOM_ID -> PlaylistIterator.Random(playlist, ascending)
                Constants.ITERATOR_TITLE_ID -> PlaylistIterator.Title(playlist, ascending)
                Constants.ITERATOR_AUTHOR_ID -> PlaylistIterator.Author(playlist, ascending)
                Constants.ITERATOR_TIME_ID -> PlaylistIterator.Time(playlist, ascending)
                else -> PlaylistIterator(playlist, ascending)
            }
        }
    }
}