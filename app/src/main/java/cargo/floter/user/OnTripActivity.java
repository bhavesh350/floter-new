package cargo.floter.user;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.RadioButton;
import android.widget.RatingBar;
import android.widget.RatingBar.OnRatingBarChangeListener;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;
import com.facebook.appevents.AppEventsConstants;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.analytics.FirebaseAnalytics.Param;
import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.mancj.slideup.SlideUp;
import com.mancj.slideup.SlideUp.Builder;
import com.mancj.slideup.SlideUp.Listener;
import com.mancj.slideup.SlideUp.State;
import com.paytm.pgsdk.PaytmConstants;
import com.paytm.pgsdk.PaytmOrder;
import com.paytm.pgsdk.PaytmPGService;
import com.paytm.pgsdk.PaytmPaymentTransactionCallback;
import cargo.floter.user.CustomActivity.ResponseCallback;
import cargo.floter.user.application.MyApp;
import cargo.floter.user.fragments.FragmentDrawer;
import cargo.floter.user.fragments.FragmentDrawer.FragmentDrawerListener;
import cargo.floter.user.model.Payment;
import cargo.floter.user.model.Trip;
import cargo.floter.user.model.TripStatus;
import cargo.floter.user.utils.AppConstants;
import cargo.floter.user.utils.LocationProvider;
import cargo.floter.user.utils.LocationProvider.LocationCallback;
import cargo.floter.user.utils.LocationProvider.PermissionCallback;
import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.protocol.HTTP;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class OnTripActivity extends CustomActivity implements ResponseCallback, FragmentDrawerListener, OnMapReadyCallback, OnConnectionFailedListener, LocationCallback, PermissionCallback {
    private float bearing = 0.0f;
    private Button button_submit;
    private Location currentLocation = null;
    private Trip currentTrip;
    private Marker destMarker = null;
    private FragmentDrawer drawerFragment;
    private Marker driverMarker = null;
    private GoogleApiClient googleApiClient;
    private boolean isOnceMarkerSet = false;
    private boolean isTripFinished = false;
    private RelativeLayout ll_feedback;
    private LocationProvider locationProvider;
    private Handler mHandler = new Handler();
    private GoogleMap mMap;
    private Toolbar mToolbar;
    private Runnable mUpdateDriverLocationToShow = new C05884();
    private SupportMapFragment mapFragment;
    private double newlat = 0.0d;
    private double newlong = 0.0d;
    private double oldlat = 0.0d;
    private double oldlong = 0.0d;
    private String orderId = "ORDER_";
    private String orderIdAppender = "";
    private Payment payment = null;
    private String paymentId = AppEventsConstants.EVENT_PARAM_VALUE_NO;
    private RatingBar rating_bar;
    private TextView share_trip;
    private SlideUp slideUp;
    private Marker sourceMarker = null;
    private String tripId = "";
    private TextView txt_call_driver;
    private TextView txt_cancel_trip;
    private TextView txt_driver_name;
    private TextView txt_rating_status;
    private TextView txt_user_name;

    class C05861 implements Runnable {
        C05861() {
        }

        public void run() {
            if (OnTripActivity.this.currentLocation == null) {
                OnTripActivity.this.locationProvider = new LocationProvider(OnTripActivity.this, OnTripActivity.this, OnTripActivity.this);
                OnTripActivity.this.locationProvider.connect();
            }
        }
    }

    class C05873 implements OnRatingBarChangeListener {
        C05873() {
        }

        public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
            if (v <= 1.0f) {
                OnTripActivity.this.txt_rating_status.setText("Bad");
            } else if (v > 1.0f && ((double) v) < 2.5d) {
                OnTripActivity.this.txt_rating_status.setText("Below Average");
            } else if (((double) v) >= 2.5d && ((double) v) < 3.5d) {
                OnTripActivity.this.txt_rating_status.setText("Average");
            } else if (((double) v) < 3.5d || ((double) v) >= 4.5d) {
                OnTripActivity.this.txt_rating_status.setText("Excellent");
            } else {
                OnTripActivity.this.txt_rating_status.setText("Good");
            }
        }
    }

    class C05884 implements Runnable {
        C05884() {
        }

        public void run() {
            RequestParams p = new RequestParams();
            p.put("trip_id", OnTripActivity.this.currentTrip.getTrip_id());
            if (MyApp.isConnectingToInternet(OnTripActivity.this)) {
                OnTripActivity.this.postCall(OnTripActivity.this.getContext(), AppConstants.BASE_URL_TRIP + "gettrips", p, "", 15);
            }
            OnTripActivity.this.mHandler.postDelayed(this, 5000);
        }
    }

    public class MapHttpConnection {
        public String readUr(String mapsApiDirectionsUrl) throws IOException {
            String data = "";
            InputStream istream = null;
            HttpURLConnection urlConnection = null;
            try {
                urlConnection = (HttpURLConnection) new URL(mapsApiDirectionsUrl).openConnection();
                urlConnection.connect();
                istream = urlConnection.getInputStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(istream));
                StringBuffer sb = new StringBuffer();
                String str = "";
                while (true) {
                    str = br.readLine();
                    if (str == null) {
                        break;
                    }
                    sb.append(str);
                }
                data = sb.toString();
                br.close();
            } catch (Exception e) {
                Log.d("Exception url", e.toString());
            } finally {
                istream.close();
                urlConnection.disconnect();
            }
            return data;
        }
    }

    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {
        private ParserTask() {
        }

        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {
            List<List<HashMap<String, String>>> routes = null;
            try {
                routes = new PathJSONParser().parse(new JSONObject(jsonData[0]));
            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        protected void onPostExecute(List<List<HashMap<String, String>>> routes) {
            PolylineOptions polyLineOptions = null;
            String distance = "";
            String duration = "";
            for (int i = 0; i < routes.size(); i++) {
                ArrayList<LatLng> points = new ArrayList();
                polyLineOptions = new PolylineOptions();
                List<HashMap<String, String>> path = (List) routes.get(i);
                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = (HashMap) path.get(j);
                    if (j == 0) {
                        distance = point.get("distance");
                    } else if (j == 1) {
                        duration = point.get("duration");
                    } else {
                        points.add(new LatLng(Double.parseDouble(point.get("lat")), Double.parseDouble((String) point.get("lng"))));
                    }
                }
                polyLineOptions.addAll(points);
                polyLineOptions.width(10.0f);
                polyLineOptions.color(Color.parseColor("#156CB3"));
            }
            OnTripActivity.this.mMap.addPolyline(polyLineOptions);
        }
    }

    public class PathJSONParser {
        public List<List<HashMap<String, String>>> parse(JSONObject jObject) {
            List<List<HashMap<String, String>>> routes = new ArrayList();
            try {
                JSONArray jRoutes = jObject.getJSONArray("routes");
                for (int i = 0; i < jRoutes.length(); i++) {
                    JSONArray jLegs = ((JSONObject) jRoutes.get(i)).getJSONArray("legs");
                    List<HashMap<String, String>> path = new ArrayList();
                    for (int j = 0; j < jLegs.length(); j++) {
                        JSONObject jDistance = ((JSONObject) jLegs.get(j)).getJSONObject("distance");
                        HashMap<String, String> hmDistance = new HashMap();
                        hmDistance.put("distance", jDistance.getString("text"));
                        JSONObject jDuration = ((JSONObject) jLegs.get(j)).getJSONObject("duration");
                        HashMap<String, String> hmDuration = new HashMap();
                        hmDuration.put("duration", jDuration.getString("text"));
                        path.add(hmDistance);
                        path.add(hmDuration);
                        JSONArray jSteps = ((JSONObject) jLegs.get(j)).getJSONArray("steps");
                        for (int k = 0; k < jSteps.length(); k++) {
                            String polyline = "";
                            List<LatLng> list = decodePoly((String) ((JSONObject) ((JSONObject) jSteps.get(k)).get("polyline")).get("points"));
                            for (int l = 0; l < list.size(); l++) {
                                HashMap<String, String> hm = new HashMap();
                                hm.put("lat", Double.toString((list.get(l)).latitude));
                                hm.put("lng", Double.toString((list.get(l)).longitude));
                                path.add(hm);
                            }
                        }
                        routes.add(path);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        private List<LatLng> decodePoly(String encoded) {
            List<LatLng> poly = new ArrayList();
            int index = 0;
            int len = encoded.length();
            int lat = 0;
            int lng = 0;
            while (index < len) {
                int index2;
                int shift = 0;
                int result = 0;
                int b = 0;
                while (true) {
                    index2 = index + 1;
                    b = encoded.charAt(index) - 63;
                    result |= (b & 31) << shift;
                    shift += 5;
                    if (b < 32) {
                        break;
                    }
                    index = index2;
                }
                lat += (result & 1) != 0 ? (result >> 1) ^ -1 : result >> 1;
                shift = 0;
                result = 0;
                index = index2;
                while (true) {
                    index2 = index + 1;
                    b = encoded.charAt(index) - 63;
                    result |= (b & 31) << shift;
                    shift += 5;
                    if (b < 32) {
                        break;
                    }
                    index = index2;
                }
                lng += (result & 1) != 0 ? (result >> 1) ^ -1 : result >> 1;
                poly.add(new LatLng(((double) lat) / 100000.0d, ((double) lng) / 100000.0d));
                index = index2;
            }
            return poly;
        }
    }

    private class ReadTask extends AsyncTask<String, Void, String> {
        private ReadTask() {
        }

        protected String doInBackground(String... url) {
            String data = "";
            try {
                data = new MapHttpConnection().readUr(url[0]);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            new ParserTask().execute(new String[]{result});
        }
    }

    class C09612 implements Listener {
        C09612() {
        }

        public void onSlide(float percent) {
        }

        public void onVisibilityChanged(int visibility) {
            if (visibility != 8) {
            }
        }
    }

    class C09625 implements OnConnectionFailedListener {
        C09625() {
        }

        public void onConnectionFailed(ConnectionResult connectionResult) {
            MyApp.showMassage(OnTripActivity.this.getContext(), "Location error " + connectionResult.getErrorCode());
        }
    }

    class C09636 implements ResultCallback<LocationSettingsResult> {
        C09636() {
        }

        public void onResult(LocationSettingsResult result) {
            Status status = result.getStatus();
            switch (status.getStatusCode()) {
                case 6:
                    try {
                        status.startResolutionForResult(OnTripActivity.this, 44);
                        return;
                    } catch (SendIntentException e) {
                        e.printStackTrace();
                        return;
                    }
                default:
                    return;
            }
        }
    }

    class C09647 implements ResultCallback<LocationSettingsResult> {
        C09647() {
        }

        public void onResult(LocationSettingsResult result) {
            Status status = result.getStatus();
            switch (status.getStatusCode()) {
                case 6:
                    try {
                        status.startResolutionForResult(OnTripActivity.this, 44);
                        return;
                    } catch (SendIntentException e) {
                        e.printStackTrace();
                        return;
                    }
                default:
                    return;
            }
        }
    }

    class C11058 extends JsonHttpResponseHandler {
        C11058() {
        }

        public void onSuccess(int statusCode, Header[] headers, JSONObject o) {
            MyApp.spinnerStop();
            if (o.optString("status").equals("OK")) {
                try {
                    OnTripActivity.this.payment =  new Gson().fromJson(o.getJSONArray("response").get(0).toString(), Payment.class);
                    OnTripActivity.this.showPaymentDialog(false);
                    return;
                } catch (JSONException e) {
                    e.printStackTrace();
                    return;
                }
            }
            OnTripActivity.this.ll_feedback.setVisibility(View.VISIBLE);
            OnTripActivity.this.slideUp.show();
            txt_user_name.setText(currentTrip.getDriver().getD_name());
            MyApp.popMessage("Message", "Please pay cash. We cannot process Paytm process system now.", OnTripActivity.this.getContext());
        }

        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
            super.onFailure(statusCode, headers, throwable, errorResponse);
            MyApp.spinnerStop();
            if (statusCode == 0) {
                MyApp.popMessage("Error!", "Time-out error occurred.\nPlease try again", OnTripActivity.this.getContext());
            } else {
                MyApp.popMessage("Error!", "Error_" + statusCode + "Some error occurred", OnTripActivity.this.getContext());
            }
        }

        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
            MyApp.spinnerStop();
            if (statusCode == 0) {
                MyApp.popMessage("Error!", "Time-out error occurred.\nPlease try again", OnTripActivity.this.getContext());
            } else {
                MyApp.popMessage("Error!", "Error_" + statusCode + "Some error occurred", OnTripActivity.this.getContext());
            }
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_ongoint_trip);
        this.mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(this.mToolbar);
        this.locationProvider = new LocationProvider(this, this, this);
        this.mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        setResponseListener(this);
        this.mapFragment.getMapAsync(this);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        actionBar.setTitle("");
        this.drawerFragment = (FragmentDrawer) getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        this.drawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), this.mToolbar);
        this.drawerFragment.setDrawerListener(this);
        this.tripId = getIntent().getStringExtra(AppConstants.EXTRA_1);
        this.currentTrip =  MyApp.getApplication().readTrip().get(this.tripId);
        setupUiElements();
        new Handler().postDelayed(new C05861(), 10000);
    }

    private void setupUiElements() {
        this.txt_rating_status = (TextView) findViewById(R.id.txt_rating_status);
        this.txt_call_driver = (TextView) findViewById(R.id.txt_call_driver);
        this.rating_bar = (RatingBar) findViewById(R.id.rating_bar);
        this.share_trip = (TextView) findViewById(R.id.share_trip);
        this.txt_cancel_trip = (TextView) findViewById(R.id.txt_cancel_trip);
        this.txt_driver_name = (TextView) findViewById(R.id.txt_driver_name);
        this.txt_user_name = (TextView) findViewById(R.id.txt_user_name);
        this.ll_feedback = (RelativeLayout) findViewById(R.id.ll_feedback);
        this.button_submit = (Button) findViewById(R.id.button_submit);
        setClick(R.id.txt_call_driver);
        setClick(R.id.share_trip);
        setTouchNClick(R.id.button_submit);
        setClick(R.id.txt_cancel_trip);
        try {
            this.txt_user_name.setText(this.currentTrip.getDriver().getD_name());
            this.txt_driver_name.setText(this.currentTrip.getDriver().getD_name() + "\n" + this.currentTrip.getDriver().getTruck_reg_no());
            this.slideUp = new Builder(this.ll_feedback).withStartState(State.HIDDEN).withStartGravity(80).build();
            this.slideUp = new Builder(this.ll_feedback).withListeners(new C09612()).withStartGravity(80).withGesturesEnabled(false).withStartState(State.HIDDEN).build();
            this.rating_bar.setOnRatingBarChangeListener(new C05873());
        }catch (Exception e){
            MyApp.showMassage(getContext(),"Some error Occurred, please try again later.");
        }

    }

    public void onClick(View v) {
        super.onClick(v);
        if (v == this.txt_call_driver) {
            Intent intent = new Intent("android.intent.action.DIAL");
            intent.setData(Uri.parse("tel:" + this.currentTrip.getDriver().getD_phone()));
            startActivity(intent);
        } else if (v == this.share_trip) {
            Intent sendIntent = new Intent();
            sendIntent.setAction("android.intent.action.SEND");
            sendIntent.putExtra("android.intent.extra.TEXT", "I am using " + getString(R.string.app_name) + " for my goods transport. You can track goods with the following link\nhttp://floter.in/tracking/triptrack.php?trip_id=" + this.tripId);
            sendIntent.putExtra("android.intent.extra.SUBJECT", getString(R.string.app_name) + " App !");
            sendIntent.setType(HTTP.PLAIN_TEXT_TYPE);
            startActivity(Intent.createChooser(sendIntent, getString(R.string.messenger_send_button_text)));
        } else if (v == this.txt_cancel_trip) {
            startActivity(new Intent(getContext(), CancelTripActivity.class).putExtra(AppConstants.EXTRA_1, this.currentTrip.getTrip_id()).putExtra(AppConstants.EXTRA_2, this.currentTrip.getDriver().getD_device_token()));
        } else if (v == this.button_submit) {
            RequestParams pp = new RequestParams();
            pp.put("driver_id", this.currentTrip.getDriver().getDriver_id());
            pp.put("api_key", "ee059a1e2596c265fd61c44f1855875e");
            pp.put("d_rating", this.rating_bar.getRating() + "");
            postCall(getContext(), AppConstants.BASE_URL + "updatedriverprofile?", pp, "", 10);
            HashMap<String, Trip> tripHashMap = MyApp.getApplication().readTrip();
            if (tripHashMap.containsKey(this.tripId)) {
                tripHashMap.remove(this.tripId);
                MyApp.getApplication().writeTrip(tripHashMap);
            }
            if (MyApp.getSharedPrefString("SHOW_PAY").equals("YES")) {
                MyApp.setSharedPrefString("SHOW_PAY", "NO");
            }
            startActivity(new Intent(getContext(), MainActivity.class));
            finishAffinity();
        }
    }

    public void onDrawerItemSelected(View view, int position) {
        if (position == 6) {
            Intent sendIntent = new Intent();
            sendIntent.setAction("android.intent.action.SEND");
            sendIntent.putExtra("android.intent.extra.TEXT", "I am using " + getString(R.string.app_name) + " App ! Why don't you try it out...\nInstall " + getString(R.string.app_name) + " now !\nhttps://play.google.com/store/apps/details?id=" + getPackageName());
            sendIntent.putExtra("android.intent.extra.SUBJECT", getString(R.string.app_name) + " App !");
            sendIntent.setType(HTTP.PLAIN_TEXT_TYPE);
            startActivity(Intent.createChooser(sendIntent, getString(R.string.messenger_send_button_text)));
        } else if (position == 8) {
            startActivity(new Intent(this, AboutUsActivity.class));
        } else if (position == 3) {
            startActivity(new Intent(this, VehicleDetails.class));
        }
    }

    protected void onStart() {
        super.onStart();
        this.locationProvider.connect();
    }

    protected void onResume() {
        super.onResume();
        if (!MyApp.isLocationEnabled(getContext())) {
            enableGPS();
        }
        try {
            MyApp.cancelNotification(getContext(), Integer.parseInt(this.tripId));
        } catch (Exception e) {
            MyApp.cancelNotification(getContext(), 0);
        }
    }

    protected void onPause() {
        super.onPause();
    }

    protected void onStop() {
        super.onStop();
        this.locationProvider.disconnect();
    }

    public void onMapReady(GoogleMap googleMap) {
        this.mMap = googleMap;
        View mapView = this.mapFragment.getView();
        View locationButton = ((View) mapView.findViewById(Integer.parseInt(AppEventsConstants.EVENT_PARAM_VALUE_YES)).getParent()).findViewById(Integer.parseInt("2"));
        if (!(mapView == null || mapView.findViewById(Integer.parseInt(AppEventsConstants.EVENT_PARAM_VALUE_YES)) == null)) {
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams)
                    locationButton.getLayoutParams();
            // position on right bottom
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
            layoutParams.setMargins(0, 0, 30, 250);
        }
        double sourceLat = Double.parseDouble(this.currentTrip.getTrip_scheduled_pick_lat());
        double sourceLng = Double.parseDouble(this.currentTrip.getTrip_scheduled_pick_lng());
        double destLat = Double.parseDouble(this.currentTrip.getTrip_scheduled_drop_lat());
        double destLng = Double.parseDouble(this.currentTrip.getTrip_scheduled_drop_lng());
        if (this.sourceMarker != null) {
            this.sourceMarker.remove();
        }
        if (this.destMarker != null) {
            this.destMarker.remove();
        }
        this.sourceMarker = this.mMap.addMarker(new MarkerOptions().position(new LatLng(sourceLat, sourceLng)).icon(BitmapDescriptorFactory.fromResource(R.drawable.pin_blue)));
        this.destMarker = this.mMap.addMarker(new MarkerOptions().position(new LatLng(destLat, destLng)).icon(BitmapDescriptorFactory.fromResource(R.drawable.pin_red)));
        this.destMarker.setSnippet(this.currentTrip.getTrip_from_loc());
        this.sourceMarker.setSnippet(this.currentTrip.getTrip_to_loc());
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1010 && ContextCompat.checkSelfPermission(getContext(), "android.permission.ACCESS_FINE_LOCATION") == -1) {
            MyApp.showMassage(getContext(), "Location Denied, you cannot use the app");
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    }

    private void changeMap(Location location) {
        if (this.mMap != null) {
            this.mMap.getUiSettings().setZoomControlsEnabled(false);
            CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(location.getLatitude(), location.getLongitude())).zoom(15.5f).tilt(0.0f).build();
            if (ContextCompat.checkSelfPermission(this, "android.permission.ACCESS_FINE_LOCATION") == 0 || ContextCompat.checkSelfPermission(this, "android.permission.ACCESS_COARSE_LOCATION") == 0) {
                this.mMap.setMyLocationEnabled(true);
            } else {
                ActivityCompat.requestPermissions(this, new String[]{"android.permission.ACCESS_FINE_LOCATION", "android.permission.ACCESS_COARSE_LOCATION"}, 1010);
            }
            this.mMap.getUiSettings().setMyLocationButtonEnabled(true);
            this.mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            return;
        }
        Toast.makeText(getApplicationContext(), "Sorry! unable to create maps", Toast.LENGTH_SHORT).show();
    }

    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        MyApp.showMassage(getContext(), "GoogleApi Connection failed...");
    }

    public void handleNewLocation(Location location) {
        this.currentLocation = location;
        if (location != null) {
            try {
                changeMap(location);
                if (!this.isOnceMarkerSet) {
                    this.isOnceMarkerSet = true;
                    LatLngBounds.Builder builder = new LatLngBounds.Builder();
                    builder.include(this.sourceMarker.getPosition());
                    builder.include(this.destMarker.getPosition());
                    this.mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(adjustBoundsForMaxZoomLevel(builder.build()), 100));
                    String url = getMapsApiDirectionsUrl(this.sourceMarker.getPosition(), this.destMarker.getPosition());
                    new ReadTask().execute(new String[]{url});
                    this.mHandler.postDelayed(this.mUpdateDriverLocationToShow, 3000);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void handleManualPermission() {
        ActivityCompat.requestPermissions(this, new String[]{"android.permission.ACCESS_FINE_LOCATION", "android.permission.ACCESS_COARSE_LOCATION"}, 1010);
    }

    private Context getContext() {
        return this;
    }

    public void enableGPS() {
        if (this.googleApiClient == null) {
            this.googleApiClient = new GoogleApiClient.Builder(this).addApi(LocationServices.API).addOnConnectionFailedListener(new C09625()).build();
            this.googleApiClient.connect();
            LocationRequest locationRequest = LocationRequest.create();
            locationRequest.setInterval(30000);
            locationRequest.setFastestInterval(5000);
            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
            builder.setAlwaysShow(true);
            LocationServices.SettingsApi.checkLocationSettings(this.googleApiClient, builder.build()).setResultCallback(new C09636());
            return;
        }
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(30000);
        locationRequest.setFastestInterval(5000);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);
        LocationServices.SettingsApi.checkLocationSettings(this.googleApiClient, builder.build()).setResultCallback(new C09647());
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onJsonObjectResponseReceived(JSONObject o, int callNumber) {

        if (o.optString("status").equals("OK") && callNumber == 15) {
            try {
                Trip t = (Trip) new Gson().fromJson(o.getJSONArray("response").getJSONObject(0).toString(), Trip.class);
                this.newlat = Double.parseDouble(t.getDriver().getD_lat());
                this.newlong = Double.parseDouble(t.getDriver().getD_lng());
                if (t != null) {
                    HashMap<String, Trip> map = MyApp.getApplication().readTrip();
                    map.put(this.tripId, t);
                    MyApp.getApplication().writeTrip(map);
                }
                if (!(!t.getTrip_status().equals(TripStatus.Finished.name()) || t.getFloter_id().equals("0") || this.isTripFinished)) {
                    this.isTripFinished = true;
                    this.paymentId = t.getFloter_id();
                    showFeedbackForm();
                }
                rotatemarker();
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (Exception e2) {
            }
        }
        /*
        r6 = this;
        r3 = "status";
        r3 = r7.optString(r3);
        r4 = "OK";
        r3 = r3.equals(r4);
        if (r3 == 0) goto L_0x008f;
    L_0x000e:
        r3 = 15;
        if (r8 != r3) goto L_0x008f;
    L_0x0012:
        r3 = new com.google.gson.Gson;	 Catch:{ JSONException -> 0x0090, Exception -> 0x0095 }
        r3.<init>();	 Catch:{ JSONException -> 0x0090, Exception -> 0x0095 }
        r4 = "response";
        r4 = r7.getJSONArray(r4);	 Catch:{ JSONException -> 0x0090, Exception -> 0x0095 }
        r5 = 0;
        r4 = r4.getJSONObject(r5);	 Catch:{ JSONException -> 0x0090, Exception -> 0x0095 }
        r4 = r4.toString();	 Catch:{ JSONException -> 0x0090, Exception -> 0x0095 }
        r5 = cargo.floter.user.model.Trip.class;
        r2 = r3.fromJson(r4, r5);	 Catch:{ JSONException -> 0x0090, Exception -> 0x0095 }
        r2 = (cargo.floter.user.model.Trip) r2;	 Catch:{ JSONException -> 0x0090, Exception -> 0x0095 }
        r3 = r2.getDriver();	 Catch:{ JSONException -> 0x0090, Exception -> 0x0095 }
        r3 = r3.getD_lat();	 Catch:{ JSONException -> 0x0090, Exception -> 0x0095 }
        r4 = java.lang.Double.parseDouble(r3);	 Catch:{ JSONException -> 0x0090, Exception -> 0x0095 }
        r6.newlat = r4;	 Catch:{ JSONException -> 0x0090, Exception -> 0x0095 }
        r3 = r2.getDriver();	 Catch:{ JSONException -> 0x0090, Exception -> 0x0095 }
        r3 = r3.getD_lng();	 Catch:{ JSONException -> 0x0090, Exception -> 0x0095 }
        r4 = java.lang.Double.parseDouble(r3);	 Catch:{ JSONException -> 0x0090, Exception -> 0x0095 }
        r6.newlong = r4;	 Catch:{ JSONException -> 0x0090, Exception -> 0x0095 }
        if (r2 == 0) goto L_0x0060;
    L_0x004c:
        r3 = cargo.floter.user.application.MyApp.getApplication();	 Catch:{ Exception -> 0x0097, JSONException -> 0x0090 }
        r1 = r3.readTrip();	 Catch:{ Exception -> 0x0097, JSONException -> 0x0090 }
        r3 = r6.tripId;	 Catch:{ Exception -> 0x0097, JSONException -> 0x0090 }
        r1.put(r3, r2);	 Catch:{ Exception -> 0x0097, JSONException -> 0x0090 }
        r3 = cargo.floter.user.application.MyApp.getApplication();	 Catch:{ Exception -> 0x0097, JSONException -> 0x0090 }
        r3.writeTrip(r1);	 Catch:{ Exception -> 0x0097, JSONException -> 0x0090 }
    L_0x0060:
        r3 = r2.getTrip_status();	 Catch:{ JSONException -> 0x0090, Exception -> 0x0095 }
        r4 = cargo.floter.user.model.TripStatus.Finished;	 Catch:{ JSONException -> 0x0090, Exception -> 0x0095 }
        r4 = r4.name();	 Catch:{ JSONException -> 0x0090, Exception -> 0x0095 }
        r3 = r3.equals(r4);	 Catch:{ JSONException -> 0x0090, Exception -> 0x0095 }
        if (r3 == 0) goto L_0x008c;
    L_0x0070:
        r3 = r2.getFloter_id();	 Catch:{ JSONException -> 0x0090, Exception -> 0x0095 }
        r4 = "0";
        r3 = r3.equals(r4);	 Catch:{ JSONException -> 0x0090, Exception -> 0x0095 }
        if (r3 != 0) goto L_0x008c;
    L_0x007c:
        r3 = r6.isTripFinished;	 Catch:{ JSONException -> 0x0090, Exception -> 0x0095 }
        if (r3 != 0) goto L_0x008c;
    L_0x0080:
        r3 = 1;
        r6.isTripFinished = r3;	 Catch:{ JSONException -> 0x0090, Exception -> 0x0095 }
        r3 = r2.getFloter_id();	 Catch:{ JSONException -> 0x0090, Exception -> 0x0095 }
        r6.paymentId = r3;	 Catch:{ JSONException -> 0x0090, Exception -> 0x0095 }
        r6.showFeedbackForm();	 Catch:{ JSONException -> 0x0090, Exception -> 0x0095 }
    L_0x008c:
        r6.rotatemarker();	 Catch:{ JSONException -> 0x0090, Exception -> 0x0095 }
    L_0x008f:
        return;
    L_0x0090:
        r0 = move-exception;
        r0.printStackTrace();
        goto L_0x008f;
    L_0x0095:
        r3 = move-exception;
        goto L_0x008f;
    L_0x0097:
        r3 = move-exception;
        goto L_0x0060;
        */
        throw new UnsupportedOperationException("Method not decompiled: cargo.floter.user.OnTripActivity.onJsonObjectResponseReceived(org.json.JSONObject, int):void");
    }

    private void showFeedbackForm() {
        MyApp.spinnerStart(getContext(), "Getting Payment info...");
        String url = AppConstants.BASE_PAYMENT;
        RequestParams p = new RequestParams();
        p.put("payment_id", this.paymentId);
        Log.d("URl:", url);
        Log.d("Request:", p.toString());
        AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(30000);
        client.post(url, p, new C11058());
    }

    private void showPaymentDialog(boolean isPaytm) {
        final Dialog dialog = new Dialog(getContext());
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.dialog_paymode);
        dialog.setCancelable(false);
        TextView txt_pay = (TextView) dialog.findViewById(R.id.txt_pay);
        final RadioButton radio_cash = (RadioButton) dialog.findViewById(R.id.radio_cash);
        final RadioButton radio_paytm = (RadioButton) dialog.findViewById(R.id.radio_paytm);
        ((TextView) dialog.findViewById(R.id.txt_amount)).setText("Rs. " + this.payment.getPay_amount());
        if (isPaytm) {
            radio_paytm.setChecked(true);
            radio_cash.setChecked(false);
        }
        radio_cash.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    radio_cash.setChecked(true);
                    radio_paytm.setChecked(false);
                    return;
                }
                radio_paytm.setChecked(true);
                radio_cash.setChecked(false);
            }
        });
        radio_paytm.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    radio_cash.setChecked(false);
                    radio_paytm.setChecked(true);
                    return;
                }
                radio_paytm.setChecked(false);
                radio_cash.setChecked(true);
            }
        });
        txt_pay.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                if (radio_cash.isChecked()) {
                    MyApp.showMassage(OnTripActivity.this.getContext(), "Cash");
                    OnTripActivity.this.updatePaymentInfo(false);
                } else {
                    MyApp.showMassage(OnTripActivity.this.getContext(), "Paytm");
                    OnTripActivity.this.updatePaymentInfo(true);
                }
                dialog.dismiss();
            }
        });
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = -1;
        lp.height = -2;
        dialog.getWindow().setAttributes(lp);
        dialog.show();
    }

    private void updatePaymentInfo(boolean b) {
        if (!b) {
            sendPaymentDoneNotificationToDriver("CASH");
            String url = AppConstants.BASE_PAYMENT.replace("getpayments?", "updatepayment?");
            RequestParams p = new RequestParams();
            p.put("payment_id", this.payment.getPayment_id());
            p.put("trip_id", this.payment.getTrip_id());
            p.put("pay_status", "PAID");
            p.put("pay_mode", "CASH");
            Log.d("URl:", url);
            Log.d("Request:", p.toString());
            AsyncHttpClient client = new AsyncHttpClient();
            client.setTimeout(30000);
            client.post(url, p, new JsonHttpResponseHandler() {
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    MyApp.spinnerStop();
                    Log.d("Response:", response.toString());
                    if (response.optString("status").equals("OK")) {
                        MyApp.setSharedPrefString("SHOW_PAY", "NO");
                        MyApp.setSharedPrefString(AppConstants.PAYBLE_TRIP_ID, "");
                        OnTripActivity.this.ll_feedback.setVisibility(View.VISIBLE);
                        OnTripActivity.this.slideUp.show();
                        txt_user_name.setText(currentTrip.getDriver().getD_name());
                    }
                }

                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    super.onFailure(statusCode, headers, throwable, errorResponse);
                    MyApp.spinnerStop();
                }

                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    MyApp.spinnerStop();
                }
            });
        } else if (this.paymentId.equals(AppEventsConstants.EVENT_PARAM_VALUE_NO)) {
            this.ll_feedback.setVisibility(View.VISIBLE);
            this.slideUp.show();
            txt_user_name.setText(currentTrip.getDriver().getD_name());
            MyApp.popMessage("Message", "Cannot process the payment through Paytm. Please try to pay with cash this time.\nThank you!", getContext());
        } else {
            this.orderId += this.currentTrip.getTrip_id() + this.orderIdAppender;
            this.orderIdAppender += AppEventsConstants.EVENT_PARAM_VALUE_YES;
            createPay(this.orderId, this.payment.getPay_amount());
        }
    }

    private void sendPaymentDoneNotificationToDriver(String payMode) {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams p = new RequestParams();
        p.put("message", "User made payment of Rs. " + this.payment.getPay_amount() + "\nthrough " + payMode);
        p.put("trip_id", this.tripId);
        p.put("trip_status", TripStatus.Finished.name());
        p.put("object", "\"{\"json\":\"json\"}\"");
        p.put("android", this.currentTrip.getDriver().getD_device_token());
        client.setTimeout(30000);
        client.post("http://floter.in/floterapi/push/DriverPushNotification?", p, new JsonHttpResponseHandler() {
            public void onSuccess(int statusCode, Header[] headers, String response) {
                Log.d("Response:", response.toString());
            }

            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                if (statusCode != 0) {
                }
            }
        });
    }

    private void createPay(String orderId, String payment) {
        MyApp.spinnerStart(getContext(), "Please wait...");
        String url = "http://floter.in/floterapi/paytm/generateChecksum.php";
        RequestParams p = new RequestParams();
        p.put(PaytmConstants.MERCHANT_ID, "FLOTER55912639344993");
        p.put("ORDER_ID", orderId);
        p.put("CUST_ID", "User_" + MyApp.getApplication().readUser().getUser_id());
        p.put("INDUSTRY_TYPE_ID", "Retail109");
        p.put("CHANNEL_ID", "WAP");
        p.put("TXN_AMOUNT", payment);
        p.put("WEBSITE", "FLOTERWAP");
        p.put("CALLBACK_URL", "https://pguat.paytm.com/paytmchecksum/paytmCallback.jsp");
        p.put("EMAIL", MyApp.getApplication().readUser().getU_email());
        p.put("MOBILE_NO", MyApp.getApplication().readUser().getU_mobile());
        Log.d("URl:", url);
        Log.d("Request:", p.toString());
        AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(30000);
        client.get(url, p, new JsonHttpResponseHandler() {
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                MyApp.spinnerStop();
                Log.d("Response:", response.toString());
                try {
                    if (response.optString("status").equals("ok")) {
                        OnTripActivity.this.createPaymentFlow(response.getJSONObject("response").optString("CHECKSUMHASH"));
                    } else {
                        MyApp.popMessage("Error", response.optString("error"), OnTripActivity.this.getContext());
                    }
                } catch (JSONException e) {
                    MyApp.showMassage(OnTripActivity.this.getContext(), "Parsing error");
                    e.printStackTrace();
                }
            }

            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                MyApp.spinnerStop();
                if (statusCode == 0) {
                    MyApp.popMessage("Error!", "Time-out error occurred.\nPlease try again", OnTripActivity.this.getContext());
                } else {
                    MyApp.popMessage("Error!", "Error_" + statusCode + "Some error occurred", OnTripActivity.this.getContext());
                }
            }

            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                MyApp.spinnerStop();
                if (statusCode == 0) {
                    MyApp.popMessage("Error!", "Time-out error occurred.\nPlease try again", OnTripActivity.this.getContext());
                } else {
                    MyApp.popMessage("Error!", "Error_" + statusCode + "Some error occurred", OnTripActivity.this.getContext());
                }
            }
        });
    }

    private void createPaymentFlow(String checksum) {
        PaytmPGService Service = PaytmPGService.getProductionService();
        Map<String, String> paramMap = new HashMap();
        paramMap.put("ORDER_ID", this.orderId);
        paramMap.put(PaytmConstants.MERCHANT_ID, "FLOTER55912639344993");
        paramMap.put("CUST_ID", "User_" + MyApp.getApplication().readUser().getUser_id());
        paramMap.put("CHANNEL_ID", "WAP");
        paramMap.put("INDUSTRY_TYPE_ID", "Retail109");
        paramMap.put("WEBSITE", "FLOTERWAP");
        paramMap.put("TXN_AMOUNT", this.payment.getPay_amount());
        paramMap.put("CALLBACK_URL", "https://pguat.paytm.com/paytmchecksum/paytmCallback.jsp");
        paramMap.put("EMAIL", MyApp.getApplication().readUser().getU_email());
        paramMap.put("MOBILE_NO", MyApp.getApplication().readUser().getU_mobile());
        paramMap.put("CHECKSUMHASH", checksum);
        Service.initialize(new PaytmOrder(paramMap), null);
        Service.startPaymentTransaction(this, true, true, new PaytmPaymentTransactionCallback() {

            class C05781 implements DialogInterface.OnClickListener {
                C05781() {
                }

                public void onClick(DialogInterface dialogInterface, int i) {
                    OnTripActivity.this.updatePaymentInfo(true);
                }
            }

            class C05792 implements DialogInterface.OnClickListener {
                C05792() {
                }

                public void onClick(DialogInterface dialogInterface, int i) {
                    OnTripActivity.this.updatePaymentInfo(false);
                }
            }

            class C05804 implements DialogInterface.OnClickListener {
                C05804() {
                }

                public void onClick(DialogInterface dialogInterface, int i) {
                    OnTripActivity.this.updatePaymentInfo(true);
                }
            }

            class C05815 implements DialogInterface.OnClickListener {
                C05815() {
                }

                public void onClick(DialogInterface dialogInterface, int i) {
                    OnTripActivity.this.updatePaymentInfo(false);
                }
            }

            class C05826 implements DialogInterface.OnClickListener {
                C05826() {
                }

                public void onClick(DialogInterface dialogInterface, int i) {
                    OnTripActivity.this.updatePaymentInfo(true);
                }
            }

            class C05837 implements DialogInterface.OnClickListener {
                C05837() {
                }

                public void onClick(DialogInterface dialogInterface, int i) {
                    OnTripActivity.this.updatePaymentInfo(false);
                }
            }

            class C05848 implements DialogInterface.OnClickListener {
                C05848() {
                }

                public void onClick(DialogInterface dialogInterface, int i) {
                    OnTripActivity.this.updatePaymentInfo(true);
                }
            }

            class C05859 implements DialogInterface.OnClickListener {
                C05859() {
                }

                public void onClick(DialogInterface dialogInterface, int i) {
                    OnTripActivity.this.updatePaymentInfo(false);
                }
            }

            class C11043 extends JsonHttpResponseHandler {
                C11043() {
                }

                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    MyApp.spinnerStop();
                    Log.d("Response:", response.toString());
                    try {
                        if (response.optString(PaytmConstants.STATUS).equals("TXN_SUCCESS")) {
                            OnTripActivity.this.checkStatus(response.getJSONObject("response").toString());
                            return;
                        }
                        MyApp.popMessage("Error", "Payment did not processed\nPlease try again", OnTripActivity.this.getContext());
                        OnTripActivity.this.showPaymentDialog(true);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    super.onFailure(statusCode, headers, throwable, errorResponse);
                    MyApp.spinnerStop();
                }

                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    MyApp.spinnerStop();
                }
            }

            public void someUIErrorOccurred(String inErrorMessage) {
                Log.d("LOG", "UI Error Occur.");
                AlertDialog.Builder b = new AlertDialog.Builder(OnTripActivity.this.getContext());
                b.setIcon(R.mipmap.ic_launcher);
                b.setMessage( "UI Error Occurred for the paytm payment flow, Do you want to pay with paytm?");
                b.setTitle( "Error!");
                b.setPositiveButton( "Pay With Paytm", new C05781());
                b.setNegativeButton( "Pay Cash", new C05792());
                b.create().show();
            }

            public void onTransactionResponse(Bundle inResponse) {
                Log.d("LOG", "Payment Transaction : " + inResponse);
                MyApp.spinnerStart(OnTripActivity.this.getContext(), "Please wait...");
                String url = "http://floter.in/floterapi/paytm/getTransactionStatus.php";
                RequestParams p = new RequestParams();
                p.put(PaytmConstants.MERCHANT_ID, "FLOTER55912639344993");
                p.put("ORDER_ID", OnTripActivity.this.orderId);
                Log.d("URl:", url);
                Log.d("Request:", p.toString());
                AsyncHttpClient client = new AsyncHttpClient();
                client.setTimeout(30000);
                client.get(url, p, new C11043());
            }

            public void networkNotAvailable() {
                Log.d("LOG", "Network Error Occur.");
                AlertDialog.Builder b = new AlertDialog.Builder(OnTripActivity.this.getContext());
                b.setIcon((int) R.mipmap.ic_launcher);
                b.setMessage( "Network Error Occurred for the paytm payment flow, Do you want to pay with paytm?");
                b.setTitle( "Error!");
                b.setPositiveButton( "Pay With Paytm", new C05804());
                b.setNegativeButton( "Pay Cash", new C05815());
                b.create().show();
            }

            public void clientAuthenticationFailed(String inErrorMessage) {
                AlertDialog.Builder b = new AlertDialog.Builder(OnTripActivity.this.getContext());
                b.setIcon((int) R.mipmap.ic_launcher);
                b.setMessage( "Client authentication Error Occurred for the paytm payment flow, Do you want to pay with paytm?");
                b.setTitle( "Error!");
                b.setPositiveButton( "Pay With Paytm", new C05826());
                b.setNegativeButton( "Pay Cash", new C05837());
                b.create().show();
            }

            public void onErrorLoadingWebPage(int iniErrorCode, String inErrorMessage, String inFailingUrl) {
                AlertDialog.Builder b = new AlertDialog.Builder(OnTripActivity.this.getContext());
                b.setIcon((int) R.mipmap.ic_launcher);
                b.setMessage( "Some Error Occurred for the paytm payment flow, Do you want to pay with paytm?");
                b.setTitle( "Error!");
                b.setPositiveButton( "Pay With Paytm", new C05848());
                b.setNegativeButton( "Pay Cash", new C05859());
                b.create().show();
            }

            public void onBackPressedCancelTransaction() {
                AlertDialog.Builder b = new AlertDialog.Builder(OnTripActivity.this.getContext());
                b.setIcon((int) R.mipmap.ic_launcher);
                b.setMessage( "Payment cancelled for the paytm payment flow by you, Do you want to pay with paytm?");
                b.setTitle( "Error!");
                b.setPositiveButton( "Pay With Paytm", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int i) {
                        OnTripActivity.this.updatePaymentInfo(true);
                    }
                });
                b.setNegativeButton( "Pay Cash", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int i) {
                        OnTripActivity.this.updatePaymentInfo(false);
                    }
                });
                b.create().show();
            }

            public void onTransactionCancel(String inErrorMessage, Bundle inResponse) {
                AlertDialog.Builder b = new AlertDialog.Builder(OnTripActivity.this.getContext());
                b.setIcon((int) R.mipmap.ic_launcher);
                b.setMessage( "Paytm Transaction failed, Do you want to pay with paytm?");
                b.setTitle( "Error!");
                b.setPositiveButton( "Pay With Paytm", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int i) {
                        OnTripActivity.this.updatePaymentInfo(true);
                    }
                });
                b.setNegativeButton( "Pay Cash", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int i) {
                        OnTripActivity.this.updatePaymentInfo(false);
                    }
                });
                b.create().show();
            }
        });
    }

    private void checkStatus(String json) {
        MyApp.spinnerStart(getContext(), "Validating Payment...");
        String url = "https://secure.paytm.in/oltp/HANDLER_INTERNAL/getTxnStatus";
        RequestParams p = new RequestParams();
        p.put("JsonData", json);
        Log.d("URl:", url);
        Log.d("Request:", p.toString());
        AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(30000);
        client.get(url, p, new JsonHttpResponseHandler() {
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                MyApp.spinnerStop();
                Log.d("Response:", response.toString());
                if (response.optString(PaytmConstants.STATUS).equals("TXN_SUCCESS")) {
                    OnTripActivity.this.updatePaymentApi(response.optString(PaytmConstants.TRANSACTION_ID), response.optString(PaytmConstants.ORDER_ID));
                    return;
                }
                OnTripActivity.this.ll_feedback.setVisibility(View.VISIBLE);
                OnTripActivity.this.slideUp.show();
                txt_user_name.setText(currentTrip.getDriver().getD_name());
            }

            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                MyApp.spinnerStop();
                OnTripActivity.this.ll_feedback.setVisibility(View.VISIBLE);
                OnTripActivity.this.slideUp.show();
                txt_user_name.setText(currentTrip.getDriver().getD_name());
            }

            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                MyApp.spinnerStop();
                OnTripActivity.this.ll_feedback.setVisibility(View.VISIBLE);
                OnTripActivity.this.slideUp.show();
                txt_user_name.setText(currentTrip.getDriver().getD_name());
            }
        });
    }

    private void updatePaymentApi(final String txnId, final String orderId) {
        sendPaymentDoneNotificationToDriver("PAYTM");
        MyApp.spinnerStart(getContext(), "Updating Payment info...");
        String url = AppConstants.BASE_PAYMENT.replace("getpayments?", "updatepayment?");
        RequestParams p = new RequestParams();
        p.put("payment_id", this.payment.getPayment_id());
        p.put("pay_mode", "PAYTM");
        p.put("trip_id", this.payment.getTrip_id());
        p.put("order_id", orderId);
        p.put("pay_status", "PAID");
        p.put(Param.TRANSACTION_ID, txnId);
        Log.d("URl:", url);
        Log.d("Request:", p.toString());
        AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(30000);
        client.post(url, p, new JsonHttpResponseHandler() {
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                MyApp.spinnerStop();
                Log.d("Response:", response.toString());
                if (response.optString("status").equals("ok")) {
                    OnTripActivity.this.ll_feedback.setVisibility(View.VISIBLE);
                    OnTripActivity.this.slideUp.show();
                    txt_user_name.setText(currentTrip.getDriver().getD_name());
                    MyApp.setSharedPrefString(AppConstants.PAYBLE_TRIP_ID, "");
                    return;
                }
                MyApp.showMassage(OnTripActivity.this.getContext(), "Retrying...");
                OnTripActivity.this.updatePaymentApi(txnId, orderId);
            }

            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                MyApp.spinnerStop();
                MyApp.showMassage(OnTripActivity.this.getContext(), "Retrying...");
                OnTripActivity.this.updatePaymentApi(txnId, orderId);
            }

            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                MyApp.spinnerStop();
                MyApp.showMassage(OnTripActivity.this.getContext(), "Retrying...");
                OnTripActivity.this.updatePaymentApi(txnId, orderId);
            }
        });
    }

    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (MyApp.getSharedPrefString("SHOW_PAY").equals("YES") && !this.paymentId.equals(AppEventsConstants.EVENT_PARAM_VALUE_NO)) {
            if (this.currentTrip.getTrip_status().equals(TripStatus.Finished.name()) || this.currentTrip.getTrip_status().equals(TripStatus.Unloading.name())) {
                try {
                    if (this.payment != null && this.payment.getPay_status().equals("PAID")) {
                        MyApp.setSharedPrefString("SHOW_PAY", "NO");
                    }
                } catch (Exception e) {
                }
                showFeedbackForm();
            }
        }
    }

    public void rotatemarker() {
        Location prevLoc = new Location("service Provider");
        prevLoc.setLatitude(this.oldlat);
        prevLoc.setLongitude(this.oldlong);
        Location newLoc = new Location("service Provider");
        newLoc.setLatitude(this.newlat);
        newLoc.setLongitude(this.newlong);
        this.bearing = prevLoc.bearingTo(newLoc);
        if (this.driverMarker != null) {
            this.driverMarker.remove();
        }
        this.driverMarker = this.mMap.addMarker(new MarkerOptions().position(new LatLng(this.newlat, this.newlong)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_truck_marker)).anchor(0.5f, 0.5f).rotation(this.bearing).flat(true));
        this.oldlong = this.newlong;
    }

    private LatLngBounds adjustBoundsForMaxZoomLevel(LatLngBounds bounds) {
        LatLng sw = bounds.southwest;
        LatLng ne = bounds.northeast;
        double deltaLat = Math.abs(sw.latitude - ne.latitude);
        double deltaLon = Math.abs(sw.longitude - ne.longitude);
        LatLng sw2;
        LatLng ne2;
        if (deltaLat < 0.005d) {
            sw2 = new LatLng(sw.latitude - (0.005d - (deltaLat / 2.0d)), sw.longitude);
            ne2 = new LatLng(ne.latitude + (0.005d - (deltaLat / 2.0d)), ne.longitude);
            ne = ne2;
            sw = sw2;
            return new LatLngBounds(sw2, ne2);
        } else if (deltaLon >= 0.005d) {
            return bounds;
        } else {
            sw2 = new LatLng(sw.latitude, sw.longitude - (0.005d - (deltaLon / 2.0d)));
            ne2 = new LatLng(ne.latitude, ne.longitude + (0.005d - (deltaLon / 2.0d)));
            ne = ne2;
            sw = sw2;
            return new LatLngBounds(sw2, ne2);
        }
    }

    public void onJsonArrayResponseReceived(JSONArray a, int callNumber) {
    }

    public void onErrorReceived(String error) {
    }

    private String getMapsApiDirectionsUrl(LatLng origin, LatLng dest) {
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        return "https://maps.googleapis.com/maps/api/directions/" + "json" + "?" + (str_origin + "&" + ("destination=" + dest.latitude + "," + dest.longitude) + "&" + "sensor=false");
    }

    public void onDestroy() {
        super.onDestroy();
        try {
            this.mHandler.removeCallbacks(this.mUpdateDriverLocationToShow);
        } catch (Exception e) {
        }
    }
}
