package cargo.floter.user;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.appevents.AppEventsConstants;
import com.github.florent37.singledateandtimepicker.dialog.SingleDateAndTimePickerDialog;
import com.google.android.gms.auth.api.credentials.CredentialsApi;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnCameraIdleListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.analytics.FirebaseAnalytics.Param;
import com.google.gson.Gson;
import com.loopj.android.http.RequestParams;
import com.mancj.slideup.SlideUp;
import com.mancj.slideup.SlideUp.Builder;
import com.mancj.slideup.SlideUp.Listener;
import com.mancj.slideup.SlideUp.State;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import cargo.floter.user.CustomActivity.ResponseCallback;
import cargo.floter.user.application.MyApp;
import cargo.floter.user.application.SingleInstance;
import cargo.floter.user.fragments.FragmentDrawer;
import cargo.floter.user.fragments.FragmentDrawer.FragmentDrawerListener;
import cargo.floter.user.model.NearbyDrivers;
import cargo.floter.user.model.NearbyDrivers.Response;
import cargo.floter.user.model.RateCard;
import cargo.floter.user.model.RateCard.RateCardResponse;
import cargo.floter.user.model.User;
import cargo.floter.user.utils.AppConstants;
import cargo.floter.user.utils.HorizontalPicker;
import cargo.floter.user.utils.HorizontalPicker.OnItemClicked;
import cargo.floter.user.utils.HorizontalPicker.OnItemSelected;
import cargo.floter.user.utils.LocationProvider;
import cargo.floter.user.utils.LocationProvider.LocationCallback;
import cargo.floter.user.utils.LocationProvider.PermissionCallback;
import cz.msebera.android.httpclient.protocol.HTTP;

