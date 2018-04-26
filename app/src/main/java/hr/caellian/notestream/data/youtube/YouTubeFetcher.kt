package hr.caellian.notestream.data.youtube

import com.google.api.client.extensions.android.http.AndroidHttp
import com.google.api.client.extensions.android.json.AndroidJsonFactory
import com.google.api.services.youtube.YouTube
import hr.caellian.notestream.NoteStream
import hr.caellian.notestream.lib.Constants

import hr.caellian.notestream.lib.Constants.KA as a
import hr.caellian.notestream.lib.Constants.KC as b
import hr.caellian.notestream.lib.Constants.KE as c

object YouTubeFetcher {
    private val ResultCount: Long = 25
    private val k = "$a$b$c"

    val httpTransport = AndroidHttp.newCompatibleTransport()
    val jsonFactory = AndroidJsonFactory()

    private val youTube: YouTube =
            YouTube.Builder(httpTransport, jsonFactory, NoteStream.instance.googleAccountCredential)
            .setApplicationName(Constants.ApplicationName).build()

    fun searchVideos() {
        val search = youTube.search().list("id,snippet")
    }

    fun searchPlaylists() {
        val search = youTube.search().list("id,snippet")

    }
}