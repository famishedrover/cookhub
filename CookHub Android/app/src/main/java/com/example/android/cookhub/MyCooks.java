package com.example.android.cookhub;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SimpleAdapter;

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
import java.util.List;
import java.util.Locale;

public class MyCooks extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    public RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private static String LOG_TAG = "Mycooks";
    private Button more;
    private EditText search;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_cooks);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MyCooks.this,AddCooks.class));
            }
        });

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.activity_main_swipe_refresh_layout);
        search=(EditText)findViewById(R.id.search);
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

        new SendDeviceDetails().execute("http://192.168.43.210:5000/api-profile-each",jobj.toString());
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new SendDeviceDetails().execute("http://192.168.43.210:5000/api-profile-each",jobj.toString());
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });


        //adding a TextChangedListener
        //to call a method whenever there is some change on the EditText
        /*search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                //after the change calling the method and passing the search input
                filter(editable.toString());
            }
        });*/

    }

/* ---------------------search functionality------------------------------------------------
    private void filter(String text,ArrayList<DataObject> list) {
        //new array list that will hold the filtered data
        ArrayList<DataObject> filterdNames = new ArrayList<>();

        //looping through existing elements
        for (DataObject s : list) {
            //if the existing elements contains the search input
            if (s.getmText1().toLowerCase().contains(text.toLowerCase())) {
                //adding the element to filtered list
                filterdNames.add(s);
            }
        }

        //calling a method of the adapter class and passing the filtered list
        mAdapter.filterList(filterdNames);
    }
   //-------------------------------------------------------------------------------------------
*/

    private class SendDeviceDetails extends AsyncTask<String, Void, ArrayList<DataObject> > {

        @Override
        protected ArrayList<DataObject> doInBackground(String... params) {

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
            ArrayList<DataObject> results = extractFeatureFromJson(data);
            return results;
        }

        @Override
        protected void onPostExecute(final ArrayList<DataObject> result) {
            super.onPostExecute(result);
            mAdapter = new MyCooksAdapter(MyCooks.this,result);
            mRecyclerView.setAdapter(mAdapter);
            addTextListener(result);
            // this is expecting a response code to be sent from your server upon receiving the POST data
        }
    }

    private ArrayList<DataObject> extractFeatureFromJson(String earthquakeJSON) {
        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(earthquakeJSON)) {
            return null;
        }

        try {
            JSONObject baseJsonResponse = new JSONObject(earthquakeJSON);
            JSONArray baseArray = baseJsonResponse.getJSONArray("cooks");
            ArrayList<DataObject> results = new ArrayList<DataObject>();
            int size = baseArray.length();
            for (int i = 0; i < size; i++) {
                JSONObject news = baseArray.getJSONObject(i);
                String name = news.getString("name");
                String image = news.getString("image");
                String description = news.getString("description");
                String cookid=news.getString("cookid");

                //Log.e("heading",type);
                //Log.v("heading",heading);
                //Log.v("heading",heading);

                DataObject obj = new DataObject(name,description,image,cookid);
                Log.d("obj", obj.toString());
                results.add(obj);

            }
            return results;

        } catch (JSONException e) {
            Log.e(LOG_TAG, "Problem parsing the earthquake JSON results", e);
        }
        return null;
    }

    public void addTextListener(final ArrayList<DataObject> list){

        search.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {}

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            public void onTextChanged(CharSequence query, int start, int before, int count) {

                query = query.toString().toLowerCase();

                final ArrayList<DataObject> filteredList = new ArrayList<>();

                for (int i = 0; i < list.size(); i++) {

                    final String text = list.get(i).getmText1().toLowerCase();
                    if (text.contains(query)) {

                        filteredList.add(list.get(i));
                    }
                }

                mRecyclerView.setLayoutManager(mLayoutManager);
                mAdapter = new MyCooksAdapter(MyCooks.this,filteredList);
                mRecyclerView.setAdapter(mAdapter);
                mAdapter.notifyDataSetChanged();  // data set changed
            }
        });
    }

}
