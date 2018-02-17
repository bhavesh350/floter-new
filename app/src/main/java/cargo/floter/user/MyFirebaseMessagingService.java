package cargo.floter.user;

/**
 * Created by SONI on 4/19/2017.
 */

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import cargo.floter.user.application.MyApp;
import cargo.floter.user.model.Trip;
import cargo.floter.user.model.TripStatus;
import cargo.floter.user.utils.AppConstants;

import java.util.Map;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";
    private LocalBroadcastManager broadcaster;

    @Override
    public void onCreate() {
        super.onCreate();

    }

    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        broadcaster = LocalBroadcastManager.getInstance(this);
        if (MyApp.getStatus(AppConstants.IS_LOGIN)) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
            Map<String, String> dataMap = remoteMessage.getData();
            if (dataMap.containsKey("trip_status")) {
                String tripStatus = dataMap.get("trip_status");
                String tripId = dataMap.get("trip_id");
                if (tripStatus.equals(TripStatus.Accepted.name())) {
                    MyApp.setStatus("IS_ON_TRIP", true);
                    if (remoteMessage.getData().size() > 0) {
                        Log.d(TAG, "Message data payload: " + remoteMessage.getData());
                        Intent i = new Intent("cargo.floter.user.NEW_RIDE");
                        i.putExtra("TYPE", "TRIP_ACCEPTED");
                        sendBroadcast(i);
                        sendNotificationAndStartTrip("Driver is on the way", Integer.parseInt(dataMap.get("trip_id")));
                    }
                } else if (tripStatus.equals(TripStatus.Declined.name())) {
                    Intent i = new Intent("cargo.floter.user.NEW_RIDE");
                    i.putExtra("TYPE", "TRIP_DECLINED");
                    sendBroadcast(i);
//                sendNotificationAndStartTrip("Driver is on the way", Integer.parseInt(dataMap.get("trip_id")));

                } else if (!tripStatus.equals(TripStatus.Cancelled.name())) {
                    if (tripStatus.equals(TripStatus.Finished.name())) {

                        sendNotification(remoteMessage.getData().get("message"));
                    } else if (tripStatus.equals(TripStatus.OnGoing.name())) {
                        sendNotification(remoteMessage.getData().get("message"));
                    } else if (tripStatus.equals(TripStatus.Accepted.name())) {
                        sendNotification(remoteMessage.getData().get("message"));
                    } else if (tripStatus.equals(TripStatus.Loading.name())) {
                        sendNotification(remoteMessage.getData().get("message"));
                    } else if (tripStatus.equals(TripStatus.Unloading.name())) {
                        sendNotification(remoteMessage.getData().get("message"));
                    } else if (tripStatus.equals(TripStatus.Reached.name())) {
                        sendNotification(remoteMessage.getData().get("message"));
                    } else if (tripStatus.equals(TripStatus.Driver_Cancel.name())) {
                        sendNotification(remoteMessage.getData().get("message"));
                    } else {
                        if (tripStatus.equals("Payment")) {
                            Intent intent = new Intent("MyData");
                            intent.putExtra("onWindowFocus", "YES");
                            broadcaster.sendBroadcast(intent);
                            MyApp.setSharedPrefString("SHOW_PAY", "YES");
                            MyApp.setSharedPrefString(AppConstants.PAYBLE_TRIP_ID, tripId);
                        }
                        sendNotification(remoteMessage.getData().get("message"));
                    }
                }
            } else if (remoteMessage.getNotification() != null) {
                Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
                sendNotification(remoteMessage.getData().get("message"));
            }
        }
    }

    private void sendNotificationAndStartTrip(String messageBody, int tripid) {
        Intent intent = new Intent(this, OnTripActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Trip Accepted")
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(messageBody))
                .setContentText(messageBody)
                .setAutoCancel(false)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(tripid, notificationBuilder.build());
    }


    /**
     * Create and show a simple notification containing the received FCM message.
     *
     * @param messageBody FCM message body received.
     */
    private void sendNotification(String messageBody) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Floter")
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(messageBody))
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }
}