public class MainActivity extends CustomActivity implements ResponseCallback, FragmentDrawerListener, OnMapReadyCallback,
        OnCameraIdleListener, OnItemSelected, OnItemClicked, OnConnectionFailedListener, LocationCallback, PermissionCallback {
    protected static final String TAG = "MainActivity";
    private LatLngBounds DELHI = new LatLngBounds(new LatLng(-44.0d, 113.0d), new LatLng(-10.0d, 154.0d));
    private String ETA = "10";
    private TextView base_price;
    private RelativeLayout click_truck;
    private String currentBookText = "";
    private LatLng destinationLocation = null;
    private String destinationString = "";
    private TextView dimension;
    private FragmentDrawer drawerFragment;
    private HashMap<String, List<Response>> driversMap;
    private GoogleApiClient googleApiClient;
    private boolean hasDriver = false;
    private ImageView img_selected_truck;
    private boolean isBookLater = false;
    private boolean isFirstSet = false;
    private String key = "Piaggio Ape";
    private RelativeLayout ll_info;
    private TextView locMarkertext;
    private LocationProvider locationProvider;
    private LatLng mCenterLatLong;
    private TextView mLocationText;
    private GoogleMap mMap;
    private Toolbar mToolbar;
    private SupportMapFragment mapFragment;
    private TextView pay_load;
    private TextView per_km_price;
    private TextView per_min_price;
    private String pickupTime = MyApp.millsToDateTime(System.currentTimeMillis() + 600000);
    private HashMap<String, RateCardResponse> f25r = new HashMap();
    private RateCard rateCard;
    private int selectedTruckIndex = 0;
    private SlideUp slideUp;
    private LatLng sourceLocation = null;
    private TypedArray truckImages;
    private String[] truckNames = null;
    private TextView txt_name_truck;

    class C05701 implements OnClickListener {
        C05701() {
        }

        public void onClick(View view) {
            Intent intent = new Intent(getContext(), SearchActivity.class);
            intent.putExtra(AppConstants.EXTRA_1, "Enter pickup location");
            startActivityForResult(intent, 122);
            isGoingToChangeCurrentLocation = true;
        }
    }

    class C05712 implements OnClickListener {
        C05712() {
        }

        public void onClick(View view) {
            slideUp.hideImmediately();
        }
    }

    class C05724 implements OnClickListener {
        C05724() {
        }

        public void onClick(View view) {
            slideUp.show();
        }
    }

    class C05735 implements OnClickListener {
        C05735() {
        }

        public void onClick(View view) {
            slideUp.hide();
        }
    }

    class C05746 implements Runnable {
        C05746() {
        }

        public void run() {
            if (sourceLocation == null) {
                locationProvider = new LocationProvider(MainActivity.this, MainActivity.this, MainActivity.this);
                locationProvider.connect();
            }
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
            int estimatedTime;
            String distance = "";
            String duration = "";
            for (int i = 0; i < routes.size(); i++) {
                ArrayList<LatLng> points = new ArrayList();
                PolylineOptions polyLineOptions = new PolylineOptions();
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
            }
            MyApp.spinnerStop();
            RateCardResponse r = SingleInstance.getInstance().getSelectedRate();
            double f = 0.0f;
            try {
                f = Float.parseFloat(distance) / 1000.0f;
                f = ((float) Math.round(100.0f * f)) / 100.0f;
            } catch (Exception e) {
            }

            if (f == 0) {
                f = (float) MyApp.distance(sourceLocation.latitude, sourceLocation.longitude
                        , destinationLocation.latitude, destinationLocation.longitude);
            }

            if (f < 3.0f) {
                f = 2.0f;
            }


            try {
                estimatedTime = Integer.parseInt(duration);
            } catch (Exception e2) {
                estimatedTime = 20;
            }
            estimatedTime /= 60;
            int charge = Integer.parseInt(r.getBase_fare());
            if (f > 2.0f) {
                try {
                    charge = (int) (((float) charge) + ((f - 2.0f) * ((float) Integer.parseInt(r.getPrice_per_km()))));
                } catch (Exception e3) {
                }
            }
//            if (estimatedTime > 120) {
//                try {
//                    charge = (int) (((float) charge) + ((f - BitmapDescriptorFactory.HUE_GREEN) * ((float) Integer.parseInt(r.getPrice_per_min_after_60_min()))));
//                } catch (Exception e4) {
//                }
//            }
            charge += (int) (((float) charge) * 0.05f);
            if (charge > 360) {
                charge = charge - 20;
            }
            if (charge > 500) {
                charge = charge - 40;
            }
            if (charge > 660) {
                charge = charge - 50;
            }
            SingleInstance.getInstance().setSourceLatLng(sourceLocation);
            SingleInstance.getInstance().setDestinationLatLng(destinationLocation);
            startActivity(new Intent(MainActivity.this, BookTripActivity.class)
                    .putExtra(AppConstants.EXTRA_1, mLocationText.getText().toString())
                    .putExtra(AppConstants.EXTRA_2, destinationString)
                    .putExtra("ETA", ETA)
                    .putExtra("isBookLater", isBookLater)
                    .putExtra("PickUpTime", pickupTime)
                    .putExtra("DURATION", estimatedTime + "")
                    .putExtra("DISTANCE", f + "")
                    .putExtra("EST_PRICE", charge + "")
                    .putExtra("PRICE", "Rs. " + charge + " - Rs. " + (((10 - (charge % 10)) + charge) + 20)));
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
                        hmDistance.put("distance", jDistance.getString(Param.VALUE));
                        JSONObject jDuration = ((JSONObject) jLegs.get(j)).getJSONObject("duration");
                        HashMap<String, String> hmDuration = new HashMap();
                        hmDuration.put("duration", jDuration.getString(Param.VALUE));
                        path.add(hmDistance);
                        path.add(hmDuration);
                        JSONArray jSteps = ((JSONObject) jLegs.get(j)).getJSONArray("steps");
                        for (int k = 0; k < jSteps.length(); k++) {
                            String polyline = "";
                            List<LatLng> list = decodePoly((String) ((JSONObject) ((JSONObject) jSteps.get(k)).get("polyline")).get("points"));
                            for (int l = 0; l < list.size(); l++) {
                                HashMap<String, String> hm = new HashMap();
                                hm.put("lat", Double.toString(((LatLng) list.get(l)).latitude));
                                hm.put("lng", Double.toString(((LatLng) list.get(l)).longitude));
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

    class C09603 implements Listener {
        C09603() {
        }

        public void onSlide(float percent) {
        }

        public void onVisibilityChanged(int visibility) {
            if (visibility != 8) {
            }
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        locationProvider = new LocationProvider(this, this, this);
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        setResponseListener(this);
        base_price = (TextView) findViewById(R.id.base_price);
        per_km_price = (TextView) findViewById(R.id.per_km_price);
        per_min_price = (TextView) findViewById(R.id.per_min_price);
        pay_load = (TextView) findViewById(R.id.pay_load);
        mLocationText = (TextView) findViewById(R.id.Locality);
        locMarkertext = (TextView) findViewById(R.id.locMarkertext);
        dimension = (TextView) findViewById(R.id.dimension);
        img_selected_truck = (ImageView) findViewById(R.id.img_selected_truck);
        txt_name_truck = (TextView) findViewById(R.id.txt_name_truck);
        click_truck = (RelativeLayout) findViewById(R.id.click_truck);
        ll_info = (RelativeLayout) findViewById(R.id.ll_info);
        mapFragment.getMapAsync(this);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        HorizontalPicker picker = (HorizontalPicker) findViewById(R.id.picker);
        picker.setOnItemClickedListener(this);
        picker.setOnItemSelectedListener(this);
        truckImages = getResources().obtainTypedArray(R.array.truckIconsActive);
        truckNames = getResources().getStringArray(R.array.truck_names);
        locMarkertext.setText("Searching Drivers...");
        currentBookText = "Searching Drivers...";
        rateCard = MyApp.getApplication().readRateCard();
        for (int i = 0; i < rateCard.getResponse().size(); i++) {
            f25r.put((rateCard.getResponse().get(i)).getCar_name(), rateCard.getResponse().get(i));
        }
        try {
            base_price.setText("Rs " + (f25r.get("Piaggio Ape")).getBase_fare());
            per_km_price.setText("Rs " + (f25r.get("Piaggio Ape")).getPrice_per_km() + "/km");
            per_min_price.setText("Rs " + (f25r.get("Piaggio Ape")).getCharge_after_free_time() + "/Min after " + (f25r.get("Piaggio Ape")).getFree_load_unload_time() + " Min");
            pay_load.setText((f25r.get("Piaggio Ape")).getCapacity() + " Kg");
            dimension.setText((f25r.get("Piaggio Ape")).getLength() + "x" + (f25r.get("Piaggio Ape")).getWidth() + "x" + (f25r.get("Piaggio Ape")).getHeight());
        } catch (Exception e) {
        }
        actionBar.setTitle("");
        drawerFragment = (FragmentDrawer) getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        drawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), mToolbar);
        drawerFragment.setDrawerListener(this);
        setupUiElements();
        mLocationText.setOnClickListener(new C05701());
        ll_info.setOnClickListener(new C05712());
        slideUp = new Builder(ll_info).withStartState(State.HIDDEN).withStartGravity(80).build();
        slideUp = new Builder(ll_info).withListeners(new C09603()).withStartGravity(80).withGesturesEnabled(false).withStartState(State.HIDDEN).build();
        click_truck.setOnClickListener(new C05724());
        ll_info.setOnClickListener(new C05735());
        new Handler().postDelayed(new C05746(), 10000);

        if (MyApp.getStatus(AppConstants.FIRST_OFFER)) {
            showFirstOfferDialog();
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                isShownNoDriverDialog = false;
            }
        }, 2000);

        Log.d("DeviceToken", "" + MyApp.getSharedPrefString(AppConstants.DEVICE_TOKEN));
    }

    private void showFirstOfferDialog() {

        final Dialog dialog = new Dialog(getContext());
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.dialog_offer);
        dialog.setCancelable(false);
        TextView txt_ok = (TextView) dialog.findViewById(R.id.txt_ok);

        txt_ok.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
//                FIRST_OFFER
                dialog.dismiss();
            }
        });

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = lp.MATCH_PARENT;
        lp.height = lp.WRAP_CONTENT;
        dialog.getWindow().setAttributes(lp);
        dialog.show();
    }

