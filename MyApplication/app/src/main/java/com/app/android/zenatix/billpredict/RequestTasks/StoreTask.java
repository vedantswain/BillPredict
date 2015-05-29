package com.app.android.zenatix.billpredict.RequestTasks;

import android.os.AsyncTask;
import android.util.Log;

import com.app.android.zenatix.billpredict.CommonUtils;
import com.app.android.zenatix.billpredict.TaskCompletedListeners.StoreCompleteListener;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 * Created by vedantdasswain on 29/05/15.
 */
public class StoreTask extends AsyncTask<Void, Void, String> {
    private static final String TAG ="Store Task" ;
    JSONObject storeObject;
    StoreCompleteListener scl;

    public StoreTask(String cno, String type,
                     float meter_reading ,String reading_date,
                     float cycle_start_reading,String cycle_start_date,
                     String location,StoreCompleteListener scl) {
        this.scl=scl;
        storeObject = new JSONObject();
        try {
            storeObject.put("cno", cno);
            storeObject.put("type", type);
            storeObject.put("meter_reading", meter_reading);
            storeObject.put("reading_date", reading_date);
            storeObject.put("cycle_start_reading", cycle_start_reading);
            storeObject.put("cycle_start_date", cycle_start_date);
        }
        catch (JSONException e){
            e.printStackTrace();
        }
    }

    @Override
    protected String doInBackground(Void... params) {
        String msg = postCustomerInfo(storeObject);
        return msg;
    }

    private String postCustomerInfo(JSONObject storeObject){
        String msg="";
        HttpClient httpClient = new DefaultHttpClient();
        // replace with your url
        HttpPost httpPost = new HttpPost(CommonUtils.STORE_API);

        StringEntity se;
        try {
            se = new StringEntity(storeObject.toString());
            se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE,"application/json"));
            httpPost.setEntity(se);

            HttpResponse response = httpClient.execute(httpPost);
            // write response to log
//            Log.d(TAG,"Post store info"+ response.getStatusLine().toString())
            return "Post storage data"+ response.getStatusLine().toString();
//            Log.d(TAG, EntityUtils.toString(response.getEntity()));
        } catch (ClientProtocolException | UnsupportedEncodingException e) {
            // Log exception
            e.printStackTrace();
        } catch (IOException e) {
            // Log exception
            e.printStackTrace();
        }
        return msg;
    }

    @Override
    protected void onPostExecute(String msg) {
        Log.i(TAG, msg);
        scl.onStoreComplete(msg);
    }
}