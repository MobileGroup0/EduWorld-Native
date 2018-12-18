package group0.eduworld.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.*;
import com.google.firebase.firestore.EventListener;
import group0.eduworld.R;

import javax.annotation.Nullable;
import java.util.*;

public class ChatSessionCardView extends FrameLayout implements View.OnClickListener, EventListener<QuerySnapshot> {
    private final static String TAG = "ChatSessionCardView";
    private ImageView notifier;
    private TextView nameTextView;
    private TextView dateTextView;
    private TextView previewTextView;

    @Override
    public void onEvent(@Nullable QuerySnapshot snapshot, @Nullable FirebaseFirestoreException e) {
        if(snapshot != null){
            for (DocumentChange documentChange: snapshot.getDocumentChanges()) {
                if(documentChange.getType() == DocumentChange.Type.ADDED) {
                    HashMap<String, Object> documentData = (HashMap) documentChange.getDocument().getData();
                        List<DocumentReference> seen_by =  (List<DocumentReference>) documentData.get("seen_by");
                        FirebaseAuth mAuth = FirebaseAuth.getInstance();
                        notifier.setVisibility(View.VISIBLE);
                        if(seen_by != null)
                            for (DocumentReference ref : seen_by) {
                                if(ref.getId() == mAuth.getUid()){
                                    notifier.setVisibility(View.INVISIBLE);
                                    break;
                                }
                            }
                    getRootView().invalidate();
                }
            }
        }
    }

    public interface ChatViewListener{
        void openChat(String id);
    }

    private boolean newMessage = false;
    private ChatViewListener listener;

    private DocumentReference source;

    public ChatSessionCardView(Context context) {
        super(context);
        init(context);
    }

    public ChatSessionCardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ChatSessionCardView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    public ChatSessionCardView(Context context, ChatViewListener bookingViewListener, DocumentReference source) {
        super(context);
        this.source = source;
        init(context);
        this.listener = bookingViewListener;
    }

    public void addBookingViewListener(ChatViewListener chatViewListener){
        this.listener = chatViewListener;
    }

    private void init(Context context) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(group0.eduworld.R.layout.view_chat_session_card, this);

        findViewById(R.id.card).setOnClickListener(this);

        source.collection("messages").orderBy("time", Query.Direction.DESCENDING).limit(1).addSnapshotListener(this);

        notifier = findViewById(group0.eduworld.R.id.msgNotifier);
        nameTextView = findViewById(group0.eduworld.R.id.nameTextView);
        dateTextView = findViewById(group0.eduworld.R.id.dateTextView);
        previewTextView = findViewById(group0.eduworld.R.id.previewTextView);
    }

    public void onClick(View view){
        if(listener != null) listener.openChat(source.getId());
    }

    public void updateData(){
        source.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                // Get name
                ArrayList participants = (ArrayList) documentSnapshot.get("participants");
                for(Object user: Objects.requireNonNull(participants)){
                    if(user instanceof DocumentReference){
                        String uid = ((DocumentReference) user).getId();
                        if(!uid.equals(FirebaseAuth.getInstance().getUid())){
                            FirebaseFirestore.getInstance().document("/users/" + uid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    DocumentSnapshot result = task.getResult();
                                    if(!Objects.requireNonNull(result).exists()) return;
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
                source.collection("messages").orderBy("time", Query.Direction.DESCENDING).limit(1).addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        if(e != null){
                            Log.d(TAG, e.getMessage());
                        }else if(queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()){
                            DocumentSnapshot doc = queryDocumentSnapshots.getDocuments().get(0);

                            // Set text
                            if(doc.contains("text")) {
                                previewTextView.setText((String) doc.get("text"));
                            }

                            // Set time
                            if(doc.contains("time")){
                                Date date = ((Date)doc.get("time"));
                                String timeString = group0.eduworld.Util.formatDateTime(date);
                                dateTextView.setText(timeString);
                            }

                            if(doc.contains("seen_by")){
                                List<DocumentReference> seen_by =  (List<DocumentReference>)doc.get("seen_by");
                                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                                notifier.setVisibility(View.INVISIBLE);
                                for (DocumentReference ref : seen_by) {
                                    if(ref.getId() == mAuth.getUid()){
                                        notifier.setVisibility(View.VISIBLE);
                                        break;
                                    }
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
