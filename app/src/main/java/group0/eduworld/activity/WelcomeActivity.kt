package group0.eduworld.activity

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import group0.eduworld.R

class WelcomeActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

     fun gotoLogin(v: View) {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }

    fun gotoSignUp(v: View) {
        val intent = Intent(this, RegistrationActivity::class.java)
        startActivity(intent)
    }

    override fun onActivityReenter(resultCode: Int, data: Intent?) {
        super.onActivityReenter(resultCode, data)
        setContentView(R.layout.activity_welcome)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)
        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()
    }

    override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        if (currentUser!=null){
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }
}