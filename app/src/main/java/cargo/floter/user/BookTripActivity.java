package cargo.floter.user;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.support.annotation.IdRes;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog.Builder;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import cargo.floter.user.CustomActivity.ResponseCallback;
import cargo.floter.user.application.MyApp;
import cargo.floter.user.application.SingleInstance;
import cargo.floter.user.model.NearbyDrivers.Response;
import cargo.floter.user.model.RateCard.RateCardResponse;
import cargo.floter.user.model.RestUser;
import cargo.floter.user.model.Trip;
import cargo.floter.user.model.TripStatus;
import cargo.floter.user.model.User;
import cargo.floter.user.utils.AppConstants;
import cargo.floter.user.utils.ContactsEditText;
import cargo.floter.user.utils.ContactsEditText.Contact;
import cz.msebera.android.httpclient.Header;
import id.arieridwan.lib.PageLoader;

public class BookTripActivity extends CustomActivity implements ResponseCallback {
    private Button btn_confirmString;
    private ImageView check_cash;
    private ImageView check_paytm;
    private RateCardResponse currentTruck;
    private Iterator<Response> driversIterator = null;
    private ContactsEditText editText;
    private EditText edt_goods;
    private EditText edt_qty_goods;
    IntentFilter filter = new IntentFilter("cargo.floter.user.NEW_RIDE");
    private boolean isCouponValid = false;
    private boolean isDriverSearched = false;
    private boolean isGoodsSet = true;
    private boolean isQtySet = false;
    private PageLoader loader;
    private String message = "Please complete 3 rides for the day and you will be applicable to apply the offer.\nThank you!";
    private String payMode = "CASH";
    private String pickupTime = "";
    private RadioGroup radio_group;
    BroadcastReceiver receiver = new C05678();
    private RelativeLayout rl_cash;
    private RelativeLayout rl_paytm;
    private Toolbar toolbar_title;
    private String tripId = "";
    private TextView txt_amount;
    private TextView txt_apply_coupon;
    private TextView txt_destination;
    private TextView txt_estimate_time;
    private TextView txt_source;
    private TextView vehicle;

