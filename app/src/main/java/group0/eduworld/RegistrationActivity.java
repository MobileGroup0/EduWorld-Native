package group0.eduworld;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegistrationActivity extends AppCompatActivity implements OnCompleteListener<AuthResult> {

    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        auth = FirebaseAuth.getInstance();
    }

    public void gotoLogin(View v) {
        supportFinishAfterTransition();
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }


    public void register(View v){
        CharSequence email = ((TextView)findViewById(R.id.email_input)).getText();
        CharSequence password = ((TextView)findViewById(R.id.password_input)).getText();

        if(email.length() < 5 || email.length() > 254){
            Toast.makeText(getBaseContext(), "Invalid Email Address", Toast.LENGTH_SHORT).show();
            return;
        }

        if(password.length() < 6){
            Toast.makeText(getBaseContext(), "Password is too short", Toast.LENGTH_SHORT).show();
            return;
        }

        auth.createUserWithEmailAndPassword(email.toString(), password.toString()).addOnCompleteListener(this);
    }

    @Override
    public void onComplete(@NonNull Task task) {
        if (task.isSuccessful()) {
            // register, update UI with the signed-in user's information
            Toast.makeText(getBaseContext(), "Registration successful.",
                    Toast.LENGTH_SHORT).show();
            supportFinishAfterTransition();
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        } else {
            Toast.makeText(getBaseContext(), "Registration failed.",
                    Toast.LENGTH_SHORT).show();
        }
    }
}
