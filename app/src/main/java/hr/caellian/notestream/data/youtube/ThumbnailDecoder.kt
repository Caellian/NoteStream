package hr.caellian.notestream.data.youtube

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.util.Log
import at.huber.youtubeExtractor.VideoMeta
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.net.MalformedURLException
import java.net.URL

/**
 * Created by caellian on 02/01/18.
 */
class ThumbnailDecoder(private var resultConsumer: (Bitmap) -> Unit): AsyncTask<VideoMeta, Void, Bitmap?>() {
    override fun doInBackground(vararg videoMeta: VideoMeta): Bitmap? {
        val meta = videoMeta[0]
        val url: URL
        try {
            url = URL(meta.maxResImageUrl)
        } catch (e: MalformedURLException) {
            return null
        }


        val baos = ByteArrayOutputStream()
        url.openStream()?.also { inStr ->
            try {
                val byteChunk = ByteArray(4096)
                var n: Int

                n = inStr.read(byteChunk)
                do {
                    baos.write(byteChunk, 0, n)
                    n = inStr.read(byteChunk)
                } while (n > 0)
            } catch (e: IOException) {
                Log.w(ThumbnailDecoder::class.java.simpleName, "Failed while reading bytes from " + url.toExternalForm(), e)
                return null
            } finally {
                try {
                    inStr.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }

        val data = baos.toByteArray()
        return BitmapFactory.decodeByteArray(data, 0, data.size)
    }

    override fun onPostExecute(result: Bitmap?) {
        result?.also { resultConsumer(it) }
    }
}