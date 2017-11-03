package com.example.android.cookhub;

/**
 * Created by archi on 27-10-2017.
 */
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import static android.R.string.no;
import static android.R.string.yes;
import static java.lang.System.load;

public class MyRecyclerViewAdapter extends RecyclerView
        .Adapter<MyRecyclerViewAdapter
        .DataObjectHolder> {
    private static String LOG_TAG = "MyRecyclerViewAdapter";
    private ArrayList<DataObject> mDataset;
    private Bitmap bmp=null;
    public Context context;

    public static class DataObjectHolder extends RecyclerView.ViewHolder
    {
        TextView title;
        TextView description;
        Button more;
        ImageView image;

        public DataObjectHolder(final View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.title);
            description = (TextView) itemView.findViewById(R.id.description);
            image=(ImageView)itemView.findViewById(R.id.image);
            more=(Button)itemView.findViewById(R.id.more);
        }
    }

    public MyRecyclerViewAdapter(Context c ,ArrayList<DataObject> myDataset) {
        context=c ;
        mDataset = myDataset;
    }

    @Override
    public DataObjectHolder onCreateViewHolder(ViewGroup parent,
                                               int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cardview, parent, false);

        DataObjectHolder dataObjectHolder = new DataObjectHolder(view);
        return dataObjectHolder;
    }

    @Override
    public void onBindViewHolder(final DataObjectHolder holder, final int position) {

        Log.i("GOY",mDataset.get(position).getmText1());

        holder.title.setText(mDataset.get(position).getmText1());
        holder.description.setText(mDataset.get(position).getmText2());

        Picasso.with(context).load(mDataset.get(position).getmImage()).into(holder.image);

        holder.more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                JSONObject jobj=new JSONObject();
                try {
                    jobj.put("id",mDataset.get(position).getmid());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.e("check",jobj.toString());
                new MyRecyclerViewAdapter.SendDeviceDetails().execute("http://192.168.43.210:5000/api-cook-each",jobj.toString());
            }
        });
    }

    public void addItem(DataObject dataObj, int index) {
        mDataset.add(index, dataObj);
        notifyItemInserted(index);
    }

    public void deleteItem(int index) {
        mDataset.remove(index);
        notifyItemRemoved(index);
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    //This method will filter the list
    //here we are passing the filtered data
    //and assigning it to the list with notifydatasetchanged method
    /*public void filterList(ArrayList<DataObject> filterdNames) {
        this.mDataset = filterdNames;
        notifyDataSetChanged();
    }*/

    public class SendDeviceDetails extends AsyncTask<String, Void, String> {

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
                String t3=jobj.getString("description");
                String t4=jobj.getString("personid");
                String t5=jobj.getString("ctype");

                Log.e("name",t1);
                Log.e("description",t2);

                UserDetails.chatWith=t4;

                Intent intent = new Intent(context, DishDetails.class);
                intent.putExtra("name", t1);
                intent.putExtra("image", t2);
                intent.putExtra("description", t3);
                intent.putExtra("personid",t4);
                context.startActivity(intent);

            } catch (JSONException e) {
                e.printStackTrace();
            }

            // this is expecting a response code to be sent from your server upon receiving the POST data
        }
    }

}