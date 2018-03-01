package hr.caellian.notestream.util

import android.support.v4.view.ViewPager
import android.view.View

/**
 * Created by caellian on 28/02/18.
 */
class SlidePageTransformer(private val minScale: Float) : ViewPager.PageTransformer {

    override fun transformPage(page: View, position: Float) {
        val pageWidth = page.width

        when {
            position <= -1 -> {
                page.alpha = 0f
            }
            position >= 1 -> {
                page.alpha = 0f
            }
            position < 0 -> {
                page.alpha = 1 + position
                page.translationX = position
            }
            position > 0 -> {
                page.alpha = 1 - position
                page.translationX = position
            }
        }
    }

}