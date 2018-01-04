package hr.caellian.notestream.data.playable

import android.util.Log
import hr.caellian.notestream.NoteStream
import hr.caellian.notestream.R

/**
 * Created by tinsv on 03/07/2017.
 */

enum class PlayableSource private constructor(id: String, private val displayName: Int) {
    LOCAL("local", R.string.location_local),
    YOUTUBE("remote-youtube", R.string.location_youtube),
    OTHER("unknown", R.string.location_unknown);

    var id: String
        internal set

    init {
        this.id = id
    }

    fun localizedDisplayName(): String {
        return NoteStream.instance?.getString(this.displayName) ?: ""
    }

    companion object {
        fun getByID(id: String): PlayableSource? {
            values().filter { it.id == id.toLowerCase() }
                    .forEach { return it }

            Log.e(PlayableSource::class.java.simpleName, "getByID: ", IllegalArgumentException("Unsupported source id: '" + id.toLowerCase() + "'!"))
            return null
        }
    }
}
