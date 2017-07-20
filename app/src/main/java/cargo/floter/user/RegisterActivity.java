package cargo.floter.user;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.gson.Gson;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

import cargo.floter.user.application.MyApp;
import cargo.floter.user.model.User;
import cargo.floter.user.utils.AppConstants;

public class RegisterActivity extends CustomActivity implements CustomActivity.ResponseCallback, GoogleApiClient.OnConnectionFailedListener {

    private Toolbar toolbar;
    private TextView txt_change, txt_phone, txt_terms;
    private EditText edt_fname, edt_lname, edt_email;
    private CallbackManager callbackManager;
    private GoogleApiClient mGoogleApiClient;
    private static int RC_SIGN_IN = 156;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        TextView mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        mTitle.setText("Create Account");
        actionBar.setTitle("");
        setupUiElements();
        setResponseListener(this);
//        AppEventsLogger.activateApp();
    }

    private void setupUiElements() {

        txt_change = (TextView) findViewById(R.id.txt_change);
        txt_phone = (TextView) findViewById(R.id.txt_phone);
        txt_terms = (TextView) findViewById(R.id.txt_terms);
        txt_terms.setPaintFlags(txt_terms.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        edt_fname = (EditText) findViewById(R.id.edt_fname);
        edt_lname = (EditText) findViewById(R.id.edt_lname);
        edt_email = (EditText) findViewById(R.id.edt_email);

        txt_phone.setText(getIntent().getStringExtra(AppConstants.EXTRA_1));
        setTouchNClick(R.id.txt_register);
        setTouchNClick(R.id.txt_change);
        setTouchNClick(R.id.btn_fb);
        setTouchNClick(R.id.btn_google);
        setTouchNClick(R.id.txt_terms);

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
//        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//                .requestIdToken(getString(R.string.default_web_client_id))
//                .requestEmail()
//                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        callbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {


            @Override
            public void onSuccess(LoginResult loginResult) {
                MyApp.spinnerStart(getContext(), getString(R.string.loading));
                GraphRequest request = GraphRequest.newMeRequest(
                        loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(
                                    JSONObject object,
                                    GraphResponse response) {
                                MyApp.spinnerStop();
                                String fb_id = null;
                                try {
                                    fb_id = object.getString("id");
                                    MyApp.setSharedPrefString(AppConstants.FB_ID, fb_id);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                try {
                                    if (MyApp.isConnectingToInternet(getContext())) {
                                        registerUser(object.getString("email"), object.getString("name").split(" ")[0], object.getString("name").split(" ")[1]);
                                        LoginManager.getInstance().logOut();
                                        object.getString("email");
                                    } else {
                                        MyApp.popMessage(getString(R.string.alert), getString(R.string.connect_working_internet), getContext());
                                    }

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                } catch (Exception e) {
                                    try {
                                        object.getString("email");
                                        registerUser(object.getString("email"), object.getString("name").split(" ")[0], object.getString("name").split(" ")[0]);
                                        LoginManager.getInstance().logOut();
                                    } catch (JSONException ee) {

                                    }

                                }
                            }
                        });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,link,email,gender");
                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }
        });

    }

    private void registerUser(String email, String firstName, String lastName) {
//        http://floter.in/floterapi/index.php/userapi/registration?u_fname=pawan&u_lname=yadav&u_email=testyd@gmail.com
        RequestParams p = new RequestParams();
        p.put("u_fname", firstName);
        p.put("u_lname", lastName);
        p.put("u_email", email);
        p.put("u_mobile", txt_phone.getText().toString());
        postCall(getContext(), AppConstants.BASE_URL + AppConstants.REGISTER, p, "Registering User...", 1);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        if (v.getId() == R.id.txt_register) {
            if (TextUtils.isEmpty(edt_fname.getText().toString())) {
                edt_fname.setError("Enter first name");
                return;
            }
            if (TextUtils.isEmpty(edt_lname.getText().toString())) {
                edt_lname.setError("Enter last name");
                return;
            }
            if (TextUtils.isEmpty(edt_email.getText().toString())) {
                edt_lname.setError("Enter email");
                return;
            }
            registerUser(edt_email.getText().toString(), edt_fname.getText().toString(), edt_lname.getText().toString());
        } else if (v == txt_change) {
            startActivity(new Intent(getContext(), LoginActivity.class));
            finishAffinity();
        } else if (v.getId() == R.id.btn_fb) {
            LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile", "user_friends", "email"));
        } else if (v == txt_terms) {
            startActivity(new Intent(RegisterActivity.this, WebActivity.class).putExtra(AppConstants.EXTRA_1, "Terms & Conditions")
                    .putExtra(AppConstants.EXTRA_2, 1));
        } else if (v.getId() == R.id.btn_google) {
            signIn();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
            return;
        }
        callbackManager.onActivityResult(requestCode, resultCode, data);

    }

    private void handleSignInResult(GoogleSignInResult result) {
        Log.d("Google sign in", "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();
            registerUser(acct.getEmail(), acct.getGivenName(), acct.getFamilyName());
            signOut();
            revokeAccess();
        } else {
            // Signed out, show unauthenticated UI.
            MyApp.showMassage(getContext(), "Failed...");
        }
    }

    // [START revokeAccess]
    private void revokeAccess() {
        Auth.GoogleSignInApi.revokeAccess(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {

                    }
                });
    }
    // [END revokeAccess]

    // [START signIn]
    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    // [END signIn]

    // [START signOut]
    private void signOut() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        // [START_EXCLUDE]

                        // [END_EXCLUDE]
                    }
                });
    }

    private Context getContext() {
        return RegisterActivity.this;
    }

    @Override
    public void onJsonObjectResponseReceived(JSONObject o, int callNumber) {
        if (o.optString("status").equals("OK") && o.optInt("code") == 200) {
            try {
                User u = new Gson().fromJson(o.getJSONObject("response").toString(), User.class);
                MyApp.getApplication().writeUser(u);
                MyApp.setStatus(AppConstants.FIRST_OFFER, true);
                startActivity(new Intent(getContext(), MainActivity.class));
                MyApp.setStatus(AppConstants.IS_LOGIN, true);
                finishAffinity();
            } catch (JSONException e) {
                MyApp.popMessage("Error!", o.optString("message") + "_" + o.optInt("code"), getContext());
                e.printStackTrace();
            }

        } else {
            MyApp.popMessage("Error!", o.optString("message"), getContext());
        }
    }

    @Override
    public void onJsonArrayResponseReceived(JSONArray a, int callNumber) {

    }

    @Override
    public void onErrorReceived(String error) {
        MyApp.popMessage("Error!", error, getContext());
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        MyApp.showMassage(getContext(), "Connection not stabilised with google");
    }

    @Override
    protected void onResume() {
        super.onResume();
        MyApp.spinnerStop();
    }

    @Override
    public void onStart() {
        super.onStart();

//        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
//        if (opr.isDone()) {
//            // If the user's cached credentials are valid, the OptionalPendingResult will be "done"
//            // and the GoogleSignInResult will be available instantly.
//            Log.d("Google login", "Got cached sign-in");
//            GoogleSignInResult result = opr.get();
//            handleSignInResult(result);
//        } else {
//            // If the user has not previously signed in on this device or the sign-in has expired,
//            // this asynchronous branch will attempt to sign in the user silently.  Cross-device
//            // single sign-on will occur in this branch.
//            MyApp.spinnerStart(getContext(), "Please wait...");
//            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
//                @Override
//                public void onResult(GoogleSignInResult googleSignInResult) {
//                    MyApp.spinnerStop();
//                    handleSignInResult(googleSignInResult);
//                }
//            });
//        }
    }
}
