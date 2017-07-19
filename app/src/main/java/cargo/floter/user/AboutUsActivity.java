package cargo.floter.user;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import cargo.floter.user.R;
import cargo.floter.user.utils.AppConstants;


public class AboutUsActivity extends CustomActivity {
    private Toolbar toolbar;
    private TextView txt_version;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("");

        setTouchNClick(R.id.txt_terms);
        setTouchNClick(R.id.txt_rate_us);
        setTouchNClick(R.id.txt_about);

        PackageInfo pInfo = null;
        try {
            pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        String version = pInfo.versionName;
        txt_version = (TextView) findViewById(R.id.txt_version);
        txt_version.setText("v"+version);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        if (v.getId() == R.id.txt_terms) {
            startActivity(new Intent(AboutUsActivity.this, WebActivity.class).putExtra(AppConstants.EXTRA_1, "Terms & Conditions")
                    .putExtra(AppConstants.EXTRA_2, 1));
        } else if (v.getId() == R.id.txt_rate_us) {
            rateApp();
        } else if (v.getId() == R.id.txt_about) {
            startActivity(new Intent(AboutUsActivity.this, WebActivity.class).putExtra(AppConstants.EXTRA_1, "About Us")
                    .putExtra(AppConstants.EXTRA_2, 2));
        }
    }

    private void rateApp() {

        Uri uri = Uri.parse("market://details?id=" + this.getPackageName());
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        // To count with Play market backstack, After pressing back button,
        // to taken back to our application, we need to add following flags
        // to intent.
        goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY
                | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        try {
            startActivity(goToMarket);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id="
                            + this.getPackageName())));
        }

    }
}
