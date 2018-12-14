package group0.eduworld;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProfileActivity extends AppCompatActivity implements OnCompleteListener<DocumentSnapshot> {
    private final static String TAG = "ProfileActivity";
    private String  mUID; // User ID of currently opened profile

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        mUID = getIntent().getCharSequenceExtra("uid").toString();
        loadProfile();
    }

    private void loadProfile() {
        try {
            FirebaseFirestore.getInstance().document("users/" + mUID).get().addOnCompleteListener(this);
        } catch (IllegalArgumentException e){
            Log.d(TAG, e.getMessage());
            finish();
        }
    }

    @Override
    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
        if(task.isSuccessful()){
            StringBuilder nameSB = new StringBuilder();
            String fname = (String) task.getResult().get("firstname");
            String lname = (String) task.getResult().get("lastname");
            if(fname != null) nameSB.append(fname); nameSB.append(" ");
            if(lname != null) nameSB.append(lname);

            ((TextView) findViewById(R.id.title_textview)).setText(nameSB.toString());
        }else if (task.isCanceled()){

        }else{

        }
    }

    public void onClick(View view){
        if(view.getId() == R.id.close_button)
            finish();
    }
}
