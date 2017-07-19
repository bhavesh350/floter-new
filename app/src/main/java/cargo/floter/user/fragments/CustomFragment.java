package cargo.floter.user.fragments;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import cargo.floter.user.CustomActivity;
import cargo.floter.user.R;
import cargo.floter.user.application.MyApp;
import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;
import java.io.UnsupportedEncodingException;
import org.json.JSONArray;
import org.json.JSONObject;

public class CustomFragment extends Fragment implements OnClickListener {
    private ResponseCallback responseCallback;

    public interface ResponseCallback {
        void onErrorReceived(String str);

        void onJsonArrayResponseReceived(JSONArray jSONArray, int i);

        void onJsonObjectResponseReceived(JSONObject jSONObject, int i);
    }

    class C11101 extends JsonHttpResponseHandler {
        C11101() {
        }

        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
            CustomFragment.this.responseCallback.onJsonObjectResponseReceived(response, 0);
            Log.d("Response:", response.toString());
        }

        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject jsonObject) {
            MyApp.spinnerStop();
            CustomFragment.this.responseCallback.onErrorReceived(CustomFragment.this.getString(R.string.something_wrong));
        }

        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
            CustomFragment.this.responseCallback.onErrorReceived(CustomFragment.this.getString(R.string.something_wrong));
        }
    }

    public void setResponseListener(ResponseCallback responseCallback) {
        this.responseCallback = responseCallback;
    }

    public View setTouchNClick(View v) {
        v.setOnClickListener(this);
        v.setOnTouchListener(CustomActivity.TOUCH);
        return v;
    }

    public void postCallJsonObject(Context c, String url, JSONObject params, String loadingMsg) {
        Log.d("URl:", url);
        Log.d("Request:", params.toString());
        StringEntity entity = null;
        try {
            entity = new StringEntity(params.toString());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(30000);
        client.post(c, url, entity, RequestParams.APPLICATION_JSON, new C11101());
    }

    public void postCall(Context c, String url, RequestParams p, String loadingMsg, final int callNumber) {
        if (!TextUtils.isEmpty(loadingMsg)) {
            MyApp.spinnerStart(c, loadingMsg);
        }
        Log.d("URl:", url);
        Log.d("Request:", p.toString());
        AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(30000);
        client.post(url, p, new JsonHttpResponseHandler() {
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                MyApp.spinnerStop();
                Log.d("Response:", response.toString());
                try {
                    CustomFragment.this.responseCallback.onJsonObjectResponseReceived(response, callNumber);
                } catch (Exception e) {
                    CustomFragment.this.responseCallback.onErrorReceived("No data available");
                }
            }

            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                MyApp.spinnerStop();
                if (statusCode == 0) {
                    try {
                        CustomFragment.this.responseCallback.onErrorReceived(CustomFragment.this.getString(R.string.timeout));
                        return;
                    } catch (IllegalStateException e) {
                        return;
                    }
                }
                CustomFragment.this.responseCallback.onErrorReceived(CustomFragment.this.getString(R.string.something_wrong) + "_" + statusCode);
            }

            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                MyApp.spinnerStop();
                if (statusCode == 0) {
                    try {
                        CustomFragment.this.responseCallback.onErrorReceived(CustomFragment.this.getString(R.string.timeout));
                        return;
                    } catch (IllegalStateException e) {
                        return;
                    }
                }
                CustomFragment.this.responseCallback.onErrorReceived(CustomFragment.this.getString(R.string.something_wrong) + "_" + statusCode);
            }
        });
    }

    public void normalPostCall(Context c, String url, String loadingMsg, final int callNumber) {
        if (!TextUtils.isEmpty(loadingMsg)) {
            MyApp.spinnerStart(c, loadingMsg);
        }
        Log.d("URl:", url);
        AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(30000);
        client.post(url, new JsonHttpResponseHandler() {
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                MyApp.spinnerStop();
                Log.d("Response:", response.toString());
                CustomFragment.this.responseCallback.onJsonObjectResponseReceived(response, callNumber);
            }

            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                MyApp.spinnerStop();
                if (statusCode == 0) {
                    CustomFragment.this.responseCallback.onErrorReceived(CustomFragment.this.getString(R.string.timeout));
                } else {
                    CustomFragment.this.responseCallback.onErrorReceived(CustomFragment.this.getString(R.string.something_wrong) + "_" + statusCode);
                }
            }

            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                MyApp.spinnerStop();
                if (statusCode == 0) {
                    CustomFragment.this.responseCallback.onErrorReceived(CustomFragment.this.getString(R.string.timeout));
                } else {
                    CustomFragment.this.responseCallback.onErrorReceived(CustomFragment.this.getString(R.string.something_wrong) + "_" + statusCode);
                }
            }
        });
    }

    public void onClick(View v) {
    }
}
