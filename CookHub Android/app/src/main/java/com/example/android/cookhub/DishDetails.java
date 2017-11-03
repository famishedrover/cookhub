package com.example.android.cookhub;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toolbar;

import com.github.clans.fab.FloatingActionMenu;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class DishDetails extends AppCompatActivity {

    public String cookid="";
    private TextView description;
    private ImageView image;
    private TextView title;
    private TextView owner;
    FloatingActionMenu materialDesignFAM;
    FloatingActionButton floatingActionButton1, floatingActionButton2, floatingActionButton3,floatingActionButton4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dish_details);

        UserDetails.username=UserId.uid;

        title=(TextView)findViewById(R.id.title);
        description=(TextView)findViewById(R.id.description);
        image=(ImageView)findViewById(R.id.image);
        owner=(TextView)findViewById(R.id.owner);
        cookid=getIntent().getStringExtra("cookid");

        title.setText(getIntent().getStringExtra("name"));
        Picasso.with(DishDetails.this).load(getIntent().getStringExtra("image")).into(image);
        description.setText(getIntent().getStringExtra("description"));
        owner.setText(getIntent().getStringExtra("personid"));

        materialDesignFAM = (FloatingActionMenu) findViewById(R.id.material_design_android_floating_action_menu);
        floatingActionButton1 = (FloatingActionButton) findViewById(R.id.material_design_floating_action_menu_item1);
        floatingActionButton2 = (FloatingActionButton) findViewById(R.id.material_design_floating_action_menu_item2);
        floatingActionButton3 = (FloatingActionButton) findViewById(R.id.material_design_floating_action_menu_item3);
        floatingActionButton4 = (FloatingActionButton) findViewById(R.id.material_design_floating_action_menu_item4);

        floatingActionButton1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //TODO something when floating action menu first item clicked
                startActivity(new Intent(DishDetails.this,Chat.class));
            }
        });
        floatingActionButton2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //TODO something when floating action menu second item clicked

            }
        });
        floatingActionButton3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //TODO something when floating action menu third item clicked

            }
        });

        //new DishDetails.SendDeviceDetails().execute("http://192.168.43.210/api-cook-each",jobj.toString());

    }

    /*public class SendDeviceDetails extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {

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
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            try {
                JSONObject jobj=new JSONObject(result);
                String t1=jobj.getString("name");
                String t2=jobj.getString("image");
                String t3=jobj.getString("desciption");
                String t4=jobj.getString("personid");
                String t5=jobj.getString("");

                UserDetails.chatWith=t4;

                title.setText(t1);
                Picasso.with(DishDetails.this).load(t2).into(image);
                description.setText(t3);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            // this is expecting a response code to be sent from your server upon receiving the POST data
        }
    }*/
}
