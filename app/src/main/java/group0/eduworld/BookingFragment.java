package group0.eduworld;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;



public class BookingFragment extends Fragment {
    private BookingFragmentListener listener;

    interface BookingFragmentListener {
        void onBookingStateChanged(BookingFragment bookingFragment);
    }

    public enum BookingState {
        NEW,
        ACCEPTED,
        DECLINED
    }

    private BookingState bookingState = BookingState.NEW;


    @Override
    public void onStart() {
        super.onStart();

        View view = getView();
        assert view != null;

        view.findViewById(R.id.choiceGroup).setVisibility(View.GONE);
        view.findViewById(R.id.editButton).setVisibility(View.GONE);
        view.findViewById(R.id.cancelledLabel).setVisibility(View.GONE);
        switch(bookingState){
            case NEW:
                getView().findViewById(R.id.choiceGroup).setVisibility(View.VISIBLE);
                break;
            case ACCEPTED:
                getView().findViewById(R.id.editButton).setVisibility(View.VISIBLE);
                break;
            case DECLINED:
                getView().findViewById(R.id.cancelledLabel).setVisibility(View.VISIBLE);
                break;
        }

        view.findViewById(R.id.acceptButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setBookingState(BookingState.ACCEPTED);
            }
        });
        view.findViewById(R.id.declineButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setBookingState(BookingState.DECLINED);
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_booking, container, false);
    }

    public void addBookingFragmentListener(BookingFragmentListener listener){
        this.listener = listener;
    }

    public void setBookingState(BookingState state) {
        if(this.bookingState == state) return;
        this.bookingState = state;
        if(listener != null) listener.onBookingStateChanged(this);
    }

    public BookingState getBookingState() {
        return this.bookingState;
    }
}
