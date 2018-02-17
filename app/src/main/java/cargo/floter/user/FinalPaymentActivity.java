package cargo.floter.user;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.RatingBar.OnRatingBarChangeListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.appevents.AppEventsConstants;
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
import cargo.floter.user.model.Payment;
import cargo.floter.user.model.Trip;
import cargo.floter.user.model.TripStatus;
import cargo.floter.user.utils.AppConstants;
import cz.msebera.android.httpclient.Header;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class FinalPaymentActivity extends CustomActivity implements ResponseCallback {
    private Button button_submit;
    private boolean isFromHistory = false;
    private RelativeLayout ll_feedback;
    private String orderId = "ORDER_";
    private String orderIdAppender = "1";
    private Payment payment = null;
    private RatingBar rating_bar;
    private SlideUp slideUp;
    private Trip currentTrip = null;
    private Toolbar toolbar;
    private String tripId = "";
    private TextView txt_from_location;
    private TextView txt_pay_cash;
    private TextView txt_pay_paytm;
    private TextView txt_payment;
    private TextView txt_rating_status;
    private TextView txt_to_location;
    private TextView txt_user_name;

    class C05682 implements OnRatingBarChangeListener {
        C05682() {
        }

        public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
            if (v <= 1.0f) {
                txt_rating_status.setText("Bad");
            } else if (v > 1.0f && ((double) v) < 2.5d) {
                txt_rating_status.setText("Below Average");
            } else if (((double) v) >= 2.5d && ((double) v) < 3.5d) {
                txt_rating_status.setText("Average");
            } else if (((double) v) < 3.5d || ((double) v) >= 4.5d) {
                txt_rating_status.setText("Excellent");
            } else {
                txt_rating_status.setText("Good");
            }
        }
    }

    class C09571 implements Listener {
        C09571() {
        }

        public void onSlide(float percent) {
        }

        public void onVisibilityChanged(int visibility) {
            if (visibility != 8) {
            }
        }
    }

    class C09585 implements PaytmPaymentTransactionCallback {

        class C11001 extends JsonHttpResponseHandler {
            C11001() {
            }

            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                MyApp.spinnerStop();
                Log.d("Response:", response.toString());
                try {
                    Log.d("Response:", response.getJSONObject("response").toString());
                    checkStatus(response.getJSONObject("response").toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                MyApp.spinnerStop();
                MyApp.popMessage("Error", "Payment did not processed\nPlease try again", getContext());
            }

            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                MyApp.spinnerStop();
                MyApp.popMessage("Error", "Payment did not processed\nPlease try again", getContext());
            }
        }

        C09585() {
        }

        public void someUIErrorOccurred(String inErrorMessage) {
            Log.d("LOG", "UI Error Occur.");
            MyApp.popMessage("Error!", "UI Error Occurred for the paytm payment flow, Do you want to pay with paytm?", getContext());
        }

        public void onTransactionResponse(Bundle inResponse) {
            if (!inResponse.getString("RESPMSG").contains("Successful")) {
                MyApp.popMessage("Error", inResponse.getString("RESPMSG"), getContext());
                return;
            }
            Log.d("LOG", "Payment Transaction : " + inResponse);
            MyApp.spinnerStart(getContext(), "Please wait...");
            String url = "http://floter.in/floterapi/paytm/getTransactionStatus.php";
            RequestParams p = new RequestParams();
            p.put(PaytmConstants.MERCHANT_ID, "FLOTER55912639344993");
            p.put("ORDER_ID", orderId);
            Log.d("URl:", url);
            Log.d("Request:", p.toString());
            AsyncHttpClient client = new AsyncHttpClient();
            client.setTimeout(30000);
            client.get(url, p, new C11001());
        }

        public void networkNotAvailable() {
            Log.d("LOG", "Network Error Occur.");
            MyApp.popMessage("Error!", "Network Error Occurred for the paytm payment flow.", getContext());
        }

        public void clientAuthenticationFailed(String inErrorMessage) {
            MyApp.popMessage("Error!", "Client authentication Error Occurred for the paytm payment flow.", getContext());
        }

        public void onErrorLoadingWebPage(int iniErrorCode, String inErrorMessage, String inFailingUrl) {
            MyApp.popMessage("Error!", "Some Error Occurred for the paytm payment flow.", getContext());
        }

        public void onBackPressedCancelTransaction() {
            MyApp.popMessage("Error!", "Payment cancelled for the paytm payment flow by you.", getContext());
        }

        public void onTransactionCancel(String inErrorMessage, Bundle inResponse) {
            MyApp.popMessage("Error!", "Paytm Transaction failed.", getContext());
        }
    }

    class C10983 extends JsonHttpResponseHandler {
        C10983() {
        }

        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
            MyApp.spinnerStop();
            Log.d("Response:", response.toString());
            if (response.optString("status").equals("OK")) {
                if (!isFromHistory) {
                    MyApp.setSharedPrefString("SHOW_PAY", "NO");
                    MyApp.setSharedPrefString(AppConstants.PAYBLE_TRIP_ID, "");
                }
                ll_feedback.setVisibility(View.VISIBLE);
                slideUp.show();
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

    class C10994 extends JsonHttpResponseHandler {
        C10994() {
        }

        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
            MyApp.spinnerStop();
            Log.d("Response:", response.toString());
            try {
                if (response.optString("status").equals("ok")) {
                    createPaymentFlow(response.getJSONObject("response").optString("CHECKSUMHASH"));
                } else {
                    MyApp.popMessage("Error", response.optString("error"), getContext());
                }
            } catch (JSONException e) {
                MyApp.showMassage(getContext(), "Parsing error");
                e.printStackTrace();
            }
        }

        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
            super.onFailure(statusCode, headers, throwable, errorResponse);
            MyApp.spinnerStop();
            if (statusCode == 0) {
                MyApp.popMessage("Error!", "Time-out error occurred.\nPlease try again", getContext());
            } else {
                MyApp.popMessage("Error!", "Error_" + statusCode + "Some error occurred", getContext());
            }
        }

        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
            MyApp.spinnerStop();
            if (statusCode == 0) {
                MyApp.popMessage("Error!", "Time-out error occurred.\nPlease try again", getContext());
            } else {
                MyApp.popMessage("Error!", "Error_" + statusCode + "Some error occurred", getContext());
            }
        }
    }

    class C11016 extends JsonHttpResponseHandler {
        C11016() {
        }

        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
            MyApp.spinnerStop();
            Log.d("Response:", response.toString());
            if (response.optString(PaytmConstants.STATUS).equals("TXN_SUCCESS")) {
                updatePaymentApi(response.optString(PaytmConstants.TRANSACTION_ID), response.optString(PaytmConstants.ORDER_ID));
                return;
            }
            ll_feedback.setVisibility(View.VISIBLE);
            slideUp.show();
        }

        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
            super.onFailure(statusCode, headers, throwable, errorResponse);
            MyApp.spinnerStop();
            ll_feedback.setVisibility(View.VISIBLE);
            slideUp.show();
        }

        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
            MyApp.spinnerStop();
            ll_feedback.setVisibility(View.VISIBLE);
            slideUp.show();
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_final_payments);
        this.toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(this.toolbar);
        setResponseListener(this);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("");
        this.isFromHistory = getIntent().getBooleanExtra(AppConstants.EXTRA_1, false);
        if (this.isFromHistory) {
            this.tripId = getIntent().getStringExtra(AppConstants.EXTRA_2);
        } else {
            this.tripId = MyApp.getSharedPrefString(AppConstants.PAYBLE_TRIP_ID);
        }
        if (TextUtils.isEmpty(this.tripId)) {
            MyApp.setSharedPrefString("SHOW_PAY", "NO");
            MyApp.setSharedPrefString(AppConstants.PAYBLE_TRIP_ID, "");
            startActivity(new Intent(getContext(), MainActivity.class));
            finish();
            return;
        }
        this.orderId += this.tripId;
        setupUiElements();
        getTripDetails();
    }

    private void getTripDetails() {
        RequestParams p = new RequestParams();
        p.put("trip_id", this.tripId);
        if (MyApp.isConnectingToInternet(this)) {
            postCall(getContext(), AppConstants.BASE_URL_TRIP + "gettrips", p, "Getting trip detail...", 1);
            return;
        }
        MyApp.showMassage(getContext(), "Internet is not connected...");
        finish();
    }

    private void getPaymentDetails(String paymentId) {
        RequestParams p = new RequestParams();
        p.put("payment_id", paymentId);
        if (MyApp.isConnectingToInternet(this)) {
            postCall(getContext(), AppConstants.BASE_PAYMENT, p, "Getting payment details...", 2);
            return;
        }
        MyApp.showMassage(getContext(), "Internet is not connected...");
        finish();
    }

    private void setupUiElements() {
        this.txt_pay_paytm = (TextView) findViewById(R.id.txt_pay_paytm);
        this.txt_user_name = (TextView) findViewById(R.id.txt_user_name);
        this.txt_pay_cash = (TextView) findViewById(R.id.txt_pay_cash);
        this.txt_to_location = (TextView) findViewById(R.id.txt_to_location);
        this.txt_from_location = (TextView) findViewById(R.id.txt_from_location);
        this.txt_payment = (TextView) findViewById(R.id.txt_payment);
        this.button_submit = (Button) findViewById(R.id.button_submit);
        this.rating_bar = (RatingBar) findViewById(R.id.rating_bar);
        this.txt_rating_status = (TextView) findViewById(R.id.txt_rating_status);
        this.ll_feedback = (RelativeLayout) findViewById(R.id.ll_feedback);


        setTouchNClick(R.id.txt_pay_paytm);
        setTouchNClick(R.id.txt_pay_cash);
        setTouchNClick(R.id.button_submit);
        this.slideUp = new Builder(this.ll_feedback).withStartState(State.HIDDEN)
                .withStartGravity(Gravity.BOTTOM).build();
        this.slideUp = new Builder(this.ll_feedback).withListeners(new C09571())
                .withStartGravity(Gravity.BOTTOM).withGesturesEnabled(false).withStartState(State.HIDDEN).build();
        this.rating_bar.setOnRatingBarChangeListener(new C05682());
    }

    public void onClick(View v) {
        super.onClick(v);
        if (v == this.txt_pay_paytm) {
            if (this.payment == null) {
                MyApp.popMessageAndFinish("Alert!", "Failed to get payment information\nplease Please try again.", this);
            } else if (this.payment.getPayment_id().equals(AppEventsConstants.EVENT_PARAM_VALUE_NO)) {
                this.ll_feedback.setVisibility(View.VISIBLE);
                this.slideUp.show();
                MyApp.popMessage("Message", "Cannot process the payment through Paytm. Please try to pay with cash this time.\nThank you!", getContext());
            } else {
                this.orderId += this.orderIdAppender;
                this.orderIdAppender += AppEventsConstants.EVENT_PARAM_VALUE_YES;
                createPay(this.orderId, this.payment.getPay_amount());
            }
        } else if (v == this.txt_pay_cash) {
            sendPaymentDoneNotificationToDriver("CASH");
            MyApp.spinnerStart(getContext(), "Updating payment info...");
            String url = AppConstants.BASE_PAYMENT.replace("getpayments?", "updatepayment?");
            RequestParams p = new RequestParams();
            p.put("payment_id", this.payment.getPayment_id());
            p.put("trip_id", this.payment.getTrip_id());
            p.put("pay_status", "PAID");
            Log.d("URl:", url);
            Log.d("Request:", p.toString());
            AsyncHttpClient client = new AsyncHttpClient();
            client.setTimeout(30000);
            client.post(url, p, new C10983());
        } else if (v == this.button_submit) {
            RequestParams pp = new RequestParams();
            txt_user_name.setText(currentTrip.getDriver().getD_name());
            pp.put("driver_id", currentTrip.getDriver().getDriver_id());
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

    private void sendPaymentDoneNotificationToDriver(String payMode) {
        if (payment == null) {
            return;
        }
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams p = new RequestParams();
        p.put("message", "User made payment of Rs. " + this.payment.getPay_amount() + "\nthrough " + payMode);
        p.put("trip_id", this.tripId);
        p.put("trip_status", TripStatus.Finished.name());
        p.put("object", "\"{\"json\":\"json\"}\"");
        p.put("android", currentTrip.getDriver().getD_device_token());
        client.setTimeout(30000);
        client.post("http://floter.in/floterapi/push/DriverPushNotification.php?", p, new JsonHttpResponseHandler() {
            public void onSuccess(int statusCode, Header[] headers, String response) {
                Log.d("Response:", response.toString());
            }

            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                if (statusCode != 0) {
                }
            }
        });
    }

    private Context getContext() {
        return this;
    }

    public void onJsonObjectResponseReceived(JSONObject o, int callNumber) {
        if (o.optString("status").equals("OK") && callNumber == 1) {
            try {
                currentTrip = new Gson().fromJson(o.getJSONArray("response").getJSONObject(0).toString(), Trip.class);
                this.txt_from_location.setText(currentTrip.getTrip_from_loc());
                this.txt_to_location.setText(currentTrip.getTrip_to_loc());
                getPaymentDetails(currentTrip.getFloter_id());
                txt_user_name.setText(currentTrip.getDriver().getD_name());
            } catch (Exception e) {
            }
        } else if (o.optString("status").equals("OK") && callNumber == 2) {
            try {
                this.payment = new Gson().fromJson(o.getJSONArray("response").get(0).toString(), Payment.class);
                this.txt_payment.setText("Rs. " + this.payment.getPay_amount());
                if (this.payment.getPay_status().equals("PAID")) {
                    this.slideUp.show();
                    MyApp.popMessage("Message", "You are done with ", getContext());
                }
            } catch (JSONException e2) {
                e2.printStackTrace();
            }
        } else {
            MyApp.popMessage("Message", "Please pay cash. We cannot process Paytm process system now.", getContext());
        }
    }

    public void onJsonArrayResponseReceived(JSONArray a, int callNumber) {
    }

    public void onErrorReceived(String error) {
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
        client.get(url, p, new C10994());
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
        Service.startPaymentTransaction(this, true, true, new C09585());
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
        client.get(url, p, new C11016());
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
                if (response.optString("status").equals("OK")) {
                    ll_feedback.setVisibility(View.VISIBLE);
                    slideUp.show();
                    if (!isFromHistory) {
                        MyApp.setSharedPrefString("SHOW_PAY", "NO");
                        MyApp.setSharedPrefString(AppConstants.PAYBLE_TRIP_ID, "");
                        return;
                    }
                    return;
                }
                MyApp.showMassage(getContext(), "Retrying...");
                updatePaymentApi(txnId, orderId);
            }

            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                MyApp.spinnerStop();
                MyApp.showMassage(getContext(), "Retrying...");
                updatePaymentApi(txnId, orderId);
            }

            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                MyApp.spinnerStop();
                MyApp.showMassage(getContext(), "Retrying...");
                updatePaymentApi(txnId, orderId);
            }
        });
    }
}
