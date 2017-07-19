package cargo.floter.user;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.loopj.android.http.RequestParams;
import cargo.floter.user.application.MyApp;
import cargo.floter.user.model.User;
import cargo.floter.user.utils.AppConstants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class VerifyPhoneNumberActivity extends CustomActivity implements CustomActivity.ResponseCallback {


    private ProgressBar progress;
    private EditText edt_verify_number;
    private TextView munual_code, txt_submit, txt_resend, txt_mobile;
    private String detailString = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_phone);
        munual_code = (TextView) findViewById(R.id.manually_code);
        txt_mobile = (TextView) findViewById(R.id.txt_mobile);
        txt_resend = (TextView) findViewById(R.id.txt_resend);
        txt_submit = (TextView) findViewById(R.id.txt_submit);

        detailString = getIntent().getStringExtra(AppConstants.EXTRA_2);

        setResponseListener(this);
        setTouchNClick(R.id.txt_submit);
        setTouchNClick(R.id.txt_resend);
        setTouchNClick(R.id.manually_code);
        txt_mobile.setText(getIntent().getStringExtra(AppConstants.EXTRA_1));
        munual_code.setText(Html.fromHtml("<u>Enter Manually</u>"));
        setupUiElements();

        IncomingSms.setListener(new IncomingSms.SmsListener() {
            @Override
            public void onNewOTP(final String otp) {

                progress.setVisibility(View.GONE);
                edt_verify_number.setText(otp);
                verifyOtp(otp);
            }
        });

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        IncomingSms.setListener(null);
    }

    private void verifyOtp(String otp) {
//        http://stubuz.com/floterapi/index.php/userapi/verifyotp?
// u_mobile=9015660024&otp=458954&detail=442aa50d-2041-11e7-929b-00163ef91450
        RequestParams p = new RequestParams();
        p.put("u_mobile", getIntent().getStringExtra(AppConstants.EXTRA_1));
        p.put("otp", otp);
        p.put("detail", detailString);
        postCall(getContext(), AppConstants.BASE_URL + AppConstants.VERIFY_OTP, p, "Please wait...", 2);
    }

    private void setupUiElements() {
        progress = (ProgressBar) findViewById(R.id.progress);
        edt_verify_number = (EditText) findViewById(R.id.edt_verify_number);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        if (v.getId() == R.id.txt_submit) {
            if (edt_verify_number.getText().toString().length() < 6) {
                MyApp.showMassage(getContext(), "Please enter otp");
                edt_verify_number.setError("Enter otp");
                return;
            }
            RequestParams p = new RequestParams();
            p.put("u_mobile", getIntent().getStringExtra(AppConstants.EXTRA_1));
            p.put("otp", edt_verify_number.getText().toString());
            p.put("detail", detailString);
            postCall(getContext(), AppConstants.BASE_URL + AppConstants.VERIFY_OTP, p, "Verifying otp...", 2);
        } else if (v == txt_resend) {
            RequestParams p = new RequestParams();
            p.put("u_mobile", getIntent().getStringExtra(AppConstants.EXTRA_1));
            postCall(getContext(), AppConstants.BASE_URL + AppConstants.SEND_OTP, p, "Requesting otp...", 1);
        } else if (v == munual_code) {
            progress.setVisibility(View.GONE);
            edt_verify_number.setEnabled(true);
            edt_verify_number.requestFocus();
        }
    }

    private Context getContext() {
        return VerifyPhoneNumberActivity.this;
    }

    @Override
    public void onJsonObjectResponseReceived(JSONObject o, int callNumber) {
        if (callNumber == 1) {
            if (o.optString("status").equals("OK") && o.optInt("code") == 200) {
//                MyApp.showMassage(getContext(), "Verify otp");
                try {
                    detailString = o.getJSONObject("response").optString("Details");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } else {
                MyApp.showMassage(getContext(), "Error_" + o.optInt("code"));
            }
        } else {
            if (o.optString("status").equals("OK") && o.optInt("code") == 200) {
                try {
                    JSONObject arr = o.getJSONObject("response");

                    try {
                        User u = new Gson().fromJson(o.getJSONObject("response").toString(), User.class);
                        MyApp.getApplication().writeUser(u);
                        startActivity(new Intent(getContext(), MainActivity.class));
                        MyApp.setStatus(AppConstants.IS_LOGIN, true);
                        finishAffinity();
                    } catch (JSONException e) {
                        MyApp.popMessage("Error!", o.optString("message") + "_" + o.optInt("code"), getContext());
                        e.printStackTrace();
                    }
//                    startActivity(new Intent(getContext(), MainActivity.class));
//                    finishAffinity();

                } catch (JSONException e) {
                    try {
                        JSONArray arr = o.getJSONArray("response");
                        if (arr.length() == 0) {
                            startActivity(new Intent(getContext(), RegisterActivity.class)
                                    .putExtra(AppConstants.EXTRA_1, txt_mobile.getText().toString()));
//                            finish();
                        }
                    } catch (JSONException e1) {
                        e1.printStackTrace();
                        MyApp.showMassage(getContext(), o.optString("message"));
                    }

//                    {"status":"OK","code":200,"message":null,"response":{"Status":"Error","Details":"OTP Mismatch","registration":1}}


                }
            }
        }
    }

    @Override
    public void onJsonArrayResponseReceived(JSONArray a, int callNumber) {

    }

    @Override
    public void onErrorReceived(String error) {
        MyApp.showMassage(getContext(), error);
    }
}
