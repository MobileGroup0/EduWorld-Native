package group0.eduworld;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.*;

public class BookingView extends FrameLayout {
    private LinearLayout choiceGroup;
    private Button editButton;
    private TextView cancelledLabel;
    private ImageButton acceptButton;
    private ImageButton declineButton;

    public interface BookingViewListener{
        void onBookingStateChanged(BookingView bookingView);
    }

    public enum BookingState {
        NEW,
        ACCEPTED,
        DECLINED
    }

    private BookingState bookingState;
    private BookingViewListener listener;

    public BookingView(Context context) {
        super(context);
        init(context);
    }

    public BookingView(Context context, BookingViewListener bookingViewListener) {
        super(context);
        init(context);
        this.listener = bookingViewListener;
    }

    public BookingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public BookingView(Context context, AttributeSet attrs, BookingViewListener bookingViewListener) {
        super(context, attrs);
        init(context);
        this.listener = bookingViewListener;
    }

    public BookingView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    public BookingView(Context context, AttributeSet attrs, int defStyle, BookingViewListener bookingViewListener) {
        super(context, attrs, defStyle);
        init(context);
        this.listener = bookingViewListener;
    }

    public void addBookingViewListener(BookingViewListener bookingViewListener){
        this.listener = bookingViewListener;
    }

    private void init(Context context) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.view_booking, this);

        choiceGroup = findViewById(R.id.choiceGroup);
        editButton = findViewById(R.id.editButton);
        cancelledLabel = findViewById(R.id.cancelledLabel);
        acceptButton = findViewById(R.id.acceptButton);
        declineButton = findViewById(R.id.declineButton);

        acceptButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                setBookingState(BookingState.ACCEPTED);
            }
        });

        declineButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                setBookingState(BookingState.DECLINED);
            }
        });

        setBookingState(BookingState.NEW);
    }

    public void setBookingState(BookingState state) {
        if(this.bookingState == state) return;
        switch (state){
            case NEW:
                choiceGroup.setVisibility(View.VISIBLE);
                editButton.setVisibility(View.GONE);
                cancelledLabel.setVisibility(View.GONE);
                break;
            case ACCEPTED:
                choiceGroup.setVisibility(View.GONE);
                editButton.setVisibility(View.VISIBLE);
                cancelledLabel.setVisibility(View.GONE);
                break;
            case DECLINED:
                choiceGroup.setVisibility(View.GONE);
                editButton.setVisibility(View.GONE);
                cancelledLabel.setVisibility(View.VISIBLE);
                break;
        }
        this.bookingState = state;
        if(listener != null) listener.onBookingStateChanged(this);
    }

    public BookingState getBookingState() {
        return this.bookingState;
    }
}
