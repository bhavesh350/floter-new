package cargo.floter.user;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.facebook.AccessToken;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.RequestParams;
import cargo.floter.user.CustomActivity.ResponseCallback;
import cargo.floter.user.adapter.TripPagerAdapter;
import cargo.floter.user.application.MyApp;
import cargo.floter.user.application.SingleInstance;
import cargo.floter.user.fragments.TripListFragment;
import cargo.floter.user.model.Trip;
import cargo.floter.user.utils.AppConstants;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class HistoryActivity extends CustomActivity implements ResponseCallback {
    private TripPagerAdapter adapter;
    private TabLayout tab_trips;
    private Toolbar toolbar;
    private ViewPager viewPager;

    class C09591 extends TypeToken<List<Trip>> {
        C09591() {
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setResponseListener(this);
        setContentView((int) R.layout.activity_history);
        this.toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(this.toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("");
        this.tab_trips = (TabLayout) findViewById(R.id.tab_trips);
        this.viewPager = (ViewPager) findViewById(R.id.pager);
        this.adapter = new TripPagerAdapter(getSupportFragmentManager());
        TripPagerAdapter tripPagerAdapter = this.adapter;
        TripListFragment tripListFragment = new TripListFragment();
        tripPagerAdapter.addFragment(TripListFragment.newInstance(1), "All");
        tripPagerAdapter = this.adapter;
        tripListFragment = new TripListFragment();
        tripPagerAdapter.addFragment(TripListFragment.newInstance(2), "Live");
        tripPagerAdapter = this.adapter;
        tripListFragment = new TripListFragment();
        tripPagerAdapter.addFragment(TripListFragment.newInstance(3), "Finished");
        tripPagerAdapter = this.adapter;
        tripListFragment = new TripListFragment();
        tripPagerAdapter.addFragment(TripListFragment.newInstance(4), "Upcoming");
        this.viewPager.setAdapter(this.adapter);
        this.tab_trips.setupWithViewPager(this.viewPager);
        this.tab_trips.setSelectedTabIndicatorColor(Color.parseColor("#0E577D"));
        loadHistory();
    }

    private void loadHistory() {
        RequestParams p = new RequestParams();
        p.put(AccessToken.USER_ID_KEY, MyApp.getApplication().readUser().getUser_id());
        postCall(getContext(), AppConstants.BASE_URL_TRIP + "gettrips", p, "Getting all trips...", 1);
    }

    public void onJsonObjectResponseReceived(JSONObject o, int callNumber) {
        if (callNumber == 1 && o.optString("status").equals("OK")) {
            try {
                SingleInstance.getInstance().setAllTrips((List) new Gson().fromJson(o.getJSONArray("response").toString(), new C09591().getType()));
                TripPagerAdapter adapter = new TripPagerAdapter(getSupportFragmentManager());
                TripListFragment tripListFragment = new TripListFragment();
                adapter.addFragment(TripListFragment.newInstance(1), "All");
                tripListFragment = new TripListFragment();
                adapter.addFragment(TripListFragment.newInstance(2), "Live");
                tripListFragment = new TripListFragment();
                adapter.addFragment(TripListFragment.newInstance(3), "Finished");
                tripListFragment = new TripListFragment();
                adapter.addFragment(TripListFragment.newInstance(4), "Upcoming");
                this.viewPager.setAdapter(adapter);
                this.tab_trips.setTabMode(TabLayout.MODE_FIXED);
                this.tab_trips.setupWithViewPager(this.viewPager);
                this.tab_trips.setSelectedTabIndicatorColor(Color.parseColor("#0E577D"));
                this.tab_trips.setVisibility(View.VISIBLE);
            } catch (JSONException e) {
                MyApp.popMessage("Error!", "Data parsing error.", getContext());
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

    private Context getContext() {
        return this;
    }
}
