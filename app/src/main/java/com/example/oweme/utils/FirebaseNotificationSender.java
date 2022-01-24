package com.example.oweme.utils;

import android.app.Activity;
import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.oweme.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class FirebaseNotificationSender {

    String token;
    String title;
    String body;
    Context mContext;
    Activity mActivity;

    private RequestQueue requestQueue;
    private final String FCM_API = "https://fcm.googleapis.com/fcm/send";
    private final String serverKey = "AAAAUFZNXko:APA91bHViScvCdlMIl_fGwVfIFUwkMpdXXeZw13q-PpdnRulmcsix0zapJ4uHpXZ7KG8tsyxMXwQmAOoMRonaFg9cgqNLMjYgM5SOqM8oYyanERStdrkLOWSRL1LsZ5q9-IEqffW0wOp";

    public FirebaseNotificationSender(String token, String title, String body, Context mContext, Activity mActivity) {
        this.token = token;
        this.title = title;
        this.body = body;
        this.mContext = mContext;
        this.mActivity = mActivity;
    }

    public void SendNotification(){

        requestQueue = Volley.newRequestQueue(mActivity);
        JSONObject mainObj = new JSONObject();
        try{
            mainObj.put("to", token);
            JSONObject notiObject = new JSONObject();
            notiObject.put("title", title);
            notiObject.put("body", body);
            notiObject.put("icon", R.mipmap.ic_launcher);
            mainObj.put("notification", notiObject);

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, FCM_API, mainObj, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {

                    // code run is got response

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    // code run is got error

                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {

                    Map<String, String> header = new HashMap<>();
                    header.put("Content-Type", "application/json");
                    header.put("Authorization", "key=" + serverKey);
                    return header;

                }
            };
            requestQueue.add(request);
        }catch (JSONException e){
            e.printStackTrace();
        }
    }
}
