package com.example.android.cookhub;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class Signup extends AppCompatActivity {

    private EditText emailEdit;
    private EditText passwordEdit;
    private EditText nameEdit;
    private EditText locationEdit;
    private EditText recruitmentEdit;
    private EditText ageEdit;
    private EditText genderEdit;
    private EditText expertiseEdit;
    private Button sign_up;
    private Button sign_in;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        emailEdit=(EditText)findViewById(R.id.email);
        passwordEdit=(EditText)findViewById(R.id.password);
        nameEdit=(EditText)findViewById(R.id.name);
        locationEdit=(EditText)findViewById(R.id.location);
        recruitmentEdit=(EditText)findViewById(R.id.recruitment);
        ageEdit=(EditText)findViewById(R.id.age);
        genderEdit=(EditText)findViewById(R.id.gender);
        expertiseEdit=(EditText)findViewById(R.id.expertise);
        sign_up=(Button)findViewById(R.id.sign_up_button);
        sign_in=(Button)findViewById(R.id.sign_in_button);



        sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email=emailEdit.getText().toString().trim();
                String password=passwordEdit.getText().toString().trim();
                String name=nameEdit.getText().toString().trim();
                String location=locationEdit.getText().toString().trim();
                String recruitment=recruitmentEdit.getText().toString().trim();
                String age=ageEdit.getText().toString().trim();
                String gender=genderEdit.getText().toString().trim();
                String expertise=expertiseEdit.getText().toString().trim();
                Log.e("GOYAL",email+" "+password);
                final JSONObject jobj=new JSONObject();
                try {
                    jobj.put("email",email);
                    jobj.put("password",password);
                    jobj.put("name",name);
                    jobj.put("location",location);
                    jobj.put("gender",gender);
                    jobj.put("age",age);
                    jobj.put("recruitment",recruitment);
                    jobj.put("expertise",expertise);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                new SendDeviceDetails().execute(IP_Add.ip+"api-signup",jobj.toString());
            }
        });

        sign_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Signup.this,LoginActivity.class));
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
            startActivity(new Intent(Signup.this,LoginActivity.class));
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
