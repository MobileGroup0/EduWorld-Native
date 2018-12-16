package group0.eduworld.activity

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View

import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import group0.eduworld.R

import kotlinx.android.synthetic.main.activity_login.*

/**
 * A login screen that offers login via email/password.
 */
class LoginActivity : AppCompatActivity() {
    private val TAG = "LoginActivity"
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private lateinit var auth: FirebaseAuth

    fun login(v: View){
        val email = email_input.text.toString();
        val password = password_input.text.toString()

        if(email.isEmpty() || password.isEmpty()){
            Toast.makeText(baseContext, "Invalid input",
                Toast.LENGTH_SHORT).show()
            return
        }

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithEmail:success")
                    val user = auth.currentUser
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithEmail:failure", task.exception)
                    Toast.makeText(baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT).show()
                }
            }
    }


    fun gotoSignUp(v: View) {
        supportFinishAfterTransition()
        val intent = Intent(this, RegistrationActivity::class.java)
        startActivity(intent)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

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
