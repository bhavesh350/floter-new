package cargo.floter.user;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog.Builder;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.appevents.AppEventsConstants;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.paytm.pgsdk.PaytmConstants;
import com.paytm.pgsdk.PaytmOrder;
import com.paytm.pgsdk.PaytmPGService;
import com.paytm.pgsdk.PaytmPaymentTransactionCallback;
import cargo.floter.user.CustomActivity.ResponseCallback;
import cargo.floter.user.application.MyApp;
import cargo.floter.user.model.Trip;
import cargo.floter.user.model.TripStatus;
import cargo.floter.user.utils.AppConstants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cz.msebera.android.httpclient.Header;

public class PaymentActivity extends CustomActivity implements ResponseCallback {
    private ProgressBar progress;
    private Toolbar toolbar;
    private TextView txt_cash;
    private TextView txt_paytm;
    private TextView txt_ride_counter;

    class C05901 implements OnClickListener {
        C05901() {
        }

        public void onClick(DialogInterface d, int i) {
            d.dismiss();
        }
    }

    class C05912 implements OnClickListener {
        C05912() {
        }

        public void onClick(DialogInterface d, int i) {
            d.dismiss();
        }
    }

    class C09654 implements PaytmPaymentTransactionCallback {

        class C11071 extends JsonHttpResponseHandler {
            C11071() {
            }

            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                MyApp.spinnerStop();
                Log.d("Response:", response.toString());
                try {
                    if (response.optString("status").equals("ok")) {
                        PaymentActivity.this.checkStatus(response.getJSONObject("response").toString());
                    } else {
                        MyApp.popMessage("Error", response.optString("error"), PaymentActivity.this);
                    }
                } catch (JSONException e) {
                    MyApp.showMassage(PaymentActivity.this, "Parsing error");
                    e.printStackTrace();
                }
            }

            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                MyApp.spinnerStop();
                if (statusCode == 0) {
                    MyApp.popMessage("Error!", "Time-out error occurred.\nPlease try again", PaymentActivity.this);
                } else {
                    MyApp.popMessage("Error!", "Error_" + statusCode + "Some error occurred", PaymentActivity.this);
                }
            }

            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                MyApp.spinnerStop();
                if (statusCode == 0) {
                    MyApp.popMessage("Error!", "Time-out error occurred.\nPlease try again", PaymentActivity.this);
                } else {
                    MyApp.popMessage("Error!", "Error_" + statusCode + "Some error occurred", PaymentActivity.this);
                }
            }
        }

        C09654() {
        }

        public void someUIErrorOccurred(String inErrorMessage) {
           Log.d("LOG", "UI Error Occur.");
            Toast.makeText(PaymentActivity.this.getApplicationContext(), " UI Error Occur. ", Toast.LENGTH_SHORT).show();
        }

        public void onTransactionResponse(Bundle inResponse) {
            Log.d("LOG", "Payment Transaction : " + inResponse);
            MyApp.spinnerStart(PaymentActivity.this, "Please wait...");
            String url = "http://stubuz.com/floterapi/paytm/getTransactionStatus.php";
            RequestParams p = new RequestParams();
            p.put(PaytmConstants.MERCHANT_ID, "FLOTER55912639344993");
            p.put("ORDER_ID", "ORD1111");
            Log.d("URl:", url);
            Log.d("Request:", p.toString());
            AsyncHttpClient client = new AsyncHttpClient();
            client.setTimeout(30000);
            client.get(url, p, new C11071());
        }

        public void networkNotAvailable() {
            Log.d("LOG", "UI Error Occur.");
            Toast.makeText(PaymentActivity.this.getApplicationContext(), " UI Error Occur. ", Toast.LENGTH_SHORT).show();
        }

        public void clientAuthenticationFailed(String inErrorMessage) {
            Log.d("LOG", "UI Error Occur.");
            Toast.makeText(PaymentActivity.this.getApplicationContext(), " Sever side Error " + inErrorMessage, Toast.LENGTH_SHORT).show();
        }

        public void onErrorLoadingWebPage(int iniErrorCode, String inErrorMessage, String inFailingUrl) {
            Toast.makeText(PaymentActivity.this.getApplicationContext(), " onErrorLoadingWebPage " + inErrorMessage, Toast.LENGTH_SHORT).show();
        }

        public void onBackPressedCancelTransaction() {
            Toast.makeText(PaymentActivity.this.getApplicationContext(), " onBackPressedCancelTransaction back pressed", Toast.LENGTH_SHORT).show();
        }

        public void onTransactionCancel(String inErrorMessage, Bundle inResponse) {
            Log.d("LOG", "Payment Transaction Failed " + inErrorMessage);
            Toast.makeText(PaymentActivity.this.getBaseContext(), "Payment Transaction Failed ",Toast.LENGTH_SHORT).show();
        }
    }

    class C09666 extends TypeToken<List<Trip>> {
        C09666() {
        }
    }

