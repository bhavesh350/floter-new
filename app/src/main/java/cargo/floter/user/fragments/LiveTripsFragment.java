//package cargo.floter.user.fragments;
//
//import android.content.Intent;
//import android.content.IntentSender.SendIntentException;
//import android.graphics.Color;
//import android.location.Location;
//import android.net.Uri;
//import android.os.AsyncTask;
//import android.os.Bundle;
//import android.os.Handler;
//import android.support.annotation.NonNull;
//import android.support.annotation.Nullable;
//import android.support.annotation.RequiresApi;
//import android.support.v4.app.ActivityCompat;
//import android.support.v4.content.ContextCompat;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.view.ViewTreeObserver.OnWindowFocusChangeListener;
//import android.widget.Button;
//import android.widget.RatingBar;
//import android.widget.RatingBar.OnRatingBarChangeListener;
//import android.widget.RelativeLayout;
//import android.widget.RelativeLayout.LayoutParams;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import com.facebook.appevents.AppEventsConstants;
//import com.google.android.gms.common.ConnectionResult;
//import com.google.android.gms.common.api.GoogleApiClient;
//import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
//import com.google.android.gms.common.api.ResultCallback;
//import com.google.android.gms.common.api.Status;
//import com.google.android.gms.location.LocationRequest;
//import com.google.android.gms.location.LocationServices;
//import com.google.android.gms.location.LocationSettingsRequest;
//import com.google.android.gms.location.LocationSettingsResult;
//import com.google.android.gms.maps.CameraUpdateFactory;
//import com.google.android.gms.maps.GoogleMap;
//import com.google.android.gms.maps.OnMapReadyCallback;
//import com.google.android.gms.maps.SupportMapFragment;
//import com.google.android.gms.maps.model.BitmapDescriptorFactory;
//import com.google.android.gms.maps.model.CameraPosition;
//import com.google.android.gms.maps.model.LatLng;
//import com.google.android.gms.maps.model.LatLngBounds;
//import com.google.android.gms.maps.model.Marker;
//import com.google.android.gms.maps.model.MarkerOptions;
//import com.google.android.gms.maps.model.PolylineOptions;
//import com.loopj.android.http.RequestParams;
//import com.mancj.slideup.SlideUp;
//import com.mancj.slideup.SlideUp.Builder;
//import com.mancj.slideup.SlideUp.Listener;
//import com.mancj.slideup.SlideUp.State;
//import cargo.floter.user.R;
//import cargo.floter.user.CancelTripActivity;
//import cargo.floter.user.MainActivity;
//import cargo.floter.user.application.MyApp;
//import cargo.floter.user.fragments.CustomFragment.ResponseCallback;
//import cargo.floter.user.model.Trip;
//import cargo.floter.user.model.TripStatus;
//import cargo.floter.user.utils.AppConstants;
//import cargo.floter.user.utils.LocationProvider;
//import cargo.floter.user.utils.LocationProvider.LocationCallback;
//import cargo.floter.user.utils.LocationProvider.PermissionCallback;
//
//import cz.msebera.android.httpclient.protocol.HTTP;
//
//import java.io.BufferedReader;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.InputStreamReader;
//import java.net.HttpURLConnection;
//import java.net.URL;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//
//import org.json.JSONArray;
//import org.json.JSONObject;
//
//public class LiveTripsFragment extends CustomFragment implements ResponseCallback, OnMapReadyCallback, OnConnectionFailedListener, LocationCallback, PermissionCallback {
//    private float bearing = 0.0f;
//    private Button button_submit;
//    private Location currentLocation = null;
//    private Trip currentTrip;
//    private Marker destMarker = null;
//    private Marker driverMarker = null;
//    private GoogleApiClient googleApiClient;
//    private boolean isOnceMarkerSet = false;
//    private RelativeLayout ll_feedback;
//    private LocationProvider locationProvider;
//    private Handler mHandler = new Handler();
//    private GoogleMap mMap;
//    private Runnable mUpdateDriverLocationToShow = new C06138();
//    private SupportMapFragment mapFragment;
//    private double newlat = 0.0d;
//    private double newlong = 0.0d;
//    private double oldlat = 0.0d;
//    private double oldlong = 0.0d;
//    private RatingBar rating_bar;
//    private TextView share_trip;
//    private SlideUp slideUp;
//    private Marker sourceMarker = null;
//    private String tripId = "";
//    private TextView txt_call_driver;
//    private TextView txt_cancel_trip;
//    private TextView txt_driver_name;
//    private TextView txt_from;
//    private TextView txt_rating_status;
//    private TextView txt_to;
//    private TextView txt_user_name;
//
//    class C06101 implements Runnable {
//        C06101() {
//        }
//
//        public void run() {
//            if (LiveTripsFragment.this.currentLocation == null) {
//                LiveTripsFragment.this.locationProvider = new LocationProvider(LiveTripsFragment.this.getActivity(), LiveTripsFragment.this, LiveTripsFragment.this);
//                LiveTripsFragment.this.locationProvider.connect();
//            }
//        }
//    }
//
//    class C06113 implements OnRatingBarChangeListener {
//        C06113() {
//        }
//
//        public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
//            if (v <= 1.0f) {
//                LiveTripsFragment.this.txt_rating_status.setText("Bad");
//            } else if (v > 1.0f && ((double) v) < 2.5d) {
//                LiveTripsFragment.this.txt_rating_status.setText("Below Average");
//            } else if (((double) v) >= 2.5d && ((double) v) < 3.5d) {
//                LiveTripsFragment.this.txt_rating_status.setText("Average");
//            } else if (((double) v) < 3.5d || ((double) v) >= 4.5d) {
//                LiveTripsFragment.this.txt_rating_status.setText("Excellent");
//            } else {
//                LiveTripsFragment.this.txt_rating_status.setText("Good");
//            }
//        }
//    }
//
////    class C06124 implements OnWindowFocusChangeListener {
////        C06124() {
////        }
////
////        public void onWindowFocusChanged(boolean hasFocus) {
////            if (MyApp.getSharedPrefString("SHOW_PAY").equals("YES")) {
////                MyApp.setSharedPrefString("SHOW_PAY", "NO");
////                if (LiveTripsFragment.this.currentTrip.getTrip_status().equals(TripStatus.Finished.name()) || LiveTripsFragment.this.currentTrip.getTrip_status().equals(TripStatus.Unloading.name())) {
////                    LiveTripsFragment.this.showFeedbackForm();
////                }
////            }
////        }
////    }
//
//    class C06138 implements Runnable {
//        C06138() {
//        }
//
//        public void run() {
//            RequestParams p = new RequestParams();
//            p.put("trip_id", LiveTripsFragment.this.currentTrip.getTrip_id());
//            LiveTripsFragment.this.postCall(LiveTripsFragment.this.getContext(), AppConstants.BASE_URL_TRIP + "gettrips", p, "", 15);
//            LiveTripsFragment.this.mHandler.postDelayed(this, 3000);
//        }
//    }
//
//    public class MapHttpConnection {
//        public String readUr(String mapsApiDirectionsUrl) throws IOException {
//            String data = "";
//            InputStream istream = null;
//            HttpURLConnection urlConnection = null;
//            try {
//                urlConnection = (HttpURLConnection) new URL(mapsApiDirectionsUrl).openConnection();
//                urlConnection.connect();
//                istream = urlConnection.getInputStream();
//                BufferedReader br = new BufferedReader(new InputStreamReader(istream));
//                StringBuffer sb = new StringBuffer();
//                String str = "";
//                while (true) {
//                    str = br.readLine();
//                    if (str == null) {
//                        break;
//                    }
//                    sb.append(str);
//                }
//                data = sb.toString();
//                br.close();
//            } catch (Exception e) {
//                Log.d("Exception url", e.toString());
//            } finally {
//                istream.close();
//                urlConnection.disconnect();
//            }
//            return data;
//        }
//    }
//
//    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {
//        private ParserTask() {
//        }
//
//        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {
//            List<List<HashMap<String, String>>> routes = null;
//            try {
//                routes = new PathJSONParser().parse(new JSONObject(jsonData[0]));
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            return routes;
//        }
//
//        protected void onPostExecute(List<List<HashMap<String, String>>> routes) {
//            PolylineOptions polyLineOptions = null;
//            String distance = "";
//            String duration = "";
//            for (int i = 0; i < routes.size(); i++) {
//                ArrayList<LatLng> points = new ArrayList();
//                polyLineOptions = new PolylineOptions();
//                List<HashMap<String, String>> path = (List) routes.get(i);
//                for (int j = 0; j < path.size(); j++) {
//                    HashMap<String, String> point = (HashMap) path.get(j);
//                    if (j == 0) {
//                        distance = (String) point.get("distance");
//                    } else if (j == 1) {
//                        duration = (String) point.get("duration");
//                    } else {
//                        points.add(new LatLng(Double.parseDouble((String) point.get("lat")), Double.parseDouble((String) point.get("lng"))));
//                    }
//                }
//                polyLineOptions.addAll(points);
//                polyLineOptions.width(10.0f);
//                polyLineOptions.color(Color.parseColor("#156CB3"));
//            }
//            LiveTripsFragment.this.mMap.addPolyline(polyLineOptions);
//        }
//    }
//
//    public class PathJSONParser {
//        public List<List<HashMap<String, String>>> parse(JSONObject jObject) {
//            List<List<HashMap<String, String>>> routes = new ArrayList();
//            try {
//                JSONArray jRoutes = jObject.getJSONArray("routes");
//                for (int i = 0; i < jRoutes.length(); i++) {
//                    JSONArray jLegs = ((JSONObject) jRoutes.get(i)).getJSONArray("legs");
//                    List<HashMap<String, String>> path = new ArrayList();
//                    for (int j = 0; j < jLegs.length(); j++) {
//                        JSONObject jDistance = ((JSONObject) jLegs.get(j)).getJSONObject("distance");
//                        HashMap<String, String> hmDistance = new HashMap();
//                        hmDistance.put("distance", jDistance.getString("text"));
//                        JSONObject jDuration = ((JSONObject) jLegs.get(j)).getJSONObject("duration");
//                        HashMap<String, String> hmDuration = new HashMap();
//                        hmDuration.put("duration", jDuration.getString("text"));
//                        path.add(hmDistance);
//                        path.add(hmDuration);
//                        JSONArray jSteps = ((JSONObject) jLegs.get(j)).getJSONArray("steps");
//                        for (int k = 0; k < jSteps.length(); k++) {
//                            String polyline = "";
//                            List<LatLng> list = decodePoly((String) ((JSONObject) ((JSONObject) jSteps.get(k)).get("polyline")).get("points"));
//                            for (int l = 0; l < list.size(); l++) {
//                                HashMap<String, String> hm = new HashMap();
//                                hm.put("lat", Double.toString(((LatLng) list.get(l)).latitude));
//                                hm.put("lng", Double.toString(((LatLng) list.get(l)).longitude));
//                                path.add(hm);
//                            }
//                        }
//                        routes.add(path);
//                    }
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            return routes;
//        }
//
//        private List<LatLng> decodePoly(String encoded) {
//            List<LatLng> poly = new ArrayList();
//            int index = 0;
//            int len = encoded.length();
//            int lat = 0;
//            int lng = 0;
//            int b = 0;
//            while (index < len) {
//                int index2;
//                int shift = 0;
//                int result = 0;
//                while (true) {
//                    index2 = index + 1;
//                    b = encoded.charAt(index) - 63;
//                    result |= (b & 31) << shift;
//                    shift += 5;
//                    if (b < 32) {
//                        break;
//                    }
//                    index = index2;
//                }
//                lat += (result & 1) != 0 ? (result >> 1) ^ -1 : result >> 1;
//                shift = 0;
//                result = 0;
//                index = index2;
//                while (true) {
//                    index2 = index + 1;
//                    b = encoded.charAt(index) - 63;
//                    result |= (b & 31) << shift;
//                    shift += 5;
//                    if (b < 32) {
//                        break;
//                    }
//                    index = index2;
//                }
//                lng += (result & 1) != 0 ? (result >> 1) ^ -1 : result >> 1;
//                poly.add(new LatLng(((double) lat) / 100000.0d, ((double) lng) / 100000.0d));
//                index = index2;
//            }
//            return poly;
//        }
//    }
//
//    private class ReadTask extends AsyncTask<String, Void, String> {
//        private ReadTask() {
//        }
//
//        protected String doInBackground(String... url) {
//            String data = "";
//            try {
//                data = new MapHttpConnection().readUr(url[0]);
//            } catch (Exception e) {
//                Log.d("Background Task", e.toString());
//            }
//            return data;
//        }
//
//        protected void onPostExecute(String result) {
//            super.onPostExecute(result);
//            new ParserTask().execute(new String[]{result});
//        }
//    }
//
//    class C09732 implements Listener {
//        C09732() {
//        }
//
//        public void onSlide(float percent) {
//        }
//
//        public void onVisibilityChanged(int visibility) {
//            if (visibility != 8) {
//            }
//        }
//    }
//
//    class C09745 implements OnConnectionFailedListener {
//        C09745() {
//        }
//
//        public void onConnectionFailed(ConnectionResult connectionResult) {
//            MyApp.showMassage(LiveTripsFragment.this.getContext(), "Location error " + connectionResult.getErrorCode());
//        }
//    }
//
//    class C09756 implements ResultCallback<LocationSettingsResult> {
//        C09756() {
//        }
//
//        public void onResult(LocationSettingsResult result) {
//            Status status = result.getStatus();
//            switch (status.getStatusCode()) {
//                case 6:
//                    try {
//                        status.startResolutionForResult(LiveTripsFragment.this.getActivity(), 44);
//                        return;
//                    } catch (SendIntentException e) {
//                        e.printStackTrace();
//                        return;
//                    }
//                default:
//                    return;
//            }
//        }
//    }
//
//    class C09767 implements ResultCallback<LocationSettingsResult> {
//        C09767() {
//        }
//
//        public void onResult(LocationSettingsResult result) {
//            Status status = result.getStatus();
//            switch (status.getStatusCode()) {
//                case 6:
//                    try {
//                        status.startResolutionForResult(LiveTripsFragment.this.getActivity(), 44);
//                        return;
//                    } catch (SendIntentException e) {
//                        e.printStackTrace();
//                        return;
//                    }
//                default:
//                    return;
//            }
//        }
//    }
//
//    public static LiveTripsFragment newInstance(String tripId) {
//        LiveTripsFragment fragment = new LiveTripsFragment();
//        Bundle bundle = new Bundle(2);
//        bundle.putString("tripId", tripId);
//        fragment.setArguments(bundle);
//        return fragment;
//    }
//
//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        return inflater.inflate(R.layout.tab_track_live_trips, container, false);
//    }
//
//    @RequiresApi(api = 18)
//    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//        this.locationProvider = new LocationProvider(getActivity(), this, this);
//        this.mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
//        setResponseListener(this);
//        this.mapFragment.getMapAsync(this);
//        new Handler().postDelayed(new C06101(), 10000);
//        this.currentTrip = (Trip) MyApp.getApplication().readTrip().get(this.tripId);
//        this.txt_from = (TextView) view.findViewById(R.id.txt_from);
//        this.txt_to = (TextView) view.findViewById(R.id.txt_to);
//        this.txt_rating_status = (TextView) view.findViewById(R.id.txt_rating_status);
//        this.txt_call_driver = (TextView) view.findViewById(R.id.txt_call_driver);
//        this.rating_bar = (RatingBar) view.findViewById(R.id.rating_bar);
//        this.share_trip = (TextView) view.findViewById(R.id.share_trip);
//        this.txt_cancel_trip = (TextView) view.findViewById(R.id.txt_cancel_trip);
//        this.txt_driver_name = (TextView) view.findViewById(R.id.txt_driver_name);
//        this.txt_user_name = (TextView) view.findViewById(R.id.txt_user_name);
//        this.ll_feedback = (RelativeLayout) view.findViewById(R.id.ll_feedback);
//        this.button_submit = (Button) view.findViewById(R.id.button_submit);
//        setTouchNClick(this.txt_call_driver);
//        setTouchNClick(this.share_trip);
//        setTouchNClick(this.button_submit);
//        setTouchNClick(this.txt_cancel_trip);
//        this.txt_to.setText("To: " + this.currentTrip.getTrip_to_loc());
//        this.txt_from.setText("From: " + this.currentTrip.getTrip_from_loc());
//        this.txt_user_name.setText(this.currentTrip.getDriver().getD_name());
//        this.txt_driver_name.setText(this.currentTrip.getDriver().getD_name() + "\n" + this.currentTrip.getDriver().getTruck_reg_no());
//        this.slideUp = new Builder(this.ll_feedback).withStartState(State.HIDDEN).withStartGravity(80).build();
//        this.slideUp = new Builder(this.ll_feedback).withListeners(new C09732()).withStartGravity(80).withGesturesEnabled(false).withStartState(State.HIDDEN).build();
//        this.rating_bar.setOnRatingBarChangeListener(new C06113());
////        getView().getViewTreeObserver().addOnWindowFocusChangeListener(new C06124());
//    }
//
//    public void onClick(View v) {
//        super.onClick(v);
//        super.onClick(v);
//        if (v == this.txt_call_driver) {
//            Intent intent = new Intent("android.intent.action.DIAL");
//            intent.setData(Uri.parse("tel:" + this.currentTrip.getDriver().getD_phone()));
//            startActivity(intent);
//        } else if (v == this.share_trip) {
//            Intent sendIntent = new Intent();
//            sendIntent.setAction("android.intent.action.SEND");
//            sendIntent.putExtra("android.intent.extra.TEXT", "I am using " + getString(R.string.app_name) + " for my goods transport. You can track goods with the following link\nhttp://floter.in/floterweb/tracking/triptrack.php?trip_id=" + this.tripId);
//            sendIntent.putExtra("android.intent.extra.SUBJECT", getString(R.string.app_name) + " App !");
//            sendIntent.setType(HTTP.PLAIN_TEXT_TYPE);
//            startActivity(Intent.createChooser(sendIntent, getString(R.string.messenger_send_button_text)));
//        } else if (v == this.txt_cancel_trip) {
//            startActivity(new Intent(getContext(), CancelTripActivity.class).putExtra(AppConstants.EXTRA_1, this.currentTrip.getTrip_id()).putExtra(AppConstants.EXTRA_2, this.currentTrip.getDriver().getD_device_token()));
//        } else if (v == this.button_submit) {
//            RequestParams pp = new RequestParams();
//            pp.put("driver_id", this.currentTrip.getDriver().getDriver_id());
//            pp.put("api_key", "ee059a1e2596c265fd61c44f1855875e");
//            pp.put("d_rating", this.rating_bar.getRating() + "");
//            postCall(getContext(), AppConstants.BASE_URL + "updatedriverprofile?", pp, "", 10);
//            HashMap<String, Trip> tripHashMap = MyApp.getApplication().readTrip();
//            if (tripHashMap.containsKey(this.tripId)) {
//                tripHashMap.remove(this.tripId);
//                MyApp.getApplication().writeTrip(tripHashMap);
//            }
//            startActivity(new Intent(getContext(), MainActivity.class));
//            getActivity().finishAffinity();
//        }
//    }
//
//    public void onStop() {
//        super.onStop();
//        this.locationProvider.disconnect();
//    }
//
//    public void onResume() {
//        super.onResume();
//        if (!MyApp.isLocationEnabled(getContext())) {
//            enableGPS();
//        }
//        try {
//            MyApp.cancelNotification(getContext(), Integer.parseInt(this.tripId));
//        } catch (Exception e) {
//            MyApp.cancelNotification(getContext(), 0);
//        }
//    }
//
//    public void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        this.tripId = getArguments().getString("tripId");
//    }
//
//    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
//    }
//
//    public void enableGPS() {
//        if (this.googleApiClient == null) {
//            this.googleApiClient = new GoogleApiClient.Builder(getActivity()).addApi(LocationServices.API).addOnConnectionFailedListener(new C09745()).build();
//            this.googleApiClient.connect();
//            LocationRequest locationRequest = LocationRequest.create();
//            locationRequest.setInterval(30000);
//            locationRequest.setFastestInterval(5000);
//            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
//            builder.setAlwaysShow(true);
//            LocationServices.SettingsApi.checkLocationSettings(this.googleApiClient, builder.build()).setResultCallback(new C09756());
//            return;
//        }
//        LocationRequest locationRequest = LocationRequest.create();
//        locationRequest.setInterval(30000);
//        locationRequest.setFastestInterval(5000);
//        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
//        builder.setAlwaysShow(true);
//        LocationServices.SettingsApi.checkLocationSettings(this.googleApiClient, builder.build()).setResultCallback(new C09767());
//    }
//
//    public void onMapReady(GoogleMap googleMap) {
//        this.mMap = googleMap;
//        View mapView = this.mapFragment.getView();
//        View locationButton = ((View) mapView.findViewById(Integer.parseInt(AppEventsConstants.EVENT_PARAM_VALUE_YES)).getParent()).findViewById(Integer.parseInt("2"));
//        if (!(mapView == null || mapView.findViewById(Integer.parseInt(AppEventsConstants.EVENT_PARAM_VALUE_YES)) == null)) {
//            LayoutParams layoutParams = (LayoutParams) locationButton.getLayoutParams();
//            layoutParams.addRule(10, 0);
//            layoutParams.addRule(12, -1);
//            layoutParams.setMargins(0, 0, 30, 200);
//        }
//        double sourceLat = Double.parseDouble(this.currentTrip.getTrip_scheduled_pick_lat());
//        double sourceLng = Double.parseDouble(this.currentTrip.getTrip_scheduled_pick_lng());
//        double destLat = Double.parseDouble(this.currentTrip.getTrip_scheduled_drop_lat());
//        double destLng = Double.parseDouble(this.currentTrip.getTrip_scheduled_drop_lng());
//        if (this.sourceMarker != null) {
//            this.sourceMarker.remove();
//        }
//        if (this.destMarker != null) {
//            this.destMarker.remove();
//        }
//        this.sourceMarker = this.mMap.addMarker(new MarkerOptions().position(new LatLng(sourceLat, sourceLng)).icon(BitmapDescriptorFactory.fromResource(R.drawable.pin_blue)));
//        this.destMarker = this.mMap.addMarker(new MarkerOptions().position(new LatLng(destLat, destLng)).icon(BitmapDescriptorFactory.fromResource(R.drawable.pin_red)));
//        this.destMarker.setSnippet(this.currentTrip.getTrip_from_loc());
//        this.sourceMarker.setSnippet(this.currentTrip.getTrip_to_loc());
//    }
//
//    public void handleNewLocation(Location location) {
//        this.currentLocation = location;
//        if (location != null) {
//            try {
//                changeMap(location);
//                if (!this.isOnceMarkerSet) {
//                    this.isOnceMarkerSet = true;
//                    LatLngBounds.Builder builder = new LatLngBounds.Builder();
//                    builder.include(this.sourceMarker.getPosition());
//                    builder.include(this.destMarker.getPosition());
//                    this.mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(adjustBoundsForMaxZoomLevel(builder.build()), 100));
//                    String url = getMapsApiDirectionsUrl(this.sourceMarker.getPosition(), this.destMarker.getPosition());
//                    new ReadTask().execute(new String[]{url});
//                    this.mHandler.postDelayed(this.mUpdateDriverLocationToShow, 3000);
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//    }
//
//    public void onStart() {
//        super.onStart();
//        this.locationProvider.connect();
//    }
//
//    public void handleManualPermission() {
//        ActivityCompat.requestPermissions(getActivity(), new String[]{"android.permission.ACCESS_FINE_LOCATION", "android.permission.ACCESS_COARSE_LOCATION"}, 1010);
//    }
//
//    /* JADX WARNING: inconsistent code. */
//    /* Code decompiled incorrectly, please refer to instructions dump. */
//    public void onJsonObjectResponseReceived(JSONObject r7, int r8) {
//        /*
//        r6 = this;
//        r3 = "status";
//        r3 = r7.optString(r3);
//        r4 = "OK";
//        r3 = r3.equals(r4);
//        if (r3 == 0) goto L_0x0076;
//    L_0x000e:
//        r3 = 15;
//        if (r8 != r3) goto L_0x0076;
//    L_0x0012:
//        r3 = new com.google.gson.Gson;	 Catch:{ JSONException -> 0x0077, Exception -> 0x007c }
//        r3.<init>();	 Catch:{ JSONException -> 0x0077, Exception -> 0x007c }
//        r4 = "response";
//        r4 = r7.getJSONArray(r4);	 Catch:{ JSONException -> 0x0077, Exception -> 0x007c }
//        r5 = 0;
//        r4 = r4.getJSONObject(r5);	 Catch:{ JSONException -> 0x0077, Exception -> 0x007c }
//        r4 = r4.toString();	 Catch:{ JSONException -> 0x0077, Exception -> 0x007c }
//        r5 = cargo.floter.user.model.Trip.class;
//        r2 = r3.fromJson(r4, r5);	 Catch:{ JSONException -> 0x0077, Exception -> 0x007c }
//        r2 = (cargo.floter.user.model.Trip) r2;	 Catch:{ JSONException -> 0x0077, Exception -> 0x007c }
//        r3 = r2.getDriver();	 Catch:{ JSONException -> 0x0077, Exception -> 0x007c }
//        r3 = r3.getD_lat();	 Catch:{ JSONException -> 0x0077, Exception -> 0x007c }
//        r4 = java.lang.Double.parseDouble(r3);	 Catch:{ JSONException -> 0x0077, Exception -> 0x007c }
//        r6.newlat = r4;	 Catch:{ JSONException -> 0x0077, Exception -> 0x007c }
//        r3 = r2.getDriver();	 Catch:{ JSONException -> 0x0077, Exception -> 0x007c }
//        r3 = r3.getD_lng();	 Catch:{ JSONException -> 0x0077, Exception -> 0x007c }
//        r4 = java.lang.Double.parseDouble(r3);	 Catch:{ JSONException -> 0x0077, Exception -> 0x007c }
//        r6.newlong = r4;	 Catch:{ JSONException -> 0x0077, Exception -> 0x007c }
//        if (r2 == 0) goto L_0x0060;
//    L_0x004c:
//        r3 = cargo.floter.user.application.MyApp.getApplication();	 Catch:{ Exception -> 0x007e, JSONException -> 0x0077 }
//        r1 = r3.readTrip();	 Catch:{ Exception -> 0x007e, JSONException -> 0x0077 }
//        r3 = r6.tripId;	 Catch:{ Exception -> 0x007e, JSONException -> 0x0077 }
//        r1.put(r3, r2);	 Catch:{ Exception -> 0x007e, JSONException -> 0x0077 }
//        r3 = cargo.floter.user.application.MyApp.getApplication();	 Catch:{ Exception -> 0x007e, JSONException -> 0x0077 }
//        r3.writeTrip(r1);	 Catch:{ Exception -> 0x007e, JSONException -> 0x0077 }
//    L_0x0060:
//        r3 = r2.getTrip_status();	 Catch:{ JSONException -> 0x0077, Exception -> 0x007c }
//        r4 = cargo.floter.user.model.TripStatus.Finished;	 Catch:{ JSONException -> 0x0077, Exception -> 0x007c }
//        r4 = r4.name();	 Catch:{ JSONException -> 0x0077, Exception -> 0x007c }
//        r3 = r3.equals(r4);	 Catch:{ JSONException -> 0x0077, Exception -> 0x007c }
//        if (r3 == 0) goto L_0x0073;
//    L_0x0070:
//        r6.showFeedbackForm();	 Catch:{ JSONException -> 0x0077, Exception -> 0x007c }
//    L_0x0073:
//        r6.rotatemarker();	 Catch:{ JSONException -> 0x0077, Exception -> 0x007c }
//    L_0x0076:
//        return;
//    L_0x0077:
//        r0 = move-exception;
//        r0.printStackTrace();
//        goto L_0x0076;
//    L_0x007c:
//        r3 = move-exception;
//        goto L_0x0076;
//    L_0x007e:
//        r3 = move-exception;
//        goto L_0x0060;
//        */
//        throw new UnsupportedOperationException("Method not decompiled: cargo.floter.user.fragments.LiveTripsFragment.onJsonObjectResponseReceived(org.json.JSONObject, int):void");
//    }
//
//    private String getMapsApiDirectionsUrl(LatLng origin, LatLng dest) {
//        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
//        return "https://maps.googleapis.com/maps/api/directions/" + "json" + "?" + (str_origin + "&" + ("destination=" + dest.latitude + "," + dest.longitude) + "&" + "sensor=false");
//    }
//
//    public void onJsonArrayResponseReceived(JSONArray a, int callNumber) {
//    }
//
//    public void onErrorReceived(String error) {
//    }
//
//    private void showFeedbackForm() {
//        this.ll_feedback.setVisibility(View.VISIBLE);
//        this.slideUp.show();
//    }
//
//    private void changeMap(Location location) {
//        if (this.mMap != null) {
//            this.mMap.getUiSettings().setZoomControlsEnabled(false);
//            CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(location.getLatitude(), location.getLongitude())).zoom(15.5f).tilt(0.0f).build();
//            if (ContextCompat.checkSelfPermission(getActivity(), "android.permission.ACCESS_FINE_LOCATION") == 0 || ContextCompat.checkSelfPermission(getContext(), "android.permission.ACCESS_COARSE_LOCATION") == 0) {
//                this.mMap.setMyLocationEnabled(true);
//            } else {
//                ActivityCompat.requestPermissions(getActivity(), new String[]{"android.permission.ACCESS_FINE_LOCATION", "android.permission.ACCESS_COARSE_LOCATION"}, 1010);
//            }
//            this.mMap.getUiSettings().setMyLocationButtonEnabled(true);
//            this.mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
//            return;
//        }
//        Toast.makeText(getActivity(), "Sorry! unable to create maps", Toast.LENGTH_SHORT).show();
//    }
//
//    public void rotatemarker() {
//        Location prevLoc = new Location("service Provider");
//        prevLoc.setLatitude(this.oldlat);
//        prevLoc.setLongitude(this.oldlong);
//        Location newLoc = new Location("service Provider");
//        newLoc.setLatitude(this.newlat);
//        newLoc.setLongitude(this.newlong);
//        this.bearing = prevLoc.bearingTo(newLoc);
//        if (this.driverMarker != null) {
//            this.driverMarker.remove();
//        }
//        this.driverMarker = this.mMap.addMarker(new MarkerOptions().position(new LatLng(this.newlat, this.newlong)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_truck_marker)).anchor(0.5f, 0.5f).rotation(this.bearing).flat(true));
//        this.oldlong = this.newlong;
//    }
//
//    private LatLngBounds adjustBoundsForMaxZoomLevel(LatLngBounds bounds) {
//        LatLng sw = bounds.southwest;
//        LatLng ne = bounds.northeast;
//        double deltaLat = Math.abs(sw.latitude - ne.latitude);
//        double deltaLon = Math.abs(sw.longitude - ne.longitude);
//        LatLng sw2;
//        LatLng ne2;
//        if (deltaLat < 0.005d) {
//            sw2 = new LatLng(sw.latitude - (0.005d - (deltaLat / 2.0d)), sw.longitude);
//            ne2 = new LatLng(ne.latitude + (0.005d - (deltaLat / 2.0d)), ne.longitude);
//            ne = ne2;
//            sw = sw2;
//            return new LatLngBounds(sw2, ne2);
//        } else if (deltaLon >= 0.005d) {
//            return bounds;
//        } else {
//            sw2 = new LatLng(sw.latitude, sw.longitude - (0.005d - (deltaLon / 2.0d)));
//            ne2 = new LatLng(ne.latitude, ne.longitude + (0.005d - (deltaLon / 2.0d)));
//            ne = ne2;
//            sw = sw2;
//            return new LatLngBounds(sw2, ne2);
//        }
//    }
//}
