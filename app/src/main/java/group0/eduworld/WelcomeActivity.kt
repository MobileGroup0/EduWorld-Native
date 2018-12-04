package group0.eduworld

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View

class WelcomeActivity : AppCompatActivity() {

     fun gotoLogin(v: View) {
         setContentView(R.layout.activity_loading)
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }

    override fun onActivityReenter(resultCode: Int, data: Intent?) {
        super.onActivityReenter(resultCode, data)
        setContentView(R.layout.activity_welcome)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)
    }
}