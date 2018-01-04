package hr.caellian.notestream.data.playable

/**
 * Created by caellyan on 24/06/17.
 */

abstract class PlayableDownloadable : PlayableRemote() {
    abstract fun download(): Boolean
}
