package com.example.android.cookhub;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class MyCooksDetail extends AppCompatActivity {

    private TextView title;
    private ImageView image;
    private TextView description;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_cooks_detail);

        title=(TextView)findViewById(R.id.title);
        image=(ImageView)findViewById(R.id.image);
        description=(TextView)findViewById(R.id.description);

        title.setText(getIntent().getStringExtra("name"));
        Picasso.with(MyCooksDetail.this).load(getIntent().getStringExtra("image")).into(image);
        description.setText(getIntent().getStringExtra("description"));
    }
}
