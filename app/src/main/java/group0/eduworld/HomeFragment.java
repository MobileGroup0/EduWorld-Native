package group0.eduworld;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class HomeFragment extends Fragment implements BookingView.BookingViewListener {

    private ArrayList<BookingView> bookingCards = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState == null){
            new BookingRetriever(this).execute();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        updateHeaders();
    }

    public void removeBookingCard(BookingView bf) {

    }

    public void updateBookingCard(BookingView bf) {
        View view = getView();
        ViewGroup parent = (ViewGroup) bf.getParent();

        if (parent != null) {
            // detach the child from parent or you get an exception if you try
            // to add it to another one
            parent.removeView(bf);
        }
        switch (bf.getBookingState()){
                case NEW:
                    ((ViewGroup) view.findViewById(R.id.booking_container_new)).addView(bf);
                    break;
                case ACCEPTED:
                    ((ViewGroup) view.findViewById(R.id.booking_container_accepted)).addView(bf);
                    break;
                case DECLINED:
                    ((ViewGroup) view.findViewById(R.id.booking_container_declined)).addView(bf);
                    break;
        }
    }

    public void updateHeaders() {

    }

    @Override
    public void onBookingStateChanged(BookingView bookingView) {
        updateBookingCard(bookingView);
        updateHeaders();
    }


    private static class BookingRetriever extends AsyncTask<Integer, Integer, ArrayList<BookingView>>{
        private WeakReference<HomeFragment> homeFragmentWeakReference;

        private final ArrayList<BookingView> fragmentList = new ArrayList<>();

        BookingRetriever(HomeFragment homeFragment){
            homeFragmentWeakReference = new WeakReference<>(homeFragment);
        }

        @Override
        protected ArrayList<BookingView> doInBackground(Integer... integers) {
            final HomeFragment homeFragment = homeFragmentWeakReference.get();

            String uid = FirebaseAuth.getInstance().getUid();
            if (uid == null) return null;

            FirebaseFirestore db = FirebaseFirestore.getInstance();

            db.collection("users")
                    .document(FirebaseAuth.getInstance().getUid())
                    .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document != null && document.exists()) {
                                    ArrayList<DocumentReference> bookingDocRefs = (ArrayList<DocumentReference>) document.get("bookings");
                                    for (DocumentReference dr: bookingDocRefs) {
                                        dr.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                DocumentSnapshot document = task.getResult();
                                                String status = document.get("status").toString().toLowerCase();
                                                BookingView bf = new BookingView(homeFragment.getContext(), homeFragment);
                                                switch (status){
                                                    case "accepted":
                                                        bf.setBookingState(BookingView.BookingState.ACCEPTED);
                                                        break;
                                                    case "declined":
                                                        bf.setBookingState(BookingView.BookingState.DECLINED);
                                                    default:
                                                        bf.setBookingState(BookingView.BookingState.NEW);
                                                }

                                                homeFragment.updateBookingCard(bf);
                                                fragmentList.add(bf);
                                            }
                                        });
                                    }
                                } else {
                                }
                            } else {
                            }
                        }
                    });
            return fragmentList;
        }
    }
}
