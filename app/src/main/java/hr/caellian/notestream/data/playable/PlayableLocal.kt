package hr.caellian.notestream.data.playable

import android.media.MediaPlayer
import android.util.Log
import hr.caellian.notestream.NoteStream
import hr.caellian.notestream.R

import java.io.IOException

import hr.caellian.notestream.data.PlayableInfo

/**
 * Created by caellyan on 16/06/17.
 */

class PlayableLocal(override val path: String,
                    override var title: String = NoteStream.instance!!.getString(R.string.unknown_title),
                    override var author: String = NoteStream.instance!!.getString(R.string.unknown_artist)) : Playable {

    override var id: String = getId(path)
        private set

    @Transient override val info: PlayableInfo = PlayableInfo(this)

    override val playableSource: PlayableSource = PlayableSource.LOCAL
    override val location: String = playableSource.localizedDisplayName()

    override fun prepare(mp: MediaPlayer): Boolean {
        mp.reset()
        try {
            mp.setDataSource(path)
            mp.prepare()
            mp.seekTo(this.info.start)
        } catch (e: IllegalStateException) {
            Log.w(TAG, "prepare: 'setDataSource' or 'prepare' failed!", e)
            return false
        } catch (e: IllegalArgumentException) {
            Log.w(TAG, "prepare: 'setDataSource' or 'prepare' failed!", e)
            return false
        } catch (e: IOException) {
            Log.w(TAG, "prepare: 'setDataSource' or 'prepare' failed!", e)
            return false
        }

        return true
    }

    override fun skipTo(mp: MediaPlayer, ms: Int): Boolean {
        mp.seekTo(Math.max(0, Math.min(ms, info.length)))
        return true
    }

    override fun equals(other: Any?): Boolean {
        if (other === this) return true
        if (other == null) return false
        if (other !is PlayableLocal) return false

        return path == other.path
    }

    companion object {
        val TAG: String = PlayableLocal::class.java.simpleName

        private const val ID_PREFIX = "playable-local-"

        fun getId(path: String): String {
            return ID_PREFIX + path.replace("[^A-Za-z0-9]+".toRegex(), "")
        }
    }
}
