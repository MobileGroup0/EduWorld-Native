package group0.eduworld

import android.net.Uri
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(),
    HomeFragment.OnFragmentInteractionListener,
    BookingFragment.OnFragmentInteractionListener {

    private val homeFragment = HomeFragment()

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_map -> {
                message.setText(R.string.title_map)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_chat -> {
                message.setText(R.string.title_chat)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_home -> {
                message.setText(R.string.title_home)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_manage -> {
                message.setText(R.string.title_manage)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_settings -> {
                message.setText(R.string.title_settings)
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
        navigation.selectedItemId = R.id.navigation_home

        // Add tab fragments
        val viewPagerAdapter = MainViewPagerAdapter(supportFragmentManager)
        viewPagerAdapter.addFragment(homeFragment)

        // Set up adapter
        mainViewPager.adapter = viewPagerAdapter
        mainTabLayout.setupWithViewPager(mainViewPager)
    }

    override fun onFragmentInteraction(uri: Uri) {

    }
}
