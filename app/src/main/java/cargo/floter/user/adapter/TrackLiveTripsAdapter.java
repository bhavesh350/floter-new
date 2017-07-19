package cargo.floter.user.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import cargo.floter.user.OnTripActivity;
import cargo.floter.user.R;
import cargo.floter.user.application.MyApp;
import cargo.floter.user.model.Trip;
import cargo.floter.user.model.TripStatus;
import cargo.floter.user.utils.AppConstants;
import cargo.floter.user.utils.MyAnimationUtils;

import java.util.HashMap;
import java.util.List;

public class TrackLiveTripsAdapter extends Adapter<TrackLiveTripsAdapter.Holder> {
    private List<Trip> list;
    private Context mContext;
    int previousPosition = 0;

    public class Holder extends ViewHolder implements OnClickListener {
        TextView txt_date_time;
        TextView txt_from;
        TextView txt_status;
        TextView txt_time;
        TextView txt_to;

        public Holder(View itemView) {
            super(itemView);
            this.txt_from = (TextView) itemView.findViewById(R.id.txt_from);
            this.txt_time = (TextView) itemView.findViewById(R.id.txt_time);
            this.txt_date_time = (TextView) itemView.findViewById(R.id.txt_date_time);
            this.txt_status = (TextView) itemView.findViewById(R.id.txt_status);
            this.txt_to = (TextView) itemView.findViewById(R.id.txt_to);
            itemView.setOnClickListener(this);
        }

        public void onClick(View view) {
            HashMap<String, Trip> allTrips = MyApp.getApplication().readTrip();
            Trip tt = allTrips.get((TrackLiveTripsAdapter.this.list.get(getLayoutPosition())).getTrip_id());
            tt.setTrip_status(TripStatus.Accepted.name());
            allTrips.put((TrackLiveTripsAdapter.this.list.get(getLayoutPosition())).getTrip_id(), tt);
            MyApp.getApplication().writeTrip(allTrips);
            TrackLiveTripsAdapter.this.mContext.startActivity(new
                    Intent(TrackLiveTripsAdapter.this.mContext,
                    OnTripActivity.class).putExtra(AppConstants.EXTRA_1,
                    (TrackLiveTripsAdapter.this.list.get(getLayoutPosition())).getTrip_id()));
        }
    }

    public TrackLiveTripsAdapter(Context context, List<Trip> list) {
        this.list = list;
        this.mContext = context;
    }

    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new Holder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_live_trips, parent, false));
    }

    public void onBindViewHolder(Holder holder, int position) {
        Trip t = this.list.get(position);
        holder.txt_status.setText(t.getTrip_status());
        if (TextUtils.isEmpty(holder.txt_status.getText().toString())) {
            holder.txt_status.setText("Unknown");
        }
        holder.txt_date_time.setText(t.getTrip_modified());
        holder.txt_from.setText("From:" + t.getTrip_from_loc());
        holder.txt_to.setText("To:" + t.getTrip_to_loc());
        try {
            holder.txt_time.setText(t.getTrip_pickup_time().split(" ")[1]);
        } catch (Exception e) {
            holder.txt_time.setText(t.getTrip_modified().split(" ")[1].substring(0, 5));
        }
        if (position > this.previousPosition) {
            MyAnimationUtils.animate(holder, true);
        } else {
            MyAnimationUtils.animate(holder, false);
        }
        this.previousPosition = position;
    }

    public int getItemCount() {
        return this.list.size();
    }
}
