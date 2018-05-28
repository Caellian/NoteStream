/*
 * The MIT License (MIT)
 * NoteStream, android music player and streamer
 * Copyright (c) 2018 Tin Švagelj <tin.svagelj.email@gmail.com> a.k.a. Caellian
 *
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL
 * THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 */

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
class ThumbnailDecoder(private var resultConsumer: (Bitmap) -> Unit) : AsyncTask<String, Void, Bitmap?>() {
    override fun doInBackground(vararg imageURL: String): Bitmap? {
        val url: URL
        try {
            url = URL(imageURL[0])
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