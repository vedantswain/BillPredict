package com.app.android.zenatix.billpredict.Receivers;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

import com.app.android.zenatix.billpredict.MainTabActivity;
import com.app.android.zenatix.billpredict.R;
import com.app.android.zenatix.billpredict.ReminderIntentService;

public class ReminderAlarmReceiver extends WakefulBroadcastReceiver {
    private static final String TAG="ReminderAlarmReceiver";

    public ReminderAlarmReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        String message = intent.getStringExtra("message");
        Log.v(TAG, "Main receiver got message: " + message);
        if(message!=null)
            if(message.contains("BillPredict.reminderAlarm")){

                Intent service = new Intent(context, ReminderIntentService.class);

                // Start the service, keeping the device awake while it is launching.
                Log.i("ReminderIntentService", "Starting service @ " + SystemClock.elapsedRealtime());
                startWakefulService(context, service);

//                sendNotification(context,"Main receiver got message: ",0);
            }
    }

    private void sendNotification(Context context,String message,int mNotificationId){
        Intent intent = new Intent(context, MainTabActivity.class);
        PendingIntent pIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setContentTitle("Reminder")
                        .setContentText(message)
                        .setAutoCancel(true)
                        .setContentIntent(pIntent);
        //Vibration
        mBuilder.setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000 });

        //LED
        mBuilder.setLights(Color.WHITE, 3000, 3000);

        NotificationManager mNotifyMgr =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        // Builds the notification and issues it.
        mNotifyMgr.notify(mNotificationId, mBuilder.build());
    }
}
