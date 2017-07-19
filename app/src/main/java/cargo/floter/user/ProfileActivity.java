package cargo.floter.user;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import cargo.floter.user.application.MyApp;
import cargo.floter.user.model.User;
import cargo.floter.user.utils.AppConstants;

public class ProfileActivity extends CustomActivity {
    private TextView info_email;
    private TextView info_mobile;
    private TextView info_name;
    private Toolbar toolbar;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) R.layout.activity_edit_info);
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
            MyApp.setStatus(AppConstants.IS_LOGIN, false);
            startActivity(new Intent(this, LoginActivity.class));
            finishAffinity();
        }
    }
}
