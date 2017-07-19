package cargo.floter.user;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.MotionEvent;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import cargo.floter.user.application.MyApp;
import cargo.floter.user.application.SingleInstance;
import cargo.floter.user.model.RateCard;

import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import cz.msebera.android.httpclient.Header;

/**
 * Created by SONI on 3/20/2017.
 */

public class SplashActivity extends CustomActivity {
    private static final int SPLASH_DURATION_MS = 500;

    private Handler mHandler = new Handler();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);
        getApiData();
//        getHashKey();

    }

    private void getApiData() {
        AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(30000);
        client.post("http://stubuz.com/floterapi/index.php/carapi/getratecard", new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, final JSONObject o) {
                if (o.optString("status").equals("OK")) {
                    RateCard u = new Gson().fromJson(o.toString(), RateCard.class);
                    MyApp.getApplication().writeRateCard(u);
                    mHandler.postDelayed(mEndSplash, SPLASH_DURATION_MS);
                } else {
                    AlertDialog.Builder b = new AlertDialog.Builder(SplashActivity.this);
                    b.setTitle("Not Connected?").setMessage("Please check your internet connection and try again");
                    b.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface d, int i) {
                            getApiData();
                            d.dismiss();
                        }
                    }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface d, int i) {
                            d.dismiss();
                            finish();
                        }
                    }).create().show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                AlertDialog.Builder b = new AlertDialog.Builder(SplashActivity.this);
                b.setTitle("Not Connected?").setMessage("Please check your internet connection and try again");
                b.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface d, int i) {
                        getApiData();
                        d.dismiss();
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface d, int i) {
                        d.dismiss();
                        finish();
                    }
                }).create().show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                AlertDialog.Builder b = new AlertDialog.Builder(SplashActivity.this);
                b.setTitle("Not Connected?").setMessage("Please check your internet connection and try again");
                b.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface d, int i) {
                        getApiData();
                        d.dismiss();
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface d, int i) {
                        d.dismiss();
                        finish();
                    }
                }).create().show();
            }
        });
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
//        mEndSplash.run();
        return super.onTouchEvent(event);
    }

    private Runnable mEndSplash = new Runnable() {
        public void run() {
            if (!isFinishing()) {
                mHandler.removeCallbacks(this);

                startActivity(new Intent(
                        SplashActivity.this, LoginActivity.class
                ));

                finish();
            }
        }

        ;
    };

//    private void getHashKey() {
//        try {
//            PackageInfo info = getPackageManager().getPackageInfo(
//                    "cargo.floter.user",
//                    PackageManager.GET_SIGNATURES);
//            for (Signature signature : info.signatures) {
//                MessageDigest md = MessageDigest.getInstance("SHA");
//                md.update(signature.toByteArray());
//                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
//            }
//        } catch (PackageManager.NameNotFoundException e) {
//
//        } catch (NoSuchAlgorithmException e) {
//
//        }
//    }
}