package hr.caellian.notestream.util;

/**
 * Created by caellyan on 20/06/17.
 */

public enum RepeatState {
    NONE,
    ALL,
    ONE;

    public RepeatState next() {
        return values()[(this.ordinal() + 1) % values().length];
    }
}
