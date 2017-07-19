package cargo.floter.user;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.internal.FacebookInitProvider;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import cargo.floter.user.application.MyApp;
import cargo.floter.user.model.Trip;
import cargo.floter.user.model.TripStatus;
import cargo.floter.user.utils.AppConstants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import cz.msebera.android.httpclient.Header;

public class OffersActivity extends CustomActivity implements CustomActivity.ResponseCallback {

    private Toolbar toolbar;
    private ProgressBar progress;
    private TextView txt_ride_counter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offers);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("");
        setResponseListener(this);
        setupUiElements();
        loadTodaysHistory();
    }

    private void setupUiElements() {
        progress = (ProgressBar) findViewById(R.id.progress);
        txt_ride_counter = (TextView) findViewById(R.id.txt_ride_counter);
        setTouchNClick(R.id.txt_book_ride);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        if (v.getId() == R.id.txt_book_ride) {
            finish();
        }
    }

    private void loadTodaysHistory() {
        RequestParams p = new RequestParams();
        p.put(AccessToken.USER_ID_KEY, MyApp.getApplication().readUser().getUser_id());
        p.put("trip_date", MyApp.millsToDate2(System.currentTimeMillis()));
        postCall(this, AppConstants.BASE_URL_TRIP + "gettrips", p, "", 1);
    }

    @Override
    public void onJsonObjectResponseReceived(JSONObject o, int callNumber) {
        this.progress.setVisibility(View.GONE);
        if (callNumber == 1 && o.optString("status").equals("OK")) {
            try {
                List<Trip> trips = new Gson().fromJson(o.getJSONArray("response").toString(), new C09666().getType());
                int count = 0;
                for (int i = 0; i < trips.size(); i++) {
                    if ((trips.get(i)).getTrip_status().equals(TripStatus.Finished.name())) {
                        count++;
                        this.txt_ride_counter.setText("" + count);
                    }
                }
            } catch (JSONException e) {
                this.txt_ride_counter.setText("0");
                e.printStackTrace();
            } catch (Exception e2) {
                this.txt_ride_counter.setText("0");
                e2.printStackTrace();
            }
        }
    }


    @Override
    public void onJsonArrayResponseReceived(JSONArray a, int callNumber) {

    }

    @Override
    public void onErrorReceived(String error) {
        this.progress.setVisibility(View.GONE);
    }

    class C09666 extends TypeToken<List<Trip>> {
        C09666() {
        }
    }

}
