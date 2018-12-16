package group0.eduworld.fragment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import group0.eduworld.R;
import group0.eduworld.view.BookingView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

public class HomeFragment extends Fragment implements BookingView.BookingViewListener {

    private ArrayList<BookingView> bookingCards = new ArrayList<>();
    public boolean shouldRefresh = true;

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
        if(shouldRefresh){
            new BookingRetriever(this).execute();
            shouldRefresh = false;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        updateHeaders();
    }

    @Override
    public void onPause() {
        super.onPause();

    }

    @Override
    public void onDetach() {
        super.onDetach();
        shouldRefresh = true;
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

        new BookingSender(this).execute(bookingView);
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

            try {
                Tasks.await(db.collection("users")
                        .document(FirebaseAuth.getInstance().getUid())
                        .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot document = task.getResult();
                                    if (document != null && document.exists()) {
                                        if(!document.contains("bookings")) return;
                                        final ArrayList<DocumentReference> bookingDocRefs = (ArrayList<DocumentReference>) document.get("bookings");
                                        for (DocumentReference dr: bookingDocRefs) {
                                            dr.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                    DocumentSnapshot document = task.getResult();
                                                    BookingView bf = new BookingView(homeFragment.getContext(), homeFragment, document.getReference());
                                                    bf.setData(document.getData());

                                                    if(homeFragment.isResumed()) {
                                                        homeFragment.updateBookingCard(bf);
                                                        homeFragment.bookingCards.add(bf);
                                                        fragmentList.add(bf);
                                                    }
                                                }
                                            });
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


    private static class BookingSender extends AsyncTask<BookingView, Integer, Integer>{
        private WeakReference<HomeFragment> homeFragmentWeakReference;

        BookingSender(HomeFragment homeFragment){
            homeFragmentWeakReference = new WeakReference<>(homeFragment);
        }

        @Override
        protected Integer doInBackground(BookingView... bookings) {
            final HomeFragment homeFragment = homeFragmentWeakReference.get();

            String uid = FirebaseAuth.getInstance().getUid();
            if (uid == null) return null;

            HashMap<String, Object> data = new HashMap<>();


            for (BookingView bv : bookings) {
                switch (bv.getBookingState()){
                    case NEW:
                        data.put("status", "new");
                        break;
                    case ACCEPTED:
                        data.put("status", "accepted");
                        break;
                    case DECLINED:
                        data.put("status", "declined");
                }
                bv.getSource().update(data);
            }

            return 0;
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
        }
    }
}