//    private void showPaymentDialog() {
//        final Dialog dialog = new Dialog(getContext());
//        dialog.getWindow().requestFeature(1);
//        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
//        dialog.setContentView(R.layout.dialog_paymode);
//        dialog.setCancelable(false);
//        TextView txt_pay = (TextView) dialog.findViewById(R.id.txt_pay);
//        final RadioButton radio_cash = (RadioButton) dialog.findViewById(R.id.radio_cash);
//        final RadioButton radio_paytm = (RadioButton) dialog.findViewById(R.id.radio_paytm);
//        radio_cash.setOnCheckedChangeListener(new OnCheckedChangeListener() {
//            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
//                if (b) {
//                    radio_cash.setChecked(true);
//                    radio_paytm.setChecked(false);
//                    return;
//                }
//                radio_paytm.setChecked(true);
//                radio_cash.setChecked(false);
//            }
//        });
//        radio_paytm.setOnCheckedChangeListener(new OnCheckedChangeListener() {
//            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
//                if (b) {
//                    radio_cash.setChecked(false);
//                    radio_paytm.setChecked(true);
//                    return;
//                }
//                radio_paytm.setChecked(false);
//                radio_cash.setChecked(true);
//            }
//        });
//        txt_pay.setOnClickListener(new OnClickListener() {
//            public void onClick(View view) {
//                if (radio_cash.isChecked()) {
//                    MyApp.showMassage(getContext(), "Cash");
//                } else {
//                    MyApp.showMassage(getContext(), "Paytm");
//                }
//                dialog.dismiss();
//            }
//        });
//        LayoutParams lp = new LayoutParams();
//        lp.copyFrom(dialog.getWindow().getAttributes());
//        lp.width = -1;
//        lp.height = -2;
//        dialog.getWindow().setAttributes(lp);
//        dialog.show();
//    }

    private void getNearbyDrivers(String lat, String lng) {
        RequestParams p = new RequestParams();
        p.put("lat", lat);
        p.put("lng", lng);
        p.put("miles", 10);
        postCall(getContext(), AppConstants.BASE_URL.replace("userapi", "driverapi") + "getnearbydriverlists?", p, "", 1);
    }

    private void setupUiElements() {
        setTouchNClick(R.id.btn_book);
        setTouchNClick(R.id.btn_book_later);
        setTouchNClick(R.id.locMarkertext);
    }

    public void onClick(View v) {
        super.onClick(v);
        if (v.getId() == R.id.btn_book_later) {
            currentBookText = locMarkertext.getText().toString();
            locMarkertext.setText(" |   Select Time >");
            locMarkertext.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_timer, 0, 0, 0);
            isBookLater = true;
        } else if (v.getId() == R.id.btn_book) {
            locMarkertext.setText(currentBookText);
            isBookLater = false;
            locMarkertext.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            if (currentBookText.contains("No Driver")) {
                MyApp.popMessage("Alert!", "No driver available with the truck selected at this location.\nPlease try with different location or different type of truck.", getContext());
            } else if (hasDriver) {
                Intent intent = new Intent(getContext(), SearchActivity.class);
                intent.putExtra(AppConstants.EXTRA_1, "Enter drop location");
                startActivityForResult(intent, 123);
            }
        } else if (v.getId() != R.id.locMarkertext) {
        } else {
            if (isBookLater) {
                new SingleDateAndTimePickerDialog.Builder(getContext()).curved().mainColor(Color.parseColor("#0E577D")).title("Select Date & Time").listener(new SingleDateAndTimePickerDialog.Listener() {
                    public void onDateSelected(Date date) {
                        if (date.getTime() <= System.currentTimeMillis() + 3600000) {
                            Snackbar.make(findViewById(R.id.btn_book_later), "Pickup time should be at least 60 minutes from the current time", (int) CredentialsApi.CREDENTIAL_PICKER_REQUEST_CODE).show();
                            return;
                        }
                        pickupTime = MyApp.millsToDateTime(date.getTime());
                        Intent intent = new Intent(getContext(), SearchActivity.class);
                        intent.putExtra(AppConstants.EXTRA_1, "Enter drop location");
                        startActivityForResult(intent, 123);
                    }
                }).display();
            } else if (hasDriver) {
                pickupTime = MyApp.millsToDateTime(System.currentTimeMillis() + ((1000 * Long.parseLong(ETA)) * 60));
                Intent intent = new Intent(getContext(), SearchActivity.class);
                intent.putExtra(AppConstants.EXTRA_1, "Enter drop location");
                startActivityForResult(intent, 123);
            }
        }
    }

    public void onDestroy() {
        super.onDestroy();
    }

    public void onDrawerItemSelected(View view, int position) {
        if (position == 5) {
            Intent sendIntent = new Intent();
            sendIntent.setAction("android.intent.action.SEND");
            sendIntent.putExtra("android.intent.extra.TEXT", "I am using " + getString(R.string.app_name) + " App ! Why don't you try it out...\nInstall " + getString(R.string.app_name) + " now !\nhttps://play.google.com/store/apps/details?id=" + getPackageName());
            sendIntent.putExtra("android.intent.extra.SUBJECT", getString(R.string.app_name) + " App !");
            sendIntent.setType(HTTP.PLAIN_TEXT_TYPE);
            startActivity(Intent.createChooser(sendIntent, getString(R.string.messenger_send_button_text)));
        } else if (position == 7) {
            startActivity(new Intent(this, AboutUsActivity.class));
        } else if (position == 2) {
            startActivity(new Intent(this, VehicleDetails.class));
        } else if (position == 1) {
            startActivity(new Intent(getContext(), HistoryActivity.class));
        } else if (position == 3) {
            startActivity(new Intent(getContext(), PaymentActivity.class));
        } else if (position == 4) {
            startActivity(new Intent(getContext(), OffersActivity.class));
        } else if (position == 6) {
            startActivity(new Intent(getContext(), SupportActivity.class));
        } else if (position == 0) {
            startActivity(new Intent(getContext(), TrackTripsActivity.class));
        }
    }

    protected void onStart() {
        super.onStart();
        locationProvider.connect();
    }

    protected void onResume() {
        super.onResume();
        if (!MyApp.isLocationEnabled(getContext())) {
            enableGPS();
        }
//        if (sourceLocation != null && isFirstSet) {
//            getNearbyDrivers(sourceLocation.latitude + "", sourceLocation.longitude + "");
//        }
        if (MyApp.getSharedPrefString("SHOW_PAY").equals("YES")) {
            startActivity(new Intent(getContext(), FinalPaymentActivity.class));
        }
    }

    protected void onPause() {
        super.onPause();
    }

    protected void onStop() {
        super.onStop();
        locationProvider.disconnect();
    }

    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnCameraIdleListener(this);
        View mapView = mapFragment.getView();
        View locationButton = ((View) mapView.findViewById(Integer.parseInt(AppEventsConstants.EVENT_PARAM_VALUE_YES)).getParent()).findViewById(Integer.parseInt("2"));
        if (mapView != null && mapView.findViewById(Integer.parseInt(AppEventsConstants.EVENT_PARAM_VALUE_YES)) != null) {
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams)
                    locationButton.getLayoutParams();
            // position on right bottom
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
            layoutParams.setMargins(0, 0, 30, 200);
        }
    }

    public void onCameraIdle() {
        if (isGoingToChangeCurrentLocation) {
            driversMap = new HashMap();
            currentBookText = "No Drivers...";
            locMarkertext.setText("No Drivers...");
            mMap.clear();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    isGoingToChangeCurrentLocation = false;
                }
            }, 1000);
            return;
        }
        Log.d("Camera position change", mMap.getCameraPosition() + "");
        mCenterLatLong = mMap.getCameraPosition().target;
        sourceLocation = mCenterLatLong;
        try {
            Location mLocation = new Location("");
            mLocation.setLatitude(mCenterLatLong.latitude);
            mLocation.setLongitude(mCenterLatLong.longitude);
            getCompleteAddressString(mCenterLatLong.latitude, mCenterLatLong.longitude);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean isGoingToChangeCurrentLocation = false;

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1010 && ContextCompat.checkSelfPermission(getContext(), "android.permission.ACCESS_FINE_LOCATION") == -1) {
            MyApp.showMassage(getContext(), "Location Denied, you cannot use the app");
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                if (!isGoingToChangeCurrentLocation) {
                    isGoingToChangeCurrentLocation = false;
                }
            }
        }, 1000);
        Place place;
        switch (requestCode) {
            case 122:
                if (resultCode == RESULT_OK) {
                    place = SingleInstance.getInstance().getSelectedPlace();
                    sourceLocation = place.getLatLng();
                    Log.i("", "Place: " + place.getName());
                    if (place.getAddress().toString().contains("Odisha") || place.getAddress().toString().contains("Delhi")
                            || place.getAddress().toString().contains("Jodhpur")) {
                        mLocationText.setText(place.getAddress().toString().replace("\n", " "));
                        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder().target(place.getLatLng()).zoom(15.5f).tilt(0.0f).build()));
                        getNearbyDrivers(place.getLatLng().latitude + "", place.getLatLng().longitude + "");
                    } else {
                        MyApp.popMessage("Alert!", "Service is not available in your area.\nThis application is restricted to Odisha state only.\nThank you", getContext());
                    }


                    return;
                } else if (resultCode != 2) {
                    return;
                } else {
                    return;
                }
            case 123:
                if (resultCode == -1) {
                    place = SingleInstance.getInstance().getSelectedPlace();
                    Log.i("", "Place: " + place.getAddress());
                    destinationLocation = place.getLatLng();
                    destinationString = place.getAddress().toString();
                    if (place.getAddress().toString().contains("Odisha") || place.getAddress().toString().contains("Delhi")
                            || place.getAddress().toString().contains("Jodhpur")) {

                        String url = getMapsApiDirectionsUrl(new LatLng(sourceLocation.latitude, sourceLocation.longitude),
                                new LatLng(destinationLocation.latitude, destinationLocation.longitude));
                        new ReadTask().execute(new String[]{url});
                        MyApp.spinnerStart(getContext(), "Please wait...");
                    } else {
                        MyApp.popMessage("Alert!", "Service is not available in your area.\nThis application is restricted to Odisha state only.\nThank you", getContext());
                    }
                    return;
                } else if (resultCode == 2) {
                    Log.i("", PlaceAutocomplete.getStatus(this, data).getStatusMessage());
                    return;
                } else {
                    return;
                }
            default:
                return;
        }


    }

    private void changeMap(Location location) {
        if (mMap != null) {
            mMap.getUiSettings().setZoomControlsEnabled(false);
            CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(location.getLatitude(), location.getLongitude())).zoom(15.5f).tilt(0.0f).build();
            if (ContextCompat.checkSelfPermission(this, "android.permission.ACCESS_FINE_LOCATION") == 0 || ContextCompat.checkSelfPermission(this, "android.permission.ACCESS_COARSE_LOCATION") == 0) {
                mMap.setMyLocationEnabled(true);
            } else {
                ActivityCompat.requestPermissions(this, new String[]{"android.permission.ACCESS_FINE_LOCATION", "android.permission.ACCESS_COARSE_LOCATION"}, 1010);
            }
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            getCompleteAddressString(location.getLatitude(), location.getLongitude());
            return;
        }
        Toast.makeText(getApplicationContext(), "Sorry! unable to create maps", Toast.LENGTH_SHORT).show();
    }

    public void onItemSelected(int index) {
        selectedTruckIndex = index;
        img_selected_truck.setImageResource(truckImages.getResourceId(index, 0));
        txt_name_truck.setText(truckNames[index]);
        if (index == 0) {
            key = "Piaggio Ape";
            try {
                base_price.setText("Rs " + (f25r.get("Piaggio Ape")).getBase_fare());
                per_km_price.setText("Rs " + (f25r.get("Piaggio Ape")).getPrice_per_km() + "/km");
                per_min_price.setText("Rs " + (f25r.get("Piaggio Ape")).getCharge_after_free_time() + "/Min after " + (f25r.get("Piaggio Ape")).getFree_load_unload_time() + " Min");
                pay_load.setText((f25r.get("Piaggio Ape")).getCapacity() + " Kg");
                dimension.setText((f25r.get("Piaggio Ape")).getLength() + "x" + (f25r.get("Piaggio Ape")).getWidth() + "x" + (f25r.get("Piaggio Ape")).getHeight());
                SingleInstance.getInstance().setSelectedRate(f25r.get("Piaggio Ape"));
            } catch (Exception e) {
            }
        } else if (index == 1) {
            key = "Tata Ace";
            base_price.setText("Rs " + (f25r.get("Tata Ace")).getBase_fare());
            per_km_price.setText("Rs " + (f25r.get("Tata Ace")).getPrice_per_km() + "/km");
            per_min_price.setText("Rs " + (f25r.get("Tata Ace")).getCharge_after_free_time() + "/Min after " + (f25r.get("Tata Ace")).getFree_load_unload_time() + " Min");
            pay_load.setText((f25r.get("Tata Ace")).getCapacity() + " Kg");
            dimension.setText((f25r.get("Tata Ace")).getLength() + "x" + (f25r.get("Tata Ace")).getWidth() + "x" + (f25r.get("Tata Ace")).getHeight());
            SingleInstance.getInstance().setSelectedRate(f25r.get("Tata Ace"));
        } else if (index == 2) {
            key = "Tata Super Ace";
            base_price.setText("Rs " + (f25r.get("Tata Super Ace")).getBase_fare());
            per_km_price.setText("Rs " + (f25r.get("Tata Super Ace")).getPrice_per_km() + "/km");
            per_min_price.setText("Rs " + (f25r.get("Tata Super Ace")).getCharge_after_free_time() + "/Min after " + (f25r.get("Tata Super Ace")).getFree_load_unload_time() + " Min");
            pay_load.setText((f25r.get("Tata Super Ace")).getCapacity() + " Kg");
            dimension.setText((f25r.get("Tata Super Ace")).getLength() + "x" + (f25r.get("Tata Super Ace")).getWidth() + "x" + (f25r.get("Tata Super Ace")).getHeight());
            SingleInstance.getInstance().setSelectedRate(f25r.get("Tata Super Ace"));
        } else if (index == 3) {
            key = "Ashok Leyland Dost";
            base_price.setText("Rs " + (f25r.get("Ashok Leyland Dost")).getBase_fare());
            per_km_price.setText("Rs " + (f25r.get("Ashok Leyland Dost")).getPrice_per_km() + "/km");
            per_min_price.setText("Rs " + (f25r.get("Ashok Leyland Dost")).getCharge_after_free_time() + "/Min after " + (f25r.get("Ashok Leyland Dost")).getFree_load_unload_time() + " Min");
            pay_load.setText((f25r.get("Ashok Leyland Dost")).getCapacity() + " Kg");
            dimension.setText((f25r.get("Ashok Leyland Dost")).getLength() + "x" + (f25r.get("Ashok Leyland Dost")).getWidth() + "x" + (f25r.get("Ashok Leyland Dost")).getHeight());
            SingleInstance.getInstance().setSelectedRate(f25r.get("Ashok Leyland Dost"));
        } else if (index == 4) {
            key = "Bolero Pick Up";
            base_price.setText("Rs " + (f25r.get("Bolero Pick Up")).getBase_fare());
            per_km_price.setText("Rs " + (f25r.get("Bolero Pick Up")).getPrice_per_km() + "/km");
            per_min_price.setText("Rs " + (f25r.get("Bolero Pick Up")).getCharge_after_free_time() + "/Min after " + (f25r.get("Bolero Pick Up")).getFree_load_unload_time() + " Min");
            pay_load.setText((f25r.get("Bolero Pick Up")).getCapacity() + " Kg");
            dimension.setText((f25r.get("Bolero Pick Up")).getLength() + "x" + (f25r.get("Bolero Pick Up")).getWidth() + "x" + (f25r.get("Bolero Pick Up")).getHeight());
            SingleInstance.getInstance().setSelectedRate(f25r.get("Bolero Pick Up"));
        } else if (index == 5) {
            key = "Tata 407";
            base_price.setText("Rs " + (f25r.get("Tata 407")).getBase_fare());
            per_km_price.setText("Rs " + (f25r.get("Tata 407")).getPrice_per_km() + "/km");
            per_min_price.setText("Rs " + (f25r.get("Tata 407")).getCharge_after_free_time() + "/Min after " + (f25r.get("Tata 407")).getFree_load_unload_time() + " Min");
            pay_load.setText((f25r.get("Tata 407")).getCapacity() + " Kg");
            dimension.setText((f25r.get("Tata 407")).getLength() + "x" + (f25r.get("Tata 407")).getWidth() + "x" + (f25r.get("Tata 407")).getHeight());
            SingleInstance.getInstance().setSelectedRate(f25r.get("Tata 407"));
        }
        if (mMap != null) {
            mMap.clear();
            if (driversMap == null) {
                return;
            }
            if (driversMap.containsKey(key)) {
                hasDriver = true;
                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                builder.include(new LatLng(sourceLocation.latitude, sourceLocation.longitude));
                List<Response> r = driversMap.get(key);
                for (int i = 0; i < r.size(); i++) {
                    builder.include(mMap.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(((Response) r.get(i)).getD_lat()), Double.parseDouble(((Response) r.get(i)).getD_lng()))).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_truck_marker))).getPosition());
                }
                if (!isBookLater) {
                    setEstimatedTimeText((r.get(0)).getDistance());
                }
