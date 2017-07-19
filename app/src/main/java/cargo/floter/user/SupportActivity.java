package cargo.floter.user;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import de.cketti.mailto.EmailIntentBuilder;

public class SupportActivity extends CustomActivity {

    private Toolbar toolbar;
    private TextView txt_mail_us;
    private TextView txt_call;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_support);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("");

        setupUiElements();
    }

    private void setupUiElements() {
        txt_mail_us = (TextView) findViewById(R.id.txt_mail_us);
        txt_call = (TextView) findViewById(R.id.txt_call);
        setTouchNClick(R.id.txt_mail_us);
        setTouchNClick(R.id.txt_call);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        if (v == txt_mail_us) {
            EmailIntentBuilder.from(SupportActivity.this)
                    .to("info@floter.net")
//                        .cc("bob@example.org")
//                        .bcc("charles@example.org")
                    .subject("Support mail")
                    .body("")
                    .start();
        } else if (v == txt_call) {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:+918895188575"));
            startActivity(intent);
        }
    }
}
