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

package hr.caellian.notestream.util

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.view.Menu
import hr.caellian.notestream.NoteStream

object Util {
    val whitespace = Regex("\\s")
    val unsafe = Regex("^[a-zA-Z0-9_]")

    fun timeToString(ms: Int, forceHourFormat: Boolean = false): String {
        val seconds = String.format("%02d", ms % 60000 / 1000)
        val minutes = String.format("%02d", ms % 3600000 / 60000)
        val hours = String.format("%02d", ms / 3600000)
        return if (forceHourFormat) {
            "$hours:$minutes:$seconds"
        } else {
            if (ms >= 3600000)
                "$hours:$minutes:$seconds"
            else
                "$minutes:$seconds"
        }
    }

    fun drawableToBitmap(drawableID: Int, width: Int = -1, height: Int = -1): Bitmap {
        var width = width
        var height = height
        val drawable = NoteStream.instance.resources?.getDrawable(drawableID, null)
        val bitmap: Bitmap
        width = Math.max(width, drawable?.intrinsicWidth ?: 0)
        height = Math.max(height, drawable?.intrinsicWidth ?: 0)

        if (drawable is BitmapDrawable && drawable.bitmap != null) {
            return drawable.bitmap
        }

        bitmap = if (drawable?.intrinsicWidth ?: 0 <= 0 || drawable?.intrinsicWidth ?: 0 <= 0) {
            Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888) // Single color bitmap will be created of 1x1 pixel
        } else {
            Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        }

        val canvas = Canvas(bitmap)
        drawable?.setBounds(0, 0, canvas.width, canvas.height)
        drawable?.draw(canvas)
        return bitmap
    }

    fun dbSafe(content: String): String {
        return content.replace(whitespace, "_").replace(unsafe, "")
    }

    fun getMenuItemFromID(menu: Menu, id: Int): Int? {
        return (0 until menu.size()).firstOrNull { menu.getItem(it).itemId == id }
    }

    fun menuRemoveItem(menu: Menu, id: Int) {
        val res = getMenuItemFromID(menu, id)
        if (res != null) menu.removeItem(res)
    }
}
