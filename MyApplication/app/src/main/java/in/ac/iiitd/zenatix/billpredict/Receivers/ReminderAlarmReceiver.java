package in.ac.iiitd.zenatix.billpredict.Receivers;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import in.ac.iiitd.zenatix.billpredict.MainTabActivity;
import in.ac.iiitd.zenatix.billpredict.R;
import in.ac.iiitd.zenatix.billpredict.SettingsActivity;

public class ReminderAlarmReceiver extends BroadcastReceiver {
    private static final String TAG="ReminderAlarmReceiver";
    private float REMINDER_INTERVAL= SettingsActivity.REMINDER_UPLOAD_INTERVAL;

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
//                sendNotification(context,"Main receiver got message: ",0);
                SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);

                SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
                Date curDate=new Date();
                Date startDate= null;
                try {
                    startDate = sdf.parse(sharedPref.getString("LAST_UPDATE_ELECTRICITY",""));
                    float lastUpdateElectricity=(curDate.getTime()-startDate.getTime())/1000/60/60/24;
                    if(lastUpdateElectricity>REMINDER_INTERVAL*2)
                        sendNotification(context,
                                "You haven't updated your Electricity meter for " + (int)lastUpdateElectricity + " days",
                                1);

                    startDate = sdf.parse(sharedPref.getString("LAST_UPDATE_WATER",""));
                    float lastUpdateWater=(curDate.getTime()-startDate.getTime())/1000/60/60/24;
                    if(lastUpdateWater>REMINDER_INTERVAL*2)
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
    }

    private void sendNotification(Context context,String message,int mNotificationId){
        Intent intent = new Intent(context, MainTabActivity.class);
        PendingIntent pIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setContentTitle("Reminder")
                        .setContentText(message)
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
