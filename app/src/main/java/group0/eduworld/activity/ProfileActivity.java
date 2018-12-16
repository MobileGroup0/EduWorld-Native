package group0.eduworld.activity;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RatingBar;
import android.widget.TextView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import group0.eduworld.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

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
        FirebaseFirestore db = FirebaseFirestore.getInstance();
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
            DocumentReference documentReference = Objects.requireNonNull(result).getReference();
            if(!result.exists()){
                documentReference.set(new HashMap<String, Object>());
            }
            String tmpString;
            ArrayList tmpStrArray;
            HashMap tmpMap = null;

            StringBuilder stringBuilder;

            // Get name
            stringBuilder = new StringBuilder();

            if(result.contains("firstname")) {
                tmpString = result.getString("firstname");
                if (tmpString != null) stringBuilder.append(tmpString);
                stringBuilder.append(" ");
            } else {
                documentReference.update("firstname", "");
            }

            if(result.contains("lastname")) {
                tmpString = result.getString("lastname");
                if(tmpString != null) stringBuilder.append(tmpString);
            } else {
                documentReference.update("lastname", "");
            }

            user_name = stringBuilder.toString();
            ((TextView) findViewById(R.id.title_textview)).setText(user_name);

            // Get about
            stringBuilder = new StringBuilder("About: ");
            if(result.contains("about")) {
              user_about = result.getString("about");
              stringBuilder.append(user_about);
            } else {
                documentReference.update("about", "");
            }
            ((TextView) findViewById(R.id.aboutView)).setText(stringBuilder.toString());

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
            if(result.contains("certification")) {
                tmpStrArray = (ArrayList) result.get("certification");
            } else {
                documentReference.update("certification", new ArrayList<String>());
            }
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

            if(result.contains("language")) {
                tmpMap = (HashMap) result.get("language");
            } else {
                documentReference.update("language", new HashMap<String, String>());
            }
            if(tmpMap != null){
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
            if(result.contains("teacher")) {
                user_teacher = result.getBoolean("teacher");
            } else {
                documentReference.update("teacher", false);
                user_teacher = false;
            }

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
