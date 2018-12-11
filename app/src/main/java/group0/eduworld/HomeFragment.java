package group0.eduworld;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Objects;

public class HomeFragment extends Fragment implements BookingFragment.BookingFragmentListener {

    private ArrayList<BookingFragment> bookingCards = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onStart() {
        super.onStart();
        @NonNull View view = Objects.requireNonNull(getView());

        new BookingRetriever(this).execute();
    }

    public void addBookingCard(BookingFragment bf) {
        @NonNull FragmentManager fm = Objects.requireNonNull(getFragmentManager());
        if(bookingCards.contains(bf) || bf.isAdded()){
            return;
        }
        bookingCards.add(bf);
        bf.addBookingFragmentListener(this);
        @NonNull FragmentTransaction ft = fm.beginTransaction();
        switch (bf.getBookingState()){
            case NEW:
                ft.add(R.id.booking_container_new, bf);
                break;
            case ACCEPTED:
                ft.add(R.id.booking_container_accepted, bf);
                break;
            case DECLINED:
                ft.add(R.id.booking_container_declined, bf);
                break;
        }
        ft.commit();
    }

    public void removeBookingCard(BookingFragment bf) {
        @NonNull FragmentManager fm = Objects.requireNonNull(getFragmentManager());
        if(bookingCards.contains(bf) || bf.isAdded()){
            return;
        }
        bookingCards.remove(bf);
        @NonNull FragmentTransaction ft = fm.beginTransaction();
        ft.remove(bf);
        ft.commit();
    }

    public void updateBookingCard(BookingFragment bf) {
        @NonNull FragmentManager fm = Objects.requireNonNull(getFragmentManager());
        if(!bookingCards.contains(bf) || !bf.isAdded()){
            return;
        }

        @NonNull FragmentTransaction ft = fm.beginTransaction();
        ft.remove(bf);
        ft.commitNow();
        ft = fm.beginTransaction();
        switch (bf.getBookingState()){
            case NEW:
                ft.add(R.id.booking_container_new, bf);
                break;
            case ACCEPTED:
                ft.add(R.id.booking_container_accepted, bf);
                break;
            case DECLINED:
                ft.add(R.id.booking_container_declined, bf);
                break;
        }

        ft.commit();
    }

    @Override
    public void onBookingStateChanged(BookingFragment bookingFragment) {
        updateBookingCard(bookingFragment);
    }


    private static class BookingRetriever extends AsyncTask<Integer, Integer, ArrayList<BookingFragment>>{
        private WeakReference<HomeFragment> homeFragmentWeakReference;

        private final ArrayList<BookingFragment> fragmentList = new ArrayList<>();

        BookingRetriever(HomeFragment homeFragment){
            homeFragmentWeakReference = new WeakReference<>(homeFragment);
        }

        @Override
        protected ArrayList<BookingFragment> doInBackground(Integer... integers) {
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
                                                BookingFragment bf = new BookingFragment();
                                                switch (status){
                                                    case "accepted":
                                                        bf.setBookingState(BookingFragment.BookingState.ACCEPTED);
                                                        break;
                                                    case "declined":
                                                        bf.setBookingState(BookingFragment.BookingState.DECLINED);
                                                    default:
                                                        bf.setBookingState(BookingFragment.BookingState.NEW);
                                                }

                                                homeFragmentWeakReference.get().addBookingCard(bf);
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
