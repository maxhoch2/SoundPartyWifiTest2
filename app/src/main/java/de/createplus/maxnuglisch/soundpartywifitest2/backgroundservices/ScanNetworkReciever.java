package de.createplus.maxnuglisch.soundpartywifitest2.backgroundservices;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import de.createplus.maxnuglisch.soundpartywifitest2.MainActivity;
import de.createplus.maxnuglisch.soundpartywifitest2.R;


public class ScanNetworkReciever extends BroadcastReceiver {
    // Prevents instantiation
    private MainActivity activity;

    public ScanNetworkReciever(MainActivity activity) {
        this.activity = activity;
        Log.e("HI","ICH BIN HIER ANGEKOMMEN A");
    }

    // Called when the BroadcastReceiver gets an Intent it's registered to receive
    @Override
    public void onReceive(Context context, Intent intent) {
        String msg = intent.getExtras().getString(ScanNetwork.Constants.EXTENDED_DATA_STATUS);

        Snackbar.make(activity.findViewById(R.id.content_main), "Scanned Network", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
        activity.updateContainerContent(msg);

    }


    private void showNotification(Context context, String title, String text, int icon) {
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
                new Intent(context, MainActivity.class), 0);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(icon)
                        .setContentTitle(title)
                        .setContentText(text);
        mBuilder.setContentIntent(contentIntent);
        mBuilder.setDefaults(Notification.DEFAULT_SOUND);
        mBuilder.setAutoCancel(true);
        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(1, mBuilder.build());

    }
}
