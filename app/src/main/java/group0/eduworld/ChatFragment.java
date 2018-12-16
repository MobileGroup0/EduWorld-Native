package group0.eduworld;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class ChatFragment extends Fragment implements ChatSessionCardView.ChatViewListener {
    public boolean shouldRefresh = true;
    private ArrayList<ChatSessionCardView> chatCards = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_chat, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        if(shouldRefresh){
            new ChatRetriever(this).execute();
            shouldRefresh = false;
        }
    }

    public void updateChatCard(ChatSessionCardView bf) {
        View view = getView();
        ViewGroup parent = (ViewGroup) bf.getParent();

        if (parent != null) {
            // detach the child from parent or you get an exception if you try
            // to add it to another one
            parent.removeView(bf);
        }

        ((ViewGroup) view.findViewById(R.id.chat_card_container)).addView(bf);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        shouldRefresh = true;
    }

    private static class ChatRetriever extends AsyncTask<Integer, Integer, ArrayList<ChatSessionCardView>> {
        private WeakReference<ChatFragment> chatFragmentWeakReference;

        private final ArrayList<ChatSessionCardView> fragmentList = new ArrayList<>();

        ChatRetriever(ChatFragment homeFragment){
            chatFragmentWeakReference = new WeakReference<>(homeFragment);
        }

        @Override
        protected ArrayList<ChatSessionCardView> doInBackground(Integer... integers) {
            final ChatFragment chatFragment = chatFragmentWeakReference.get();

            String uid = FirebaseAuth.getInstance().getUid();
            if (uid == null) return null;

            FirebaseFirestore db = FirebaseFirestore.getInstance();

            try {
                Tasks.await(db.collection("users")
                        .document(FirebaseAuth.getInstance().getUid())
                        .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot document = task.getResult();
                                    if (document != null && document.exists()) {
                                        if(!document.contains("chats")) return;
                                        final ArrayList<DocumentReference> chatDocRefs = (ArrayList<DocumentReference>) document.get("chats");
                                        for (DocumentReference dr: chatDocRefs) {
                                            ChatSessionCardView cf = new ChatSessionCardView(chatFragment.getContext(), chatFragment, dr);
                                            cf.updateData();

                                            if (chatFragment.isResumed()) {
                                                chatFragment.updateChatCard(cf);
                                                chatFragment.chatCards.add(cf);
                                                fragmentList.add(cf);
                                            }
                                        }
                                    } else {
                                    }
                                } else {
                                }
                            }
                        }));
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return fragmentList;
        }
    }
}
