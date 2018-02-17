package cargo.floter.user;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.MotionEvent;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import cargo.floter.user.application.MyApp;
import cargo.floter.user.application.SingleInstance;
import cargo.floter.user.model.RateCard;

import org.json.JSONObject;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import cargo.floter.user.model.Version;
import cargo.floter.user.utils.AppConstants;
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

        if (!MyApp.getStatus("NewApp")) {
            MyApp.setStatus("NewApp", true);
            MyApp.setStatus(AppConstants.IS_LOGIN, false);
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... params) {
                    try {
                        FirebaseInstanceId.getInstance().deleteInstanceId();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return null;
                }

                @Override
                protected void onPostExecute(Void result) {
                    String token = FirebaseInstanceId.getInstance().getToken();
                    MyApp.setStatus(AppConstants.IS_LOGIN, false);
                }
            }.execute();
        }

    }

    private void getApiData() {
        AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(30000);
        client.post("http://floter.in/floterapi/index.php/carapi/getratecard", new JsonHttpResponseHandler() {

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
                FirebaseDatabase mFirebaseInstance = FirebaseDatabase.getInstance();
                mFirebaseInstance.getReference("user").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // whenever data at this location is updated.
                        String value = dataSnapshot.getValue(String.class);
                        PackageManager manager = getPackageManager();
                        PackageInfo info = null;
                        try {
                            info = manager.getPackageInfo(
                                    getPackageName(), 0);
                            String version = info.versionCode + "";
                            if (version.equals(value) /*&& !value.equals("24")*/) {
                                startActivity(new Intent(
                                        SplashActivity.this, LoginActivity.class
                                ));
                                finish();
                            } else if (value.equals("24")) {
                                MyApp.popMessageAndFinish("Floter", "Advanced work is under process," +
                                        " we will update you soon.\nThank you", SplashActivity.this);

                            } else {
                                AlertDialog.Builder b = new AlertDialog.Builder(SplashActivity.this);
                                b.setTitle("Update App");
                                b.setMessage("New version is available of the app, please update the app first to use" +
                                        " improved and better features of the app." +
                                        "\nThank you.")
                                        .setPositiveButton("Update", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface d, int which) {
                                                d.dismiss();
                                                startActivity(new Intent(
                                                        Intent.ACTION_VIEW,
                                                        Uri.parse("http://play.google.com/store/apps/details?id="
                                                                + getPackageName())));
                                                finish();
                                            }
                                        }).setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface d, int which) {
                                        d.dismiss();
                                        finish();
                                    }
                                }).create().show();
                            }
                        } catch (PackageManager.NameNotFoundException e) {
                            e.printStackTrace();
                        }

                        Log.d("myRef", "Value is: " + value);
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        Log.w("myRef", "Failed to read value.", error.toException());
                    }
                });

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