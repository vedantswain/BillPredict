package com.app.android.zenatix.billpredict;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests com
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class ReminderIntentService extends IntentService {
    private float REMINDER_INTERVAL= SettingsActivity.REMINDER_UPLOAD_INTERVAL;

    public ReminderIntentService() {
        super("ReminderIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            checkInterval(getApplicationContext());
        }
    }

    private void checkInterval(Context context) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);

        SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
        Date curDate=new Date();
        Date startDate= null;
        try {
            startDate = sdf.parse(sharedPref.getString("LAST_UPDATE_ELECTRICITY",""));
            long lastUpdateElectricityinMillis=curDate.getTime()-startDate.getTime();
            float lastUpdateElectricity=(lastUpdateElectricityinMillis)/1000/60/60/24;
            if(lastUpdateElectricityinMillis>REMINDER_INTERVAL*2)
                sendNotification(context,
                        "You haven't updated your Electricity meter for " + (int)lastUpdateElectricity + " days",
                        1);

            startDate = sdf.parse(sharedPref.getString("LAST_UPDATE_WATER",""));
            long lastUpdateWaterinMillis=curDate.getTime()-startDate.getTime();
            float lastUpdateWater=(lastUpdateWaterinMillis)/1000/60/60/24;
            if(lastUpdateWaterinMillis>REMINDER_INTERVAL*2)
                sendNotification(context,
                        "You haven't updated your Water meter for " + (int)lastUpdateWater + " days",
                        2);


        } catch (ParseException e) {
            sendNotification(context,
                    "You haven't updated your meter readings even once",
                    0);
            e.printStackTrace();
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
