package cargo.floter.user;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.facebook.AccessToken;
import com.facebook.internal.AnalyticsEvents;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.RequestParams;
import cargo.floter.user.CustomActivity.ResponseCallback;
import cargo.floter.user.adapter.TrackLiveTripsAdapter;
import cargo.floter.user.application.MyApp;
import cargo.floter.user.application.SingleInstance;
import cargo.floter.user.model.Trip;
import cargo.floter.user.model.TripStatus;
import cargo.floter.user.utils.AppConstants;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class TrackTripsActivity extends CustomActivity implements ResponseCallback {
    private TrackLiveTripsAdapter adapter;
    private RelativeLayout rl_nodata;
    private RecyclerView rv_trips;
    private Toolbar toolbar;
    private List<Trip> trips;
    private TextView txt_book_now;
    private TextView txt_title;

    class C09701 extends TypeToken<List<Trip>> {
        C09701() {
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) R.layout.activity_live_trips);
        this.toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(this.toolbar);
        setResponseListener(this);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("");
        this.txt_title = (TextView) this.toolbar.findViewById(R.id.txt_title);
        this.txt_title.setText("Track Live Trips");
        this.rv_trips = (RecyclerView) findViewById(R.id.rv_trips);
        this.rl_nodata = (RelativeLayout) findViewById(R.id.rl_nodata);
        this.txt_book_now = (TextView) findViewById(R.id.txt_book_now);
        setTouchNClick(R.id.txt_book_now);
        this.trips = new ArrayList();
        this.adapter = new TrackLiveTripsAdapter(this, this.trips);
        this.rv_trips.setLayoutManager(new LinearLayoutManager(getContext(), 1, false));
        this.rv_trips.setAdapter(this.adapter);
        loadTrips();
    }

    public void onClick(View v) {
        super.onClick(v);
        if (v == this.txt_book_now) {
            finish();
        }
    }

    private void loadTrips() {
        RequestParams p = new RequestParams();
        p.put(AccessToken.USER_ID_KEY, MyApp.getApplication().readUser().getUser_id());
        p.put("trip_date", MyApp.millsToDate2(System.currentTimeMillis()));
        postCall(getContext(), AppConstants.BASE_URL_TRIP + "gettrips", p, "Getting live trips...", 1);
    }

    private Context getContext() {
        return this;
    }

    public void onJsonObjectResponseReceived(JSONObject o, int callNumber) {
        if (callNumber == 1 && o.optString("status").equals("OK")) {
            try {
                List<Trip> trips = new Gson().fromJson(o.getJSONArray("response").toString(), new C09701().getType());
                SingleInstance.getInstance().setAllTrips(trips);
                int counterData = 0;
                for (Trip t : trips) {
                    if (!(t.getTrip_status().equals(TripStatus.Finished.name())
                            || t.getTrip_status().equals(TripStatus.Pending.name())
                            || t.getTrip_status().equals(TripStatus.Declined.name())
                            || t.getTrip_status().equals(TripStatus.Cancelled.name())
                            || t.getTrip_status().equals(TripStatus.Driver_Cancel.name())
                            || t.getTrip_status().equals(AnalyticsEvents.PARAMETER_DIALOG_OUTCOME_VALUE_UNKNOWN)
                            || TextUtils.isEmpty(t.getTrip_status()))) {
                        this.trips.add(t);
                        counterData++;
                        HashMap<String, Trip> tripsAll = MyApp.getApplication().readTrip();
                        if (!tripsAll.containsKey(t.getTrip_id())) {
                            tripsAll.put(t.getTrip_id(), t);
                            MyApp.getApplication().writeTrip(tripsAll);
                        }
                    }
                }
                this.adapter.notifyDataSetChanged();
                if (counterData == 0) {
                    this.rl_nodata.setVisibility(View.VISIBLE);
                } else {
                    this.rl_nodata.setVisibility(View.GONE);
                }
            } catch (JSONException e) {
                this.rl_nodata.setVisibility(View.VISIBLE);
                e.printStackTrace();
            } catch (Exception e2) {
                MyApp.popMessage("Error!", "Data not available", getContext());
                e2.printStackTrace();
            }
        }
    }

    public void onJsonArrayResponseReceived(JSONArray a, int callNumber) {
    }

    public void onErrorReceived(String error) {
        MyApp.popMessage("Error!", error, getContext());
    }
}
