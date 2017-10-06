package cargo.floter.user.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.internal.AnalyticsEvents;
import java.util.ArrayList;
import java.util.List;

import cargo.floter.user.R;
import cargo.floter.user.adapter.HistoryAdapter;
import cargo.floter.user.application.SingleInstance;
import cargo.floter.user.model.Trip;
import cargo.floter.user.model.TripStatus;

public class TripListFragment extends CustomFragment {
    private HistoryAdapter adapter;
    private int position = 1;
    private RelativeLayout rl_nodata;
    private RecyclerView rv_history;
    private TextView txt_book_now;

    public static TripListFragment newInstance(int pos) {
        TripListFragment fragment = new TripListFragment();
        Bundle bundle = new Bundle(2);
        bundle.putInt("POS", pos);
        fragment.setArguments(bundle);
        return fragment;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.tab_trips, container, false);
    }

    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.rv_history = (RecyclerView) view.findViewById(R.id.rc_history);
        this.rl_nodata = (RelativeLayout) view.findViewById(R.id.rl_nodata);
        this.txt_book_now = (TextView) view.findViewById(R.id.txt_book_now);
        setTouchNClick(this.txt_book_now);
        List<Trip> trips;
        if (this.position == 3) {
            trips = new ArrayList();
            for (Trip t : SingleInstance.getInstance().getAllTrips()) {
                if (t.getTrip_status().equals(TripStatus.Finished.name())) {
                    trips.add(t);
                }
            }
            if (trips.size() == 0) {
                this.rl_nodata.setVisibility(View.VISIBLE);
            } else {
                this.rl_nodata.setVisibility(View.GONE);
            }
            this.adapter = new HistoryAdapter(this, trips);
            this.rv_history.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
            this.rv_history.setAdapter(this.adapter);
        } else if (this.position == 1) {
            if (SingleInstance.getInstance().getAllTrips().size() == 0) {
                this.rl_nodata.setVisibility(View.VISIBLE);
            } else {
                this.rl_nodata.setVisibility(View.GONE);
            }
            this.adapter = new HistoryAdapter(this, SingleInstance.getInstance().getAllTrips());
            this.rv_history.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
            this.rv_history.setAdapter(this.adapter);
        } else if (this.position == 2) {
            trips = new ArrayList();
            for (Trip t2 : SingleInstance.getInstance().getAllTrips()) {
                if (!(t2.getTrip_status().equals(TripStatus.Finished.name())
                        || t2.getTrip_status().equals(TripStatus.Pending.name())
                        || t2.getTrip_status().equals(TripStatus.Cancelled.name())
                        || t2.getTrip_status().equals(TripStatus.Driver_Cancel.name())
                        || t2.getTrip_status().equals(TripStatus.Declined.name())
                        || t2.getTrip_status().equals(TripStatus.Upcoming.name())
                        || t2.getTrip_status().equals(AnalyticsEvents.PARAMETER_DIALOG_OUTCOME_VALUE_UNKNOWN)
                        || TextUtils.isEmpty(t2.getTrip_status()))) {
                    trips.add(t2);
                }
            }
            if (trips.size() == 0) {
                this.rl_nodata.setVisibility(View.VISIBLE);
            } else {
                this.rl_nodata.setVisibility(View.GONE);
            }
            this.adapter = new HistoryAdapter(this, trips);
            this.rv_history.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
            this.rv_history.setAdapter(this.adapter);
        } else if (this.position == 4) {
            trips = new ArrayList();
            for (Trip t22 : SingleInstance.getInstance().getAllTrips()) {
                if (t22.getTrip_status().equals(TripStatus.Upcoming.name())) {
                    trips.add(t22);
                }
            }
            if (trips.size() == 0) {
                this.rl_nodata.setVisibility(View.VISIBLE);
            } else {
                this.rl_nodata.setVisibility(View.GONE);
            }
            this.adapter = new HistoryAdapter(this, trips);
            this.rv_history.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
            this.rv_history.setAdapter(this.adapter);
        }
    }

    public void onClick(View v) {
        super.onClick(v);
        if (v == this.txt_book_now) {
            getActivity().finish();
        }
    }

    public void onResume() {
        super.onResume();
    }

    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.position = getArguments().getInt("POS");
    }
}