//    class C11063 extends JsonHttpResponseHandler {
//        C11063() {
//        }
//
//        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
//            MyApp.spinnerStop();
//            Log.d("Response:", response.toString());
////            try {
////                if (response.optString("status").equals("ok")) {
//////                    PaymentActivity.this.createPaymentFlow(response.getJSONObject("response").optString("CHECKSUMHASH"));
////                } else {
////                    MyApp.popMessage("Error", response.optString("error"), PaymentActivity.this);
////                }
////            } catch (JSONException e) {
////                MyApp.showMassage(PaymentActivity.this, "Parsing error");
////                e.printStackTrace();
////            }
//        }
//
//        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
//            super.onFailure(statusCode, headers, throwable, errorResponse);
//            MyApp.spinnerStop();
//            if (statusCode == 0) {
//                MyApp.popMessage("Error!", "Time-out error occurred.\nPlease try again", PaymentActivity.this);
//            } else {
//                MyApp.popMessage("Error!", "Error_" + statusCode + "Some error occurred", PaymentActivity.this);
//            }
//        }
//
//        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
//            MyApp.spinnerStop();
//            if (statusCode == 0) {
//                MyApp.popMessage("Error!", "Time-out error occurred.\nPlease try again", PaymentActivity.this);
//            } else {
//                MyApp.popMessage("Error!", "Error_" + statusCode + "Some error occurred", PaymentActivity.this);
//            }
//        }
//    }

    class C11085 extends JsonHttpResponseHandler {
        C11085() {
        }

        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
            MyApp.spinnerStop();
            Log.d("Response:", response.toString());
            try {
                if (response.optString("status").equals("ok")) {
                    PaymentActivity.this.checkStatus(response.getJSONObject("response").optString("CHECKSUMHASH"));
                } else {
                    MyApp.popMessage("Error", response.optString("error"), PaymentActivity.this);
                }
            } catch (JSONException e) {
                MyApp.showMassage(PaymentActivity.this, "Parsing error");
                e.printStackTrace();
            }
        }

        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
            super.onFailure(statusCode, headers, throwable, errorResponse);
            MyApp.spinnerStop();
            if (statusCode == 0) {
                MyApp.popMessage("Error!", "Time-out error occurred.\nPlease try again", PaymentActivity.this);
            } else {
                MyApp.popMessage("Error!", "Error_" + statusCode + "Some error occurred", PaymentActivity.this);
            }
        }

        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
            MyApp.spinnerStop();
            if (statusCode == 0) {
                MyApp.popMessage("Error!", "Time-out error occurred.\nPlease try again", PaymentActivity.this);
            } else {
                MyApp.popMessage("Error!", "Error_" + statusCode + "Some error occurred", PaymentActivity.this);
            }
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payments);
        this.toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(this.toolbar);
        setResponseListener(this);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle( "");
        setupUiElements();
        loadTodaysHistory();
    }

    private void setupUiElements() {
        this.progress = (ProgressBar) findViewById(R.id.progress);
        this.txt_ride_counter = (TextView) findViewById(R.id.txt_ride_counter);
    }

    private void loadTodaysHistory() {
        RequestParams p = new RequestParams();
        p.put(AccessToken.USER_ID_KEY, MyApp.getApplication().readUser().getUser_id());
        p.put("trip_date", MyApp.millsToDate2(System.currentTimeMillis()));
        postCall(this, AppConstants.BASE_URL_TRIP + "gettrips", p, "", 1);
    }

    public void onClick(View v) {
        super.onClick(v);
        Builder b;
        if (v == this.txt_paytm) {
            b = new Builder(this);
            b.setTitle( "Pay With Paytm");
            b.setIcon( R.mipmap.ic_launcher);
            b.setMessage( "At the end of the trip you will find a option to pay with Paytm.\nThank you!");
            b.setPositiveButton( "OK", new C05901());
            b.create().show();
        } else if (v == this.txt_cash) {
            b = new Builder(this);
            b.setTitle( "Pay With Cash");
            b.setIcon( R.mipmap.ic_launcher);
            b.setMessage( "At the end of the trip, your driver's phone will show you the amount to pay.");
            b.setPositiveButton( "OK", new C05912());
            b.create().show();
        }
    }

