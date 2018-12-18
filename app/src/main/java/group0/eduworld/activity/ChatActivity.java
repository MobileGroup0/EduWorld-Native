package group0.eduworld.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.*;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.functions.FirebaseFunctions;
import group0.eduworld.R;
import group0.eduworld.messaging.ChatRecyclerViewAdapter;
import group0.eduworld.messaging.Message;
import group0.eduworld.messaging.ReceivedMessage;
import group0.eduworld.messaging.SentMessage;

import javax.annotation.Nullable;
import java.util.*;

public class ChatActivity extends AppCompatActivity implements EventListener<QuerySnapshot> {
    private final static String TAG = "ChatActivity";
    private FirebaseFunctions mFunctions;

    private DocumentReference chatDocRef;
    private RecyclerView recyclerView;
    private ChatRecyclerViewAdapter viewAdapter;
    private EditText chatTextBox;
    private RecyclerView.LayoutManager layoutManager;
    private ListenerRegistration listenerRegistration;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        mFunctions = FirebaseFunctions.getInstance();

        String chat_id = getIntent().getStringExtra("chat_id");
        if(chat_id != null){
            chatDocRef = FirebaseFirestore.getInstance().collection("chats").document(chat_id);
        }

        chatTextBox = findViewById(R.id.edittext_chatbox);
        viewAdapter = new ChatRecyclerViewAdapter(this, new ArrayList<Message>());
        layoutManager = new LinearLayoutManager(this);
        recyclerView = findViewById(R.id.chat_recycler);
        recyclerView.setAdapter(viewAdapter);
        recyclerView.setLayoutManager(layoutManager);
    }

    public void onClick(View view){
        if(view.getId() == R.id.close_button)
            finish();
        else if(view.getId() == R.id.button_chatbox_send){
            Map messageData = new HashMap();
            messageData.put("text", chatTextBox.getText().toString());
            messageData.put("chat_id", chatDocRef.getId());

            mFunctions
                    .getHttpsCallable("postMessage")
                    .call(messageData);

            chatTextBox.setText("");
        }
    }

    private void addMessage (HashMap data){
        Message msg;
        String sender_uid = ((DocumentReference) Objects.requireNonNull(data.get("sender"))).getId();
        if (sender_uid.equals(FirebaseAuth.getInstance().getUid())){
            msg = new SentMessage();
        } else {
            msg = new ReceivedMessage();
            ((ReceivedMessage) msg).setSender(sender_uid);
        }
        msg.setCreatedAt((Date) Objects.requireNonNull(data.get("time")));
        msg.setText((String) Objects.requireNonNull(data.get("text")));
        viewAdapter.addMessage(msg);
        recyclerView.scrollToPosition(viewAdapter.getItemCount()-1);
    }


    @Override
    protected void onResume() {
        super.onResume();
        listenerRegistration = chatDocRef.collection("messages")
                .orderBy("time", Query.Direction.DESCENDING)
                .limit(10)
                .addSnapshotListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(listenerRegistration!=null) {
            listenerRegistration.remove();
            listenerRegistration = null;
        }
    }

    @Override
    public void onEvent(@Nullable QuerySnapshot snapshot, @Nullable FirebaseFirestoreException e) {
        if(snapshot != null){
            Stack<HashMap> changes = new Stack<>();
            for (DocumentChange documentChange: snapshot.getDocumentChanges()) {
                if(documentChange.getType() == DocumentChange.Type.ADDED) {
                    HashMap<String, Object> documentData = (HashMap) documentChange.getDocument().getData();
                    ArrayList<DocumentReference> seen_by = (ArrayList<DocumentReference>)documentData.get("seen_by");
                    boolean already_seen = false;
                    if(seen_by != null) {for(DocumentReference dr : seen_by) if(dr.getId().equals(FirebaseAuth.getInstance().getUid())) already_seen = true;}
                    else seen_by = new ArrayList<>();
                    if(!already_seen) {
                        seen_by.add(FirebaseFirestore.getInstance().collection("users").document(FirebaseAuth.getInstance().getUid()));
                        documentChange.getDocument().getReference().update("seen_by", seen_by);
                    }
                    changes.push(documentData);
                }
            }
            while (!changes.empty()){
                addMessage(changes.pop());
            }
        }
    }
}
