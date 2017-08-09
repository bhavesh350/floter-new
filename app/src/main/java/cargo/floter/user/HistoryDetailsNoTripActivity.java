package cargo.floter.user;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.facebook.appevents.AppEventsConstants;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.LatLngBounds.Builder;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import cargo.floter.user.application.MyApp;
import cargo.floter.user.application.SingleInstance;
import cargo.floter.user.model.Trip;
import cargo.floter.user.model.TripStatus;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

public class HistoryDetailsNoTripActivity extends CustomActivity implements OnMapReadyCallback {
    private GoogleMap mMap;
    private SupportMapFragment mapFragment;
    private Toolbar toolbar;
    private TextView trip_date;
    private TextView trip_id;
    private TextView trip_status;
    private TextView txt_destination_address;
    private TextView txt_source_address;
    private TextView txt_truck_name;

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
                        distance = (String) point.get("distance");
                    } else if (j == 1) {
                        duration = (String) point.get("duration");
                    } else {
                        points.add(new LatLng(Double.parseDouble((String) point.get("lat")), Double.parseDouble((String) point.get("lng"))));
                    }
                }
                polyLineOptions.addAll(points);
                polyLineOptions.width(10.0f);
                polyLineOptions.color(Color.parseColor("#156CB3"));
            }
            HistoryDetailsNoTripActivity.this.mMap.addPolyline(polyLineOptions);
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

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) R.layout.activity_no_trip);
        this.toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(this.toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("");
        setupUiElements();
    }

    private void setupUiElements() {
        Trip currentTrip = SingleInstance.getInstance().getHistoryTrip();

        if(currentTrip.getTrip_status().equals(TripStatus.Upcoming.name()))
        MyApp.popMessage("Message!",  currentTrip.getDriver().getD_name() + " has been assigned for this trip." +
                "\nYou can call him driver " + currentTrip.getDriver().getD_phone() +
                "\nIf any problem to contact with him you may contact to Floter support." +
                "\nThank you.", HistoryDetailsNoTripActivity.this);

        this.mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        this.mapFragment.getMapAsync(this);
        this.txt_truck_name = (TextView) findViewById(R.id.txt_truck_name);
        this.txt_destination_address = (TextView) findViewById(R.id.txt_destination_address);
        this.txt_source_address = (TextView) findViewById(R.id.txt_source_address);
        this.trip_status = (TextView) findViewById(R.id.trip_status);
        this.trip_date = (TextView) findViewById(R.id.trip_date);
        this.trip_id = (TextView) findViewById(R.id.trip_id);
        this.txt_truck_name.setText(currentTrip.getDriver().getCar_name());
        this.txt_destination_address.setText(currentTrip.getTrip_to_loc());
        this.txt_source_address.setText(currentTrip.getTrip_from_loc());
        this.trip_status.setText(currentTrip.getTrip_status());
        try {
            this.trip_date.setText(currentTrip.getTrip_modified());
        } catch (Exception e) {
        }

        this.trip_id.setText("FDA-" + currentTrip.getTrip_id());
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
        Trip currentTrip = SingleInstance.getInstance().getHistoryTrip();
        double sourceLat = Double.parseDouble(currentTrip.getTrip_scheduled_pick_lat());
        double sourceLng = Double.parseDouble(currentTrip.getTrip_scheduled_pick_lng());
        double destLat = Double.parseDouble(currentTrip.getTrip_scheduled_drop_lat());
        double destLng = Double.parseDouble(currentTrip.getTrip_scheduled_drop_lng());
        this.mMap.getUiSettings().setScrollGesturesEnabled(false);
        this.mMap.addMarker(new MarkerOptions().position(new LatLng(sourceLat, sourceLng)).icon(BitmapDescriptorFactory.fromResource(R.drawable.pin_blue)));
        this.mMap.addMarker(new MarkerOptions().position(new LatLng(destLat, destLng)).icon(BitmapDescriptorFactory.fromResource(R.drawable.pin_red)));
        Builder builder = new Builder();
        builder.include(new LatLng(sourceLat, sourceLng));
        builder.include(new LatLng(destLat, destLng));
        this.mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(adjustBoundsForMaxZoomLevel(builder.build()), 200));
        String url = getMapsApiDirectionsUrl(new LatLng(sourceLat, sourceLng), new LatLng(destLat, destLng));
        new ReadTask().execute(new String[]{url});
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

    private String getMapsApiDirectionsUrl(LatLng origin, LatLng dest) {
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        return "https://maps.googleapis.com/maps/api/directions/" + "json" + "?" + (str_origin + "&" + ("destination=" + dest.latitude + "," + dest.longitude) + "&" + "sensor=false");
    }
}
