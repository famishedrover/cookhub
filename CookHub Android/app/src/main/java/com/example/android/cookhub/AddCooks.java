package com.example.android.cookhub;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class AddCooks extends AppCompatActivity {

    private EditText edit1;
    private EditText edit2;
    private EditText edit3;
    private EditText edit4;
    private EditText edit5;
    private EditText edit6;
    private EditText edit7;
    private EditText edit8;
    private EditText edit9;
    private EditText edit10;
    private EditText edit11;
    private Button upload;
    private Button submit;
    private Button stage;
    private RadioGroup rg;
    private RadioButton rprivate;
    private RadioButton rpublic;
    String name="";
    String type="";
    String cost="";
    String preperation="";
    String region="";
    String description="";
    String time="";
    String ingredient="";
    String comments="";
    boolean priv,pub;
    File destination;
    private static final int PICK_Camera_IMAGE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_cooks);

        edit1=(EditText)findViewById(R.id.name);
        edit2=(EditText)findViewById(R.id.type);
        edit3=(EditText)findViewById(R.id.Cost);
        edit4=(EditText)findViewById(R.id.preperation);
        edit5=(EditText)findViewById(R.id.region);
        edit6=(EditText)findViewById(R.id.description);
        edit7=(EditText)findViewById(R.id.time);
        edit8=(EditText)findViewById(R.id.ingredient);
        edit9=(EditText)findViewById(R.id.comments);
        upload=(Button)findViewById(R.id.upload);
        submit=(Button)findViewById(R.id.submit);
        stage=(Button)findViewById(R.id.stage);
        rg=(RadioGroup)findViewById(R.id.radio_group);
        rprivate=(RadioButton)findViewById(R.id.rprivate);
        rpublic=(RadioButton)findViewById(R.id.rpublic);
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name=edit1.getText().toString();
                type=edit2.getText().toString();
                cost=edit3.getText().toString();
                preperation=edit4.getText().toString();
                region=edit5.getText().toString();
                description=edit6.getText().toString();
                time=edit7.getText().toString();
                ingredient=edit8.getText().toString();
                comments=edit9.getText().toString();
                priv=rprivate.isChecked();
                pub=rpublic.isChecked();

                JSONObject jobj=new JSONObject();
                try {
                    jobj.put("name",name);
                    jobj.put("type",type);
                    jobj.put("cost",cost);
                    jobj.put("preperation",preperation);
                    jobj.put("region",region);
                    jobj.put("description",description);
                    jobj.put("time",time);
                    jobj.put("ingredient",ingredient);
                    jobj.put("comments",comments);
                    jobj.put("private",priv);
                    jobj.put("public",pub);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                new AddCooks.SendDeviceDetails().execute("",jobj.toString());
            }
        });
    }

    private class SendDeviceDetails extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {

            String data = "";

            HttpURLConnection httpURLConnection = null;
            try {

                httpURLConnection = (HttpURLConnection) new URL(params[0]).openConnection();
                //httpURLConnection.setRequestProperty("Content-type","application/json");
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

            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Log.e("ARCHIT", result);
            /*String status="";
            try {
                JSONObject jobj=new JSONObject(result);
                status=jobj.getString("status");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            if(status=="1"){
                startActivity(new Intent(LoginActivity.this,MainActivity.class));
            }
            else {
                // toast message
            }*/

            // this is expecting a response code to be sent from your server upon receiving the POST data
        }
    }
}
