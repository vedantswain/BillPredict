package com.app.android.zenatix.billpredict.RequestTasks;

import android.os.AsyncTask;
import android.util.Log;

import com.app.android.zenatix.billpredict.CommonUtils;
import com.app.android.zenatix.billpredict.TaskCompletedListeners.HistoryCompleteListener;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 * Created by vedantdasswain on 29/05/15.
 */
public class HistoryTask extends AsyncTask<Void, Void, String> {
    private static final String TAG ="history Task" ;
    JSONObject historyObject;
    HistoryCompleteListener hcl;

    public HistoryTask(String cno, String type,
                     JSONArray data,
                     HistoryCompleteListener hcl) {
        this.hcl=hcl;
        historyObject = new JSONObject();
        try {
            historyObject.put("cno", cno);
            historyObject.put("type", type);
            historyObject.put("data", data);
        }
        catch (JSONException e){
            e.printStackTrace();
        }
    }

    @Override
    protected String doInBackground(Void... params) {
        String msg = postCustomerInfo(historyObject);
        return msg;
    }

    private String postCustomerInfo(JSONObject historyObject){
        String msg="";
        HttpClient httpClient = new DefaultHttpClient();
        // replace with your url
        HttpPost httpPost = new HttpPost(CommonUtils.HISTORY_API);

        StringEntity se;
        try {
            se = new StringEntity(historyObject.toString());
            se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE,"application/json"));
            httpPost.setEntity(se);

            HttpResponse response = httpClient.execute(httpPost);
            // write response to log
//            Log.d(TAG,"Post history info"+ response.getStatusLine().toString())
            return "Post history data"+ response.getStatusLine().toString();
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
        hcl.onHistoryComplete(msg);
    }
}