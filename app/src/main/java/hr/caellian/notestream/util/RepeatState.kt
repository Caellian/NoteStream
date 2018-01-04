package hr.caellian.notestream.util

/**
 * Created by caellyan on 20/06/17.
 */

enum class RepeatState {
    NONE,
    ALL,
    ONE;

    operator fun next(): RepeatState {
        return values()[(this.ordinal + 1) % values().size]
    }
}