//    private void createPay() {
//        MyApp.spinnerStart(this, "Please wait...");
//        String url = "http://stubuz.com/floterapi/paytm/generateChecksum.php";
//        RequestParams p = new RequestParams();
//        p.put(PaytmConstants.MERCHANT_ID, "FLOTER55912639344993");
//        p.put("ORDER_ID", "ORD1111");
//        p.put("CUST_ID", "CUS41");
//        p.put("INDUSTRY_TYPE_ID", "Retail109");
//        p.put("CHANNEL_ID", "WAP");
//        p.put("TXN_AMOUNT", AppEventsConstants.EVENT_PARAM_VALUE_YES);
//        p.put("WEBSITE", "FLOTERWAP");
//        p.put("CALLBACK_URL", "https://pguat.paytm.com/paytmchecksum/paytmCallback.jsp");
//        p.put("EMAIL", "bsoni@mail.com");
//        p.put("MOBILE_NO", "9999999999");
//        Log.d("URl:", url);
//        Log.d("Request:", p.toString());
//        AsyncHttpClient client = new AsyncHttpClient();
//        client.setTimeout(30000);
//        client.get(url, p, new C11063());
//    }

//    private void createPaymentFlow(String checksum) {
//        PaytmPGService Service = PaytmPGService.getProductionService();
//        Map<String, String> paramMap = new HashMap();
//        paramMap.put("ORDER_ID", "ORD1111");
//        paramMap.put(PaytmConstants.MERCHANT_ID, "FLOTER55912639344993");
//        paramMap.put("CUST_ID", "CUS41");
//        paramMap.put("CHANNEL_ID", "WAP");
//        paramMap.put("INDUSTRY_TYPE_ID", "Retail109");
//        paramMap.put("WEBSITE", "FLOTERWAP");
//        paramMap.put("TXN_AMOUNT", AppEventsConstants.EVENT_PARAM_VALUE_YES);
//        paramMap.put("CALLBACK_URL", "https://pguat.paytm.com/paytmchecksum/paytmCallback.jsp");
//        paramMap.put("EMAIL", "bsoni@mail.com");
//        paramMap.put("MOBILE_NO", "9999999999");
//        paramMap.put("CHECKSUMHASH", checksum);
//        Service.initialize(new PaytmOrder(paramMap), null);
//        Service.startPaymentTransaction(this, true, true, new C09654());
//    }

    private Context getContext() {
        return this;
    }

    private void checkStatus(String json) {
        MyApp.spinnerStart(this, "Validating Payment...");
        String url = "https://secure.paytm.in/oltp/HANDLER_INTERNAL/getTxnStatus";
        RequestParams p = new RequestParams();
        p.put("JsonData", json);
        Log.d("URl:", url);
        Log.d("Request:", p.toString());
        AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(30000);
        client.get(url, p, new C11085());
    }

    public void onJsonObjectResponseReceived(JSONObject o, int callNumber) {
        this.progress.setVisibility(View.GONE);
        if (callNumber == 1 && o.optString("status").equals("OK")) {
            try {
                List<Trip> trips =  new Gson().fromJson(o.getJSONArray("response").toString(), new C09666().getType());
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

    public void onJsonArrayResponseReceived(JSONArray a, int callNumber) {
    }

    public void onErrorReceived(String error) {
        this.progress.setVisibility(View.GONE);
    }
}
