package cargo.floter.user;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.google.gson.Gson;
import com.loopj.android.http.RequestParams;
import cargo.floter.user.CustomActivity;
import cargo.floter.user.R;
import cargo.floter.user.adapter.CatagoryAdapter;
import cargo.floter.user.application.MyApp;
import cargo.floter.user.model.RestUser;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;


/**
 * Created by Abhishek on 20-04-2017.
 */

public class CategoryActivity extends CustomActivity implements CustomActivity.ResponseCallback {

    private RecyclerView rv_cat;
    private CatagoryAdapter adapter;
    private ArrayList<RestUser.Response> horizontalList;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catagory);
        rv_cat = (RecyclerView) findViewById(R.id.rc_cat);

        horizontalList = new ArrayList<>();

        setResponseListener(this);

        adapter = new CatagoryAdapter(this, horizontalList);

        LinearLayoutManager llm
                = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rv_cat.setLayoutManager(llm);
        rv_cat.setAdapter(adapter);
        loadCategory();


    }

    private void loadCategory() {
        RequestParams p = new RequestParams();
       /* http://floter.in/floterapi/index.php/goodstypeapi/getgoodstypes?api_key=ee059a1e2596c265fd61c44f1855875e*/
        p.put("api_key", "ee059a1e2596c265fd61c44f1855875e");
        postCall(this, "http://floter.in/floterapi/index.php/goodstypeapi/getgoodstypes?", p, "Please Wait...", 1);

    }


    @Override
    public void onJsonObjectResponseReceived(JSONObject o, int callNumber) {
        Log.d("response", o.toString());
        if (o.optString("status").equals("OK")) {
            //   RestUser u = new Gson().fromJson(o.toString(), RestUser.class);
//            MyApp.getApplication().writeUser(u);
            MyApp.showMassage(this, "Goods Selection success");
            RestUser u = new Gson().fromJson(o.toString(), RestUser.class);

            horizontalList.addAll(u.getResponse());
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onJsonArrayResponseReceived(JSONArray a, int callNumber) {

    }

    @Override
    public void onErrorReceived(String error) {

    }
}
