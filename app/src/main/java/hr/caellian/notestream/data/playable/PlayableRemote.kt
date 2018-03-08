package hr.caellian.notestream.data.playable

import hr.caellian.notestream.NoteStream

/**
 * Created by tinsv on 29/06/2017.
 */

abstract class PlayableRemote : Playable {
    var available: Boolean = false
        protected set(available) {
            field = available
            for (availabilityListener in NoteStream.AVAILABILITY_LISTENERS) {
                availabilityListener.onAvailableStateChanged(this.id, available)
            }
        }

    interface AvailabilityListener {
        fun onAvailableStateChanged(playableID: String, state: Boolean)
    }
}
