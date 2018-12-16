package group0.eduworld.activity

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import group0.eduworld.*
import group0.eduworld.fragment.ChatFragment
import group0.eduworld.fragment.HomeFragment
import group0.eduworld.fragment.MapFragment
import group0.eduworld.fragment.SettingsFragment
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val chatFragment = ChatFragment()
    private val mapFragment = MapFragment()
    private val homeFragment = HomeFragment()
    private val settingsFragment = SettingsFragment()

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_map -> {
                message.setText(R.string.title_map)
                switchFragment(mapFragment)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_chat -> {
                message.setText(R.string.title_chat)
                switchFragment(chatFragment);
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_home -> {
                message.setText(R.string.title_home)
                switchFragment(homeFragment)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_manage -> {
                message.setText(R.string.title_manage)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_settings -> {
                message.setText(R.string.title_settings)
                switchFragment(settingsFragment)
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
        switchFragment(homeFragment)
    }

    fun logOut(v: View){
        FirebaseAuth.getInstance().signOut()
        finish()
    }

    fun openProfile(v: View){
        val uid = FirebaseAuth.getInstance().uid
        val intent = Intent(this, ProfileActivity::class.java)
        intent.putExtra("uid", uid)
        startActivity(intent)
    }

    fun switchFragment(fragment: Fragment){
        val ft = supportFragmentManager.beginTransaction()
        ft.replace(R.id.mainFrame, fragment)
        ft.commit()
    }
}
