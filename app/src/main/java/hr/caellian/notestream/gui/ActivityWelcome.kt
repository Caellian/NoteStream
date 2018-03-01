package hr.caellian.notestream.gui

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v4.view.PagerAdapter
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import hr.caellian.notestream.R
import hr.caellian.notestream.gui.fragments.welcome.FragmentPermission
import hr.caellian.notestream.gui.fragments.welcome.FragmentSignIn
import hr.caellian.notestream.util.SlidePageTransformer
import me.relex.circleindicator.CircleIndicator

/**
 * Created by caellian on 21/02/18.
 */
class ActivityWelcome : AppCompatActivity() {

    lateinit var pager: ViewPager
    lateinit var pagerAdapter: PagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(null) // TODO: Store current page
        setContentView(R.layout.activity_welcome)

        val pager = findViewById<ViewPager>(R.id.welcome_content)
        pagerAdapter = PageSlideAdapter(supportFragmentManager)
        pager.adapter = pagerAdapter
        pager.setPageTransformer(true, SlidePageTransformer(0.85f))
        this.pager = pager

        val indicator = findViewById<CircleIndicator>(R.id.contentIndicator)!!
        indicator.configureIndicator(20,20,40)
        indicator.setViewPager(pager)


        // TODO: Handle intents from other apps properly.
    }

    override fun onBackPressed() {
        if (pager.currentItem == 0) {
            super.onBackPressed()
        } else {
            pager.currentItem--
        }
    }

    class PageSlideAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {
        override fun getItem(position: Int): Fragment {
            return when {
                position == 0 -> FragmentSignIn()
                position == 1 -> FragmentPermission(FragmentPermission.Permission.ACCESS_EXTERNAL)
                position == 2 -> FragmentPermission(FragmentPermission.Permission.PHONE_STATE)
                else -> FragmentSignIn()

            }
        }

        override fun getCount(): Int {
            return 3
        }
    }

    companion object {
        const val PageCount = 5
    }
}