package cargo.floter.user;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.TextView;
import com.google.gson.Gson;
import com.loopj.android.http.RequestParams;
import cargo.floter.user.CustomActivity.ResponseCallback;
import cargo.floter.user.adapter.VehicleAdapter;
import cargo.floter.user.model.RateCard;
import cargo.floter.user.model.RateCard.RateCardResponse;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONObject;

public class VehicleDetails extends CustomActivity implements ResponseCallback {
    private VehicleAdapter adapter;
    private ArrayList<RateCardResponse> listdata;
    private RecyclerView recView;
    private Toolbar toolbar_title;

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vehicle_details);
        this.toolbar_title = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(this.toolbar_title);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        ((TextView) this.toolbar_title.findViewById(R.id.toolbar_title)).setText("Rate Card");
        actionBar.setTitle((CharSequence) "");
        setupUiElements();
        setResponseListener(this);
        this.listdata = new ArrayList();
        this.adapter = new VehicleAdapter(this.listdata, this);
        this.recView = (RecyclerView) findViewById(R.id.rec_list);
        this.recView.setLayoutManager(new LinearLayoutManager(this, 1, false));
        this.recView.setAdapter(this.adapter);
        loadRateCard();
    }

    private void setupUiElements() {
    }

    private void loadRateCard() {
        normalPostCall(this, "http://stubuz.com/floterapi/index.php/carapi/getratecard", "Getting RateCards...", 1);
    }

    public void onJsonObjectResponseReceived(JSONObject o, int callNumber) {
        Log.d("response", o.toString());
        if (o.optString("status").equals("OK")) {
            this.listdata.addAll((new Gson().fromJson(o.toString(), RateCard.class)).getResponse());
            this.adapter.notifyDataSetChanged();
        }
    }

    public void onJsonArrayResponseReceived(JSONArray a, int callNumber) {
    }

    public void onErrorReceived(String error) {
    }
}
