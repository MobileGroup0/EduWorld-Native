package group0.eduworld;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class BookingFragment extends Fragment {
    private int bookingState = 0;

    @Override
    public void onStart() {
        super.onStart();

        View view = getView();
        assert view != null;

        view.findViewById(R.id.choiceGroup).setVisibility(View.GONE);
        view.findViewById(R.id.editButton).setVisibility(View.GONE);
        view.findViewById(R.id.cancelledLabel).setVisibility(View.GONE);
        switch(bookingState){
            case 0:
                getView().findViewById(R.id.choiceGroup).setVisibility(View.VISIBLE);
                break;
            case 1:
                getView().findViewById(R.id.editButton).setVisibility(View.VISIBLE);
                break;
            case 2:
                getView().findViewById(R.id.cancelledLabel).setVisibility(View.VISIBLE);
                break;
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }
}
