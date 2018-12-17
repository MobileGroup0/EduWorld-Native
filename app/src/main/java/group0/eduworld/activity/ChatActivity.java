package group0.eduworld.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import group0.eduworld.R;
import group0.eduworld.messaging.ChatRecyclerViewAdapter;
import group0.eduworld.messaging.Message;
import group0.eduworld.messaging.ReceivedMessage;
import group0.eduworld.messaging.SentMessage;

import java.util.ArrayList;
import java.util.Date;

public class ChatActivity extends AppCompatActivity {
    private final static String TAG = "ChatActivity";
    private RecyclerView recyclerView;
    private ChatRecyclerViewAdapter viewAdapter;
    private RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        ArrayList<Message> messages = new ArrayList<>();
        Message tmp = new SentMessage();
        tmp.setText("Test message");
        tmp.setCreatedAt(new Date(new Date().getTime() - 6000000000L));
        messages.add(tmp);

        tmp = new ReceivedMessage();
        tmp.setText("Test message 2");
        tmp.setCreatedAt(new Date(new Date().getTime() - 600000000));
        ((ReceivedMessage) tmp).setSender("Stuart");
        messages.add(tmp);

        tmp = new ReceivedMessage();
        tmp.setText("Test message 3");
        tmp.setCreatedAt(new Date(new Date().getTime() - 200000));
        ((ReceivedMessage) tmp).setSender("Stuart");
        messages.add(tmp);

        tmp = new SentMessage();
        tmp.setText("Test message 4");
        tmp.setCreatedAt(new Date());
        messages.add(tmp);

        viewAdapter = new ChatRecyclerViewAdapter(this, messages);
        layoutManager = new LinearLayoutManager(this);
        recyclerView = findViewById(R.id.chat_recycler);
        recyclerView.setAdapter(viewAdapter);
        recyclerView.setLayoutManager(layoutManager);
    }

    public void onClick(View view){
        if(view.getId() == R.id.close_button)
            finish();
    }
}
