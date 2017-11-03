package com.example.android.cookhub;

import android.app.Dialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;

public class Notifications extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    public RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private static String LOG_TAG = "NotificationsActivity";
    private SwipeRefreshLayout mSwipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Notifications");

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.activity_main_swipe_refresh_layout);
        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        final JSONObject jobj=new JSONObject();
        try {
            jobj.put("id",UserId.uid);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        new Notifications.SendDeviceDetails().execute("http://192.168.43.210:5000/api-notification",jobj.toString());
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                 new Notifications.SendDeviceDetails().execute("http://192.168.43.210:5000/api-notification",jobj.toString());
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private class SendDeviceDetails extends AsyncTask<String, Void, ArrayList<NotObject>> {

        @Override
        protected ArrayList<NotObject> doInBackground(String... params) {

            String data = "";

            HttpURLConnection httpURLConnection = null;
            try {

                httpURLConnection = (HttpURLConnection) new URL(params[0]).openConnection();
                httpURLConnection.setRequestMethod("POST");

                httpURLConnection.setDoOutput(true);

                DataOutputStream wr = new DataOutputStream(httpURLConnection.getOutputStream());
                wr.writeBytes("PostData=" + params[1]);
                wr.flush();
                wr.close();

                InputStream in = httpURLConnection.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(in);

                int inputStreamData = inputStreamReader.read();
                while (inputStreamData != -1) {
                    char current = (char) inputStreamData;
                    inputStreamData = inputStreamReader.read();
                    data += current;
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (httpURLConnection != null) {
                    httpURLConnection.disconnect();
                }
            }
            Log.e("jjjj",data);
            ArrayList<NotObject> results = extractFeatureFromJson(data);
            return results;
        }

        @Override
        protected void onPostExecute(final ArrayList<NotObject> result) {
            super.onPostExecute(result);
            mAdapter = new NotAdapter(Notifications.this,result);
            mRecyclerView.setAdapter(mAdapter);
            // this is expecting a response code to be sent from your server upon receiving the POST data
        }
    }

    private ArrayList<NotObject> extractFeatureFromJson(String earthquakeJSON) {
        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(earthquakeJSON)) {
            return null;
        }

        try {
            JSONObject baseJsonResponse = new JSONObject(earthquakeJSON);
            JSONArray baseArray = baseJsonResponse.getJSONArray("notifications");
            ArrayList<NotObject> results = new ArrayList<NotObject>();
            int size = baseArray.length();
            for (int i = 0; i < size; i++) {
                JSONObject news = baseArray.getJSONObject(i);
                String type = news.getString("type");
                String timestamp = news.getString("timestamp");
                String cook = news.getString("cook");
                String sendername = news.getString("sendername");
                String senderid = news.getString("senderid");

                Log.e("heading",type);
                //Log.v("heading",heading);
                //Log.v("heading",heading);

                NotObject obj = new NotObject(type+" "+cook+"by "+sendername, timestamp);
                Log.d("obj", obj.toString());
                results.add(obj);

            }
            return results;

        } catch (JSONException e) {
            Log.e(LOG_TAG, "Problem parsing the earthquake JSON results", e);
        }
        return null;
    }

}
