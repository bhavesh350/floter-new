package cargo.floter.user;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by SONI on 5/12/2017.
 */

public class TripUpdateBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String value = intent.getStringExtra("TYPE");
        if (value.equals("TRIP_ACCEPTED")) {
            Intent i = new Intent(context, BookTripActivity.class);
//                i.setClassName("com.test", "com.test.MainActivity");
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
//                isDriverSearched = true;
//                loader.stopProgress();
//                startActivity(new Intent(getContext(), OnTripActivity.class));
//                finish();
        }
    }
}
