package com.app.android.zenatix.billpredict.RequestTasks;

import android.os.AsyncTask;
import android.util.Log;

import com.app.android.zenatix.billpredict.CommonUtils;
import com.app.android.zenatix.billpredict.TaskCompletedListeners.UpdateCompleteListener;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPut;
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
public class UpdateTask extends AsyncTask<Void, Void, String> {
    private static final String TAG ="Update Task" ;
    JSONObject storeObject;
    UpdateCompleteListener ucl;

    public UpdateTask(String cno, String type,
                     float old_reading ,float new_reading,
                     UpdateCompleteListener ucl) {
        this.ucl=ucl;
        storeObject = new JSONObject();
        try {
            storeObject.put("customer_no", cno);
            storeObject.put("type", type);
            storeObject.put("old_reading", old_reading);
            storeObject.put("new_reading", new_reading);
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
        HttpPut httpPut = new HttpPut(CommonUtils.STORE_API);

        StringEntity se;
        try {
            se = new StringEntity(storeObject.toString());
            se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE,"application/json"));
            httpPut.setEntity(se);

            HttpResponse response = httpClient.execute(httpPut);
            // write response to log
//            Log.d(TAG,"Post store info"+ response.getStatusLine().toString())
            return "Put storage data"+ response.getStatusLine().toString();
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
        ucl.onUpdateComplete(msg);
    }
}


