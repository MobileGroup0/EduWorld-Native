package group0.eduworld;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RatingBar;
import android.widget.TextView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;

public class ProfileActivity extends AppCompatActivity implements OnCompleteListener<DocumentSnapshot> {
    private final static String TAG = "ProfileActivity";
    private String  mUID; // User ID of currently opened profile

    private String user_name;
    private String user_about;
    private ArrayList<String> user_subjects = new ArrayList<>();
    private ArrayList<String>  user_certifications = new ArrayList<>();
    private HashMap<String, Long> user_languages = new HashMap<>();
    private double user_rating;
    private boolean user_teacher;

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
            DocumentSnapshot result = task.getResult();

            String tmpString;
            ArrayList tmpStrArray;
            HashMap tmpMap;

            StringBuilder stringBuilder;

            // Get name
            stringBuilder = new StringBuilder();
            tmpString = result.getString("firstname");
            if(tmpString != null) stringBuilder.append(tmpString); stringBuilder.append(" ");

            tmpString = result.getString("lastname");
            if(tmpString != null) stringBuilder.append(tmpString);

            user_name = stringBuilder.toString();
            ((TextView) findViewById(R.id.title_textview)).setText(user_name);

            // Get about
            user_about = result.getString("about");
            ((TextView) findViewById(R.id.aboutView)).setText(user_about);
            Log.i(TAG, user_about);

            // Get subjects
            tmpStrArray = (ArrayList) result.get("subjects");
            stringBuilder = new StringBuilder("Subjects: \n");
            if(tmpStrArray != null){
                for (Object o : tmpStrArray){
                    user_subjects.add((String) o);
                    stringBuilder.append((String) o);
                    stringBuilder.append('\n');
                }
            }
            ((TextView) findViewById(R.id.subjectView)).setText(stringBuilder.toString());

            // Get certifications
            tmpStrArray = (ArrayList) result.get("certification");
            stringBuilder = new StringBuilder("Certifications: \n");
            if(tmpStrArray != null){
                for (Object o : tmpStrArray){
                    user_certifications.add((String) o);
                    stringBuilder.append((String) o);
                    stringBuilder.append('\n');
                }
            }
            ((TextView) findViewById(R.id.certificationView)).setText(stringBuilder.toString());

            // Get languages
            stringBuilder = new StringBuilder("Languages: ");
            tmpMap = (HashMap) result.get("language");
            if(tmpStrArray != null){
                for (Object key : tmpMap.keySet()){
                    user_languages.put((String) key, (Long) tmpMap.get(key));
                    stringBuilder.append((String) key);
                    stringBuilder.append('(');
                    stringBuilder.append(tmpMap.get(key));
                    stringBuilder.append(")     ");
                }
            }
            ((TextView) findViewById(R.id.languageView)).setText(stringBuilder.toString());

            // Get ?teacher?
            user_teacher = result.getBoolean("teacher");
            if(user_teacher) {
                ((TextView) findViewById(R.id.user_type_view)).setText("Teacher");

                // Get rating
                user_rating = result.getDouble("rating");
                RatingBar ratingBar = findViewById(R.id.ratingBar);
                ratingBar.setRating((float) user_rating);
                ratingBar.setVisibility(View.VISIBLE);
            }
            else ((TextView) findViewById(R.id.user_type_view)).setText("Student");

        } else if (task.isCanceled()){

        } else{

        }
    }

    public void onClick(View view){
        if(view.getId() == R.id.close_button)
            finish();
    }
}
