package com.example.fengmapdemo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.fengmapdemo.location.MapCoord;
import com.example.fengmapdemo.pojo.MyLocation;
import com.fengmap.android.map.geometry.FMMapCoord;
import com.google.gson.Gson;

import org.json.JSONObject;


import java.io.InputStream;

import java.util.ArrayList;
import java.util.List;


public class MainActivity2 extends AppCompatActivity {

    List<MyLocation> myLocations = new ArrayList<>();
    MyLocation myLocation = new MyLocation();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        String response = getIntent().getStringExtra("images");
        Log.d("response1", response);
        try{
            JSONObject jsonObject = new JSONObject(response);
            for(int i = 0; i < 9; i++) {
                JSONObject temp = jsonObject.getJSONObject(""+i);
                MyLocation myLocation = new MyLocation();
                myLocation.setFloor(temp.optInt("floor"));
                myLocation.setX(temp.optDouble("x"));
                myLocation.setY(temp.optDouble("y"));
                myLocation.setImagePath("http://10.176.54.13:8081" + temp.optString("image_path"));
                String img = temp.optString("image_path");
                int locationId = Integer.parseInt(img.split("_")[2].split("\\.")[0]);
                myLocation.setLocationId(locationId);
                myLocations.add(myLocation);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        Intent intent = new Intent(this, MainActivity.class);
        Log.d("response", "" + myLocations.get(0).getLocationId());
        myLocation = myLocations.get(0);
        MapCoord mapCoord = new MapCoord(myLocation.getFloor(), new FMMapCoord(myLocation.getX(), myLocation.getY()));
        Log.d("mapcoord", ""+mapCoord.getMapCoord().x + "," + mapCoord.getMapCoord().y);
        intent.putExtra("mapcoord", mapCoord);
        startActivity(intent);
    }


    @Override
    protected void onResume() {
        super.onResume();

        Intent intent = new Intent(this, MainActivity.class);

        ImageView top1 = (ImageView) findViewById(R.id.top1);
        TextView top1_text = (TextView) findViewById(R.id.top1_text);
        try {
            top1.setImageBitmap(doInBackground(myLocations.get(0).getImagePath()));
            top1_text.setText(""+ myLocations.get(0).getLocationId());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        ImageView top2 = (ImageView) findViewById(R.id.top2);
        TextView top2_text = (TextView) findViewById(R.id.top2_text);
        try {
            top2.setImageBitmap(doInBackground(myLocations.get(1).getImagePath()));
            top2_text.setText(""+ myLocations.get(1).getLocationId());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        ImageView top3 = (ImageView) findViewById(R.id.top3);
        TextView top3_text = (TextView) findViewById(R.id.top3_text);
        try {
            top3.setImageBitmap(doInBackground(myLocations.get(2).getImagePath()));
            top3_text.setText(""+ myLocations.get(2).getLocationId());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        ImageView top4 = (ImageView) findViewById(R.id.top4);
        TextView top4_text = (TextView) findViewById(R.id.top4_text);
        try {
            top4.setImageBitmap(doInBackground(myLocations.get(3).getImagePath()));
            top4_text.setText(""+ myLocations.get(3).getLocationId());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        ImageView top5 = (ImageView) findViewById(R.id.top5);
        TextView top5_text = (TextView) findViewById(R.id.top5_text);
        try {
            top5.setImageBitmap(doInBackground(myLocations.get(4).getImagePath()));
            top5_text.setText(""+ myLocations.get(4).getLocationId());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        ImageView top6 = (ImageView) findViewById(R.id.top6);
        TextView top6_text = (TextView) findViewById(R.id.top6_text);
        try {
            top6.setImageBitmap(doInBackground(myLocations.get(5).getImagePath()));
            top6_text.setText(""+ myLocations.get(5).getLocationId());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        ImageView top7 = (ImageView) findViewById(R.id.top7);
        TextView top7_text = (TextView) findViewById(R.id.top7_text);
        try {
            top7.setImageBitmap(doInBackground(myLocations.get(6).getImagePath()));
            top7_text.setText(""+ myLocations.get(6).getLocationId());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        ImageView top8 = (ImageView) findViewById(R.id.top8);
        TextView top8_text = (TextView) findViewById(R.id.top8_text);
        try {
            top8.setImageBitmap(doInBackground(myLocations.get(7).getImagePath()));
            top8_text.setText(""+ myLocations.get(7).getLocationId());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        ImageView top9 = (ImageView) findViewById(R.id.top9);
        TextView top9_text = (TextView) findViewById(R.id.top9_text);
        try {
            top9.setImageBitmap(doInBackground(myLocations.get(8).getImagePath()));
            top9_text.setText(""+ myLocations.get(8).getLocationId());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        top1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myLocation = myLocations.get(0);
                MapCoord mapCoord = new MapCoord(myLocation.getFloor(), new FMMapCoord(myLocation.getX(), myLocation.getY()));
                intent.putExtra("mapcoord", mapCoord);
                startActivity(intent);
            }
        });

        top2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myLocation = myLocations.get(1);
                MapCoord mapCoord = new MapCoord(myLocation.getFloor(), new FMMapCoord(myLocation.getX(), myLocation.getY()));
                intent.putExtra("mapcoord", mapCoord);
                startActivity(intent);
            }
        });

        top3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myLocation = myLocations.get(2);
                MapCoord mapCoord = new MapCoord(myLocation.getFloor(), new FMMapCoord(myLocation.getX(), myLocation.getY()));
                intent.putExtra("mapcoord", mapCoord);
                startActivity(intent);
            }
        });

        top4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myLocation = myLocations.get(3);
                MapCoord mapCoord = new MapCoord(myLocation.getFloor(), new FMMapCoord(myLocation.getX(), myLocation.getY()));
                intent.putExtra("mapcoord", mapCoord);
                startActivity(intent);
            }
        });

        top5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myLocation = myLocations.get(4);
                MapCoord mapCoord = new MapCoord(myLocation.getFloor(), new FMMapCoord(myLocation.getX(), myLocation.getY()));
                intent.putExtra("mapcoord", mapCoord);
                startActivity(intent);
            }
        });

        top6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myLocation = myLocations.get(5);
                MapCoord mapCoord = new MapCoord(myLocation.getFloor(), new FMMapCoord(myLocation.getX(), myLocation.getY()));
                intent.putExtra("mapcoord", mapCoord);
                startActivity(intent);
            }
        });

        top7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myLocation = myLocations.get(6);
                MapCoord mapCoord = new MapCoord(myLocation.getFloor(), new FMMapCoord(myLocation.getX(), myLocation.getY()));
                intent.putExtra("mapcoord", mapCoord);
                startActivity(intent);
            }
        });

        top8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myLocation = myLocations.get(7);
                MapCoord mapCoord = new MapCoord(myLocation.getFloor(), new FMMapCoord(myLocation.getX(), myLocation.getY()));
                intent.putExtra("mapcoord", mapCoord);
                startActivity(intent);
            }
        });

        top9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myLocation = myLocations.get(8);
                MapCoord mapCoord = new MapCoord(myLocation.getFloor(), new FMMapCoord(myLocation.getX(), myLocation.getY()));
                intent.putExtra("mapcoord", mapCoord);
                startActivity(intent);
            }
        });
    }

    protected Bitmap doInBackground(String url) throws InterruptedException {
        final Bitmap[] tmpBitmap = {null};
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    InputStream is = new java.net.URL(url).openStream();
                    tmpBitmap[0] = BitmapFactory.decodeStream(is);
                    is.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        t.start();
        t.join();
        return tmpBitmap[0];
    }
}