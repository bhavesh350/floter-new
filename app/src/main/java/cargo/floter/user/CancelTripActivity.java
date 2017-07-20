package cargo.floter.user;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import cargo.floter.user.CustomActivity.ResponseCallback;
import cargo.floter.user.application.MyApp;
import cargo.floter.user.model.Trip;
import cargo.floter.user.model.TripStatus;
import cargo.floter.user.utils.AppConstants;
import cz.msebera.android.httpclient.Header;
import java.util.HashMap;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class CancelTripActivity extends CustomActivity implements ResponseCallback {
    private String deviceToken = "";
    private RadioGroup radio_group;
    private String selectedText;
    private Toolbar toolbar;
    private String tripId = "";

    class C10931 extends JsonHttpResponseHandler {
        C10931() {
        }

        public void onSuccess(int statusCode, Header[] headers, String response) {
            Log.d("Response:", response.toString());
        }

        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
            if (statusCode != 0) {
            }
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cancel_trip);
        this.toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(this.toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("");
        this.radio_group = (RadioGroup) findViewById(R.id.radio_group);
        setTouchNClick(R.id.txt_submit);
        setResponseListener(this);
        this.tripId = getIntent().getStringExtra(AppConstants.EXTRA_1);
        this.deviceToken = getIntent().getStringExtra(AppConstants.EXTRA_2);
        if (TextUtils.isEmpty(this.tripId)) {
            MyApp.showMassage(this, "Invalid trip");
            finish();
        }
    }

    public void onClick(View v) {
        super.onClick(v);
        if (v.getId() == R.id.txt_submit) {
            callCancelTripApi();
        }
    }

    private void callCancelTripApi() {
        this.selectedText = ((RadioButton) this.radio_group.getChildAt(this.radio_group.indexOfChild(this.radio_group.findViewById(this.radio_group.getCheckedRadioButtonId())))).getText().toString();
        RequestParams p = new RequestParams();
        p.put("trip_id", this.tripId);
        p.put("trip_status", TripStatus.Cancelled.name());
        p.put("trip_feedback", this.selectedText);
        postCall(this, AppConstants.BASE_URL_TRIP + "updatetrip", p, "Please wait...", 1);
    }

    public void onJsonObjectResponseReceived(JSONObject o, int callNumber) {
        if (o.optString("status").equals("OK") && callNumber == 1) {
            Trip t = null;
            try {
                t = (Trip) new Gson().fromJson(o.getJSONObject("response").toString(), Trip.class);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (t == null) {
                MyApp.popMessage("Error!", "Trip not found...", this);
            } else if (t.getTrip_status().equals(TripStatus.Cancelled.name())) {
                AsyncHttpClient client = new AsyncHttpClient();
                RequestParams p = new RequestParams();
                p.put("message", "Trip cancelled by the user with reason:\n" + this.selectedText);
                p.put("trip_id", this.tripId);
                p.put("trip_status", TripStatus.Cancelled.name());
                p.put("object", "\"{\"json\":\"json\"}\"");
                p.put("android", this.deviceToken);
                client.setTimeout(30000);
                client.post("http://floter.in/floterapi/push/DriverPushNotification?", p, new C10931());
                HashMap<String, Trip> trips = MyApp.getApplication().readTrip();
                if (trips.containsKey(this.tripId)) {
                    trips.remove(this.tripId);
                    MyApp.getApplication().writeTrip(trips);
                }
                startActivity(new Intent(this, MainActivity.class));
                finishAffinity();
            } else {
                MyApp.showMassage(this, "Some problem occurred while cancel the trip");
            }
        }
    }

    public void onJsonArrayResponseReceived(JSONArray a, int callNumber) {
    }

    public void onErrorReceived(String error) {
    }
}
