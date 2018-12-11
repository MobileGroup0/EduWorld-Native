package group0.eduworld

import android.net.Uri
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.functions.FirebaseFunctions
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(),
    HomeFragment.OnFragmentInteractionListener,
    BookingFragment.OnFragmentInteractionListener {

    private val homeFragment = HomeFragment()
    private val settingsFragment = SettingsFragment()

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

    override fun onStart() {
        super.onStart()

        val funcData = hashMapOf(
            "uid" to FirebaseAuth.getInstance().uid
        )

        FirebaseFunctions.getInstance().getHttpsCallable("getProfile")
            .call(funcData)
            .continueWith { task ->
                // This continuation runs on either success or failure, but if the task
                // has failed then result will throw an Exception which will be
                // propagated down.
                val result = task.result?.data as HashMap<*,*>

                result
            }.addOnFailureListener {
                Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
            }.addOnSuccessListener {
                Toast.makeText(this, "logged in as " + it["name"], Toast.LENGTH_LONG).show()
            }
    }

    override fun onFragmentInteraction(uri: Uri) {

    }

    fun logOut(v: View){
        FirebaseAuth.getInstance().signOut()
        finish()
    }

    fun switchFragment(fragment: Fragment){
        val ft = supportFragmentManager.beginTransaction()
        ft.replace(R.id.mainFrame, fragment)
        ft.commit()
    }
}
