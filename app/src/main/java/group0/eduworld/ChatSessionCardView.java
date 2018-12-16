package group0.eduworld;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.format.DateFormat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.*;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.*;
import com.google.firebase.firestore.EventListener;

import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class ChatSessionCardView extends FrameLayout {
    private final static String TAG = "ChatSessionCardView";
    private ImageView notifier;
    private TextView nameTextView;
    private TextView dateTextView;
    private TextView previewTextView;

    public interface ChatViewListener{
    }

    private boolean newMessage = false;
    private ChatViewListener listener;

    private DocumentReference source;

    public ChatSessionCardView(Context context, DocumentReference source) {
        super(context);
        init(context);
        this.source = source;
    }

    public ChatSessionCardView(Context context, ChatViewListener bookingViewListener, DocumentReference source) {
        super(context);
        init(context);
        this.listener = bookingViewListener;
        this.source = source;
    }

    public ChatSessionCardView(Context context, AttributeSet attrs, DocumentReference source) {
        super(context, attrs);
        init(context);
        this.source = source;
    }

    public ChatSessionCardView(Context context, AttributeSet attrs, int defStyle, DocumentReference source) {
        super(context, attrs, defStyle);
        init(context);
        this.source = source;
    }

    public void addBookingViewListener(ChatViewListener chatViewListener){
        this.listener = chatViewListener;
    }

    private void init(Context context) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.view_chat_session_card, this);

        notifier = findViewById(R.id.msgNotifier);
        nameTextView = findViewById(R.id.nameTextView);
        dateTextView = findViewById(R.id.dateTextView);
        previewTextView = findViewById(R.id.previewTextView);
    }

    public void updateData(){
        source.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                // Get name
                ArrayList participants = (ArrayList) documentSnapshot.get("participants");
                for(Object user: participants){
                    if(user instanceof DocumentReference){
                        String uid = ((DocumentReference) user).getId();
                        if(!uid.equals(FirebaseAuth.getInstance().getUid())){
                            FirebaseFirestore.getInstance().document("/users/" + uid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    DocumentSnapshot result = task.getResult();
                                    if(!result.exists()) return;
                                    StringBuilder stringBuilder;

                                    // Get name
                                    stringBuilder = new StringBuilder();
                                    String tmpString;
                                    if(result.contains("firstname")) {
                                        tmpString = result.getString("firstname");
                                        if (tmpString != null) stringBuilder.append(tmpString);
                                        stringBuilder.append(" ");
                                    } else {
                                        result.getReference().update("firstname", "");
                                    }

                                    if(result.contains("lastname")) {
                                        tmpString = result.getString("lastname");
                                        if(tmpString != null) stringBuilder.append(tmpString);
                                    } else {
                                        result.getReference().update("lastname", "");
                                    }

                                    nameTextView.setText(stringBuilder.toString());
                                }
                            });
                        }
                    }
                }

                // Get latest message
                source.collection("messenges").orderBy("time", Query.Direction.ASCENDING).limit(1).addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        if(e != null){
                            Log.d(TAG, e.getMessage());
                            return;
                        }else if(queryDocumentSnapshots != null){
                            DocumentSnapshot doc = queryDocumentSnapshots.getDocuments().get(0);
                            // Set text
                            if(doc.contains("text")){
                                previewTextView.setText((String) doc.get("text"));
                            }

                            // Set time
                            if(doc.contains("time")){
                                Locale locale = Locale.getDefault();
                                Date date = ((Date)doc.get("time"));
                                Date today = new Date();

                                long passedDays = TimeUnit.MILLISECONDS.toDays(today.getTime() - date.getTime());
                                if(passedDays < 1) {
                                    dateTextView.setText(DateFormat.getTimeFormat(getContext()).format(date));
                                }else if (passedDays < 2){
                                    dateTextView.setText(R.string.time_yesterday);
                                } else if (passedDays < 7){
                                    dateTextView.setText(DateFormat.format(DateFormat.getBestDateTimePattern(locale, "EEE"), date));
                                } else if (passedDays < 365){
                                    dateTextView.setText(DateFormat.format(DateFormat.getBestDateTimePattern(locale, "MM dd"), date));
                                } else {
                                    dateTextView.setText(DateFormat.format(DateFormat.getBestDateTimePattern(locale, "MM dd, yyyy"), date));
                                }
                            }
                        }
                    }
                });
            }
        });
    }

    public DocumentReference getSource(){
        return source;
    }
}
