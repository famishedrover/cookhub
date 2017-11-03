package com.example.android.cookhub;

import android.app.Dialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class LoginActivity extends AppCompatActivity {

    private EditText emailEdit;
    private EditText passwordEdit;
    private Button login;
    private Button sign_up;
    private String email="";
    private String password="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailEdit=(EditText)findViewById(R.id.email);
        passwordEdit=(EditText)findViewById(R.id.password);
        login=(Button)findViewById(R.id.btn_login);
        sign_up=(Button)findViewById(R.id.btn_signup);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email=emailEdit.getText().toString();
                password=passwordEdit.getText().toString();

                final JSONObject jobj=new JSONObject();
                try {
                    jobj.put("emaillogin",email);
                    jobj.put("passwordlogin",password);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                new SendDeviceDetails().execute("http://192.168.43.210:5000/apiauthlogin",jobj.toString());
            }
        });

        sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this,Signup.class));
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
            String status="";
            try {
                JSONObject jobj=new JSONObject(result);
                status=jobj.getString("status");
                UserId.uid=jobj.getString("id");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            if(status=="1"){
                startActivity(new Intent(LoginActivity.this,MainActivity.class));
            }
            else {
                // toast message
            }

            // this is expecting a response code to be sent from your server upon receiving the POST data
        }
    }

}
