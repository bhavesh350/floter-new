package cargo.floter.user;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.google.firebase.iid.FirebaseInstanceId;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;

import cargo.floter.user.application.MyApp;
import cargo.floter.user.model.User;
import cargo.floter.user.utils.AppConstants;

public class ProfileActivity extends CustomActivity implements CustomActivity.ResponseCallback {
    private TextView info_email;
    private TextView info_mobile;
    private TextView info_name;
    private Toolbar toolbar;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_info);
        setResponseListener(this);
        this.toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(this.toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle((CharSequence) "");
        User u = MyApp.getApplication().readUser();
        this.info_mobile = (TextView) findViewById(R.id.info_mobile);
        this.info_email = (TextView) findViewById(R.id.info_email);
        this.info_name = (TextView) findViewById(R.id.info_name);
        this.info_mobile.setText(u.getU_mobile());
        this.info_email.setText(u.getU_email());
        this.info_name.setText(u.getU_fname() + " " + u.getU_lname());
        setTouchNClick(R.id.logout);
        setTouchNClick(R.id.txt_edt);
    }

    public void editProfile(View v) {
    }

    public void onClick(View v) {
        super.onClick(v);
        if (v.getId() == R.id.logout) {
            RequestParams pp = new RequestParams();
            User u = MyApp.getApplication().readUser();
            pp.put(AccessToken.USER_ID_KEY, u.getUser_id());
            pp.put("api_key", u.getApi_key());
            pp.put("u_lat", "0.0");
            pp.put("u_lng", "0.0");
            postCall(ProfileActivity.this, AppConstants.BASE_URL + "updateuserprofile?", pp, "", 0);


            MyApp.spinnerStart(ProfileActivity.this, "Logging you out...");
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
                    MyApp.spinnerStop();
                    String token = FirebaseInstanceId.getInstance().getToken();
                    Log.d("deviceToken", "deviceToken\n" + token);
                    MyApp.setStatus(AppConstants.IS_LOGIN, false);
                    startActivity(new Intent(ProfileActivity.this, LoginActivity.class));
                    finishAffinity();
                }
            }.execute();


        }
    }

    @Override
    public void onJsonObjectResponseReceived(JSONObject o, int callNumber) {

    }

    @Override
    public void onJsonArrayResponseReceived(JSONArray a, int callNumber) {

    }

    @Override
    public void onErrorReceived(String error) {

    }
}
