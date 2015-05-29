package com.app.android.zenatix.billpredict.RequestTasks;

import android.os.AsyncTask;
import android.util.Log;

import com.app.android.zenatix.billpredict.CommonUtils;
import com.app.android.zenatix.billpredict.TaskCompletedListeners.RegisterCompleteListener;

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
 * Created by vedantdasswain on 26/05/15.
 */
public class RegisterTask extends AsyncTask<Void, Void, String>{
    private static final String TAG ="Register Task" ;
    JSONObject userObject;
    RegisterCompleteListener rcl;

        public RegisterTask(String cno_water, String service_water,
                            String cno_electricity, String service_electricity,
                            RegisterCompleteListener rcl) {
            this.rcl=rcl;
            userObject = new JSONObject();
            try {
                userObject.put("customer_no_water", cno_water);
                userObject.put("service_water", service_water);
                userObject.put("customer_no_electricity", cno_electricity);
                userObject.put("service_electricity", service_electricity);
            }
            catch (JSONException e){
                e.printStackTrace();
            }
        }

        @Override
        protected String doInBackground(Void... params) {
            String msg = postCustomerInfo(userObject);
            return msg;
        }

        private String postCustomerInfo(JSONObject userObject){
            String msg="";
            HttpClient httpClient = new DefaultHttpClient();
            // replace with your url
            HttpPost httpPost = new HttpPost(CommonUtils.REGISTER_API);

            StringEntity se;
            try {
                se = new StringEntity(userObject.toString());
                se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE,"application/json"));
                httpPost.setEntity(se);

                HttpResponse response = httpClient.execute(httpPost);
                // write response to log
//            Log.d(TAG,"Post user info"+ response.getStatusLine().toString())
                return "Post customer info"+ response.getStatusLine().toString();
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
        rcl.onRegisterComplete(msg);
    }
}

