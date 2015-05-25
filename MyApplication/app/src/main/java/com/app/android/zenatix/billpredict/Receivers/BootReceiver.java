package com.app.android.zenatix.billpredict.Receivers;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.util.Log;

import com.app.android.zenatix.billpredict.SettingsActivity;

public class BootReceiver extends BroadcastReceiver {
    private Intent reminderServiceIntent;
    private PendingIntent reminderServicePendingIntent;
    private AlarmManager reminderAlarmMgr;
    private Context context;
    private static String TAG="BootReciever";

    public BootReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            this.context=context;
            setReminderAlarm();
        }
    }

    private void setReminderAlarm(){
        reminderServiceIntent = new Intent(context, ReminderAlarmReceiver.class);
        reminderServiceIntent.setAction("BillPredict.reminderAlarm");
        reminderServiceIntent.putExtra("message", "BillPredict.reminderAlarm");

        boolean alarmUp = (PendingIntent.getBroadcast(context, 0,
                reminderServiceIntent,
                PendingIntent.FLAG_UPDATE_CURRENT) != null);

        if (alarmUp)
            Log.d(TAG, "Alarm is already active");
        else
            Log.d(TAG, "Alarm is not active");


        reminderServicePendingIntent = PendingIntent.getBroadcast(context,
                4816, reminderServiceIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        reminderAlarmMgr= (AlarmManager)context.getSystemService(context.ALARM_SERVICE);
        reminderAlarmMgr.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                SystemClock.elapsedRealtime()+ SettingsActivity.REMINDER_UPLOAD_INTERVAL,
                SettingsActivity.REMINDER_UPLOAD_INTERVAL, reminderServicePendingIntent);

    }
}
