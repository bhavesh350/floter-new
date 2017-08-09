package cargo.floter.user.adapter;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.internal.AnalyticsEvents;

import cargo.floter.user.R;
import cargo.floter.user.HistoryDetailsActivity;
import cargo.floter.user.HistoryDetailsNoTripActivity;
import cargo.floter.user.OnTripActivity;
import cargo.floter.user.application.MyApp;
import cargo.floter.user.application.SingleInstance;
import cargo.floter.user.model.Trip;
import cargo.floter.user.model.TripStatus;
import cargo.floter.user.utils.AppConstants;
import cargo.floter.user.utils.MyAnimationUtils;

import java.util.HashMap;
import java.util.List;

public class HistoryAdapter extends Adapter<HistoryAdapter.Holder> {
    private List<Trip> list;
    private Fragment mContext;
    int previousPosition = 0;

    public class Holder extends ViewHolder implements OnClickListener {
        TextView txt_date_time;
        TextView txt_from;
        TextView txt_status;
        TextView txt_to;

        public Holder(View itemView) {
            super(itemView);
            this.txt_from = (TextView) itemView.findViewById(R.id.txt_from);
            this.txt_date_time = (TextView) itemView.findViewById(R.id.txt_date_time);
            this.txt_status = (TextView) itemView.findViewById(R.id.txt_status);
            this.txt_to = (TextView) itemView.findViewById(R.id.txt_to);
            itemView.setOnClickListener(this);
        }

        public void onClick(View view) {
            Intent intent;
            if ((list.get(getLayoutPosition())).getTrip_status().equals(TripStatus.Finished.name())) {
                intent = new Intent(mContext.getActivity(), HistoryDetailsActivity.class);
                SingleInstance.getInstance().setHistoryTrip(list.get(getLayoutPosition()));
                mContext.startActivity(intent);
            } else if ((list.get(getLayoutPosition())).getTrip_status().equals(TripStatus.Declined.name())) {
                intent = new Intent(mContext.getActivity(), HistoryDetailsNoTripActivity.class);
                SingleInstance.getInstance().setHistoryTrip(list.get(getLayoutPosition()));
                mContext.startActivity(intent);
            } else if ((list.get(getLayoutPosition())).getTrip_status().equals(TripStatus.Finished.name())
                    || (list.get(getLayoutPosition())).getTrip_status().equals(TripStatus.Pending.name())
                    || (list.get(getLayoutPosition())).getTrip_status().equals(TripStatus.Upcoming.name())
                    || (list.get(getLayoutPosition())).getTrip_status().equals("Unknown")
                    || TextUtils.isEmpty((list.get(getLayoutPosition())).getTrip_status())) {
                intent = new Intent(mContext.getActivity(), HistoryDetailsNoTripActivity.class);
                SingleInstance.getInstance().setHistoryTrip(list.get(getLayoutPosition()));
                mContext.startActivity(intent);
            } else {
                HashMap<String, Trip> allTrips = MyApp.getApplication().readTrip();
                if (allTrips.containsKey((list.get(getLayoutPosition())).getTrip_id())) {
                    Trip tt = allTrips.get((list.get(getLayoutPosition())).getTrip_id());
                    tt.setTrip_status(TripStatus.Accepted.name());
                    allTrips.put((list.get(getLayoutPosition())).getTrip_id(), tt);
                } else {
                    Trip t = list.get(getLayoutPosition());
                    t.setTrip_status(TripStatus.Accepted.name());
                    allTrips.put((list.get(getLayoutPosition())).getTrip_id(), t);
                }


                MyApp.getApplication().writeTrip(allTrips);
                mContext.getActivity().startActivity(new Intent(mContext.getActivity(),
                        OnTripActivity.class).putExtra(AppConstants.EXTRA_1,
                        (list.get(getLayoutPosition())).getTrip_id()));
                mContext.getActivity().finish();

            }
        }
    }

    public HistoryAdapter(Fragment context, List<Trip> list) {
        this.list = list;
        this.mContext = context;
    }

    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new Holder(LayoutInflater.from(parent.getContext()).inflate(R.layout.history_item, parent, false));
    }

    public void onBindViewHolder(Holder holder, int position) {
        Trip t = this.list.get(position);
        holder.txt_status.setText(t.getTrip_status());
        if (TextUtils.isEmpty(holder.txt_status.getText().toString())) {
            holder.txt_status.setText("Unknown");
        }
        try {
            holder.txt_date_time.setText(MyApp.convertTime(t.getTrip_modified()));
        } catch (Exception e) {
        }

        holder.txt_from.setText("From:" + t.getTrip_from_loc());
        holder.txt_to.setText("To:" + t.getTrip_to_loc());
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