    class C05601 implements OnItemClickListener {
        C05601() {
        }

        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            BookTripActivity.this.editText.setText(BookTripActivity.this.getStringNumber(((Contact) parent.getItemAtPosition(position)).id + ""));
        }
    }

    class C05612 implements OnCheckedChangeListener {
        C05612() {
        }

        public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
            if (i == R.id.radio_loose) {
                BookTripActivity.this.isGoodsSet = true;
                BookTripActivity.this.isQtySet = false;
                BookTripActivity.this.edt_qty_goods.setText("");
                BookTripActivity.this.edt_qty_goods.setVisibility(View.GONE);
            } else if (i == R.id.radio_qty) {
                BookTripActivity.this.isQtySet = true;
                BookTripActivity.this.isGoodsSet = true;
                BookTripActivity.this.edt_qty_goods.setVisibility(View.VISIBLE);
            }
        }
    }

    class C05623 implements OnClickListener {
        C05623() {
        }

        public void onClick(DialogInterface d, int i) {
            BookTripActivity.this.isCouponValid = true;
            MyApp.showMassage(BookTripActivity.this.getContext(), "Coupon applied successfully.");
            d.dismiss();
        }
    }

    class C05634 implements OnClickListener {
        C05634() {
        }

        public void onClick(DialogInterface dialogInterface, int i) {
            dialogInterface.dismiss();
        }
    }

    class C05645 implements OnClickListener {
        C05645() {
        }

        public void onClick(DialogInterface d, int i) {
            d.dismiss();
        }
    }

    class C05678 extends BroadcastReceiver {
        C05678() {
        }

        public void onReceive(Context context, Intent intent) {
            if (intent.getStringExtra("TYPE").equals("TRIP_ACCEPTED")) {
                BookTripActivity.this.isDriverSearched = true;
                BookTripActivity.this.loader.stopProgress();
                BookTripActivity.this.loader.setVisibility(View.VISIBLE);
                HashMap<String, Trip> allTrips = MyApp.getApplication().readTrip();
                Trip tt = allTrips.get(BookTripActivity.this.tripId);
                tt.setTrip_status(TripStatus.Accepted.name());
                allTrips.put(BookTripActivity.this.tripId, tt);
                MyApp.getApplication().writeTrip(allTrips);
                MyApp.setSharedPrefString("HIDE_DRIVERS", tt.getDriver_id() + ",");
                MyApp.setStatus("IS_ON_TRIP", false);
                if (MyApp.getStatus(AppConstants.FIRST_OFFER)) {
                    MyApp.setStatus(AppConstants.FIRST_OFFER, false);
                }
                BookTripActivity.this.startActivity(new Intent(BookTripActivity.this.getContext(), OnTripActivity.class).putExtra(AppConstants.EXTRA_1, BookTripActivity.this.tripId));
                BookTripActivity.this.finish();
            } else if (intent.getStringExtra("TYPE").equals("TRIP_DECLINED")) {
                driverRequestMethod();
                handler.removeCallbacks(driverRequestRunnable);
                handler.postDelayed(driverRequestRunnable, 20000);
            }
        }
    }

    private Handler handler = new Handler();

    class C09566 extends TypeToken<List<Trip>> {
        C09566() {
        }
    }

    private Runnable driverRequestRunnable = new Runnable() {
        @Override
        public void run() {
            {
                if (!BookTripActivity.this.isDriverSearched) {
                    BookTripActivity.this.driverRequestMethod();
                }
            }
        }
    };

    class C10927 extends JsonHttpResponseHandler {


        class C05662 implements Runnable {
            C05662() {
            }

            public void run() {
                if (!BookTripActivity.this.isDriverSearched) {
                    BookTripActivity.this.driverRequestMethod();
                }
            }
        }

        C10927() {
        }


        public void onSuccess(int statusCode, Header[] headers, String response) {
            Log.d("Response:", response.toString());
            if (BookTripActivity.this.driversIterator.hasNext() && !BookTripActivity.this.isDriverSearched) {
                handler.postDelayed(driverRequestRunnable, 20000);
            }
        }

        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
            if (statusCode != 0) {
                try {
                    if (!BookTripActivity.this.isDriverSearched) {
                        new Handler().postDelayed(new C05662(), 20000);
                    }
                } catch (Exception e) {
                }
            }
        }
    }

    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.book_trip);
        this.toolbar_title = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(this.toolbar_title);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        ((TextView) this.toolbar_title.findViewById(R.id.toolbar_title)).setText("Booking Details");
        setResponseListener(this);
        this.editText = (ContactsEditText) findViewById(android.R.id.input);
        this.editText.setText(MyApp.getApplication().readUser().getU_mobile());
        this.editText.setOnItemClickListener(new C05601());
        this.currentTruck = SingleInstance.getInstance().getSelectedRate();
        this.pickupTime = getIntent().getStringExtra("PickUpTime");
        setUpUiElements();
        loadTodaysHistory();
    }

    private void setUpUiElements() {
        this.radio_group = (RadioGroup) findViewById(R.id.radio_group);
        this.edt_qty_goods = (EditText) findViewById(R.id.edt_qty_goods);
        this.vehicle = (TextView) findViewById(R.id.vehicle);
        this.txt_apply_coupon = (TextView) findViewById(R.id.txt_apply_coupon);
        this.btn_confirmString = (Button) findViewById(R.id.btn_confirmString);
        this.txt_destination = (TextView) findViewById(R.id.txt_destination);
        this.txt_source = (TextView) findViewById(R.id.txt_source);
        this.txt_amount = (TextView) findViewById(R.id.txt_amount);
        this.edt_goods = (EditText) findViewById(R.id.edt_goods);
        this.check_cash = (ImageView) findViewById(R.id.check_cash);
        this.check_paytm = (ImageView) findViewById(R.id.check_paytm);
        this.rl_paytm = (RelativeLayout) findViewById(R.id.rl_paytm);
        this.rl_cash = (RelativeLayout) findViewById(R.id.rl_cash);
        this.txt_estimate_time = (TextView) findViewById(R.id.txt_estimate_time);
        this.vehicle.setText(this.currentTruck.getCar_name());
        this.txt_estimate_time.setText("ETA : " + getIntent().getStringExtra("ETA") + " Min");
        this.txt_amount.setText(getIntent().getStringExtra("PRICE"));
        this.txt_source.setText(getIntent().getStringExtra(AppConstants.EXTRA_1));
        this.txt_destination.setText(getIntent().getStringExtra(AppConstants.EXTRA_2));
        setTouchNClick(R.id.btn_confirmString);
        setTouchNClick(R.id.txt_apply_coupon);
        setTouchNClick(R.id.rl_paytm);
        setTouchNClick(R.id.rl_cash);
        this.loader = (PageLoader) findViewById(R.id.pageloader);
        this.loader.setLoadingImageHeight(200);
        this.loader.setLoadingImageWidth(200);
        this.loader.setTextSize(20);
        this.radio_group.setOnCheckedChangeListener(new C05612());
    }

    public void onClick(View v) {
        super.onClick(v);
        if (v == this.rl_paytm) {
            this.check_paytm.setImageResource(R.drawable.btn_checked);
            this.check_cash.setImageResource(R.drawable.btn_unchecked);
            this.payMode = "PAYTM";
        } else if (v == this.rl_cash) {
            this.check_cash.setImageResource(R.drawable.btn_checked);
            this.check_paytm.setImageResource(R.drawable.btn_unchecked);
            this.payMode = "CASH";
        } else if (v.getId() == R.id.btn_confirmString) {
            requestTrip();
        } else if (v == this.txt_apply_coupon) {
            Builder b = new Builder(getContext());
            b.setTitle("Apply Coupon!");
            if (this.message.equals("GO")) {
                b.setMessage("To get 5% discount on your next trip, use coupon code\n'FLOTER05'");
                b.setPositiveButton("APPLY", new C05623());
                b.setNegativeButton("CANCEL", new C05634());
            } else {
                b.setMessage(this.message);
                b.setPositiveButton("OK", new C05645());
            }
            b.create().show();
        }
    }

    private void loadTodaysHistory() {
        RequestParams p = new RequestParams();
        p.put("user_id", MyApp.getApplication().readUser().getUser_id());
        p.put("trip_date", MyApp.millsToDate2(System.currentTimeMillis()));
        postCall(getContext(), AppConstants.BASE_URL_TRIP + "gettrips", p, "", 11);
    }

    private void requestTrip() {
        if (TextUtils.isEmpty(this.edt_goods.getText().toString())) {
            this.edt_goods.setError("Enter goods type");
            MyApp.showMassage(getContext(), "Write down goods type that you want to carry");
        } else if (!this.isGoodsSet) {
            MyApp.showMassage(getContext(), "Please enter some quantity");
        } else if (this.isQtySet && TextUtils.isEmpty(this.edt_qty_goods.getText().toString())) {
            MyApp.showMassage(getContext(), "Please enter some quantity");
        } else {
            User u = MyApp.getApplication().readUser();
            RequestParams p = new RequestParams();
            p.put("trip_date", MyApp.millsToDate2(System.currentTimeMillis()));
            p.put("user_id", u.getUser_id());
            p.put("trip_from_loc", this.txt_source.getText().toString());
            p.put("trip_to_loc", this.txt_destination.getText().toString());
            p.put("trip_distance", getIntent().getStringExtra("DISTANCE"));
            p.put("trip_fare", getIntent().getStringExtra("EST_PRICE"));
            p.put("trip_wait_time", getIntent().getStringExtra("ETA"));
            p.put("trip_pickup_time", this.pickupTime);
            p.put("later_booking_time", this.pickupTime);
            p.put("trip_drop_time", getIntent().getStringExtra("DURATION"));
            p.put("trip_reason", "");
            p.put("trip_feedback", "");
            p.put("trip_rating", "");
            LatLng s = SingleInstance.getInstance().getSourceLatLng();
            LatLng d = SingleInstance.getInstance().getDestinationLatLng();
            p.put("trip_scheduled_pick_lat", Double.valueOf(s.latitude));
            p.put("trip_scheduled_pick_lng", Double.valueOf(s.longitude));
            p.put("trip_actual_pick_lat", Double.valueOf(s.latitude));
            p.put("trip_actual_pick_lng", Double.valueOf(s.longitude));
            p.put("trip_scheduled_drop_lat", Double.valueOf(d.latitude));
            p.put("trip_scheduled_drop_lng", Double.valueOf(d.longitude));
            p.put("trip_actual_drop_lat", Double.valueOf(d.latitude));
            p.put("trip_actual_drop_lng", Double.valueOf(d.longitude));
            p.put("trip_pay_mode", this.payMode);
            p.put("trip_pay_amount", getIntent().getStringExtra("EST_PRICE"));
            p.put("trip_pay_date", MyApp.getDateOrTimeFromMillis(Long.valueOf(System.currentTimeMillis())));
            p.put("trip_pay_status", "PENDING");
            p.put("trip_driver_commision", "");
            if (TextUtils.isEmpty(this.edt_qty_goods.getText().toString())) {
                p.put("goods_type", this.edt_goods.getText().toString() + " (Loose)");
            } else {
                p.put("goods_type", this.edt_goods.getText().toString() + " (" + this.edt_qty_goods.getText().toString() + "Kg)");
            }
            if (getIntent().getBooleanExtra("isBookLater", false)) {
                p.put("driver_id", "91");
                p.put("book_later", 1);
            } else {
                p.put("driver_id", ((Response) SingleInstance.getInstance().getNotifiableDrivers().get(0)).getDriver_id());
                p.put("book_later", 0);
            }
            p.put("promo_id", "");
            p.put("trip_promo_code", "");
            p.put("trip_promo_amt", "0");
            if (this.isCouponValid) {
                p.put("promo_id", "5");
                p.put("trip_promo_code", "FLOTER05");
                p.put("trip_promo_amt", 5);
            } else if (MyApp.getStatus(AppConstants.FIRST_OFFER)) {
                p.put("promo_id", "6");
                p.put("trip_promo_code", "FIRST50");
                p.put("trip_promo_amt", "50");
            }
            if (getIntent().getBooleanExtra("isBookLater", false)) {
                p.put("trip_status", TripStatus.Upcoming.name());
            } else {
                p.put("trip_status", TripStatus.Pending.name());
            }
            p.put("trip_validity", "Present");
            postCall(getContext(), "http://floter.in/floterapi/index.php/tripapi/save?", p, "Please wait...", 2);
        }
    }

    private Context getContext() {
        return this;
    }

    public void onJsonObjectResponseReceived(JSONObject o, int callNumber) {
        Log.d("response", o.toString());
        if (o.optString("status").equals("OK") && callNumber == 1) {
            RestUser u = new Gson().fromJson(o.toString(), RestUser.class);
            List<String> dataList = new ArrayList();
            for (RestUser.Response s : u.getResponse()) {
                dataList.add(s.getGoodtype_name());
            }
        } else if (callNumber == 2 && o.optString("status").equals("OK")) {
            try {
                Trip trip = new Gson().fromJson(o.getJSONObject("response").toString(), Trip.class);
                this.tripId = trip.getTrip_id();
                HashMap<String, Trip> map = MyApp.getApplication().readTrip();
                map.put(this.tripId, trip);
                MyApp.getApplication().writeTrip(map);
                if (getIntent().getBooleanExtra("isBookLater", false)) {
                    MyApp.popMessageAndFinish("Message", "Your request has been sent to Floter team. We will notify you after assigning a Driver for your trip.\nThank you.", this);
                } else if (this.driversIterator == null) {
                    this.driversIterator = SingleInstance.getInstance().getNotifiableDrivers().iterator();
                    driverRequestMethod();
                }
            } catch (Exception e) {
            }
        } else if (callNumber == 3) {
            Trip trip = null;
            try {
                trip = new Gson().fromJson(o.getJSONObject("response").toString(), Trip.class);
                this.tripId = trip.getTrip_id();
                HashMap<String, Trip> map = MyApp.getApplication().readTrip();
                map.put(this.tripId, trip);
                MyApp.getApplication().writeTrip(map);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        } else {
            if (callNumber == 11 && o.optString("status").equals("OK")) {
                try {
                    List<Trip> trips = (List) new Gson().fromJson(o.getJSONArray("response").toString(), new C09566().getType());
                    int count = 0;
                    for (int i = 0; i < trips.size(); i++) {
                        if ((trips.get(i)).getTrip_status().equals(TripStatus.Finished.name())) {
                            count++;
                        }
                        if (count >= 3) {
                            this.message = "GO";
                        } else {
                            this.message = "Please complete 3 rides for the day and you will be applicable to apply the offer.\nThank you!";
                        }
                    }
                    return;
                } catch (JSONException e2) {
                    this.message = "Please complete 3 rides for the day and you will be applicable to apply the offer.\nThank you!";
                    e2.printStackTrace();
                    return;
                } catch (Exception e3) {
                    this.message = "Please complete 3 rides for the day and you will be applicable to apply the offer.\nThank you!";
                    e3.printStackTrace();
                    return;
                }
            }
            MyApp.popMessage("Error!", o.optString("message"), getContext());
        }
    }

    private String getStringNumber(String id) {
        String number = "No Number";
        Cursor phones = getContentResolver().query(Phone.CONTENT_URI, null, "contact_id = " + id, null, null);
        while (phones.moveToNext()) {
            number = phones.getString(phones.getColumnIndex("data1"));
        }
        phones.close();
        return number;
    }

    private void driverRequestMethod() {
        try {
            if (this.driversIterator.hasNext()) {
                Response nd = this.driversIterator.next();
                RequestParams p1 = new RequestParams();
                p1.put("trip_id", this.tripId);
                p1.put("trip_status", TripStatus.Pending.name());
                p1.put("driver_id", nd.getDriver_id());
                postCall(getContext(), AppConstants.BASE_URL_TRIP + "updatetrip", p1, "", 3);
                this.btn_confirmString.setVisibility(View.GONE);
                this.loader.startProgress();
                this.loader.setVisibility(View.VISIBLE);
                RequestParams p = new RequestParams();
                p.put("message", "New Trip");
                p.put("trip_id", this.tripId);
                p.put("trip_status", TripStatus.Pending.name());
                p.put("android", nd.getD_device_token());
                JSONObject jo = new JSONObject();
                jo.put("u_name", MyApp.getApplication().readUser().getU_fname() + " " + MyApp.getApplication().readUser().getU_lname());
                jo.put("est_dist", getIntent().getStringExtra("DISTANCE"));
                jo.put("est_cost", getIntent().getStringExtra("EST_PRICE"));
                jo.put("est_time", getIntent().getStringExtra("DURATION"));
                LatLng d = SingleInstance.getInstance().getDestinationLatLng();
                jo.put("dest_lat", d.latitude);
                jo.put("dest_lng", d.longitude);
                if (TextUtils.isEmpty(this.edt_qty_goods.getText().toString())) {
                    jo.put("goods_type", this.edt_goods.getText().toString() + " (Loose)");
                } else {
                    jo.put("goods_type", this.edt_goods.getText().toString() + " (" + this.edt_qty_goods.getText().toString() + ")");
                }
                jo.put("est_amount", getIntent().getStringExtra("EST_PRICE"));
                jo.put("token", MyApp.getSharedPrefString(AppConstants.DEVICE_TOKEN));
                jo.put("contact", this.editText.getText().toString());
                jo.put("dest_address", this.txt_destination.getText().toString());
                jo.put("source_address", this.txt_source.getText().toString());
                jo.put("u_rating", "4.0");
                jo.put("timestamp", System.currentTimeMillis());
                p.put("object", jo.toString());
                AsyncHttpClient client = new AsyncHttpClient();
                client.setTimeout(30000);
                client.post("http://floter.in/floterapi/push/DriverPushNotification?", p, new C10927());
            } else {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (!isDriverSearched) {
                            loader.stopProgress();
                            MyApp.popMessageAndFinish("Alert!", "It seems that no driver is able to connect right now," +
                                    " please try again after sometime.\nThank you.", BookTripActivity.this);
                        }

                    }
                }, 15000);

            }
        } catch (Exception e) {
            loader.stopProgress();
            MyApp.popMessageAndFinish("Alert!", "It seems that no driver is able to connect right now," +
                    " please try again after sometime.\nThank you.", BookTripActivity.this);
        }
    }

    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (MyApp.getStatus("IS_ON_TRIP")) {
            unregisterReceiver(this.receiver);
            MyApp.setStatus("IS_ON_TRIP", false);
            HashMap<String, Trip> allTrips = MyApp.getApplication().readTrip();
            try {
                Trip tt = allTrips.get(this.tripId);
                tt.setTrip_status(TripStatus.Accepted.name());
                allTrips.put(this.tripId, tt);
                MyApp.getApplication().writeTrip(allTrips);
                if (MyApp.getStatus(AppConstants.FIRST_OFFER)) {
                    MyApp.setStatus(AppConstants.FIRST_OFFER, false);
                }
                isDriverSearched = true;
                startActivity(new Intent(getContext(), OnTripActivity.class).putExtra(AppConstants.EXTRA_1, this.tripId));
                finish();
            } catch (Exception e) {
            }
        }
    }

    protected void onResume() {
        registerReceiver(this.receiver, this.filter);
        super.onResume();
        if (ContextCompat.checkSelfPermission(getContext(), "android.permission.READ_CONTACTS") == -1) {
            ActivityCompat.requestPermissions(this, new String[]{"android.permission.READ_CONTACTS"}, 1010);
        }
    }

    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(this.receiver);
    }

    protected void onStop() {
        super.onStop();
    }

    public void onJsonArrayResponseReceived(JSONArray a, int callNumber) {
    }

    public void onErrorReceived(String error) {
        MyApp.showMassage(getContext(), "Some error occurred");
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 11 && resultCode != -1) {
        }
    }
}
