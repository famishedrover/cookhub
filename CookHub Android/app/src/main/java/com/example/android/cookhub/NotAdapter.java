package com.example.android.cookhub;

/**
 * Created by archi on 27-10-2017.
 */
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
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

public class NotAdapter extends RecyclerView
        .Adapter<NotAdapter
        .NotObjectHolder> {
    private static String LOG_TAG = "NotAdapter";
    private ArrayList<NotObject> mDataset;
    public Context context;

    public static class NotObjectHolder extends RecyclerView.ViewHolder
    {
        TextView time;
        TextView description;

        public NotObjectHolder(final View itemView) {
            super(itemView);
            description = (TextView) itemView.findViewById(R.id.title);
            time = (TextView) itemView.findViewById(R.id.time);
        }
    }

    public NotAdapter(Context c ,ArrayList<NotObject> myDataset) {
        context=c ;
        mDataset = myDataset;
    }

    @Override
    public NotObjectHolder onCreateViewHolder(ViewGroup parent,
                                               int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.notifications_list_item, parent, false);

        NotObjectHolder notObjectHolder = new NotObjectHolder(view);
        return notObjectHolder;
    }

    @Override
    public void onBindViewHolder(final NotObjectHolder holder, final int position) {

        Log.i("GOY",mDataset.get(position).getmText1());

        holder.description.setText(mDataset.get(position).getmText1());
        holder.time.setText(mDataset.get(position).getmText2());

    }

    public void addItem(NotObject dataObj, int index) {
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
    public void filterList(ArrayList<NotObject> filterdNames) {
        this.mDataset = filterdNames;
        notifyDataSetChanged();
    }

}