//                CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(adjustBoundsForMaxZoomLevel(builder.build()), 50);
                SingleInstance.getInstance().setNotifiableDrivers(r);
                return;
            }
            hasDriver = false;
            if (!isBookLater) {
                currentBookText = "No Drivers...";
                locMarkertext.setText("No Drivers...");
            }
        }
    }

    private void setEstimatedTimeText(String distance) {
        double dist = Double.parseDouble(distance);
        if (dist <= 2.0d) {
            locMarkertext.setText("10 Min | Book Now");
            currentBookText = "10 Min | Book Now";
            ETA = "10";
        } else if (dist > 2.0d && dist <= 4.0d) {
            locMarkertext.setText("15 Min | Book Now");
            currentBookText = "15 Min | Book Now";
            ETA = "15";
        } else if (dist > 4.0d && dist <= 7.0d) {
            locMarkertext.setText("20 Min | Book Now");
            currentBookText = "20 Min | Book Now";
            ETA = "20";
        } else if (dist > 7.0d && dist <= 10.0d) {
            locMarkertext.setText("25 Min | Book Now");
            currentBookText = "25 Min | Book Now";
            ETA = "25";
        } else if (dist > 10.0d && dist <= 13.0d) {
            locMarkertext.setText("30 Min | Book Now");
            currentBookText = "30 Min | Book Now";
            ETA = "30";
        } else if (dist > 13.0d && dist <= 16.0d) {
            locMarkertext.setText("40 Min | Book Now");
            currentBookText = "40 Min | Book Now";
            ETA = "40";
        } else if (dist > 16.0d && dist <= 18.0d) {
            locMarkertext.setText("45 Min | Book Now");
            currentBookText = "45 Min | Book Now";
            ETA = "45";
        } else if (dist <= 18.0d || dist > 20.0d) {
            locMarkertext.setText("55 Min | Book Now");
            currentBookText = "55 Min | Book Now";
            ETA = "55";
        } else {
            locMarkertext.setText("50 Min | Book Now");
            currentBookText = "50 Min | Book Now";
            ETA = "50";
        }
    }

    public void onItemClicked(int index) {
        img_selected_truck.setImageResource(truckImages.getResourceId(index, 0));
        txt_name_truck.setText(truckNames[index]);
    }

    private String lastAddress = "";

    private String getCompleteAddressString(double LATITUDE, double LONGITUDE) {

        String strAdd = "";
        try {
            List<Address> addresses = new Geocoder(this, Locale.getDefault()).getFromLocation(LATITUDE, LONGITUDE, 1);
            if (addresses != null) {
                Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder("");
                for (int i = 0; i < returnedAddress.getMaxAddressLineIndex(); i++) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append(", ");
                }
                strAdd = strReturnedAddress.toString();
                if (!lastAddress.equals(strAdd)) {
                    getNearbyDrivers(LATITUDE + "", LONGITUDE + "");
                }
                lastAddress = strAdd;
                Log.w("address", "" + strAdd);
                if (returnedAddress.getAdminArea().equals("Odisha") || returnedAddress.getAdminArea().equals("Delhi")
                        || returnedAddress.getAdminArea().contains("Rajasthan")) {
                    mLocationText.setText(strReturnedAddress.toString());
                    return strAdd;
                } else {
                    strAdd = "";
                    MyApp.popMessage("Alert!", "Service is not available in your area.\nThis application is restricted to Odisha state only.\nThank you", getContext());
                    return strAdd;
                }
            }
            Log.w("address", "No Address returned!");
            return strAdd;
        } catch (Exception e) {
            e.printStackTrace();
            Log.w("address", "Cannot get Address!");
            return strAdd;
        }
    }

    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        MyApp.showMassage(getContext(), "GoogleApi Connection failed...");
    }

    public void handleNewLocation(Location location) {

        if (location != null) {
            try {
                if (!isFirstSet) {
                    sourceLocation = new LatLng(location.getLatitude(), location.getLongitude());
                    changeMap(location);
                    isFirstSet = true;
                    RequestParams pp = new RequestParams();
                    User u = MyApp.getApplication().readUser();
                    pp.put(AccessToken.USER_ID_KEY, u.getUser_id());
                    pp.put("api_key", u.getApi_key());
                    pp.put("u_lat", Double.valueOf(location.getLatitude()));
                    pp.put("u_lng", Double.valueOf(location.getLongitude()));
                    pp.put("u_device_token", MyApp.getSharedPrefString(AppConstants.DEVICE_TOKEN));
                    pp.put("u_device_type", "Android");
                    postCall(getContext(), AppConstants.BASE_URL + "updateuserprofile?", pp, "", 0);
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
        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(this).addApi(LocationServices.API).addOnConnectionFailedListener(new OnConnectionFailedListener() {
                public void onConnectionFailed(ConnectionResult connectionResult) {
                    MyApp.showMassage(getContext(), "Location error " + connectionResult.getErrorCode());
                }
            }).build();
            googleApiClient.connect();
            LocationRequest locationRequest = LocationRequest.create();
            locationRequest.setInterval(30000);
            locationRequest.setFastestInterval(5000);
            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
            builder.setAlwaysShow(true);
            LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build()).setResultCallback(new ResultCallback<LocationSettingsResult>() {
                public void onResult(LocationSettingsResult result) {
                    Status status = result.getStatus();
                    switch (status.getStatusCode()) {
                        case 6:
                            try {
                                status.startResolutionForResult(MainActivity.this, 44);
                                return;
                            } catch (SendIntentException e) {
                                e.printStackTrace();
                                return;
                            }
                        default:
                            return;
                    }
                }
            });
            return;
        }
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(30000);
        locationRequest.setFastestInterval(5000);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);
        LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build()).setResultCallback(new ResultCallback<LocationSettingsResult>() {
            public void onResult(LocationSettingsResult result) {
                Status status = result.getStatus();
                switch (status.getStatusCode()) {
                    case 6:
                        try {
                            status.startResolutionForResult(MainActivity.this, 44);
                            return;
                        } catch (SendIntentException e) {
                            e.printStackTrace();
                            return;
                        }
                    default:
                        return;
                }
            }
        });
    }

    private boolean isShownNoDriverDialog = true;

    public void onJsonObjectResponseReceived(JSONObject o, int callNumber) {
        if (callNumber != 1) {
            return;
        }
        if (o.optString("status").equals("OK")) {
            driversMap = new HashMap();
            NearbyDrivers nd = new Gson().fromJson(o.toString(), NearbyDrivers.class);
            if (nd.getResponse().size() == 0) {
                if (!isShownNoDriverDialog) {
                    isShownNoDriverDialog = true;
                    MyApp.popMessage("Message", "No driver available in your area, please try after some time.", getContext());
                }
                return;
            }
            for (int i = 0; i < nd.getResponse().size(); i++) {
                List<Response> list;
                if (driversMap.containsKey((nd.getResponse().get(i)).getCar_name())) {
                    list = (List) driversMap.get((nd.getResponse().get(i)).getCar_name());
                    list.add(nd.getResponse().get(i));
                    driversMap.put((nd.getResponse().get(i)).getCar_name(), list);
                } else {
                    list = new ArrayList();
                    list.add(nd.getResponse().get(i));
                    driversMap.put((nd.getResponse().get(i)).getCar_name(), list);
                }
            }
            onItemSelected(selectedTruckIndex);
        } else if (!isBookLater) {
            currentBookText = "No Driver...";
            locMarkertext.setText("No Driver...");
        }
    }

    private LatLngBounds adjustBoundsForMaxZoomLevel(LatLngBounds bounds) {
        LatLng sw = bounds.southwest;
        LatLng ne = bounds.northeast;
        double deltaLat = Math.abs((sw.latitude - sourceLocation.latitude) - (ne.latitude - sourceLocation.latitude));
        double deltaLon = Math.abs((sw.longitude - sourceLocation.longitude) - (ne.longitude - sourceLocation.longitude));
        LatLng latLng;
        LatLng ne2;
        LatLngBounds latLngBounds;
        if (deltaLat < 0.005d) {
            latLng = new LatLng(sw.latitude - (0.005d - (deltaLat / 2.0d)), sw.longitude);
            ne2 = new LatLng(ne.latitude + (0.005d - (deltaLat / 2.0d)), ne.longitude);
            latLngBounds = new LatLngBounds(latLng, ne2);
            ne = ne2;
            sw = latLng;
        } else if (deltaLon < 0.005d) {
            latLng = new LatLng(sw.latitude, sw.longitude - (0.005d - (deltaLon / 2.0d)));
            ne2 = new LatLng(ne.latitude, ne.longitude + (0.005d - (deltaLon / 2.0d)));
            latLngBounds = new LatLngBounds(latLng, ne2);
            ne = ne2;
            sw = latLng;
        }
        LatLngBounds.Builder displayBuilder = new LatLngBounds.Builder();
        displayBuilder.include(new LatLng(sourceLocation.latitude, sourceLocation.longitude));
        displayBuilder.include(new LatLng(sourceLocation.latitude + deltaLat, sourceLocation.longitude + deltaLon));
        displayBuilder.include(new LatLng(sourceLocation.latitude - deltaLat, sourceLocation.longitude - deltaLon));
        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(displayBuilder.build(), 100));
        mMap.setMaxZoomPreference(15.5f);
        return bounds;
    }

    public void onJsonArrayResponseReceived(JSONArray a, int callNumber) {
    }

    public void onErrorReceived(String error) {
    }

    private String getMapsApiDirectionsUrl(LatLng origin, LatLng dest) {
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        return "https://maps.googleapis.com/maps/api/directions/" + "json" + "?" + (str_origin + "&" + ("destination=" + dest.latitude + "," + dest.longitude) + "&" + "sensor=false");
    }
}
